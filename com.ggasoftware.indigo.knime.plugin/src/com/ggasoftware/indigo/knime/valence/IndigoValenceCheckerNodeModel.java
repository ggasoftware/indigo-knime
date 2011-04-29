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

package com.ggasoftware.indigo.knime.valence;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.data.container.*;
import org.knime.core.data.def.*;
import org.knime.core.node.*;

import com.ggasoftware.indigo.knime.cell.IndigoMolCell;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

public class IndigoValenceCheckerNodeModel extends NodeModel
{
   private final IndigoValenceCheckerSettings _settings = new IndigoValenceCheckerSettings();

   /**
    * Constructor for the node model.
    */
   protected IndigoValenceCheckerNodeModel()
   {
      super(1, 2);
   }

   protected DataTableSpec[] getDataTableSpecs (DataTableSpec inputTableSpec)
         throws InvalidSettingsException
   {
      int colIdx = inputTableSpec.findColumnIndex(_settings.colName);

      if (colIdx == -1)
         throw new InvalidSettingsException("column not found");

      DataColumnSpec invalidOutputColumnSpec = new DataColumnSpecCreator(
            _settings.colName, StringCell.TYPE).createSpec();
      DataColumnSpec[] invalidOutputColumnSpecs = new DataColumnSpec[inputTableSpec
            .getNumColumns()];

      for (int i = 0; i < inputTableSpec.getNumColumns(); i++)
      {
         DataColumnSpec columnSpec = inputTableSpec.getColumnSpec(i);

         if (i == colIdx)
            invalidOutputColumnSpecs[i] = invalidOutputColumnSpec;
         else
            invalidOutputColumnSpecs[i] = columnSpec;
      }

      return new DataTableSpec[] { inputTableSpec,
            new DataTableSpec(invalidOutputColumnSpecs) };
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute (final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception
   {

      DataTableSpec inputTableSpec = inData[0].getDataTableSpec();
      DataTableSpec[] outputSpecs = getDataTableSpecs(inputTableSpec);

      BufferedDataContainer validOutputContainer = exec
            .createDataContainer(outputSpecs[0]);
      BufferedDataContainer invalidOutputContainer = exec
            .createDataContainer(outputSpecs[1]);

      int colIdx = inputTableSpec.findColumnIndex(_settings.colName);

      if (colIdx == -1)
         throw new Exception("column not found");

      CloseableRowIterator it = inData[0].iterator();
      int rowNumber = 1;

      while (it.hasNext())
      {
         DataRow inputRow = it.next();
         RowKey key = inputRow.getKey();
         DataCell[] cells;

         cells = new DataCell[inputRow.getNumCells()];

         String str;

         try
         {
            IndigoPlugin.lock();
            str = ((IndigoMolCell) (inputRow.getCell(colIdx)))
                  .getIndigoObject().checkBadValence();
         }
         finally
         {
            IndigoPlugin.unlock();
         }

         if (str != null && !str.equals(""))
         {
            for (int i = 0; i < inputRow.getNumCells(); i++)
            {
               if (i == colIdx)
                  cells[i] = new StringCell(str);
               else
                  cells[i] = inputRow.getCell(i);
            }
            invalidOutputContainer.addRowToTable(new DefaultRow(key, cells));
         }
         else
         {
            validOutputContainer.addRowToTable(inputRow);
         }
         exec.checkCanceled();
         exec.setProgress(rowNumber / (double) inData[0].getRowCount(),
               "Adding row " + rowNumber);

         rowNumber++;
      }

      validOutputContainer.close();
      invalidOutputContainer.close();
      return new BufferedDataTable[] { validOutputContainer.getTable(),
            invalidOutputContainer.getTable() };
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
      return getDataTableSpecs(inSpecs[0]);
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
