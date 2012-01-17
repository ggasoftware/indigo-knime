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

package com.ggasoftware.indigo.knime.common;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.data.container.*;
import org.knime.core.node.*;

import com.ggasoftware.indigo.*;
import com.ggasoftware.indigo.knime.cell.IndigoMolCell;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

public class IndigoSimpleNodeModel extends IndigoNodeModel
{

   public static abstract class Transformer
   {
      public abstract void transform (IndigoObject io);
   }

   // the logger instance
   private static final NodeLogger logger = NodeLogger
         .getLogger(IndigoSimpleNodeModel.class);

   IndigoSimpleSettings _settings = new IndigoSimpleSettings();
   Transformer _transformer;
   String _message;

   public IndigoSimpleNodeModel(String message, Transformer transformer)
   {
      super(1, 1);
      _message = message;
      _transformer = transformer;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute (final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception
   {

      ColumnRearranger crea = createRearranger(inData[0].getDataTableSpec());

      return new BufferedDataTable[] { exec.createColumnRearrangeTable(
            inData[0], crea, exec) };
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void reset ()
   {
   }

   class Converter implements CellFactory
   {
      int _colIndex;
      private final DataColumnSpec[] m_colSpec;

      Converter(final DataTableSpec inSpec, final DataColumnSpec cs,
            final IndigoSimpleSettings settings, final int colIndex)
      {
         _colIndex = colIndex;

         DataType type = IndigoMolCell.TYPE;

         if (settings.appendColumn.getBooleanValue())
         {
            m_colSpec = new DataColumnSpec[] { new DataColumnSpecCreator(
                  DataTableSpec
                  .getUniqueColumnName(inSpec, settings.newColName.getStringValue()),
                  type).createSpec() };
         }
         else
         {
            m_colSpec = new DataColumnSpec[] { new DataColumnSpecCreator(
                  settings.colName.getStringValue(), type).createSpec() };
         }
      }

      public DataCell getCell (final DataRow row)
      {
         DataCell cell = row.getCell(_colIndex);
         if (cell.isMissing())
            return cell;
         else
         {
            try
            {
               IndigoPlugin.lock();
               IndigoMolValue iv = (IndigoMolValue) cell;
               IndigoObject io = iv.getIndigoObject().clone();
               _transformer.transform(io);
               return new IndigoMolCell(io);
            }
            catch (IndigoException ex)
            {
               logger.error("Could not " + _message + " for " + row.getKey() + 
                     ": " + ex.getMessage(), ex);
               return DataType.getMissingCell();
            }
            finally
            {
               IndigoPlugin.unlock();
            }
         }
      }

      @Override
      public DataCell[] getCells (DataRow row)
      {
         return new DataCell[] { getCell(row) };
      }

      @Override
      public DataColumnSpec[] getColumnSpecs ()
      {
         return m_colSpec;
      }

      @Override
      public void setProgress (int curRowNr, int rowCount, RowKey lastKey,
            ExecutionMonitor exec)
      {
         exec.setProgress((double)curRowNr / rowCount);
      }
   }

   private ColumnRearranger createRearranger (final DataTableSpec inSpec)
   {
      ColumnRearranger crea = new ColumnRearranger(inSpec);

      DataType type = IndigoMolCell.TYPE;

      DataColumnSpec cs;
      if (_settings.appendColumn.getBooleanValue())
      {
         String name = DataTableSpec.getUniqueColumnName(inSpec,
               _settings.newColName.getStringValue());
         cs = new DataColumnSpecCreator(name, type).createSpec();
      }
      else
      {
         cs = new DataColumnSpecCreator(_settings.colName.getStringValue(), type).createSpec();
      }

      Converter conv = new Converter(inSpec, cs, _settings,
            inSpec.findColumnIndex(_settings.colName.getStringValue()));

      if (_settings.appendColumn.getBooleanValue())
      {
         crea.append(conv);
      }
      else
      {
         crea.replace(conv, _settings.colName.getStringValue());
      }

      return crea;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected DataTableSpec[] configure (final DataTableSpec[] inSpecs)
         throws InvalidSettingsException
   {
      _settings.colName.setStringValue(searchIndigoColumn(inSpecs[0], _settings.colName.getStringValue(), IndigoMolValue.class));
      return new DataTableSpec[] { createRearranger(inSpecs[0]).createSpec() };
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
      IndigoSimpleSettings s = new IndigoSimpleSettings();
      s.loadSettingsFrom(settings);
      if (s.colName.getStringValue() == null || s.colName.getStringValue().length() < 1)
         throw new InvalidSettingsException("No column name given");
      if (s.appendColumn.getBooleanValue() && ((s.newColName.getStringValue() == null) || (s.newColName.getStringValue().length() < 1)))
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
