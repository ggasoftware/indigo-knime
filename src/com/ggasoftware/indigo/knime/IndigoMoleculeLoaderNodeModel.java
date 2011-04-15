package com.ggasoftware.indigo.knime;

import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.data.container.*;
import org.knime.core.data.def.*;
import org.knime.core.node.*;

import com.ggasoftware.indigo.*;

import java.io.File;

/**
 * This is the model implementation of IndigoMoleculeLoader.
 * 
 * @author GGA Software Services LLC
 */
public class IndigoMoleculeLoaderNodeModel extends NodeModel
{

   private final IndigoMoleculeLoaderSettings _settings = new IndigoMoleculeLoaderSettings();

   protected IndigoMoleculeLoaderNodeModel()
   {
      super(1, 2);
   }

   protected DataTableSpec[] getDataTableSpecs (DataTableSpec inputTableSpec)
         throws InvalidSettingsException
   {
      String newColName = _settings.newColName;
      int newColIdx = inputTableSpec.getNumColumns();
      int colIdx = inputTableSpec.findColumnIndex(_settings.colName);

      if (colIdx == -1)
         throw new InvalidSettingsException("column not found");

      if (_settings.replaceColumn)
      {
         newColName = _settings.colName;
         newColIdx = colIdx;
      }

      DataColumnSpec validOutputColumnSpec = new DataColumnSpecCreator(
            newColName, IndigoMolCell.TYPE).createSpec();
      DataColumnSpec invalidOutputColumnSpec = new DataColumnSpecCreator(
            newColName, StringCell.TYPE).createSpec();

      DataColumnSpec[] validOutputColumnSpecs, invalidOutputColumnSpecs;

      if (_settings.replaceColumn)
      {
         validOutputColumnSpecs = new DataColumnSpec[inputTableSpec
               .getNumColumns()];
         invalidOutputColumnSpecs = new DataColumnSpec[inputTableSpec
               .getNumColumns()];
      }
      else
      {
         validOutputColumnSpecs = new DataColumnSpec[inputTableSpec
               .getNumColumns() + 1];
         invalidOutputColumnSpecs = new DataColumnSpec[inputTableSpec
               .getNumColumns() + 1];
      }

      for (int i = 0; i < inputTableSpec.getNumColumns(); i++)
      {
         DataColumnSpec columnSpec = inputTableSpec.getColumnSpec(i);

         if (_settings.replaceColumn && i == newColIdx)
         {
            validOutputColumnSpecs[i] = validOutputColumnSpec;
            invalidOutputColumnSpecs[i] = invalidOutputColumnSpec;
         }
         else
         {
            validOutputColumnSpecs[i] = columnSpec;
            invalidOutputColumnSpecs[i] = columnSpec;
         }
      }

      if (!_settings.replaceColumn)
      {
         validOutputColumnSpecs[newColIdx] = validOutputColumnSpec;
         invalidOutputColumnSpecs[newColIdx] = invalidOutputColumnSpec;
      }

      return new DataTableSpec[] { new DataTableSpec(validOutputColumnSpecs),
            new DataTableSpec(invalidOutputColumnSpecs) };
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute (final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception
   {

      DataTableSpec inputTableSpec = inData[0].getDataTableSpec();
      DataTableSpec[] outputSpecs = getDataTableSpecs(inputTableSpec);

      BufferedDataContainer validOutputContainer = exec
            .createDataContainer(outputSpecs[0]);
      BufferedDataContainer invalidOutputContainer = exec
            .createDataContainer(outputSpecs[1]);

      int newColIdx = inputTableSpec.getNumColumns();
      int colIdx = inputTableSpec.findColumnIndex(_settings.colName);

      if (colIdx == -1)
         throw new Exception("column not found");

      if (_settings.replaceColumn)
      {
         newColIdx = colIdx;
      }

      CloseableRowIterator it = inData[0].iterator();
      int rowNumber = 1;

      try
      {
         IndigoPlugin.lock();
         Indigo indigo = IndigoPlugin.getIndigo();

         indigo.setOption("ignore-stereochemistry-errors",
               _settings.ignoreStereochemistryErrors);
         indigo.setOption("treat-x-as-pseudoatom",
               _settings.treatXAsPseudoatom);

         while (it.hasNext())
         {
            DataRow inputRow = it.next();
            RowKey key = inputRow.getKey();
            DataCell[] cells;

            if (_settings.replaceColumn)
               cells = new DataCell[inputRow.getNumCells()];
            else
               cells = new DataCell[inputRow.getNumCells() + 1];

            try
            {
               IndigoObject mol = indigo.loadMolecule(inputRow.getCell(colIdx)
                     .toString());

               for (int i = 0; i < inputRow.getNumCells(); i++)
               {
                  if (_settings.replaceColumn && i == newColIdx)
                     cells[i] = new IndigoMolCell(mol);
                  else
                     cells[i] = inputRow.getCell(i);
               }
               if (!_settings.replaceColumn)
                  cells[newColIdx] = new IndigoMolCell(mol);

               validOutputContainer.addRowToTable(new DefaultRow(key, cells));
            }
            catch (IndigoException e)
            {
               for (int i = 0; i < inputRow.getNumCells(); i++)
               {
                  if (_settings.replaceColumn && i == newColIdx)
                     cells[i] = new StringCell(e.getMessage());
                  else
                     cells[i] = inputRow.getCell(i);
               }
               if (!_settings.replaceColumn)
                  cells[newColIdx] = new StringCell(e.getMessage());
               invalidOutputContainer.addRowToTable(new DefaultRow(key, cells));
            }
            exec.checkCanceled();
            exec.setProgress(rowNumber / (double) inData[0].getRowCount(),
                  "Adding row " + rowNumber);

            rowNumber++;
         }
      }
      finally
      {
         IndigoPlugin.unlock();
      }

      validOutputContainer.close();
      invalidOutputContainer.close();
      return new BufferedDataTable[] { validOutputContainer.getTable(),
            invalidOutputContainer.getTable() };
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void reset ()
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected DataTableSpec[] configure (final DataTableSpec[] inSpecs)
         throws InvalidSettingsException
   {
      DataTableSpec inputTableSpec = inSpecs[0];
      return getDataTableSpecs(inputTableSpec);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveSettingsTo (final NodeSettingsWO settings)
   {

      _settings.saveSettings(settings);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadValidatedSettingsFrom (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {

      _settings.loadSettings(settings);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void validateSettings (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {

      IndigoMoleculeLoaderSettings s = new IndigoMoleculeLoaderSettings();
      s.loadSettings(settings);
      if (!s.replaceColumn
            && ((s.newColName == null) || (s.newColName.length() < 1)))
      {
         throw new InvalidSettingsException("No name for new column given");
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadInternals (final File internDir,
         final ExecutionMonitor exec) throws IOException,
         CanceledExecutionException
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveInternals (final File internDir,
         final ExecutionMonitor exec) throws IOException,
         CanceledExecutionException
   {
   }
}
