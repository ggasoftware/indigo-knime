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
      BufferedDataTable molDataTable = inData[IndigoRGroupDecomposerSettings.MOL_PORT];
      BufferedDataTable scafDataTable = inData[IndigoRGroupDecomposerSettings.SCAF_PORT];
      
      int molColIdx = molDataTable.getDataTableSpec().findColumnIndex(_settings.molColumn.getStringValue());
      if (molColIdx == -1)
         throw new Exception("column not found");

      int scafColIdx = scafDataTable.getDataTableSpec().findColumnIndex(_settings.scaffoldColumn.getStringValue());
      if (scafColIdx == -1)
         throw new Exception("scaffold column not found");
      
      
      IndigoObject query = null;
      
      {
         CloseableRowIterator it = scafDataTable.iterator();
         if (!it.hasNext())
            appendWarningMessage("no query molecule found in the data source");
         DataRow row = it.next();
         
         if(row.getCell(scafColIdx).isMissing())
            appendWarningMessage("no query molecule found in the data source");
         try {
            query = ((IndigoQueryMolValue)row.getCell(scafColIdx)).getIndigoObject();
         } catch (IndigoException e) {
            appendWarningMessage("can not load query molecule: " + e.getMessage());
         }
         if (it.hasNext())
            LOGGER.warn("second data source contains more than one row; ignoring all others");
      }
      
      IndigoObject deco = null;
      int rsites = 0;
      
      DataTableSpec scafSpec = calcDataTableSpec2();
      BufferedDataContainer scafOutputContainer = exec.createDataContainer(scafSpec);
      
      BufferedDataContainer rgOutputContainer = null;
      
      if (query != null) {
         try {
            IndigoPlugin.lock();
            Indigo indigo = IndigoPlugin.getIndigo();
            IndigoObject arr = indigo.createArray();

            for (DataRow inputRow : molDataTable) {
               if (inputRow.getCell(molColIdx).isMissing()) {
                  LOGGER.warn("Molecule table contains missing cells: ignoring");
                  continue;
               }
               try {
                  IndigoMolCell molcell = (IndigoMolCell) inputRow.getCell(molColIdx);
                  arr.arrayAdd(molcell.getIndigoObject());
               } catch (IndigoException e) {
                  appendWarningMessage("can not load target molecule RowId = '" + inputRow.getKey() + "': " + e.getMessage());
               }
            }
            
            if(arr.count() == 0) {
               appendWarningMessage("no target molecules were loaded");
            } else {
               deco = indigo.decomposeMolecules(query, arr);
               rsites = deco.decomposedMoleculeScaffold().countRSites();
            }
         } catch (IndigoException e) {
            appendWarningMessage("Error while decompose molecules: " + e.getMessage());
            deco = null;
         } finally {
            IndigoPlugin.unlock();
         }
         
         DataTableSpec rgSpec = calcDataTableSpec(molDataTable.getDataTableSpec(), rsites);
         rgOutputContainer = exec.createDataContainer(rgSpec);
         
         if (deco != null) {

            IndigoObject deco_iter = deco.iterateDecomposedMolecules();

            for (DataRow inputRow : molDataTable) {
               RowKey key = inputRow.getKey();
               DataCell[] cells = new DataCell[inputRow.getNumCells() + rsites + 1];
               int i;

               for (i = 0; i < inputRow.getNumCells(); i++)
                  cells[i] = inputRow.getCell(i);

               cells[inputRow.getNumCells()] = DataType.getMissingCell();

               for (i = 1; i <= rsites; i++)
                  cells[inputRow.getNumCells() + i] = DataType.getMissingCell();
               /*
                * Skip missing and invalid cells
                */
               if (inputRow.getCell(molColIdx).isMissing()) {
                  continue;
               }
               try {
                  IndigoMolCell molcell = (IndigoMolCell) inputRow.getCell(molColIdx);
                  molcell.getIndigoObject();
               } catch (IndigoException e) {
                  continue;
               }
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
                        cells[inputRow.getNumCells() + rg.index()] = new IndigoMolCell(frag);
                        // cells[inputRow.getNumCells() + rg.index()] = new
                        // IndigoMolCell(frag.clone());
                        frag.remove();
                     }
                  }
                  cells[inputRow.getNumCells()] = new IndigoMolCell(deco_mol);
                  // cells[inputRow.getNumCells()] = new
                  // IndigoMolCell(deco_mol.clone());
               } catch (IndigoException e) {
                  appendWarningMessage("error while adding decomposition for RowId = '" + inputRow.getKey() + "': " + e.getMessage());
               } finally {
                  IndigoPlugin.unlock();
               }
               rgOutputContainer.addRowToTable(new DefaultRow(key, cells));
            }
         }
         if(rgOutputContainer == null) {
            rgOutputContainer = exec.createDataContainer(calcDataTableSpec(molDataTable.getDataTableSpec(), 0));
         }
         
         if(deco != null) {
            DataCell[] cells = new DataCell[1];
            String molfile = deco.decomposedMoleculeScaffold().molfile();
            cells[0] = IndigoQueryMolCell.fromString(molfile);
            scafOutputContainer.addRowToTable(new DefaultRow("Row1", cells));
         }

      }
      
      handleWarningMessages();
      rgOutputContainer.close();
      scafOutputContainer.close();
      return new BufferedDataTable[] { rgOutputContainer.getTable(), scafOutputContainer.getTable() };
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
      /*
       * Set loading parameters warning message
       */
      if(_settings.warningMessage != null) {
         setWarningMessage(_settings.warningMessage);
      }
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
