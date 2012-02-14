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

package com.ggasoftware.indigo.knime.scaffold;


import java.io.File;
import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.data.def.*;
import org.knime.core.node.*;

import com.ggasoftware.indigo.*;
import com.ggasoftware.indigo.knime.cell.IndigoMolCell;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolCell;
import com.ggasoftware.indigo.knime.common.IndigoNodeModel;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

public class IndigoScaffoldFinderNodeModel extends IndigoNodeModel
{
   IndigoScaffoldFinderSettings _settings = new IndigoScaffoldFinderSettings();
   private static final NodeLogger LOGGER = NodeLogger.getLogger(IndigoScaffoldFinderNodeModel.class);

   /**
    * Constructor for the node model.
    */
   protected IndigoScaffoldFinderNodeModel()
   {
      super(1, 1);
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute (final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception
   {
      DataTableSpec inputTableSpec = inData[IndigoScaffoldFinderSettings.INPUT_PORT].getDataTableSpec();
      DataColumnSpec spec = new DataColumnSpecCreator(_settings.newColName.getStringValue(), IndigoQueryMolCell.TYPE).createSpec();
      DataTableSpec outputSpec = new DataTableSpec(spec);
      
      BufferedDataContainer outputContainer = exec.createDataContainer(outputSpec);

      int colIdx = inputTableSpec.findColumnIndex(_settings.colName.getStringValue());

      if (colIdx == -1)
         throw new Exception("column not found");

      IndigoObject scaffolds = null;
      
      try
      {
         IndigoPlugin.lock();
         Indigo indigo = IndigoPlugin.getIndigo();

         IndigoObject arr = indigo.createArray();
         
         for (DataRow inputRow : inData[IndigoScaffoldFinderSettings.INPUT_PORT])
         {
            if(inputRow.getCell(colIdx).isMissing()) {
               LOGGER.warn("Molecule table contains missing cells: ignoring RowId '" + inputRow.getKey() + "'");
               continue;
            }
               
            IndigoMolCell molcell = (IndigoMolCell)inputRow.getCell(colIdx);
            IndigoObject molObj = molcell.getIndigoObject();
            String str = molObj.checkBadValence();
            if (str != null && !str.equals("")) {
               LOGGER.warn("Molecule table contains incorrect molecules: skipping row with RowId = '" + inputRow.getKey() + "': " + str);
            } else {
               arr.arrayAdd(molObj);
            }
         }
         IndigoObject extracted = null;
         
         if (_settings.tryExactMethod.getBooleanValue())
         {
            try
            {
               extracted = indigo.extractCommonScaffold(arr, "exact " + _settings.maxIterExact.getIntValue());
            }
            catch (IndigoException e)
            {
               LOGGER.warn("exact method has reached iteration limit: trying to search approximate");
            }
         }
         
         if (extracted == null)
            extracted = indigo.extractCommonScaffold(arr, "approx " + _settings.maxIterApprox.getIntValue());
         
         scaffolds = extracted.allScaffolds();
      }
      catch (IndigoException e)
      {
         LOGGER.error("internal error while launching extract scaffold: " + e.getMessage());
      }
      finally
      {
         IndigoPlugin.unlock();
      }
      if (scaffolds != null) {
         int i = 1;
         {
            for (IndigoObject scaf : scaffolds.iterateArray()) {
               DataCell[] cells = new DataCell[1];
               String molfile = scaf.molfile();
               cells[0] = IndigoQueryMolCell.fromString(molfile);
               outputContainer
                     .addRowToTable(new DefaultRow("Row" + i++, cells));
            }
         }
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

   /**
    * {@inheritDoc}
    */
   @Override
   protected DataTableSpec[] configure (final DataTableSpec[] inSpecs)
         throws InvalidSettingsException
   {
      if (_settings.newColName.getStringValue() == null || _settings.newColName.getStringValue().length() < 1)
         _settings.newColName .setStringValue("Scaffold");
      _settings.colName.setStringValue(searchIndigoColumn(inSpecs[IndigoScaffoldFinderSettings.INPUT_PORT], _settings.colName.getStringValue(), IndigoMolValue.class));
      DataColumnSpec spec = new DataColumnSpecCreator(_settings.newColName.getStringValue(), IndigoQueryMolCell.TYPE).createSpec();
      /*
       * Set loading parameters warning message
       */
      if(_settings.warningMessage != null) {
         setWarningMessage(_settings.warningMessage);
      }
      return new DataTableSpec[] { new DataTableSpec(spec) };
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
      IndigoScaffoldFinderSettings s = new IndigoScaffoldFinderSettings();
      s.loadSettingsFrom(settings);
      if (s.colName.getStringValue() == null || s.colName.getStringValue().length() < 1)
         throw new InvalidSettingsException("column name must be specified");
      if (s.newColName.getStringValue() == null || s.newColName.getStringValue().length() < 1)
         throw new InvalidSettingsException("new column name must be specified");
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
