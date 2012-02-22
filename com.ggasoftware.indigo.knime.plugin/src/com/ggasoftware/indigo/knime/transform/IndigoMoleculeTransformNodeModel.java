package com.ggasoftware.indigo.knime.transform;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.ggasoftware.indigo.IndigoException;
import com.ggasoftware.indigo.IndigoObject;
import com.ggasoftware.indigo.knime.cell.IndigoDataValue;
import com.ggasoftware.indigo.knime.cell.IndigoMolCell;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryReactionValue;
import com.ggasoftware.indigo.knime.common.IndigoNodeModel;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

/**
 * This is the model implementation of IndigoMoleculeTransform.
 * 
 */
public class IndigoMoleculeTransformNodeModel extends IndigoNodeModel {

   private final IndigoMoleculeTransformSettings _settings = new IndigoMoleculeTransformSettings();
   
   // the logger instance
   private static final NodeLogger LOGGER = NodeLogger
         .getLogger(IndigoMoleculeTransformNodeModel.class);

   /**
    * Constructor for the node model.
    */
   protected IndigoMoleculeTransformNodeModel() {
      super(2, 1);
   }

   class Transformation	{
      IndigoObject reaction;
      String rowid; 
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception {
      BufferedDataTable reactionTable = inData[IndigoMoleculeTransformSettings.REACTION_PORT];
      BufferedDataTable moleculeTable = inData[IndigoMoleculeTransformSettings.MOL_PORT];
      DataTableSpec moleculeSpec = moleculeTable.getDataTableSpec();
      DataTableSpec reactionSpec = reactionTable.getDataTableSpec();
      
      DataTableSpec outputSpec = _getDataTableSpec(moleculeSpec);
      BufferedDataContainer outputContainer = exec.createDataContainer(outputSpec);
      /*
       * Search columns
       */
      int moleculeColIdx = moleculeSpec.findColumnIndex(_settings.molColumn.getStringValue());
      if(moleculeColIdx == -1)
         throw new RuntimeException("column '" + _settings.molColumn.getStringValue() + "' can not be found");
      
      int reactionColIdx = reactionSpec.findColumnIndex(_settings.reactionColumn.getStringValue());
      if(reactionColIdx == -1)
         throw new RuntimeException("column '" + _settings.reactionColumn.getStringValue() + "' can not be found");
      
      boolean missingWarning = false;
      /*
       * Create reactions list
       */
      LinkedList<Transformation> reactionsList = new LinkedList<Transformation>();
      
      for(DataRow inputRow : reactionTable) {
         
         DataCell dataCell = inputRow.getCell(reactionColIdx);
         
         if(dataCell.isMissing()) {
            if(!missingWarning)
               LOGGER.warn("table with reactions contains missing cells: skipping");
            missingWarning = true;
            continue;
         }
         try {
            IndigoPlugin.lock();

            Transformation transformation = new Transformation();
            transformation.rowid = inputRow.getKey().toString();
            transformation.reaction = ((IndigoDataValue)dataCell).getIndigoObject();
            
            reactionsList.add(transformation);
            
         } catch (IndigoException e) {
            appendWarningMessage("Error while loading reaction with RowId '" + inputRow.getKey() + "': " + e.getMessage());
         } finally {
            IndigoPlugin.unlock();
         }
      }
      /*
       * Iterate and transform molecules
       */
      missingWarning = false;
      /*
       * Define transform column
       */
      int transformColIdx = -1;
      if(_settings.appendColumn.getBooleanValue()) 
         transformColIdx = outputSpec.getNumColumns() - 1;
      else
         transformColIdx = moleculeColIdx;
      
      int rowNumber = 1;
      for (DataRow inputRow : moleculeTable) {
         DataCell dataCell = inputRow.getCell(moleculeColIdx);
         DataCell transformCell = null;
         
         if (dataCell.isMissing())
            transformCell = dataCell;
         
         if(reactionsList.isEmpty()) {
            if(!missingWarning)
               LOGGER.warn("reactions list is empty: no transformations were applied");
            transformCell = dataCell;
            missingWarning = true;
         }
         /*
          * Transform given molecule
          */
         if(transformCell == null) {
            String transformRowId = null;
            try {
               IndigoPlugin.lock();
               
               IndigoObject io = ((IndigoDataValue)dataCell).getIndigoObject();
               IndigoObject molecule = io;
               /*
                * Apply all the reactions from the list
                */
               for(Transformation transformation : reactionsList) {
                  transformRowId = transformation.rowid; 
                  IndigoPlugin.getIndigo().transform(transformation.reaction, molecule);
               }
               /*
                * Create result cell
                */
               transformCell = new IndigoMolCell(molecule);
               
            } catch (IndigoException e) {
               String message = "Warning while applying a transformation: " + e.getMessage() + " for molecule with rowId: " + inputRow.getKey().toString();
               if (transformRowId != null)
                  message += ". Transformation rowId: " + transformRowId;
               appendWarningMessage(message);
               transformCell = null;
            } finally {
               IndigoPlugin.unlock();
            }
         }
         /*
          * Create result row cells
          */
         if(transformCell == null)
            transformCell = DataType.getMissingCell();
         
         DataCell[] outputCells = new DataCell[outputSpec.getNumColumns()];
         for (int c_idx = 0; c_idx < outputSpec.getNumColumns(); c_idx++) {
            if(c_idx == transformColIdx)
               outputCells[c_idx] = transformCell;
            else
               outputCells[c_idx] = inputRow.getCell(c_idx);
         }
         /*
          * Add result row
          */
         outputContainer.addRowToTable(new DefaultRow(inputRow.getKey(),
               outputCells));
         
         exec.checkCanceled();
         exec.setProgress(rowNumber / (double) moleculeTable.getRowCount(),
               "Adding row " + rowNumber);
         rowNumber++;
      }
      
      
      handleWarningMessages();
      outputContainer.close();
      return new BufferedDataTable[] { outputContainer.getTable() };
   }

