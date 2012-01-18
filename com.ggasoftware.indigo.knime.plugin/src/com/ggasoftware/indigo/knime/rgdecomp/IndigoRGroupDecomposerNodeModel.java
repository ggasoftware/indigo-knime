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

      specs[inSpec.getNumColumns()] = new DataColumnSpecCreator(_settings.newScafColName.getStringValue(), IndigoMolCell.TYPE).createSpec();
      
      for (i = 1; i <= rsites; i++)
         specs[inSpec.getNumColumns() + i] =
            new DataColumnSpecCreator(_settings.newColPrefix.getStringValue() + i, IndigoMolCell.TYPE).createSpec();

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
      int molColIdx = inData[IndigoRGroupDecomposerSettings.MOL_PORT].getDataTableSpec().findColumnIndex(_settings.molColumn.getStringValue());
      if (molColIdx == -1)
         throw new Exception("column not found");

      int scafColIdx = inData[IndigoRGroupDecomposerSettings.SCAF_PORT].getDataTableSpec().findColumnIndex(_settings.scaffoldColumn.getStringValue());
      if (scafColIdx == -1)
         throw new Exception("scaffold column not found");
      
      IndigoObject query;
      
      {
         CloseableRowIterator it = inData[IndigoRGroupDecomposerSettings.SCAF_PORT].iterator();
         if (!it.hasNext())
            throw new Exception("no query molecule found in the data source");
         DataRow row = it.next();
         
         if(row.getCell(scafColIdx).isMissing())
            throw new Exception("no query molecule found in the data source");
         
         query = ((IndigoQueryMolValue)row.getCell(scafColIdx)).getIndigoObject();
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
         
         for (DataRow inputRow : inData[IndigoRGroupDecomposerSettings.MOL_PORT])
         {
            if(inputRow.getCell(molColIdx).isMissing()) {
               LOGGER.warn("Molecule table contains missing cells: ignoring");
               continue;
            }
            IndigoMolCell molcell = (IndigoMolCell)inputRow.getCell(molColIdx);
            arr.arrayAdd(molcell.getIndigoObject());
         }
         
         deco = indigo.decomposeMolecules(query, arr);
         rsites = deco.decomposedMoleculeScaffold().countRSites();
      }
      finally
      {
         IndigoPlugin.unlock();
      }
      
      DataTableSpec spec = calcDataTableSpec(inData[IndigoRGroupDecomposerSettings.MOL_PORT].getDataTableSpec(), rsites);
      DataTableSpec spec2 = calcDataTableSpec2();

      BufferedDataContainer outputContainer = exec.createDataContainer(spec);
      BufferedDataContainer outputContainer2 = exec.createDataContainer(spec2);

      IndigoObject deco_iter = deco.iterateDecomposedMolecules();
      
      for (DataRow inputRow : inData[IndigoRGroupDecomposerSettings.MOL_PORT])
      {
         RowKey key = inputRow.getKey();
         DataCell[] cells = new DataCell[inputRow.getNumCells() + rsites + 1];
         int i;

         for (i = 0; i < inputRow.getNumCells(); i++)
            cells[i] = inputRow.getCell(i);

         cells[inputRow.getNumCells()] = DataType.getMissingCell();
         
         for (i = 1; i <= rsites; i++)
            cells[inputRow.getNumCells() + i] =  DataType.getMissingCell();
         /*
          * Skip missing cells
          */
         if (!inputRow.getCell(molColIdx).isMissing())
            try {
               IndigoPlugin.lock();
               if (!deco_iter.hasNext()) {
                  LOGGER.error("deco iterator ended unexpectedly");
                  break;
               }

               IndigoObject deco_mol = deco_iter.next().decomposedMoleculeWithRGroups();

               for (IndigoObject rg : deco_mol.iterateRGroups()) {
                  IndigoObject frag_iter = rg.iterateRGroupFragments();
                  if (frag_iter.hasNext()) {
                     IndigoObject frag = frag_iter.next();
                     cells[inputRow.getNumCells() + rg.index()] = new IndigoMolCell(frag.clone());
                     frag.remove();
                  }
               }

               cells[inputRow.getNumCells()] = new IndigoMolCell(deco_mol.clone());
            } finally {
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
      _settings.molColumn.setStringValue(searchIndigoColumn(inSpecs[IndigoRGroupDecomposerSettings.MOL_PORT], _settings.molColumn.getStringValue(), IndigoMolValue.class));
      _settings.scaffoldColumn.setStringValue(searchIndigoColumn(inSpecs[IndigoRGroupDecomposerSettings.SCAF_PORT], _settings.scaffoldColumn.getStringValue(), IndigoQueryMolValue.class));
      return new DataTableSpec[2];
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
      IndigoRGroupDecomposerSettings s = new IndigoRGroupDecomposerSettings();
      s.loadSettingsFrom(settings);
      
      if (s.molColumn.getStringValue() == null || s.molColumn.getStringValue().length() < 1)
         throw new InvalidSettingsException("column name must be specified");
      if (s.scaffoldColumn.getStringValue() == null || s.scaffoldColumn.getStringValue().length() < 1)
         throw new InvalidSettingsException("query column name must be specified");
      if (s.newColPrefix.getStringValue() == null || s.newColPrefix.getStringValue().length() < 1)
         throw new InvalidSettingsException("prefix must be specified");
      if (s.newScafColName.getStringValue() == null || s.newScafColName.getStringValue().length() < 1)
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
