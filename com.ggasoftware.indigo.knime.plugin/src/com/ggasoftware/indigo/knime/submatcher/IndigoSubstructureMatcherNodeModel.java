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

package com.ggasoftware.indigo.knime.submatcher;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.IntCell;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.*;

import com.ggasoftware.indigo.*;
import com.ggasoftware.indigo.knime.cell.IndigoMolCell;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolValue;
import com.ggasoftware.indigo.knime.common.IndigoNodeModel;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;
import com.ggasoftware.indigo.knime.submatcher.IndigoSubstructureMatcherSettings.Mode;

public class IndigoSubstructureMatcherNodeModel extends IndigoNodeModel
{
   IndigoSubstructureMatcherSettings _settings = new IndigoSubstructureMatcherSettings();

   private static final NodeLogger LOGGER = NodeLogger.getLogger(IndigoSubstructureMatcherNodeModel.class);
   
   /**
    * Constructor for the node model.
    */
   protected IndigoSubstructureMatcherNodeModel()
   {
      super(2, 2);
   }
   
   protected DataTableSpec getDataTableSpec (DataTableSpec inputTableSpec) throws InvalidSettingsException
   {
      if (_settings.appendColumn)
         if (_settings.newColName == null || _settings.newColName.length() < 1)
            throw new InvalidSettingsException("New column name must be specified");
      
      DataColumnSpec[] specs;
      
      int columnsCount = inputTableSpec.getNumColumns();
      if (_settings.appendColumn)
         columnsCount++;
      if (_settings.appendQueryKeyColumn)
         columnsCount++;
      if (_settings.appendQueryMatchCountKeyColumn)
         columnsCount++;
      
      specs = new DataColumnSpec[columnsCount];

      int i;
      
      for (i = 0; i < inputTableSpec.getNumColumns(); i++)
         specs[i] = inputTableSpec.getColumnSpec(i);
      
      if (_settings.appendColumn)
         specs[i++] = new DataColumnSpecCreator(_settings.newColName, IndigoMolCell.TYPE).createSpec();
      if (_settings.appendQueryKeyColumn)
         specs[i++] = new DataColumnSpecCreator(_settings.queryKeyColumn, StringCell.TYPE).createSpec();
      if (_settings.appendQueryMatchCountKeyColumn)
         specs[i++] = new DataColumnSpecCreator(_settings.queryMatchCountKeyColumn, IntCell.TYPE).createSpec();
      
      return new DataTableSpec(specs);
   }
   
   private QueryWithData[] loadQueries(BufferedDataTable queriesTableData)
         throws InvalidSettingsException
   {
      DataTableSpec queryItemsSpec = queriesTableData.getDataTableSpec();
      int queryColIdx = queryItemsSpec.findColumnIndex(_settings.colName2);
      if (queryColIdx == -1)
         throw new InvalidSettingsException("query column '"
               + _settings.colName2 + "' not found");

      QueryWithData[] queries = new QueryWithData[queriesTableData.getRowCount()];
      if (queries.length == 0)
         LOGGER.warn("There are no query molecules in the table");

      int index = 0;
      boolean warningPrinted = false;
      RowIterator it = queriesTableData.iterator();
      while (it.hasNext()) {
         DataRow row = it.next();
         queries[index] = new QueryWithData();
         queries[index].query = getIndigoQueryMoleculeOrNull(row.getCell(queryColIdx));
         queries[index].rowKey = row.getKey().toString();
         if (queries[index].query == null && !warningPrinted) {
            LOGGER.warn("query table contains missing cells");
            warningPrinted = true;
         }
         index++;
      }
      return queries;
   }

   private IndigoObject getIndigoQueryMoleculeOrNull(DataCell cell) {
      if (cell.isMissing())
         return null;
      return ((IndigoQueryMolValue) cell).getIndigoObject();
   }
   
   class AlignTargetQueryData
   {
      boolean first = true;
      int natoms_align = 0;
      int[] atoms = null;
      float[] xyz = null;
      
