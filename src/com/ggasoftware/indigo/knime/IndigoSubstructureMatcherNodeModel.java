package com.ggasoftware.indigo.knime;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.data.container.*;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.*;

import com.ggasoftware.indigo.*;

/**
 * This is the model implementation of IndigoSmartsMatcher.
 * 
 * 
 * @author GGA Software Services LLC
 */
public class IndigoSubstructureMatcherNodeModel extends NodeModel
{
   IndigoSubstructureMatcherSettings _settings = new IndigoSubstructureMatcherSettings();

   private static final NodeLogger LOGGER = NodeLogger.getLogger(IndigoSubstructureMatcherNodeModel.class);
   
   /**
    * Constructor for the node model.
    */
   protected IndigoSubstructureMatcherNodeModel()
   {
      super(2, 2);
   }
   
   protected DataTableSpec getDataTableSpec (DataTableSpec inputTableSpec) throws InvalidSettingsException
   {
      DataColumnSpec[] specs;
      
      if (_settings.appendColumn)
         specs = new DataColumnSpec[inputTableSpec.getNumColumns() + 1];
      else
         specs = new DataColumnSpec[inputTableSpec.getNumColumns()];

      int i;
      
      for (i = 0; i < inputTableSpec.getNumColumns(); i++)
         specs[i] = inputTableSpec.getColumnSpec(i);
      
      if (_settings.appendColumn)
         specs[i] = new DataColumnSpecCreator(_settings.newColName, IndigoMolCell.TYPE).createSpec();
      
      return new DataTableSpec(specs);
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute (final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception
   {
      DataTableSpec inputTableSpec = inData[0].getDataTableSpec();
      DataTableSpec inputTableSpec2 = inData[1].getDataTableSpec();

      BufferedDataContainer validOutputContainer = exec
            .createDataContainer(inputTableSpec);
      BufferedDataContainer invalidOutputContainer = exec
            .createDataContainer(inputTableSpec);

      int colIdx = inputTableSpec.findColumnIndex(_settings.colName);

      if (colIdx == -1)
         throw new Exception("column not found");

      int colIdx2 = inputTableSpec2.findColumnIndex(_settings.colName2);

      if (colIdx2 == -1)
         throw new Exception("query column not found");
      
      IndigoObject query;
      
      {
         CloseableRowIterator it = inData[1].iterator();
         if (!it.hasNext())
            throw new Exception("no query molecule found in the data source");
         DataRow row = it.next();
         query = ((IndigoQueryMolValue)row.getCell(colIdx2)).getIndigoObject();
         if (it.hasNext())
            LOGGER.warn("second data source contains more than one row; ignoring all others");
      }
      
      CloseableRowIterator it = inData[0].iterator();
      int rowNumber = 1;

      try
      {
         IndigoPlugin.lock();

         Indigo indigo = IndigoPlugin.getIndigo();

         while (it.hasNext())
         {
            DataRow inputRow = it.next();

            IndigoObject match = indigo.substructureMatcher(((IndigoMolCell) (inputRow.getCell(colIdx)))
                        .getIndigoObject()).match(query);

            if (match != null)
            {
               validOutputContainer.addRowToTable(inputRow);
            }
            else
            {
               invalidOutputContainer.addRowToTable(inputRow);
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
      return new DataTableSpec[] { getDataTableSpec(inSpecs[0]), inSpecs[0] };
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
      IndigoSubstructureMatcherSettings s = new IndigoSubstructureMatcherSettings();
      s.loadSettings(settings);

      if (s.colName == null || s.colName.length() < 1)
         throw new InvalidSettingsException("column name must be specified");
      if (s.colName2 == null || s.colName2.length() < 1)
         throw new InvalidSettingsException("query column name must be specified");
      if (s.appendColumn && (s.newColName == null || s.newColName.length() < 1))
         throw new InvalidSettingsException("new column name must be specified");
      if (s.appendColumn && !s.highlight && !s.align)
         throw new InvalidSettingsException("without highlighting or alignment, appending new column makes no sense");
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
