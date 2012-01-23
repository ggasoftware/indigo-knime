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

package com.ggasoftware.indigo.knime.convert.base;

import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.data.container.*;
import org.knime.core.data.def.*;
import org.knime.core.node.*;

import com.ggasoftware.indigo.*;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class IndigoLoaderNodeModel extends NodeModel
{
   protected final IndigoLoaderSettings _settings;
   
   protected Class<? extends DataValue>[] _valueFilterClasses = null;
   
   public void set_valueFilterClasses(
			Class<? extends DataValue>[] valueFilterClasses) {
		_valueFilterClasses = valueFilterClasses;
	}

	protected IndigoLoaderNodeModel(boolean query)
   {
      super(1, 2);
      _settings = new IndigoLoaderSettings(query);
   }
   
   abstract protected DataType getDataCellType();
   
   abstract protected DataCell createDataCell(Indigo indigo, DataCell src);

   protected DataTableSpec[] getDataTableSpecs (DataTableSpec inputTableSpec)
         throws InvalidSettingsException
   {
      if (_settings.colName.getStringValue() == null || _settings.colName.getStringValue().length() < 1)
         throw new InvalidSettingsException("Column name not specified");
      if (_settings.appendColumn.getBooleanValue())
         if (_settings.newColName.getStringValue() == null || _settings.newColName.getStringValue().length() < 1)
            throw new InvalidSettingsException("No new column name specified");
      
      String newColName = _settings.newColName.getStringValue();
      int newColIdx = inputTableSpec.getNumColumns();
      int colIdx = inputTableSpec.findColumnIndex(_settings.colName.getStringValue());

      if (colIdx == -1)
         throw new InvalidSettingsException("column not found");
 
      if (!_settings.appendColumn.getBooleanValue())
      {
         newColName = _settings.colName.getStringValue();
         newColIdx = colIdx;
      }

      DataColumnSpec validOutputColumnSpec = new DataColumnSpecCreator(newColName, getDataCellType()).createSpec();
      DataColumnSpec invalidOutputColumnSpec = new DataColumnSpecCreator(newColName, StringCell.TYPE).createSpec();

      DataColumnSpec[] validOutputColumnSpecs, invalidOutputColumnSpecs;

      if (_settings.appendColumn.getBooleanValue())
      {
         validOutputColumnSpecs = new DataColumnSpec[inputTableSpec.getNumColumns() + 1];
         invalidOutputColumnSpecs = new DataColumnSpec[inputTableSpec.getNumColumns() + 1];
      }
      else
      {
         validOutputColumnSpecs = new DataColumnSpec[inputTableSpec.getNumColumns()];
         invalidOutputColumnSpecs = new DataColumnSpec[inputTableSpec.getNumColumns()];
      }

      for (int i = 0; i < inputTableSpec.getNumColumns(); i++)
      {
         DataColumnSpec columnSpec = inputTableSpec.getColumnSpec(i);

         if (!_settings.appendColumn.getBooleanValue() && i == newColIdx)
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

      if (_settings.appendColumn.getBooleanValue())
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
      DataTableSpec inputTableSpec = inData[IndigoLoaderSettings.INPUT_PORT].getDataTableSpec();
      DataTableSpec[] outputSpecs = getDataTableSpecs(inputTableSpec);

      BufferedDataContainer validOutputContainer = exec
            .createDataContainer(outputSpecs[0]);
      BufferedDataContainer invalidOutputContainer = exec
            .createDataContainer(outputSpecs[1]);

      int newColIdx = inputTableSpec.getNumColumns();
      int colIdx = inputTableSpec.findColumnIndex(_settings.colName.getStringValue());

      if (colIdx == -1)
         throw new Exception("column not found");

      if (!_settings.appendColumn.getBooleanValue())
         newColIdx = colIdx;

      CloseableRowIterator it = inData[IndigoLoaderSettings.INPUT_PORT].iterator();
      int rowNumber = 1;

      Indigo indigo = IndigoPlugin.getIndigo();

      while (it.hasNext())
      {
         DataRow inputRow = it.next();
         RowKey key = inputRow.getKey();
         DataCell[] cells = new DataCell[inputRow.getNumCells() + (_settings.appendColumn.getBooleanValue() ? 1 : 0)];

         DataCell molcell = inputRow.getCell(colIdx);
         DataCell newcell = null;
         String message = null;

         try
         {
            IndigoPlugin.lock();
            indigo.setOption("ignore-stereochemistry-errors", _settings.ignoreStereochemistryErrors.getBooleanValue());
            indigo.setOption("treat-x-as-pseudoatom", _settings.treatXAsPseudoatom.getBooleanValue());

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
               cells[i] = (!_settings.appendColumn.getBooleanValue() && i == newColIdx) ? newcell : inputRow.getCell(i);
            }
            if (_settings.appendColumn.getBooleanValue())
            {
               cells[newColIdx] = newcell;
            }

            validOutputContainer.addRowToTable(new DefaultRow(key, cells));
         }
         else
         {
            for (int i = 0; i < inputRow.getNumCells(); i++)
            {
               if (!_settings.appendColumn.getBooleanValue() && i == newColIdx)
                  cells[i] = new StringCell(message);
               else
                  cells[i] = inputRow.getCell(i);
            }
            if (_settings.appendColumn.getBooleanValue())
               cells[newColIdx] = new StringCell(message);
            invalidOutputContainer.addRowToTable(new DefaultRow(key, cells));
         }
         
         exec.checkCanceled();
         exec.setProgress(rowNumber / (double) inData[IndigoLoaderSettings.INPUT_PORT].getRowCount(),
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
   	if (_settings.colName.getStringValue() == null || _settings.colName.getStringValue().length() < 1) {
   		// Try to deduce column name automatically
         List<String> compatible_columns = new ArrayList<String>();
         // Collect them in the order of classes
         for (Class<? extends DataValue> cl : _valueFilterClasses) {
            for (DataColumnSpec c : inSpecs[0]) {
            	String name = c.getName();
            	if (compatible_columns.contains(name))
            		continue;
            	if (c.getType().isCompatible(cl))
            		compatible_columns.add(c.getName());
            }
         }
         if (compatible_columns.isEmpty())
            throw new InvalidSettingsException("There is no compatible colums in the source table");
         else
         {
         	_settings.colName.setStringValue(compatible_columns.get(0));
         	_settings.newColName.setStringValue(_settings.colName.getStringValue() + " (Indigo)");
         	if (compatible_columns.size() > 1)
	            setWarningMessage("Column \"" + _settings.colName.getStringValue() + "\" was used by default.");
         }
   	}
   	if(_settings.warningMessage != null) {
   	   setWarningMessage(_settings.warningMessage);
   	}
   	
      DataTableSpec inputTableSpec = inSpecs[0];
      return getDataTableSpecs(inputTableSpec);
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
   protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
         throws InvalidSettingsException {
      _settings.loadSettingsFrom(settings);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void validateSettings (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      IndigoLoaderSettings s = new IndigoLoaderSettings(_settings.query);
      s.loadSettingsFrom(settings);
      
      if (s.appendColumn.getBooleanValue())
         if (s.newColName.getStringValue() == null || s.newColName.getStringValue().length() < 1)
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
