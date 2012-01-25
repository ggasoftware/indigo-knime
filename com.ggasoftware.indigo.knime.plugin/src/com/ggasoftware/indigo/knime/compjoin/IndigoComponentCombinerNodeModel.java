package com.ggasoftware.indigo.knime.compjoin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolCell;
import com.ggasoftware.indigo.knime.common.IndigoNodeModel;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

/**
 * This is the model implementation of IndigoComponentCombiner.
 * 
 * 
 */
public class IndigoComponentCombinerNodeModel extends IndigoNodeModel {

   
   private final IndigoComponentCombinerSettings _settings = new IndigoComponentCombinerSettings();
   // the logger instance
   private static final NodeLogger LOGGER = NodeLogger
         .getLogger(IndigoComponentCombinerNodeModel.class);

   /**
    * Constructor for the node model.
    */
   protected IndigoComponentCombinerNodeModel() {
      super(1, 1);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception {
      
      BufferedDataTable inputTable = inData[IndigoComponentCombinerSettings.INPUT_PORT];
      DataTableSpec inputSpec = inputTable.getDataTableSpec();
      /*
       * Create output spec
       */
      DataType columnType = _defineColumnType(inputSpec);
      DataTableSpec outputSpec = _getDataTableSpec(inputSpec, columnType);
      BufferedDataContainer outputContainer = exec.createDataContainer(outputSpec);
      
      /*
       * Search column indexes
       */
      ArrayList<Integer> includeList = new ArrayList<Integer>();
      for (String colName : _settings.colNames.getIncludeList()) {
         int colIdx = inputSpec.findColumnIndex(colName);

         if (colIdx == -1)
            throw new RuntimeException("column '" + colName + "' not found");
         
         includeList.add(colIdx);
      }
      /*
       * Iterate through all the table
       */
      int rowNumber = 1;
      for (DataRow inputRow : inputTable) {
         /*
          * Prepare cells
          */
         DataCell[] outputCells= new DataCell[inputRow.getNumCells() + 1];
         int c_idx;
         for (c_idx = 0; c_idx < inputRow.getNumCells(); c_idx++)
            outputCells[c_idx] = inputRow.getCell(c_idx);
         
         IndigoObject resultMol = null;
         MOLCELL_TYPE cellType = null;
         /*
          * For query types
          */
         StringBuilder queryCellData = new StringBuilder();
         IndigoQueryMolCell queryCell = null;
         try {
            IndigoPlugin.lock();
            
            for(Integer colIdx : includeList) {
               DataCell dataCell = inputRow.getCell(colIdx);
               /*
                * Skip null cells
                */
               if(dataCell.isMissing())
                  continue;
               /*
                * Merge with molecule
                */
               IndigoObject io = ((IndigoDataValue)dataCell).getIndigoObject();
               /*
                * Define type
                */
               MOLCELL_TYPE inputType = defineMolCellType(dataCell);
               
               if(cellType == null) {
                  /*
                   * Create cell clone
                   */
                  cellType = inputType;
                  if(cellType.equals(MOLCELL_TYPE.QuerySmarts) || cellType.equals(MOLCELL_TYPE.QuerySmile)) {
                     queryCell = (IndigoQueryMolCell)dataCell;
                     queryCellData.append(queryCell.getSource());
                  } else {
                     resultMol = io.clone();
                  }
               } else {
                  /*
                   * cell type already exists
                   */
                  if(!cellType.equals(inputType)) {
                     String errMessage = "can not merge two different types: '" + cellType.toString() + "' '" + inputType.toString() + "'";
                     resultMol = null;
                     cellType = null;
                     throw new RuntimeException(errMessage);
                  }
                  /*
                   * Merge with molecule
                   */
                  switch (cellType) {
                  case Molecule:
                     resultMol.merge(io);
                     break;
                  case QueryMolecule:
                     resultMol.merge(io);
                     break;
                  case QuerySmarts:
                     queryCell = (IndigoQueryMolCell)dataCell;
                     queryCellData.append(".");
                     queryCellData.append(queryCell.getSource());
                     break;
                  case QuerySmile:
                     queryCell = (IndigoQueryMolCell)dataCell;
                     queryCellData.append(".");
                     queryCellData.append(queryCell.getSource());
                     break;
                  }
               }
            }
         } catch (Exception e) {
            LOGGER.warn(e.getMessage());
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
                  outputCells[c_idx] = new IndigoMolCell(resultMol);
                  break;
               case QueryMolecule:
                  outputCells[c_idx] = IndigoQueryMolCell.fromString(resultMol
                        .molfile());
                  break;
               case QuerySmarts:
                  outputCells[c_idx] = IndigoQueryMolCell
                        .fromSmarts(queryCellData.toString());
                  break;
               case QuerySmile:
                  outputCells[c_idx] = IndigoQueryMolCell
                        .fromString(queryCellData.toString());
                  break;
               }
            } catch (IndigoException e) {
               LOGGER.warn("can not create result cell: " + e.getMessage());
               outputCells[c_idx] = DataType.getMissingCell();
            }
         }
         
         outputContainer.addRowToTable(new DefaultRow(inputRow.getKey(), outputCells));
         
         exec.checkCanceled();
         exec.setProgress(rowNumber / (double) inputTable.getRowCount(),
               "Adding row " + rowNumber);

         rowNumber++;
      }
      
