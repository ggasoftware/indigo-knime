package com.ggasoftware.indigo.knime.rbuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.ggasoftware.indigo.IndigoException;
import com.ggasoftware.indigo.IndigoObject;
import com.ggasoftware.indigo.knime.cell.IndigoDataValue;
import com.ggasoftware.indigo.knime.cell.IndigoMolCell;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolCell;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryReactionCell;
import com.ggasoftware.indigo.knime.cell.IndigoReactionCell;
import com.ggasoftware.indigo.knime.common.IndigoNodeModel;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

/**
 * This is the model implementation of IndigoReactionBuilder.
 * 
 * 
 * @author
 */
public class IndigoReactionBuilderNodeModel extends IndigoNodeModel {
   
   
   class ReactionSmilesBuilder {
      public String reactant;
      public String product;
      public String catalyst;
      
      public String toString() {
         StringBuilder smilesBuilder = new StringBuilder();
         if(reactant != null)
            smilesBuilder.append(reactant);
         smilesBuilder.append(">");
         if(catalyst != null)
            smilesBuilder.append(catalyst);
         smilesBuilder.append(">");
         if(product != null)
            smilesBuilder.append(product);
         return smilesBuilder.toString();
      }
   }
   
   abstract public class ReactionRule {
      int colIdx = -1;
      abstract void addSelf(IndigoObject resultMol, IndigoObject resultReaction);
      abstract void addSelf(String resultMol, ReactionSmilesBuilder smilesBuilder);
   }
   /*
    * Reaction types building
    */
   public class ReactantReactionRule extends ReactionRule {
      void addSelf(IndigoObject resultMol, IndigoObject resultReaction) {
         resultReaction.addReactant(resultMol);
      }
      void addSelf(String resultMol, ReactionSmilesBuilder smilesBuilder) {
         smilesBuilder.reactant = resultMol;
      }
   }
   public class ProductReactionRule extends ReactionRule {
      void addSelf(IndigoObject resultMol, IndigoObject resultReaction) {
         resultReaction.addProduct(resultMol);
      }
      void addSelf(String resultMol, ReactionSmilesBuilder smilesBuilder) {
         smilesBuilder.product = resultMol;
      }
   }
   public class CatalystReactionRule extends ReactionRule {
      void addSelf(IndigoObject resultMol, IndigoObject resultReaction) {
         resultReaction.addCatalyst(resultMol);
      }
      void addSelf(String resultMol, ReactionSmilesBuilder smilesBuilder) {
         smilesBuilder.catalyst = resultMol;
      }
   }

   private final IndigoReactionBuilderSettings _settings = new IndigoReactionBuilderSettings();