      public void align (IndigoObject target, IndigoObject query, IndigoObject match)
      {
         int i = 0;
         
         if (!target.hasCoord())
            target.layout();

         if (first)
         {
            for (IndigoObject atom : query.iterateAtoms())
            {
               IndigoObject mapped = match.mapAtom(atom);
               if (mapped != null && (mapped.isPseudoatom() || mapped.atomicNumber() != 1))
                  natoms_align++;
            }
            if (natoms_align > 1)
            {
               atoms = new int[natoms_align];
               xyz = new float[natoms_align * 3];
               
               for (IndigoObject atom : query.iterateAtoms())
               {
                  IndigoObject mapped = match.mapAtom(atom);
                  
                  IndigoObject atomForAlign;
                  if (_settings.alignByQuery)
                     atomForAlign = atom;
                  else 
                     atomForAlign = mapped;
                     
                  if (mapped != null && (mapped.isPseudoatom() || mapped.atomicNumber() != 1))
                  {
                     atoms[i] = mapped.index();
                     System.arraycopy(atomForAlign.xyz(), 0, xyz, i++ * 3, 3);
                  }
               }
               if (_settings.alignByQuery)
                  target.alignAtoms(atoms, xyz);
            }
            first = false;
         }
         else
         {
            if (atoms != null)
            {
               for (IndigoObject atom : query.iterateAtoms())
               {
                  IndigoObject mapped = match.mapAtom(atom);
                  if (mapped != null && (mapped.isPseudoatom() || mapped.atomicNumber() != 1))
                     atoms[i++] = mapped.index();
               }

               target.alignAtoms(atoms, xyz);
            }
         }
      }
   }
   
