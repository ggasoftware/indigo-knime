package com.ggasoftware.indigo.knime.rautomapper;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.ggasoftware.indigo.IndigoException;
import com.ggasoftware.indigo.knime.cell.IndigoDataValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryReactionCell;
import com.ggasoftware.indigo.knime.cell.IndigoReactionCell;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

import com.ggasoftware.indigo.knime.rautomapper.IndigoReactionAutomapperSettings.AAMode;

/**
 * This is the model implementation of ReactionAutomapper.
 * 
 * 
 * @author
 */
public class IndigoReactionAutomapperNodeModel extends NodeModel {

   private final IndigoReactionAutomapperSettings _settings = new IndigoReactionAutomapperSettings();

   public static final int INPUT_PORT = 0;

   /**
    * Constructor for the node model.
    */
   protected IndigoReactionAutomapperNodeModel() {
      super(1, 2);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute(final BufferedDataTable[] inData, final ExecutionContext exec) throws Exception {
      DataTableSpec inputTableSpec = inData[0].getDataTableSpec();
      DataTableSpec[] outputSpecs = getDataTableSpecs(inputTableSpec);

      BufferedDataContainer validOutputContainer = exec.createDataContainer(outputSpecs[0]);
      BufferedDataContainer invalidOutputContainer = exec.createDataContainer(outputSpecs[1]);

      int colIdx = inputTableSpec.findColumnIndex(_settings.m_column.getStringValue());
      if (colIdx == -1)
         throw new Exception("column not found");

      int newColIdx = _settings.m_replace.getBooleanValue() ? colIdx : inputTableSpec.getNumColumns();

      int rowNumber = 1;
      for (DataRow inputRow : inData[0]) {
         DataCell[] cells = new DataCell[inputRow.getNumCells() + (_settings.m_replace.getBooleanValue() ? 0 : 1)];

         DataCell newcell = null;
         String message = null;

         try {
            IndigoPlugin.lock();

            if (inputRow.getCell(colIdx) instanceof IndigoReactionCell) {
               newcell = new IndigoReactionCell(((IndigoReactionCell) inputRow.getCell(colIdx)).getIndigoObject().clone());
            } else if (inputRow.getCell(colIdx) instanceof IndigoQueryReactionCell) {
               IndigoQueryReactionCell reactionCell = (IndigoQueryReactionCell) inputRow.getCell(colIdx);
               newcell = reactionCell.clone();
            } else {
               newcell = DataType.getMissingCell();
            }
            if (!newcell.isMissing())
               ((IndigoDataValue) newcell).getIndigoObject().automap(AAMode.values()[_settings.m_mode.getIntValue()].name().toLowerCase());
         } catch (IndigoException e) {
            message = e.getMessage();
         } finally {
            IndigoPlugin.unlock();
         }

         if (newcell != null) {
            for (int i = 0; i < inputRow.getNumCells(); i++) {
               cells[i] = _settings.m_replace.getBooleanValue() && i == newColIdx ? newcell : inputRow.getCell(i);
            }
            if (!_settings.m_replace.getBooleanValue()) {
               cells[newColIdx] = newcell;
            }
            validOutputContainer.addRowToTable(new DefaultRow(inputRow.getKey(), cells));
         } else {
            for (int i = 0; i < inputRow.getNumCells(); i++) {
               cells[i] = (_settings.m_replace.getBooleanValue() && i == newColIdx) ? new StringCell(message) : inputRow.getCell(i);
            }
            if (!_settings.m_replace.getBooleanValue()) {
               cells[newColIdx] = new StringCell(message);
            }
            invalidOutputContainer.addRowToTable(new DefaultRow(inputRow.getKey(), cells));
         }

         exec.checkCanceled();
         exec.setProgress(rowNumber / (double) inData[0].getRowCount(), "Adding row " + rowNumber);

         rowNumber++;
      }

      validOutputContainer.close();
      invalidOutputContainer.close();
      return new BufferedDataTable[] { validOutputContainer.getTable(), invalidOutputContainer.getTable() };
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void reset() {
   }

   protected DataTableSpec[] getDataTableSpecs(DataTableSpec inputTableSpec) throws InvalidSettingsException {
      if (_settings.m_column.getStringValue() == null || _settings.m_column.getStringValue().length() < 1)
         throw new InvalidSettingsException("Column name not specified");

      if (!_settings.m_replace.getBooleanValue())
         if (_settings.m_newColumn.getStringValue() == null || _settings.m_newColumn.getStringValue().length() < 1)
            throw new InvalidSettingsException("No new column name specified");

      int colIdx = inputTableSpec.findColumnIndex(_settings.m_column.getStringValue());
      if (colIdx == -1)
         throw new InvalidSettingsException("column not found");

      String newColName = _settings.m_newColumn.getStringValue();
      int newColIdx = inputTableSpec.getNumColumns();
      if (_settings.m_replace.getBooleanValue()) {
         newColName = _settings.m_column.getStringValue();
         newColIdx = colIdx;
      }

      DataType newtype = inputTableSpec.getColumnSpec(colIdx).getType();

      DataColumnSpec validOutputColumnSpec = new DataColumnSpecCreator(newColName, newtype).createSpec();
      DataColumnSpec invalidOutputColumnSpec = new DataColumnSpecCreator(newColName, StringCell.TYPE).createSpec();

      DataColumnSpec[] validOutputColumnSpecs, invalidOutputColumnSpecs;

      if (_settings.m_replace.getBooleanValue()) {
         validOutputColumnSpecs = new DataColumnSpec[inputTableSpec.getNumColumns()];
         invalidOutputColumnSpecs = new DataColumnSpec[inputTableSpec.getNumColumns()];
      } else {
         validOutputColumnSpecs = new DataColumnSpec[inputTableSpec.getNumColumns() + 1];
         invalidOutputColumnSpecs = new DataColumnSpec[inputTableSpec.getNumColumns() + 1];
      }

      for (int i = 0; i < inputTableSpec.getNumColumns(); i++) {
         DataColumnSpec columnSpec = inputTableSpec.getColumnSpec(i);

         if (_settings.m_replace.getBooleanValue() && i == newColIdx) {
            validOutputColumnSpecs[i] = validOutputColumnSpec;
            invalidOutputColumnSpecs[i] = invalidOutputColumnSpec;
         } else {
            validOutputColumnSpecs[i] = columnSpec;
            invalidOutputColumnSpecs[i] = columnSpec;
         }
      }

      if (!_settings.m_replace.getBooleanValue()) {
         validOutputColumnSpecs[newColIdx] = validOutputColumnSpec;
         invalidOutputColumnSpecs[newColIdx] = invalidOutputColumnSpec;
      }

      return new DataTableSpec[] { new DataTableSpec(validOutputColumnSpecs), new DataTableSpec(invalidOutputColumnSpecs) };
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected DataTableSpec[] configure(final DataTableSpec[] inSpecs) throws InvalidSettingsException {
      return getDataTableSpecs(inSpecs[INPUT_PORT]);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveSettingsTo(final NodeSettingsWO settings) {
      _settings.saveSettingsTo(settings);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadValidatedSettingsFrom(final NodeSettingsRO settings) throws InvalidSettingsException {
      _settings.loadSettingsFrom(settings);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void validateSettings(final NodeSettingsRO settings) throws InvalidSettingsException {
      _settings.validateSettings(settings);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveInternals(final File internDir, final ExecutionMonitor exec) throws IOException, CanceledExecutionException {
   }

}
