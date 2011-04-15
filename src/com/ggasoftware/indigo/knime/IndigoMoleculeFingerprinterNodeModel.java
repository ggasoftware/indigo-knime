package com.ggasoftware.indigo.knime;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.vector.bitvector.SparseBitVectorCell;
import org.knime.core.data.vector.bitvector.SparseBitVectorCellFactory;
import org.knime.core.node.*;

import com.ggasoftware.indigo.*;

/**
 * This is the model implementation of IndigoMoleculeFingerprinter.
 * 
 * 
 * @author
 */
public class IndigoMoleculeFingerprinterNodeModel extends NodeModel
{

   private final IndigoMoleculeFingerprinterSettings _settings = new IndigoMoleculeFingerprinterSettings();
   
   /**
    * Constructor for the node model.
    */
   protected IndigoMoleculeFingerprinterNodeModel ()
   {
      super(1, 1);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute (final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception
   {
      DataTableSpec spec = getDataTableSpec(inData[0].getDataTableSpec());

      BufferedDataContainer outputContainer = exec.createDataContainer(spec);

      int colIdx = spec.findColumnIndex(_settings.colName);

      if (colIdx == -1)
         throw new Exception("column not found");

      CloseableRowIterator it = inData[0].iterator();
      int rowNumber = 1;

      try
      {
         IndigoPlugin.lock();

         IndigoPlugin.getIndigo().setOption("fp-sim-qwords", _settings.fpSizeQWords);
         IndigoPlugin.getIndigo().setOption("fp-tau-qwords", 0);
         IndigoPlugin.getIndigo().setOption("fp-any-qwords", 0);
         IndigoPlugin.getIndigo().setOption("fp-ord-qwords", 0);
         
         while (it.hasNext())
         {
            DataRow inputRow = it.next();
            RowKey key = inputRow.getKey();
            DataCell[] cells = new DataCell[inputRow.getNumCells() + 1];
            IndigoObject io = ((IndigoMolCell) (inputRow.getCell(colIdx)))
                  .getIndigoObject().clone();
            int i;

            io.aromatize();
            String fp = io.fingerprint("sim").toString().substring(6);

            for (i = 0; i < inputRow.getNumCells(); i++)
               cells[i] = inputRow.getCell(i);
            cells[i++] = new SparseBitVectorCellFactory(fp).createDataCell();

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

   protected DataTableSpec getDataTableSpec (DataTableSpec inSpec)
   {
      DataColumnSpec[] specs = new DataColumnSpec[inSpec.getNumColumns() + 1];

      int i;

      for (i = 0; i < inSpec.getNumColumns(); i++)
         specs[i] = inSpec.getColumnSpec(i);

      specs[i] = new DataColumnSpecCreator(_settings.newColName, SparseBitVectorCell.TYPE).createSpec(); 
         
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
      IndigoMoleculeFingerprinterSettings s = new IndigoMoleculeFingerprinterSettings();
      s.loadSettings(settings);
      
      if (s.fpSizeQWords < 1)
         throw new InvalidSettingsException("fingerprint size must be a positive integer");
      if (s.colName == null || s.colName.equals(""))
         throw new InvalidSettingsException("No column name given");
      if (s.newColName == null || s.newColName.equals(""))
         throw new InvalidSettingsException("No name for new column given");
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
