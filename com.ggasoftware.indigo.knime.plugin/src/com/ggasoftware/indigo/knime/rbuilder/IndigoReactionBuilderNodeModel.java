package com.ggasoftware.indigo.knime.rbuilder;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
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

import com.ggasoftware.indigo.knime.cell.IndigoMolCell;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolCell;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolValue;
import com.ggasoftware.indigo.knime.common.IndigoNodeModel;

/**
 * This is the model implementation of IndigoReactionBuilder.
 * 
 * 
 * @author
 */
public class IndigoReactionBuilderNodeModel extends IndigoNodeModel {
   
   private final IndigoReactionBuilderSettings _settings = new IndigoReactionBuilderSettings();

   // the logger instance
   private static final NodeLogger LOGGER = NodeLogger
         .getLogger(IndigoReactionBuilderNodeModel.class);

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

      return new BufferedDataTable[] { null };
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
                * If not exist then search the appropriate one
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