   class QueryWithData
   {
      IndigoObject query;
      String rowKey;
      AlignTargetQueryData alignData = new AlignTargetQueryData(); 
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute (final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception
   {
      DataTableSpec inputTableSpec = inData[0].getDataTableSpec();
      
      BufferedDataContainer validOutputContainer = exec
            .createDataContainer(getDataTableSpec(inputTableSpec));
      BufferedDataContainer invalidOutputContainer = exec
            .createDataContainer(inputTableSpec);

      int colIdx = inputTableSpec.findColumnIndex(_settings.colName);

      if (colIdx == -1)
         throw new Exception("column not found");

      QueryWithData[] queries = loadQueries(inData[1]);
      
      int rowNumber = 1;

      for (DataRow inputRow : inData[0]) {
         IndigoObject target = ((IndigoMolCell) (inputRow.getCell(colIdx))).getIndigoObject();

         int matchCount = 0;
         StringBuilder queriesRowKey = new StringBuilder();
         try {
            IndigoPlugin.lock();
            target = target.clone();
            
            int totalQueries = queries.length;
            for (QueryWithData query : queries) {
               if (query.query == null) {
                  totalQueries--;
                  continue;
               }
               if (matchTarget(query.query, query.alignData, target)) {
                  matchCount++;
                  if (queriesRowKey.length() > 0)
                     queriesRowKey.append(", ");
                  queriesRowKey.append(query.rowKey);
               }
            }
         } finally {
            IndigoPlugin.unlock();
         }

         boolean hasMatch = true;
         // Check matchCount
         if (_settings.matchAnyAtLeastSelected)
            hasMatch = (matchCount >= _settings.matchAnyAtLeast);
         if (_settings.matchAllSelected)
            hasMatch = (matchCount == queries.length);
         
         if (hasMatch) {
            int columnsCount = inputRow.getNumCells();
            if (_settings.appendColumn)
               columnsCount++;
            if (_settings.appendQueryKeyColumn)
               columnsCount++;
            if (_settings.appendQueryMatchCountKeyColumn)
               columnsCount++;
            
            DataCell[] cells = new DataCell[columnsCount];
            int i;

            for (i = 0; i < inputRow.getNumCells(); i++) {
               if (!_settings.appendColumn && i == colIdx)
                  cells[i] = new IndigoMolCell(target);
               else
                  cells[i] = inputRow.getCell(i);
            }
            if (_settings.appendColumn)
               cells[i++] = new IndigoMolCell(target);
            if (_settings.appendQueryKeyColumn)
               cells[i++] = new StringCell(queriesRowKey.toString());
            if (_settings.appendQueryMatchCountKeyColumn)
               cells[i++] = new IntCell(matchCount);
            
            validOutputContainer.addRowToTable(new DefaultRow(inputRow
                  .getKey(), cells));
         } else
            invalidOutputContainer.addRowToTable(inputRow);
         exec.checkCanceled();
         exec.setProgress(rowNumber / (double) inData[0].getRowCount(),
               "Processing row " + rowNumber);
         rowNumber++;
      }

      validOutputContainer.close();
      invalidOutputContainer.close();
      return new BufferedDataTable[] { validOutputContainer.getTable(),
            invalidOutputContainer.getTable() };
   }

   private boolean matchTarget(IndigoObject query,
         AlignTargetQueryData alignData, IndigoObject target)
   {
      Indigo indigo = IndigoPlugin.getIndigo();
      
      String mode = "";
      
      IndigoObject match = null;
      
      if (!_settings.exact || target.countHeavyAtoms() <= query.countAtoms())
      {
         if (_settings.mode == Mode.Resonance)
            mode = "RES";
         else if (_settings.mode == Mode.Tautomer)
         {
            mode = "TAU R* R-C";
 
            indigo.clearTautomerRules();
            indigo.setTautomerRule(1, "N,O,P,S,As,Se,Sb,Te", "N,O,P,S,As,Se,Sb,Te");
            indigo.setTautomerRule(2, "0C", "N,O,P,S");
            indigo.setTautomerRule(3, "1C", "N,O");
         }
      
         match = indigo.substructureMatcher(target, mode).match(query);
      }
      
      if (match != null && _settings.exact)
      {
         // test that the target does not have unmapped heavy atoms
         int nmapped_heavy = 0;
         
         for (IndigoObject atom : query.iterateAtoms())
         {
            IndigoObject mapped = match.mapAtom(atom);
            if (mapped != null)
               if (mapped.isRSite() || mapped.isPseudoatom() || mapped.atomicNumber() > 1)
                  nmapped_heavy++;
         }
         
         if (nmapped_heavy < target.countHeavyAtoms())
            match = null;
      }
      
      if (match != null)
      {
         if (_settings.highlight)
         {
            for (IndigoObject atom : query.iterateAtoms())
            {
               IndigoObject mapped = match.mapAtom(atom);
               if (mapped != null)
                  mapped.highlight();
            }
            for (IndigoObject bond : query.iterateBonds())
            {
               IndigoObject mapped = match.mapBond(bond);
               if (mapped != null)
                  mapped.highlight();
            }
         }
         
         if (_settings.align)
            alignData.align(target, query, match);
      }
      return (match != null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void reset ()
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected DataTableSpec[] configure (final DataTableSpec[] inSpecs)
         throws InvalidSettingsException
   {
      _settings.colName = searchIndigoColumn(inSpecs[0], _settings.colName, IndigoMolValue.class);
      _settings.colName2 = searchIndigoColumn(inSpecs[1], _settings.colName2, IndigoQueryMolValue.class);
      return new DataTableSpec[] { getDataTableSpec(inSpecs[0]), inSpecs[0] };
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
      IndigoSubstructureMatcherSettings s = new IndigoSubstructureMatcherSettings();
      s.loadSettings(settings);

      if (s.colName == null || s.colName.length() < 1)
         throw new InvalidSettingsException("column name must be specified");
      if (s.colName2 == null || s.colName2.length() < 1)
         throw new InvalidSettingsException("query column name must be specified");
      if (s.appendColumn && (s.newColName == null || s.newColName.length() < 1))
         throw new InvalidSettingsException("new column name must be specified");
      if (s.appendQueryKeyColumn && (s.queryKeyColumn == null || s.queryKeyColumn.length() < 1))
         throw new InvalidSettingsException("query key column name must be specified");
      if (s.appendQueryMatchCountKeyColumn && (s.queryMatchCountKeyColumn == null || s.queryMatchCountKeyColumn.length() < 1))
         throw new InvalidSettingsException("query match count column name must be specified");
      if (!s.matchAllSelected && !s.matchAnyAtLeastSelected)
         throw new InvalidSettingsException("At least one match option should be selected: match any or match all");
      if (s.appendColumn && !s.highlight && !s.align)
         throw new InvalidSettingsException("without highlighting or alignment, appending new column makes no sense");
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