      outputContainer.close();
      return new BufferedDataTable[] { outputContainer.getTable() };
   }
   
   
   
   protected DataTableSpec _getDataTableSpec (DataTableSpec inSpec, DataType columnType) throws InvalidSettingsException {
      DataColumnSpec[] specs = new DataColumnSpec[inSpec.getNumColumns() + 1];
      
      int col_idx;

      for (col_idx = 0; col_idx < inSpec.getNumColumns(); col_idx++)
         specs[col_idx] = inSpec.getColumnSpec(col_idx);

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
      DataTableSpec dataTableSpec = inSpecs[IndigoComponentCombinerSettings.INPUT_PORT];
      DataType result = _defineColumnType(dataTableSpec);
      if(result == null) {
         /*
          * Select list by autoconfig
          */
         ArrayList<String> excludeList = new ArrayList<String>();
         ArrayList<String> includeList = new ArrayList<String>();
         
         for (int col_idx = 0; col_idx < dataTableSpec.getNumColumns(); col_idx++) {
            DataColumnSpec columnSpec = dataTableSpec.getColumnSpec(col_idx);
            String colName = columnSpec.getName();
            
            if(_settings.columnFilter.includeColumn(columnSpec)) {
               if(result == null)
                  result = columnSpec.getType();
               
               if(result.equals(columnSpec.getType()))
                  includeList.add(colName);
               else
                  excludeList.add(colName);
                  
            } else {
               excludeList.add(colName);
            }
         }
         /*
          * Add all columns
          */
         if(includeList.isEmpty())
            result = null;
         else {
            _settings.colNames.setIncludeList(includeList);
            _settings.colNames.setExcludeList(excludeList);
            StringBuilder autoMes = new StringBuilder();
            autoMes.append("Component combiner auto configuration: include columns: ");
            for(String incName : includeList) {
               autoMes.append("'").append(incName).append("' ");
            }
            setWarningMessage(autoMes.toString());
         }
      }
      
      if(result == null)
         throw new InvalidSettingsException("selected column list can not be empty");
      
      if(!result.equals(IndigoMolCell.TYPE) && !result.equals(IndigoQueryMolCell.TYPE))
         throw new InvalidSettingsException("unsupported type: " + result.toString());
      
      if(_settings.warningMessage != null) {
         setWarningMessage(_settings.warningMessage);
      }
      return new DataTableSpec[] { null };
   }

   private DataType _defineColumnType(DataTableSpec dataTableSpec) throws InvalidSettingsException {
      DataType result = null;
      for(String colName : _settings.colNames.getIncludeList()) {
         DataType tableType = dataTableSpec.getColumnSpec(colName).getType();
         if(result != null) {
            if(!result.equals(tableType))
               throw new InvalidSettingsException("table contains two different types: '" + result.toString() + "', '" + tableType.toString());
         } else {
            result = tableType;
         }
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
      IndigoComponentCombinerSettings s = new IndigoComponentCombinerSettings();
      s.loadSettingsFrom(settings);
      
      if(s.colNames.getIncludeList().isEmpty())
         throw new InvalidSettingsException("component column list is empty");
      
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
