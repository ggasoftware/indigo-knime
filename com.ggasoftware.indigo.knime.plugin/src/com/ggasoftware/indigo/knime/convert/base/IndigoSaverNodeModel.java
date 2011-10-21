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

import java.io.File;
import java.io.IOException;

import org.knime.chem.types.CMLCell;
import org.knime.chem.types.CMLCellFactory;
import org.knime.chem.types.MolCell;
import org.knime.chem.types.MolCellFactory;
import org.knime.chem.types.RxnCell;
import org.knime.chem.types.RxnCellFactory;
import org.knime.chem.types.SdfCell;
import org.knime.chem.types.SdfCellFactory;
import org.knime.chem.types.SmilesCell;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DataValue;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.CellFactory;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.ggasoftware.indigo.IndigoException;
import com.ggasoftware.indigo.IndigoObject;
import com.ggasoftware.indigo.knime.cell.IndigoDataValue;
import com.ggasoftware.indigo.knime.common.IndigoNodeModel;
import com.ggasoftware.indigo.knime.convert.base.IndigoSaverSettings.Format;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

abstract public class IndigoSaverNodeModel extends IndigoNodeModel
{
   private final IndigoSaverSettings _settings = new IndigoSaverSettings();

   private static final NodeLogger LOGGER = NodeLogger
         .getLogger(IndigoSaverNodeModel.class);

   private final Class<? extends DataValue> _dataValueClass;
   
   /**
    * Constructor for the node model.
    */
   protected IndigoSaverNodeModel(Class<? extends DataValue> dataValueClass)
   {
      super(1, 1);
      _dataValueClass = dataValueClass;
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
            final IndigoSaverSettings settings, final int colIndex)
      {
         _colIndex = colIndex;

         DataType type = null;

         if (_settings.destFormat == Format.Mol)
            type = MolCell.TYPE;
         else if (_settings.destFormat == Format.Rxn)
             type = RxnCell.TYPE;
         else if (_settings.destFormat == Format.SDF)
            type = SdfCell.TYPE;
         else if (_settings.destFormat == Format.Smiles || _settings.destFormat == Format.CanonicalSmiles)
            type = SmilesCell.TYPE;
         else
            type = CMLCell.TYPE;

         if (settings.replaceColumn)
         {
            m_colSpec = new DataColumnSpec[] { new DataColumnSpecCreator(
                  settings.colName, type).createSpec() };
         }
         else
         {
            m_colSpec = new DataColumnSpec[] { new DataColumnSpecCreator(
                  DataTableSpec
                        .getUniqueColumnName(inSpec, settings.newColName),
                  type).createSpec() };
         }
      }

      public DataCell getCell (final DataRow row)
      {
         DataCell cell = row.getCell(_colIndex);
         if (cell.isMissing())
         {
            return cell;
         }
         else
         {
            IndigoObject io = ((IndigoDataValue)cell).getIndigoObject();
            try
            {
               IndigoPlugin.lock();
               
               if (_settings.destFormat == Format.Mol || _settings.destFormat == Format.SDF ||_settings.destFormat == Format.CML || _settings.destFormat == Format.Rxn)
                  if (_settings.generateCoords && !io.hasCoord())
                  {
                     io = io.clone();
                     io.layout();
                  }
               
               if (_settings.destFormat == Format.Mol)
                  return MolCellFactory.create(io.molfile());
               if (_settings.destFormat == Format.Rxn)
                   return RxnCellFactory.create(io.rxnfile());
               if (_settings.destFormat == Format.SDF) {
                  return SdfCellFactory.create(io.molfile() + "\n$$$$\n");
               }
               if (_settings.destFormat == Format.Smiles)
                  return new SmilesCell(io.smiles());
               if (_settings.destFormat == Format.CanonicalSmiles)
               {
                  IndigoObject clone = io.clone();
                  clone.aromatize();
                  return new SmilesCell(clone.canonicalSmiles());
               }
               return CMLCellFactory.create(io.cml());
            }
            catch (IndigoException ex)
            {
               LOGGER.error("Could not convert molecule: " + ex.getMessage(), ex);
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

      DataType type = null;
      if (_settings.destFormat == Format.Mol)
         type = MolCell.TYPE;
      else if (_settings.destFormat == Format.Rxn)
          type = RxnCell.TYPE;
      else if (_settings.destFormat == Format.SDF)
         type = SdfCell.TYPE;
      else if (_settings.destFormat == Format.Smiles || _settings.destFormat == Format.CanonicalSmiles)
         type = SmilesCell.TYPE;
      else if (_settings.destFormat == Format.CML)
         type = CMLCell.TYPE;

      DataColumnSpec cs;
      if (_settings.replaceColumn)
      {
         cs = new DataColumnSpecCreator(_settings.colName, type).createSpec();
      }
      else
      {
         String name = DataTableSpec.getUniqueColumnName(inSpec,
               _settings.newColName);
         cs = new DataColumnSpecCreator(name, type).createSpec();
      }

      Converter conv = new Converter(inSpec, cs, _settings,
            inSpec.findColumnIndex(_settings.colName));

      if (_settings.replaceColumn)
      {
         crea.replace(conv, _settings.colName);
      }
      else
      {
         crea.append(conv);
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
	   _settings.colName = searchIndigoColumn(inSpecs[0], _settings.colName, _dataValueClass);
      return new DataTableSpec[] { createRearranger(inSpecs[0]).createSpec() };
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
      IndigoSaverSettings s = new IndigoSaverSettings();
      s.loadSettings(settings);
      if (!s.replaceColumn)
         if (s.newColName == null || s.newColName.length() < 1)
            throw new InvalidSettingsException("No name for the new column given");
      
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
