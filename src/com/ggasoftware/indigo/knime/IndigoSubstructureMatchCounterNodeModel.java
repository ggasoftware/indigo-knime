package com.ggasoftware.indigo.knime;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.data.container.*;
import org.knime.core.data.def.*;

import com.ggasoftware.indigo.*;

public class IndigoSubstructureMatchCounterNodeModel extends NodeModel
{
   IndigoSubstructureMatchCounterSettings _settings = new IndigoSubstructureMatchCounterSettings();

   private static final NodeLogger LOGGER = NodeLogger.getLogger(IndigoSubstructureMatchCounterNodeModel.class);
   
   protected IndigoSubstructureMatchCounterNodeModel()
   {
      super(2, 1);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute (final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception
   {
      DataTableSpec spec = getDataTableSpec(inData[0].getDataTableSpec());
      DataTableSpec spec2 = getDataTableSpec(inData[1].getDataTableSpec());

      BufferedDataContainer outputContainer = exec.createDataContainer(spec);

      int colIdx = spec.findColumnIndex(_settings.colName);

      if (colIdx == -1)
         throw new Exception("column not found");

      int colIdx2 = spec2.findColumnIndex(_settings.colName2);

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

         indigo.setOption("embedding-uniqueness", _settings.uniqueness.name()
               .toLowerCase());

         while (it.hasNext())
         {
            DataRow inputRow = it.next();
            RowKey key = inputRow.getKey();
            DataCell[] cells = new DataCell[inputRow.getNumCells() + 1];
            IndigoObject io = ((IndigoMolCell) (inputRow.getCell(colIdx)))
                  .getIndigoObject();
            int i;

            for (i = 0; i < inputRow.getNumCells(); i++)
               cells[i] = inputRow.getCell(i);

            IndigoObject matcher = indigo.substructureMatcher(io);

            cells[i++] = new IntCell(matcher.countMatches(query));

            outputContainer.addRowToTable(new DefaultRow(key, cells));
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

      outputContainer.close();
      return new BufferedDataTable[] { outputContainer.getTable() };
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void reset ()
   {
   }

   protected DataTableSpec getDataTableSpec (DataTableSpec inSpec) throws InvalidSettingsException
   {
      if (_settings.newColName == null || _settings.newColName.length() < 1)
         throw new InvalidSettingsException("New column name must be specified");
      
      DataColumnSpec[] specs = new DataColumnSpec[inSpec.getNumColumns() + 1];

      int i;

      for (i = 0; i < inSpec.getNumColumns(); i++)
         specs[i] = inSpec.getColumnSpec(i);

      specs[i] = new DataColumnSpecCreator(_settings.newColName, IntCell.TYPE)
            .createSpec();

      return new DataTableSpec(specs);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected DataTableSpec[] configure (final DataTableSpec[] inSpecs)
         throws InvalidSettingsException
   {
      return new DataTableSpec[] { getDataTableSpec(inSpecs[0]) };
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
      IndigoSubstructureMatchCounterSettings s = new IndigoSubstructureMatchCounterSettings();
      s.loadSettings(settings);
      if (s.colName == null || s.colName.length() < 1)
         throw new InvalidSettingsException("column name must be specified");
      if (s.colName2 == null || s.colName2.length() < 1)
         throw new InvalidSettingsException("query column name must be specified");
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