   /**
    * Constructor for the node model.
    */
   protected IndigoReactionBuilderNodeModel() {
      super(1, 1);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception {
      
      BufferedDataTable inputTable = inData[IndigoReactionBuilderSettings.INPUT_PORT];
      DataTableSpec inputSpec = inputTable.getDataTableSpec();
      /*
       * Create output spec
       */
      DataType columnType = _defineColumnType(inputSpec);
      DataTableSpec outputSpec = _getDataTableSpec(inputSpec, columnType);
      BufferedDataContainer outputContainer = exec.createDataContainer(outputSpec);
      
      /*
       * Define the rules
       */
      HashMap<SettingsModelBoolean, ReactionRule> reactionRules = new HashMap<SettingsModelBoolean, ReactionRule>();
      reactionRules.put(_settings.addReactants, new ReactantReactionRule());
      reactionRules.put(_settings.addProducts, new ProductReactionRule());
      reactionRules.put(_settings.addCatalysts, new CatalystReactionRule());
      
      HashMap<SettingsModelBoolean, SettingsModelString> columnMap = _settings.getSettingsColumnMap();
      /*
       * Search the columns
       */
      for(SettingsModelBoolean keyMap : columnMap.keySet()) {
         if(keyMap.getBooleanValue()) {
            ReactionRule reactionRule = reactionRules.get(keyMap);
            String keyValue = columnMap.get(keyMap).getStringValue();
            /*
             * Search column index
             */
            reactionRule.colIdx = inputSpec.findColumnIndex(keyValue);
            if (reactionRule.colIdx == -1)
               throw new RuntimeException("column '" + keyValue + "' not found");
         }
      }
      
      /*
       * Iterate through all the table
       */
      int rowNumber = 1;
      for (DataRow inputRow : inputTable) {
         /*
          * Prepare cells
          */
         DataCell[] outputCells = new DataCell[inputRow.getNumCells() + 1];
         int c_idx;
         for (c_idx = 0; c_idx < inputRow.getNumCells(); c_idx++)
            outputCells[c_idx] = inputRow.getCell(c_idx);
         
         MOLCELL_TYPE cellType = null;
         
         /*
          * For molecules and query molecules
          */
         IndigoObject resultReaction = null;
         /*
          * For smiles and smarts
          */
         ReactionSmilesBuilder smilesBuilder = null;
         
         try {
            IndigoPlugin.lock();

            for (SettingsModelBoolean keyMap : reactionRules.keySet()) {
               if (keyMap.getBooleanValue()) {
                  ReactionRule reactionRule = reactionRules.get(keyMap);
                  /*
                   * Prepare data cell
                   */
                  DataCell dataCell = inputRow.getCell(reactionRule.colIdx);
                  if (dataCell.isMissing())
                     continue;
                  /*
                   * Define type
                   */
                  MOLCELL_TYPE inputType = defineMolCellType(dataCell);
                  
                  if(cellType == null) {
                     cellType = inputType;
                     /*
                      * Create builder statement
                      */
                     switch (cellType) {
                     case Molecule:
                        resultReaction = IndigoPlugin.getIndigo().createReaction();
                        break;
                     case QueryMolecule:
                        resultReaction = IndigoPlugin.getIndigo().createQueryReaction();
                        break;
                     case QuerySmarts:
                        smilesBuilder = new ReactionSmilesBuilder();
                        break;
                     case QuerySmile:
                        smilesBuilder = new ReactionSmilesBuilder();
                        break;
                     }
                  } else {
                     /*
                      * cell type already exists
                      */
                     if(!cellType.equals(inputType)) {
                        String errMessage = "can not merge two different types: '" + cellType.toString() + "' '" + inputType.toString() + "'";
                        resultReaction = null;
                        cellType = null;
                        throw new RuntimeException(errMessage);
                     }
                  }
                  /*
                   * Append molecule from cell
                   */
                  if(cellType.equals(MOLCELL_TYPE.QuerySmarts) || cellType.equals(MOLCELL_TYPE.QuerySmile)) {
                     /*
                      * Get source for smile or smarts
                      */
                     IndigoQueryMolCell queryCell = (IndigoQueryMolCell)dataCell;
                     reactionRule.addSelf(queryCell.getSource(), smilesBuilder);
                  } else {
                     /*
                      * Molecule or query molecule
                      * Iterate components
                      */
                     IndigoObject inputMol = ((IndigoDataValue)dataCell).getIndigoObject();
                     for (IndigoObject comp : inputMol.iterateComponents())
                        reactionRule.addSelf(comp.clone(), resultReaction);
                  }
               }
            }
         } catch (Exception e) {
            appendWarningMessage("Could not calculate result reaction for RowId '" + inputRow.getKey()+ "': " + e.getMessage());
            cellType = null;
         } finally {
            IndigoPlugin.unlock();
         }
         
         if(cellType == null) {
            outputCells[c_idx] = DataType.getMissingCell();
         } else {
            try {
               switch (cellType) {
               case Molecule:
                  outputCells[c_idx] = new IndigoReactionCell(resultReaction);
                  break;
               case QueryMolecule:
                  outputCells[c_idx] = IndigoQueryReactionCell.fromString(resultReaction.molfile());
                  break;
               case QuerySmarts:
                  outputCells[c_idx] = IndigoQueryReactionCell.fromSmarts(smilesBuilder.toString());
                  break;
               case QuerySmile:
                  outputCells[c_idx] = IndigoQueryReactionCell.fromString(smilesBuilder.toString());
                  break;
               }
            } catch (IndigoException e) {
               appendWarningMessage("can not create result cell: " + e.getMessage());
               outputCells[c_idx] = DataType.getMissingCell();
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

      handleWarningMessages();
      outputContainer.close();

      return new BufferedDataTable[] { outputContainer.getTable() };
   }
   
   protected DataTableSpec _getDataTableSpec (DataTableSpec inSpec, DataType columnType) throws InvalidSettingsException {
      DataColumnSpec[] specs = new DataColumnSpec[inSpec.getNumColumns() + 1];
      
      int col_idx;

      for (col_idx = 0; col_idx < inSpec.getNumColumns(); col_idx++)
         specs[col_idx] = inSpec.getColumnSpec(col_idx);
      
      if(columnType.equals(IndigoMolCell.TYPE))
         columnType = IndigoReactionCell.TYPE;
      else if(columnType.equals(IndigoQueryMolCell.TYPE))
         columnType = IndigoQueryReactionCell.TYPE;
      else
         throw new RuntimeException("internal error: unsupported column type");

      specs[col_idx] = new DataColumnSpecCreator(_settings.newColName.getStringValue(), columnType).createSpec();

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
      
      DataTableSpec dataTableSpec = inSpecs[IndigoReactionBuilderSettings.INPUT_PORT];
      DataType result = _defineColumnType(dataTableSpec);
      
      if(result == null) {
         /*
          * Search appropriate types
          */
         LinkedList<String> includeList = new LinkedList<String>();
         DataType selectedType = null;
         for (int col_idx = 0; col_idx < dataTableSpec.getNumColumns(); col_idx++) {
            DataColumnSpec columnSpec = dataTableSpec.getColumnSpec(col_idx);
            if(_settings.columnFilter.includeColumn(columnSpec)) {
               String tableColName = columnSpec.getName();
               if(selectedType == null)
                  selectedType = columnSpec.getType();
               /*
                * Search only the one type columns
                */
               if(selectedType.equals(columnSpec.getType()))
                  includeList.addFirst(tableColName);
            }
         }
         if(includeList.isEmpty())
            throw new InvalidSettingsException("no supported types in the given table");
         /*
          * Autoselect columns
          */
         HashMap<SettingsModelBoolean, SettingsModelString> columnMap = _settings.getSettingsColumnMap();
         for(SettingsModelBoolean keyMap : columnMap.keySet()) {
            if(keyMap.getBooleanValue()) {
               SettingsModelString keyValue = columnMap.get(keyMap);
               String colName = keyValue.getStringValue();
               /*
                * If not exist then search for the appropriate one
                */
               if(colName == null || colName.length() < 1) {
                  String tableColName;
                  if(includeList.size() == 1)
                     tableColName = includeList.getFirst();
                  else
                     tableColName = includeList.pollFirst();
                  /*
                   * Set selected column
                   */
                  keyValue.setStringValue(tableColName);
                  result = selectedType;
                  setWarningMessage("autoconfig: set selected column '" + tableColName + "' for '" + keyValue.getKey() + "'");
               } else {
                  /*
                   * Check for correctness
                   */
                  searchMixedIndigoColumn(dataTableSpec, keyValue, IndigoMolValue.class, IndigoQueryMolValue.class);
               }
            }
         }
      }
      
      if(result == null)
         throw new InvalidSettingsException("no columns were selected");
      
      if(!result.equals(IndigoMolCell.TYPE) && !result.equals(IndigoQueryMolCell.TYPE))
         throw new InvalidSettingsException("unsupported type: " + result.toString());
      
      if(_settings.warningMessage != null) {
         setWarningMessage(_settings.warningMessage);
      }

      return new DataTableSpec[] { null };
   }
   
   private DataType _defineColumnType(DataTableSpec tableSpec) throws InvalidSettingsException {
      
      DataType result = null;
      
      HashMap<SettingsModelBoolean, SettingsModelString> columnMap = _settings.getSettingsColumnMap();
      for(SettingsModelBoolean keyMap : columnMap.keySet()) {
         if(keyMap.getBooleanValue()) {
            String colName = columnMap.get(keyMap).getStringValue();
            result = _checkTableType(tableSpec, colName, result);
         }
      }
         
      return result;
   }
   private DataType _checkTableType(DataTableSpec dataTableSpec, String colName, DataType result) throws InvalidSettingsException {
      if(colName == null || colName.length() < 1)
         return result;
      
      DataType tableType = dataTableSpec.getColumnSpec(colName).getType();
      
      if(result != null) {
         if(!result.equals(tableType))
            throw new InvalidSettingsException("selected columns contain two different types: '" + result.toString() + "', '" + tableType.toString());
      } else {
         result = tableType;
      }
      return result;
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
      
      IndigoReactionBuilderSettings s = new IndigoReactionBuilderSettings();
      s.loadSettingsFrom(settings);
      
      if(s.newColName.getStringValue() == null || s.newColName.getStringValue().length() < 1)
         throw new InvalidSettingsException("result column name can not be empty");

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
