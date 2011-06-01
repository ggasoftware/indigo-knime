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
      DataTableSpec spec = getDataTableSpec(inData[0].getDataTableSpec());
      DataTableSpec spec2 = getDataTableSpec(inData[1].getDataTableSpec());

      BufferedDataContainer outputContainer = exec.createDataContainer(spec);

      int colIdx = spec.findColumnIndex(_settings.colName);

      if (colIdx == -1)
         throw new Exception("column not found");

      int colIdx2 = spec2.findColumnIndex(_settings.colName2);

      if (colIdx2 == -1)
         throw new Exception("query column not found");
      
      IndigoObject query;
      
      {
         CloseableRowIterator it = inData[1].iterator();
         if (!it.hasNext())
            throw new Exception("no query molecule found in the data source");
         DataRow row = it.next();
         query = ((IndigoQueryMolValue)row.getCell(colIdx2)).getIndigoObject();
         if (it.hasNext())
            LOGGER.warn("second data source contains more than one row; ignoring all others");
      }
      
      CloseableRowIterator it = inData[0].iterator();
      int rowNumber = 1;

      Indigo indigo = IndigoPlugin.getIndigo();

      while (it.hasNext())
      {
         DataRow inputRow = it.next();
         RowKey key = inputRow.getKey();
         DataCell[] cells;
         int i;
         
         if (_settings.appendColumn)
            cells = new DataCell[inputRow.getNumCells() + 2];
         else
            cells = new DataCell[inputRow.getNumCells() + 1];
         
         for (i = 0; i < inputRow.getNumCells(); i++)
            cells[i] = inputRow.getCell(i);
         
         if (inputRow.getCell(colIdx).isMissing())
         {
            cells[i] = DataType.getMissingCell();
            if (_settings.appendColumn)
               cells[i + 1] = DataType.getMissingCell();
         }
         else
         {
            IndigoObject io = ((IndigoMolCell) (inputRow.getCell(colIdx))).getIndigoObject();
   
   
            try
            {
               IndigoPlugin.lock();
               indigo.setOption("embedding-uniqueness", _settings.uniqueness.name()
                     .toLowerCase());
               IndigoObject matcher = indigo.substructureMatcher(io);
   
               cells[i++] = new IntCell(matcher.countMatches(query));
               
               if (_settings.highlight)
               {
                  IndigoObject highlighted = indigo.createMolecule();
                  IndigoObject mapping = highlighted.merge(io);
                  
                  for (IndigoObject match : matcher.iterateMatches(query))
                  {
                     for (IndigoObject qatom : query.iterateAtoms())
                     {
                        IndigoObject mapped = match.mapAtom(qatom);
                        if (mapped != null)
                           mapping.mapAtom(mapped).highlight();
                     }
                     for (IndigoObject qbond : query.iterateBonds())
                     {
                        IndigoObject mapped = match.mapBond(qbond);
                        if (mapped != null)
                           mapping.mapBond(mapped).highlight();
                     }
                  }
                  
                  if (_settings.appendColumn)
                     cells[i] = new IndigoMolCell(highlighted);
                  else
                     cells[colIdx] =  new IndigoMolCell(highlighted);
               }
            }
            finally
            {
               IndigoPlugin.unlock();
            }
         }

         outputContainer.addRowToTable(new DefaultRow(key, cells));
         exec.checkCanceled();
         exec.setProgress(rowNumber / (double) inData[0].getRowCount(),
               "Adding row " + rowNumber);

         rowNumber++;
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

   protected DataTableSpec getDataTableSpec (DataTableSpec inSpec) throws InvalidSettingsException
   {
      if (_settings.newColName == null || _settings.newColName.length() < 1)
         throw new InvalidSettingsException("New column name must be specified");
      
      DataColumnSpec[] specs;
      
      if (_settings.appendColumn)
         specs = new DataColumnSpec[inSpec.getNumColumns() + 2];
      else
         specs = new DataColumnSpec[inSpec.getNumColumns() + 1];

      int i;

      for (i = 0; i < inSpec.getNumColumns(); i++)
         specs[i] = inSpec.getColumnSpec(i);

      specs[i] = new DataColumnSpecCreator(_settings.newColName, IntCell.TYPE).createSpec();
      
      if (_settings.appendColumn)
         specs[i + 1] = new DataColumnSpecCreator(_settings.newColName2, IndigoMolCell.TYPE).createSpec();

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
