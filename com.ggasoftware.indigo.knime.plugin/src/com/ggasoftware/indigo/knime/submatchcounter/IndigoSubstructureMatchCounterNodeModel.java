/****************************************************************************
 * Copyright (C) 2011 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 3 as published by the Free Software
 * Foundation.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 ***************************************************************************/

package com.ggasoftware.indigo.knime.submatchcounter;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.data.def.*;

import com.ggasoftware.indigo.*;
import com.ggasoftware.indigo.knime.IndigoNodeSettings;
import com.ggasoftware.indigo.knime.IndigoNodeSettings.STRUCTURE_TYPE;
import com.ggasoftware.indigo.knime.cell.IndigoDataCell;
import com.ggasoftware.indigo.knime.cell.IndigoDataValue;
import com.ggasoftware.indigo.knime.cell.IndigoMolCell;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryReactionValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionCell;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;
import com.ggasoftware.indigo.knime.common.IndigoNodeModel;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;
import com.ggasoftware.indigo.knime.submatchcounter.IndigoSubstructureMatchCounterSettings.Uniqueness;

public class IndigoSubstructureMatchCounterNodeModel extends IndigoNodeModel
{
   
   public static final int TARGET_PORT = 0;
   public static final int QUERY_PORT = 1;
   
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
      final BufferedDataTable targetsTable = inData[TARGET_PORT];
      final BufferedDataTable queriesTable = inData[QUERY_PORT];
      
      final DataTableSpec targetItemsSpec = targetsTable.getDataTableSpec();
      final DataTableSpec queryItemsSpec = queriesTable.getDataTableSpec();
      
      _defineStructureType(targetItemsSpec, queryItemsSpec);
      
      final DataTableSpec outSpec = getOutDataTableSpec(targetsTable.getDataTableSpec(), 
               queriesTable.getRowCount());

      BufferedDataContainer outputContainer = exec.createDataContainer(outSpec);
      
      int targetColIdx = _settings.getTargetColumnIdx(targetItemsSpec);

      IndigoObject[] queries = loadQueries(queriesTable);
      
      int rowNumber = 1;

      Indigo indigo = IndigoPlugin.getIndigo();

      for (DataRow inputRow : targetsTable) {
         RowKey key = inputRow.getKey();

         DataCell[] cells = 
            getResultRow(outSpec, targetColIdx, queries, indigo, inputRow);

         outputContainer.addRowToTable(new DefaultRow(key, cells));
         exec.checkCanceled();
         exec.setProgress(rowNumber / (double) targetsTable.getRowCount(),
               "Adding row " + rowNumber);

         rowNumber++;
      }