   private DataTableSpec _getDataTableSpec(DataTableSpec inSpec) {
      DataColumnSpec[] specs;
      
      if(_settings.appendColumn.getBooleanValue())
         specs = new DataColumnSpec[inSpec.getNumColumns() + 1];
      else
         specs = new DataColumnSpec[inSpec.getNumColumns()];
      
      int col_idx;

      for (col_idx = 0; col_idx < inSpec.getNumColumns(); col_idx++)
         specs[col_idx] = inSpec.getColumnSpec(col_idx);
      
      if(_settings.appendColumn.getBooleanValue())
         specs[col_idx] = new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec, _settings.newColName.getStringValue()), IndigoMolCell.TYPE).createSpec();
      
      return new DataTableSpec(specs);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void reset() {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
         throws InvalidSettingsException {
      DataTableSpec rspec = inSpecs[IndigoMoleculeTransformSettings.REACTION_PORT];
      DataTableSpec mspec = inSpecs[IndigoMoleculeTransformSettings.MOL_PORT];
      
      searchIndigoColumn(rspec, _settings.reactionColumn, IndigoQueryReactionValue.class);
      searchIndigoColumn(mspec, _settings.molColumn, IndigoMolValue.class);
      /*
       * Set loading parameters warning message
       */
      if(_settings.warningMessage != null) {
         setWarningMessage(_settings.warningMessage);
      }
      
      return new DataTableSpec[] { null };
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
   protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
         throws InvalidSettingsException {
      _settings.loadSettingsFrom(settings);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void validateSettings(final NodeSettingsRO settings)
         throws InvalidSettingsException {
      IndigoMoleculeTransformSettings s = new IndigoMoleculeTransformSettings();
      s.loadSettingsFrom(settings);
      
      if (s.appendColumn.getBooleanValue() && ((s.newColName.getStringValue() == null) || (s.newColName.getStringValue().length() < 1)))
         throw new InvalidSettingsException("No name for new column given");
      
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadInternals(final File internDir,
         final ExecutionMonitor exec) throws IOException,
         CanceledExecutionException {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveInternals(final File internDir,
         final ExecutionMonitor exec) throws IOException,
         CanceledExecutionException {
   }

}
