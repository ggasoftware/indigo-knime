package com.ggasoftware.indigo.knime.rsplitter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.ggasoftware.indigo.IndigoObject;
import com.ggasoftware.indigo.knime.cell.IndigoDataValue;
import com.ggasoftware.indigo.knime.cell.IndigoMolCell;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolCell;
import com.ggasoftware.indigo.knime.cell.IndigoQueryReactionCell;
import com.ggasoftware.indigo.knime.cell.IndigoQueryReactionValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionCell;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;
import com.ggasoftware.indigo.knime.common.IndigoNodeModel;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

/**
 * This is the model implementation of IndigoReactionSplitter.
 * 
 * 
 */
public class IndigoReactionSplitterNodeModel extends IndigoNodeModel {
   
   abstract public class ReactionRule {
      int colIdx = -1;
      abstract List<IndigoObject> extractSelf(IndigoObject reaction);
      abstract String extractSelf(String reaction);
   }
   /*
    * Reaction types building
    */
   public class ReactantReactionRule extends ReactionRule {
      List<IndigoObject> extractSelf(IndigoObject reaction) {
         LinkedList<IndigoObject> result = new LinkedList<IndigoObject>();
         for(IndigoObject mol : reaction.iterateReactants())
            result.add(mol.clone());
         return result;
      }
      String extractSelf(String reaction) {
         String result = null;
         String[] splittedReaction = reaction.split(">");
         if(splittedReaction.length > 0 && splittedReaction[0] != null && !splittedReaction[0].isEmpty())
            result = splittedReaction[0];
         return result;
      }
   }
   public class ProductReactionRule extends ReactionRule {
      List<IndigoObject> extractSelf(IndigoObject reaction) {
         LinkedList<IndigoObject> result = new LinkedList<IndigoObject>();
         for(IndigoObject mol : reaction.iterateProducts())
            result.add(mol.clone());
         return result;
      }
      
      String extractSelf(String reaction) {
         String result = null;
         String[] splittedReaction = reaction.split(">");
         if(splittedReaction.length > 2 && splittedReaction[2] != null && !splittedReaction[2].isEmpty())
            result = splittedReaction[2];
         return result;
      }
   }
   public class CatalystReactionRule extends ReactionRule {
      List<IndigoObject> extractSelf(IndigoObject reaction) {
         LinkedList<IndigoObject> result = new LinkedList<IndigoObject>();
         for(IndigoObject mol : reaction.iterateCatalysts())
            result.add(mol.clone());
         return result;
      }
      
      String extractSelf(String reaction) {
         String result = null;
         String[] splittedReaction = reaction.split(">");
         if(splittedReaction.length > 1 && splittedReaction[1] != null && !splittedReaction[1].isEmpty())
            result = splittedReaction[1];
         return result;
      }
   }
   
   private final IndigoReactionSplitterSettings _settings = new IndigoReactionSplitterSettings();

   // the logger instance
   private static final NodeLogger LOGGER = NodeLogger
         .getLogger(IndigoReactionSplitterNodeModel.class);

