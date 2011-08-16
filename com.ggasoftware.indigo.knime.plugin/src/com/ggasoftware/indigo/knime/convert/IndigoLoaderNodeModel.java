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

package com.ggasoftware.indigo.knime.convert;

import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.data.container.*;
import org.knime.core.data.def.*;
import org.knime.core.node.*;

import com.ggasoftware.indigo.*;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

import java.io.File;

public abstract class IndigoLoaderNodeModel extends NodeModel
{
   private final IndigoLoaderSettings _settings = new IndigoLoaderSettings();

   protected IndigoLoaderNodeModel()
   {
      super(1, 2);
   }
   
   abstract protected DataType getDataCellType();
   
   abstract protected DataCell createDataCell(Indigo indigo, DataCell src);

   protected DataTableSpec[] getDataTableSpecs (DataTableSpec inputTableSpec)
         throws InvalidSettingsException
   {
      if (_settings.colName == null || _settings.colName.length() < 1)
         throw new InvalidSettingsException("Column name not specified");
      if (!_settings.replaceColumn)
         if (_settings.newColName == null || _settings.newColName.length() < 1)
            throw new InvalidSettingsException("No new column name specified");
      
      String newColName = _settings.newColName;
      int newColIdx = inputTableSpec.getNumColumns();
      int colIdx = inputTableSpec.findColumnIndex(_settings.colName);

      if (colIdx == -1)
         throw new InvalidSettingsException("column not found");
 
      if (_settings.replaceColumn)
      {
         newColName = _settings.colName;
         newColIdx = colIdx;
      }

      DataColumnSpec validOutputColumnSpec = new DataColumnSpecCreator(newColName, getDataCellType()).createSpec();
      DataColumnSpec invalidOutputColumnSpec = new DataColumnSpecCreator(newColName, StringCell.TYPE).createSpec();

      DataColumnSpec[] validOutputColumnSpecs, invalidOutputColumnSpecs;

      if (_settings.replaceColumn)
      {
         validOutputColumnSpecs = new DataColumnSpec[inputTableSpec.getNumColumns()];
         invalidOutputColumnSpecs = new DataColumnSpec[inputTableSpec.getNumColumns()];
      }
      else
      {
         validOutputColumnSpecs = new DataColumnSpec[inputTableSpec.getNumColumns() + 1];
         invalidOutputColumnSpecs = new DataColumnSpec[inputTableSpec.getNumColumns() + 1];
      }

      for (int i = 0; i < inputTableSpec.getNumColumns(); i++)
      {
         DataColumnSpec columnSpec = inputTableSpec.getColumnSpec(i);

         if (_settings.replaceColumn && i == newColIdx)
         {
            validOutputColumnSpecs[i] = validOutputColumnSpec;
            invalidOutputColumnSpecs[i] = invalidOutputColumnSpec;
         }
         else
         {
            validOutputColumnSpecs[i] = columnSpec;
            invalidOutputColumnSpecs[i] = columnSpec;
         }
      }

      if (!_settings.replaceColumn)
      {
         validOutputColumnSpecs[newColIdx] = validOutputColumnSpec;
         invalidOutputColumnSpecs[newColIdx] = invalidOutputColumnSpec;
      }

      return new DataTableSpec[] { new DataTableSpec(validOutputColumnSpecs),
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

      int newColIdx = inputTableSpec.getNumColumns();
      int colIdx = inputTableSpec.findColumnIndex(_settings.colName);

      if (colIdx == -1)
         throw new Exception("column not found");

      if (_settings.replaceColumn)
         newColIdx = colIdx;

      CloseableRowIterator it = inData[0].iterator();
      int rowNumber = 1;

      Indigo indigo = IndigoPlugin.getIndigo();

      while (it.hasNext())
      {
         DataRow inputRow = it.next();
         RowKey key = inputRow.getKey();
         DataCell[] cells = new DataCell[inputRow.getNumCells() + (_settings.replaceColumn ? 0 : 1)];

         DataCell molcell = inputRow.getCell(colIdx);
         DataCell newcell = null;
         String message = null;

         try
         {
            IndigoPlugin.lock();
            indigo.setOption("ignore-stereochemistry-errors", _settings.ignoreStereochemistryErrors);
            indigo.setOption("treat-x-as-pseudoatom", _settings.treatXAsPseudoatom);

            newcell = createDataCell(indigo, molcell);
         }
         catch (IndigoException e)
         {
            message = e.getMessage();
         }
         finally
         {
            IndigoPlugin.unlock();
         }
         
         if (newcell != null)
         {
            for (int i = 0; i < inputRow.getNumCells(); i++)
            {
               cells[i] = (_settings.replaceColumn && i == newColIdx) ? newcell : inputRow.getCell(i);
            }
            if (!_settings.replaceColumn)
            {
               cells[newColIdx] = newcell;
            }

            validOutputContainer.addRowToTable(new DefaultRow(key, cells));
         }
         else
         {
            for (int i = 0; i < inputRow.getNumCells(); i++)
            {
               if (_settings.replaceColumn && i == newColIdx)
                  cells[i] = new StringCell(message);
               else
                  cells[i] = inputRow.getCell(i);
            }
            if (!_settings.replaceColumn)
               cells[newColIdx] = new StringCell(message);
            invalidOutputContainer.addRowToTable(new DefaultRow(key, cells));
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
      DataTableSpec inputTableSpec = inSpecs[0];
      return getDataTableSpecs(inputTableSpec);
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
      IndigoLoaderSettings s = new IndigoLoaderSettings();
      s.loadSettings(settings);
      if (!s.replaceColumn)
         if (s.newColName == null || s.newColName.length() < 1)
            throw new InvalidSettingsException("No name for new column given");
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
