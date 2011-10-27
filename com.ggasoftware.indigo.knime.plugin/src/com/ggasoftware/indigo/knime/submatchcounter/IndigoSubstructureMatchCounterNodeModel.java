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
import org.knime.core.data.container.*;
import org.knime.core.data.def.*;

import com.ggasoftware.indigo.*;
import com.ggasoftware.indigo.knime.cell.IndigoMolCell;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolValue;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

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
      final BufferedDataTable targetsTable = inData[0];
      final BufferedDataTable queriesTable = inData[1];
      
      final DataTableSpec targetItemsSpec = targetsTable.getDataTableSpec();
      final DataTableSpec outSpec = 
         getOutDataTableSpec(targetsTable.getDataTableSpec(), 
               queriesTable.getRowCount());

      BufferedDataContainer outputContainer = exec.createDataContainer(outSpec);

      int targetColIdx = targetItemsSpec.findColumnIndex(_settings.colName);
      if (targetColIdx == -1)
         throw new InvalidSettingsException("column '" + _settings.colName + "' is not found in the first port");

      IndigoObject[] queries = loadQueries(queriesTable);
      
      CloseableRowIterator it = targetsTable.iterator();
      int rowNumber = 1;

      Indigo indigo = IndigoPlugin.getIndigo();

      while (it.hasNext())
      {
         DataRow inputRow = it.next();
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
      int queryColIdx = queryItemsSpec.findColumnIndex(_settings.colName2);
      if (queryColIdx == -1)
         throw new InvalidSettingsException("query column '" + _settings.colName2 + "' not found");
      
      IndigoObject[] queries = new IndigoObject[queriesTableData.getRowCount()];
      if (queries.length == 0)
         LOGGER.warn("There are no query molecules in the table");

      int index = 0;
      boolean warningPrinted = false;
      RowIterator it = queriesTableData.iterator();
      while (it.hasNext())
      {
         DataRow row = it.next();
         queries[index] = getIndigoQueryMoleculeOrNull(row.getCell(queryColIdx));
         if (queries[index] == null && !warningPrinted) {
            LOGGER.warn("query table contains missing cells");
            warningPrinted = true;
         }
         index++;
      }
      return queries;
   }
   
   private IndigoObject getIndigoQueryMoleculeOrNull(DataCell cell)
   {
      if (cell.isMissing())
         return null;
      return ((IndigoQueryMolValue)cell).getIndigoObject();
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
         if (_settings.appendColumn) {
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
         
         IndigoObject target = ((IndigoMolCell)targetCell).getIndigoObject();
         
         IndigoObject highlighted = null, mapping = null;
         if (_settings.highlight) {
            highlighted = indigo.createMolecule();
            mapping = highlighted.merge(target);
         }
         
         indigo.setOption("embedding-uniqueness", _settings.uniqueness
               .name().toLowerCase());
         IndigoObject matcher = indigo.substructureMatcher(target);
         
         for (IndigoObject q : queries) {
            if (q == null) {
               cells[i++] = DataType.getMissingCell();
               continue;
            }
            cells[i++] = new IntCell(matcher.countMatches(q));

            if (_settings.highlight) {
               for (IndigoObject match : matcher.iterateMatches(q)) {
                  for (IndigoObject qatom : q.iterateAtoms()) {
                     IndigoObject mapped = match.mapAtom(qatom);
                     if (mapped != null)
                        mapping.mapAtom(mapped).highlight();
                  }
                  for (IndigoObject qbond : q.iterateBonds()) {
                     IndigoObject mapped = match.mapBond(qbond);
                     if (mapped != null)
                        mapping.mapBond(mapped).highlight();
                  }
               }
            }
         }

         if (_settings.highlight) {
            if (_settings.appendColumn)
               cells[i] = new IndigoMolCell(highlighted);
            else
               cells[colIdx] = new IndigoMolCell(highlighted);
         }
      } finally {
         IndigoPlugin.unlock();
      }
      return cells;
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
      if (_settings.newColName == null || _settings.newColName.length() < 1)
         throw new InvalidSettingsException("New column name must be specified");
      
      DataColumnSpec[] specs;

      int additionalRows = queryRowsCount;
      if (_settings.appendColumn)
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
         specs[i] = new DataColumnSpecCreator(_settings.newColName + suffix, IntCell.TYPE).createSpec();
         i++;
      }

      // Add molecule column that highlighted by each query   
      if (_settings.appendColumn) {
         specs[i] = new DataColumnSpecCreator(_settings.newColName2, IndigoMolCell.TYPE).createSpec();
         i++;
      }
      
      return new DataTableSpec(specs);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected DataTableSpec[] configure (final DataTableSpec[] inSpecs)
         throws InvalidSettingsException
   {
      // Return null table because table specification depends on the 
      // number of the rows in the query table
      return new DataTableSpec[1];
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
      if (s.newColName == null || s.newColName.length() < 1)
         throw new InvalidSettingsException("new counter column name must be specified");
      if (s.appendColumn)
         if (s.newColName2 == null || s.newColName2.length() < 1)
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
