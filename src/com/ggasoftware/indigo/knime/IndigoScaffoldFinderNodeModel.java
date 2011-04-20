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

package com.ggasoftware.indigo.knime;


import java.io.File;
import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.data.container.*;
import org.knime.core.data.def.*;
import org.knime.core.node.*;

import com.ggasoftware.indigo.*;

public class IndigoScaffoldFinderNodeModel extends NodeModel
{
   IndigoScaffoldFinderSettings _settings = new IndigoScaffoldFinderSettings();

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
      DataTableSpec inputTableSpec = inData[0].getDataTableSpec();
      DataColumnSpec spec = new DataColumnSpecCreator(_settings.newColName, IndigoQueryMolCell.TYPE).createSpec();
      DataTableSpec outputSpec = new DataTableSpec(spec);
      
      BufferedDataContainer outputContainer = exec.createDataContainer(outputSpec);

      int colIdx = inputTableSpec.findColumnIndex(_settings.colName);

      if (colIdx == -1)
         throw new Exception("column not found");

      IndigoObject scaffolds;
      
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
         IndigoObject extracted = null;
         
         if (_settings.tryExactMethod)
         {
            try
            {
               extracted = indigo.extractCommonScaffold(arr, "exact " + _settings.maxIterExact);
            }
            catch (IndigoException e)
            {
            }
         }
         
         if (extracted == null)
            extracted = indigo.extractCommonScaffold(arr, "approx " + _settings.maxIterApprox);
         
         scaffolds = extracted.allScaffolds();
      }
      finally
      {
         IndigoPlugin.unlock();
      }
      
      int i = 1;
      {
         for (IndigoObject scaf : scaffolds.iterateArray())
         {
            DataCell[] cells = new DataCell[1];
            String molfile = scaf.molfile();
            cells[0] = new IndigoQueryMolCell(molfile, false);
            outputContainer.addRowToTable(new DefaultRow("Row" + i++, cells));
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
      if (_settings.newColName == null || _settings.newColName.length() < 1)
         throw new InvalidSettingsException("New column name must be specified");
      DataColumnSpec spec = new DataColumnSpecCreator(_settings.newColName, IndigoQueryMolCell.TYPE).createSpec();
      return new DataTableSpec[] { new DataTableSpec(spec) };
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
      IndigoScaffoldFinderSettings s = new IndigoScaffoldFinderSettings();
      s.loadSettings(settings);
      if (s.colName == null || s.colName.length() < 1)
         throw new InvalidSettingsException("column name must be specified");
      if (s.newColName == null || s.newColName.length() < 1)
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