      outputContainer.close();
      return new BufferedDataTable[] { outputContainer.getTable() };
   }

   private IndigoObject[] loadQueries(BufferedDataTable queriesTableData)
         throws InvalidSettingsException {
      DataTableSpec queryItemsSpec = queriesTableData.getDataTableSpec();
      
      int queryColIdx = _settings.getQueryColumnIdx(queryItemsSpec);
      
      IndigoObject[] queries = new IndigoObject[queriesTableData.getRowCount()];
      
      if (queries.length == 0)
         LOGGER.warn("There are no query molecules in the table");

      int index = 0;
      boolean warningPrinted = false;
      for (DataRow row : queriesTableData) {
         queries[index] = getIndigoQueryStructureOrNull(row.getCell(queryColIdx));
         if (queries[index] == null && !warningPrinted) {
            LOGGER.warn("query table contains missing cells");
            warningPrinted = true;
         }
         index++;
      }
      return queries;
   }
   
   private IndigoObject getIndigoQueryStructureOrNull(DataCell cell)
   {
      if (cell.isMissing())
         return null;
      return ((IndigoDataValue)cell).getIndigoObject();
   }
   
   private DataCell[] getResultRow(DataTableSpec outSpec, int colIdx,
      IndigoObject[] queries, Indigo indigo, DataRow inputRow)
   {
      DataCell[] cells;
      int i;
      
      cells = new DataCell[outSpec.getNumColumns()];
      
      for (i = 0; i < inputRow.getNumCells(); i++)
         cells[i] = inputRow.getCell(i);
      
      DataCell targetCell = inputRow.getCell(colIdx);
      if (targetCell.isMissing()) {
         // Mark all columns as missing
         if (_settings.appendColumn.getBooleanValue()) {
            cells[i] = DataType.getMissingCell();
            i++;
         }
         
         for (int j = 0; j < queries.length; j++) {
            cells[i] = DataType.getMissingCell();
            i++;
         }
         return cells;
      }
      
      try {
         IndigoPlugin.lock();
         
         IndigoObject target = ((IndigoDataCell)targetCell).getIndigoObject();
         
         IndigoObject highlighted = target.clone();
         
         indigo.setOption("embedding-uniqueness", Uniqueness.values()[_settings.uniqueness
               .getIntValue()].name().toLowerCase());
         
         IndigoObject matcher = indigo.substructureMatcher(highlighted);
         
         for (IndigoObject q : queries) {
            if (q == null) {
               cells[i++] = DataType.getMissingCell();
               continue;
            }
            cells[i++] = new IntCell(matcher.countMatches(q));

            if (_settings.highlight.getBooleanValue()) {
               /*
                * Not working for reactions at the moment
                */
               for (IndigoObject match : matcher.iterateMatches(q)) {
                  _highlightStructures(match, q);
               }
            }
         }

         if (_settings.highlight.getBooleanValue()) {
            if (_settings.appendColumn.getBooleanValue())
               cells[i] = _createNewDataCell(highlighted);
            else
               cells[colIdx] = _createNewDataCell(highlighted);
         }
      } finally {
         IndigoPlugin.unlock();
      }
      return cells;
   }

   private void _highlightStructures(IndigoObject match, IndigoObject query) {
      switch (_settings.structureType) {
      case Molecule:
         _highlightMolecule(match, query);
         break;
      case Reaction:
         for(IndigoObject mol : query.iterateMolecules()) 
            _highlightMolecule(match, mol);
         break;
      case Unknown:
         throw new RuntimeException("Structure type is not defined");
      }
   }
   
   private void _highlightMolecule(IndigoObject match, IndigoObject query) {
      for (IndigoObject atom : query.iterateAtoms()) {
         IndigoObject mapped = match.mapAtom(atom);
         if (mapped != null)
            mapped.highlight();
      }
      for (IndigoObject bond : query.iterateBonds()) {
         IndigoObject mapped = match.mapBond(bond);
         if (mapped != null)
            mapped.highlight();
      }
   }

   private DataCell _createNewDataCell(IndigoObject target) {
      DataCell result = null;
      switch (_settings.structureType) {
      case Molecule:
         result = new IndigoMolCell(target);
         break;
      case Reaction:
         result = new IndigoReactionCell(target);
         break;
      case Unknown:
         throw new RuntimeException("Structure type is not defined");
      }
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void reset ()
   {
   }

   protected DataTableSpec getOutDataTableSpec (DataTableSpec inSpec, int queryRowsCount) throws InvalidSettingsException
   {
      if (_settings.newColName.getStringValue() == null || _settings.newColName.getStringValue().length() < 1)
         throw new InvalidSettingsException("New column name must be specified");
      
      DataColumnSpec[] specs;

      int additionalRows = queryRowsCount;
      if (_settings.appendColumn.getBooleanValue())
         additionalRows += 1;
      
      specs = new DataColumnSpec[inSpec.getNumColumns() + additionalRows];

      int i;

      for (i = 0; i < inSpec.getNumColumns(); i++)
         specs[i] = inSpec.getColumnSpec(i);

      for (int j = 0; j < queryRowsCount; j++)
      {
         String suffix = "";
         if (queryRowsCount > 1)
            suffix = String.format(" %d", j + 1);
         specs[i] = new DataColumnSpecCreator(_settings.newColName.getStringValue() + suffix, IntCell.TYPE).createSpec();
         i++;
      }

      // Add molecule column that highlighted by each query   
      if (_settings.appendColumn.getBooleanValue()) {
         specs[i] = _getNewColumnSpec(_settings.appendColumnName.getStringValue());
         i++;
      }
      
      return new DataTableSpec(specs);
   }
   
   private DataColumnSpec _getNewColumnSpec(String colName) throws InvalidSettingsException {
      DataColumnSpec result = null;
      switch(_settings.structureType){
         case Molecule:
            result = new DataColumnSpecCreator(colName, IndigoMolCell.TYPE).createSpec();
            break;
         case Reaction:
            result = new DataColumnSpecCreator(colName, IndigoReactionCell.TYPE).createSpec();
            break;
         case Unknown:
            throw new InvalidSettingsException("Structure type is not defined");
      }
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected DataTableSpec[] configure (final DataTableSpec[] inSpecs)
         throws InvalidSettingsException
   {
      searchMixedIndigoColumn(inSpecs[TARGET_PORT], _settings.targetColName, IndigoMolValue.class, IndigoReactionValue.class);
      searchMixedIndigoColumn(inSpecs[QUERY_PORT], _settings.queryColName, IndigoQueryMolValue.class, IndigoQueryReactionValue.class);
      
      STRUCTURE_TYPE stype = _defineStructureType(inSpecs[TARGET_PORT], inSpecs[QUERY_PORT]);
      if(stype.equals(STRUCTURE_TYPE.Unknown)) 
         throw new InvalidSettingsException("can not define structure type: reaction or molecule columns");
      /*
       * Set loading parameters warning message
       */
      if(_settings.warningMessage != null) {
         setWarningMessage(_settings.warningMessage);
      }
      // Return null table because table specification depends on the 
      // number of the rows in the query table
      return new DataTableSpec[] {null};
   }
   
   private STRUCTURE_TYPE _defineStructureType(DataTableSpec tSpec, DataTableSpec qSpec) {
      STRUCTURE_TYPE stype = IndigoNodeSettings.getStructureType(tSpec, qSpec,
            _settings.targetColName.getColumnName(), _settings.queryColName.getColumnName());
      _settings.structureType = stype;
      return stype;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveSettingsTo (final NodeSettingsWO settings)
   {
      _settings.saveSettingsTo(settings);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadValidatedSettingsFrom (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      _settings.loadSettingsFrom(settings);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void validateSettings (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      IndigoSubstructureMatchCounterSettings s = new IndigoSubstructureMatchCounterSettings();
      s.loadSettingsFrom(settings);
      if (s.targetColName.getStringValue() == null || s.targetColName.getStringValue().length() < 1)
         throw new InvalidSettingsException("column name must be specified");
      if (s.queryColName.getStringValue() == null || s.queryColName.getStringValue().length() < 1)
         throw new InvalidSettingsException("query column name must be specified");
      if (s.newColName.getStringValue() == null || s.newColName.getStringValue().length() < 1)
         throw new InvalidSettingsException("new counter column name must be specified");
      if (s.appendColumn.getBooleanValue())
         if (s.appendColumnName.getStringValue() == null || s.appendColumnName.getStringValue().length() < 1)
            throw new InvalidSettingsException("new highlighted column name must be specified");
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