   /**
    * Constructor for the node model.
    */
   protected IndigoReactionSplitterNodeModel() {
      super(1, 1);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception {
      BufferedDataTable inputTable = inData[IndigoReactionSplitterSettings.INPUT_PORT];
      DataTableSpec inputSpec = inputTable.getDataTableSpec();
      /*
       * Create output spec
       */
      
      int colIdx = inputSpec.findColumnIndex(_settings.reactionColumn.getStringValue());
      
      if (colIdx == -1)
         throw new Exception("column '" + _settings.reactionColumn.getStringValue() + "' not found");
      
      DataTableSpec outputSpec = _getDataTableSpec(inputSpec, inputSpec.getColumnSpec(colIdx).getType());
      BufferedDataContainer outputContainer = exec.createDataContainer(outputSpec);
      
      /*
       * Define the rules
       */
      HashMap<SettingsModelBoolean, ReactionRule> reactionRules = new HashMap<SettingsModelBoolean, ReactionRule>();
      
      reactionRules.put(_settings.extractReactants, new ReactantReactionRule());
      reactionRules.put(_settings.extractProducts, new ProductReactionRule());
      reactionRules.put(_settings.extractCatalysts, new CatalystReactionRule());
      
      final HashMap<SettingsModelBoolean, SettingsModelString> columnMap = _settings
      .getSettingsColumnMap();
      
      /*
       * Iterate through all the table
       */
      int rowNumber = 1;
      for (DataRow inputRow : inputTable) {
         /*
          * Prepare cells
          */
         DataCell[] outputCells = new DataCell[outputSpec.getNumColumns()];
         int c_idx;
         for (c_idx = 0; c_idx < inputRow.getNumCells(); c_idx++)
            outputCells[c_idx] = inputRow.getCell(c_idx);
         
         DataCell dataCell = inputRow.getCell(colIdx);
         /*
          * Check for missing cell
          */
         if (dataCell.isMissing()) {
            for (SettingsModelBoolean keyMap : columnMap.keySet()) {
               if (keyMap.getBooleanValue()) {
                  /*
                   * Add missing cells
                   */
                  outputCells[c_idx] = DataType.getMissingCell();
                  c_idx++;
               }
            }
         } else {
            REACTIONCELL_TYPE cellType = defineReactionCellType(dataCell);

            for (SettingsModelBoolean keyMap : columnMap.keySet()) {
               if (keyMap.getBooleanValue()) {
                  /*
                   * For molecules and query molecules
                   */
                  IndigoObject resultMolecule = null;
                  /*
                   * For smiles and smarts
                   */
                  String resultSmiles = null;
                  try {
                     IndigoPlugin.lock();
                     ReactionRule reactionRule = reactionRules.get(keyMap);
                     /*
                      * Create objects
                      */
                     if(cellType.equals(REACTIONCELL_TYPE.Reaction) || cellType.equals(REACTIONCELL_TYPE.QueryReaction)) {
                        IndigoObject inputReaction = ((IndigoDataValue)dataCell).getIndigoObject();
                        List<IndigoObject> molecules = reactionRule.extractSelf(inputReaction);
                        /*
                         * Merge all components
                         */
                        for(IndigoObject mol : molecules) {
                           if(resultMolecule == null)
                              resultMolecule = mol;
                           else
                              resultMolecule.merge(mol);
                        }
                     } else {
                        /*
                         * Get source for smile or smarts
                         */
                        IndigoQueryReactionCell queryCell = (IndigoQueryReactionCell) dataCell;
                        resultSmiles = reactionRule.extractSelf(queryCell.getSource());
                     }
                  } catch (Exception e) {
                     LOGGER.warn(e.getMessage());
                     resultMolecule = null;
                     resultSmiles = null;
                  } finally {
                     IndigoPlugin.unlock();
                  }
                  /*
                   * Create output cells
                   */
                  switch (cellType) {
                  case QueryReaction:
                     if (resultMolecule != null)
                        outputCells[c_idx] = IndigoQueryMolCell
                              .fromString(resultMolecule.molfile());
                     break;
                  case QuerySmarts:
                     if (resultSmiles != null)
                        outputCells[c_idx] = IndigoQueryMolCell
                              .fromSmarts(resultSmiles);
                     break;
                  case QuerySmile:
                     if (resultSmiles != null)
                        outputCells[c_idx] = IndigoQueryMolCell
                              .fromString(resultSmiles);
                     break;
                  case Reaction:
                     if (resultMolecule != null)
                        outputCells[c_idx] = new IndigoMolCell(resultMolecule);
                     break;
                  }
                  /*
                   * Create missing cell (if any)
                   */
                  if (outputCells[c_idx] == null)
                     outputCells[c_idx] = DataType.getMissingCell();
                  /*
                   * Increment column index
                   */
                  c_idx++;
               }
            }
         }
         /*
          * Add result row
          */
         outputContainer.addRowToTable(new DefaultRow(inputRow.getKey(),
               outputCells));

         exec.checkCanceled();
         exec.setProgress(rowNumber / (double) inputTable.getRowCount(),
               "Adding row " + rowNumber);

         rowNumber++;
      }
      
      outputContainer.close();
      return new BufferedDataTable[] { outputContainer.getTable() };
   }

   private DataTableSpec _getDataTableSpec(DataTableSpec inSpec,
         DataType columnType) {
      /*
       * Define column types
       */
      if(columnType.equals(IndigoReactionCell.TYPE))
         columnType = IndigoMolCell.TYPE;
      else if(columnType.equals(IndigoQueryReactionCell.TYPE))
         columnType = IndigoQueryMolCell.TYPE;
      else
         throw new RuntimeException("internal error: unsupported column type: " + columnType.toString());

      int ncolumns = inSpec.getNumColumns();
      
      final HashMap<SettingsModelBoolean, SettingsModelString> columnMap = _settings
            .getSettingsColumnMap();
      /*
       * Prepare specs
       */
      for (SettingsModelBoolean keyMap : columnMap.keySet())
         if (keyMap.getBooleanValue())
            ncolumns++;
      
      DataColumnSpec[] specs = new DataColumnSpec[ncolumns];

      int col_idx;

      for (col_idx = 0; col_idx < inSpec.getNumColumns(); col_idx++)
         specs[col_idx] = inSpec.getColumnSpec(col_idx);
      /*
       * Append new specs
       */
      for(SettingsModelBoolean keyMap : columnMap.keySet()) {
         if(keyMap.getBooleanValue()) {
            SettingsModelString keyValue = columnMap.get(keyMap);
            specs[col_idx] = new DataColumnSpecCreator(keyValue.getStringValue(), columnType).createSpec();
            col_idx++;
         }
      }
      
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
      
      searchMixedIndigoColumn(inSpecs[IndigoReactionSplitterSettings.INPUT_PORT], _settings.reactionColumn, IndigoReactionValue.class, IndigoQueryReactionValue.class);
      
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
      
      IndigoReactionSplitterSettings s = new IndigoReactionSplitterSettings();
      s.loadSettingsFrom(settings);
      
      boolean extractionExists = false;
      
      final HashMap<SettingsModelBoolean, SettingsModelString> columnMap = s.getSettingsColumnMap();
      
      for(SettingsModelBoolean keyMap : columnMap.keySet()) 
         extractionExists |= keyMap.getBooleanValue();
      
      if(!extractionExists)
         throw new InvalidSettingsException("output extraction columns were not defined");
      
      for(SettingsModelBoolean keyMap : columnMap.keySet()) {
         SettingsModelString keyValue = columnMap.get(keyMap);
         if(keyMap.getBooleanValue() && (keyValue.getStringValue() == null || keyValue.getStringValue().length() < 1))
            throw new InvalidSettingsException("'" + keyValue.getKey() + "' column name can not be empty");
      }
      
      
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
