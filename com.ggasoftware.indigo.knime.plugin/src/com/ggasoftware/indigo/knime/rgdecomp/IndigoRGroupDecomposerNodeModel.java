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

package com.ggasoftware.indigo.knime.rgdecomp;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.*;

import com.ggasoftware.indigo.*;
import com.ggasoftware.indigo.knime.cell.*;
import com.ggasoftware.indigo.knime.common.IndigoNodeModel;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

public class IndigoRGroupDecomposerNodeModel extends IndigoNodeModel
{
   IndigoRGroupDecomposerSettings _settings = new IndigoRGroupDecomposerSettings();
   
   private static final NodeLogger LOGGER = NodeLogger.getLogger(IndigoRGroupDecomposerNodeModel.class);
   
   /**
    * Constructor for the node model.
    */
   protected IndigoRGroupDecomposerNodeModel()
   {
      super(2, 2);
   }

   protected DataTableSpec calcDataTableSpec (DataTableSpec inSpec, int rsites)
   {
      DataColumnSpec[] specs = new DataColumnSpec[inSpec.getNumColumns() + rsites + 1];

      int i;

      for (i = 0; i < inSpec.getNumColumns(); i++)
         specs[i] = inSpec.getColumnSpec(i);

      specs[inSpec.getNumColumns()] = new DataColumnSpecCreator(_settings.newScafColName, IndigoMolCell.TYPE).createSpec();
      
      for (i = 1; i <= rsites; i++)
         specs[inSpec.getNumColumns() + i] =
            new DataColumnSpecCreator(_settings.newColPrefix + i, IndigoMolCell.TYPE).createSpec();

      return new DataTableSpec(specs);
   }
   
   protected DataTableSpec calcDataTableSpec2 ()
   {
      return new DataTableSpec(new DataColumnSpecCreator("Scaffold", IndigoQueryMolCell.TYPE).createSpec());
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute (final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception
   {
      int colIdx = inData[0].getDataTableSpec().findColumnIndex(_settings.colName);
      if (colIdx == -1)
         throw new Exception("column not found");

      int colIdx2 = inData[1].getDataTableSpec().findColumnIndex(_settings.colName2);
      if (colIdx2 == -1)
         throw new Exception("scaffold column not found");
      
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
      
      IndigoObject deco;
      int rsites = 0;
      
      
      try
      {
         IndigoPlugin.lock();
         Indigo indigo = IndigoPlugin.getIndigo();
         IndigoObject arr = indigo.createArray();
         
         CloseableRowIterator it = inData[0].iterator();
         while (it.hasNext())
         {
            DataRow inputRow = it.next();

            IndigoMolCell molcell = (IndigoMolCell)inputRow.getCell(colIdx);
            arr.arrayAdd(molcell.getIndigoObject());
         }
         
         deco = indigo.decomposeMolecules(query, arr);
         rsites = deco.decomposedMoleculeScaffold().countRSites();
      }
      finally
      {
         IndigoPlugin.unlock();
      }
      
      DataTableSpec spec = calcDataTableSpec(inData[0].getDataTableSpec(), rsites);
      DataTableSpec spec2 = calcDataTableSpec2();

      BufferedDataContainer outputContainer = exec.createDataContainer(spec);
      BufferedDataContainer outputContainer2 = exec.createDataContainer(spec2);

      IndigoObject deco_iter = deco.iterateDecomposedMolecules();
      
      CloseableRowIterator it = inData[0].iterator();
      while (it.hasNext())
      {
         DataRow inputRow = it.next();
         RowKey key = inputRow.getKey();
         DataCell[] cells = new DataCell[inputRow.getNumCells() + rsites + 1];
         int i;

         for (i = 0; i < inputRow.getNumCells(); i++)
            cells[i] = inputRow.getCell(i);

         cells[inputRow.getNumCells()] = DataType.getMissingCell();
         
         for (i = 1; i <= rsites; i++)
            cells[inputRow.getNumCells() + i] =  DataType.getMissingCell();
         
         try
         {
            IndigoPlugin.lock();
            if (!deco_iter.hasNext())
            {
               LOGGER.error("deco iterator ended unexpectedly");
               break;
            }
            
            IndigoObject deco_mol = deco_iter.next().decomposedMoleculeWithRGroups();
            
            for (IndigoObject rg : deco_mol.iterateRGroups())
            {
               IndigoObject frag_iter = rg.iterateRGroupFragments();
               if (frag_iter.hasNext())
               {
                  IndigoObject frag = frag_iter.next();
                  cells[inputRow.getNumCells() + rg.index()] = new IndigoMolCell(frag.clone());
                  frag.remove();
               }
            }
            
            cells[inputRow.getNumCells()] = new IndigoMolCell(deco_mol.clone());
         }
         finally
         {
            IndigoPlugin.unlock();
         }
         
         outputContainer.addRowToTable(new DefaultRow(key, cells));
      }
      
      DataCell[] cells = new DataCell[1];
      String molfile = deco.decomposedMoleculeScaffold().molfile();
      cells[0] = IndigoQueryMolCell.fromString(molfile);
      outputContainer2.addRowToTable(new DefaultRow("Row1", cells));      
      
      outputContainer.close();
      outputContainer2.close();
      return new BufferedDataTable[] { outputContainer.getTable(), outputContainer2.getTable() };
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
      return new DataTableSpec[2];
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
      IndigoRGroupDecomposerSettings s = new IndigoRGroupDecomposerSettings();
      s.loadSettings(settings);
      
      if (s.colName == null || s.colName.length() < 1)
         throw new InvalidSettingsException("column name must be specified");
      if (s.colName2 == null || s.colName2.length() < 1)
         throw new InvalidSettingsException("query column name must be specified");
      if (s.newColPrefix == null || s.newColPrefix.length() < 1)
         throw new InvalidSettingsException("prefix must be specified");
      if (s.newScafColName == null || s.newScafColName.length() < 1)
    	  throw new InvalidSettingsException("scaffold column name must be specified");
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
