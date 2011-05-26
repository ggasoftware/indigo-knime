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

package com.ggasoftware.indigo.knime.convert.molsaver;

import java.io.File;
import java.io.IOException;

import org.knime.chem.types.*;
import org.knime.core.data.*;
import org.knime.core.data.container.*;
import org.knime.core.node.*;

import com.ggasoftware.indigo.IndigoException;
import com.ggasoftware.indigo.IndigoObject;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolValue;
import com.ggasoftware.indigo.knime.common.IndigoNodeModel;
import com.ggasoftware.indigo.knime.convert.molsaver.IndigoMoleculeSaverSettings.Format;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

public class IndigoMoleculeSaverNodeModel extends IndigoNodeModel
{
   private final IndigoMoleculeSaverSettings _settings = new IndigoMoleculeSaverSettings();

   private static final NodeLogger LOGGER = NodeLogger
         .getLogger(IndigoMoleculeSaverNodeModel.class);

   private boolean _query;
   
   /**
    * Constructor for the node model.
    */
   protected IndigoMoleculeSaverNodeModel (boolean query)
   {
      super(1, 1);
      _query = query;
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
            final IndigoMoleculeSaverSettings settings, final int colIndex)
      {
         _colIndex = colIndex;

         DataType type = null;

         if (_settings.destFormat == Format.Mol)
            type = MolCell.TYPE;
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
            IndigoObject io;
            
            if (_query)
               io = ((IndigoQueryMolValue)cell).getIndigoObject();
            else
               io = ((IndigoMolValue)cell).getIndigoObject();
            try
            {
               IndigoPlugin.lock();
               
               if (_settings.destFormat == Format.Mol || _settings.destFormat == Format.SDF ||_settings.destFormat == Format.CML)
                  if (_settings.generateCoords && !io.hasCoord())
                  {
                     io = io.clone();
                     io.layout();
                  }
               
               if (_settings.destFormat == Format.Mol)
                  return MolCellFactory.create(io.molfile());
               if (_settings.destFormat == Format.SDF)
                  return SdfCellFactory.create(io.molfile());
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
      }
   }

   private ColumnRearranger createRearranger (final DataTableSpec inSpec)
   {
      ColumnRearranger crea = new ColumnRearranger(inSpec);

      DataType type = null;
      if (_settings.destFormat == Format.Mol)
         type = MolCell.TYPE;
      else if (_settings.destFormat == Format.SDF)
         type = SdfCell.TYPE;
      else if (_settings.destFormat == Format.Smiles
            || _settings.destFormat == Format.CanonicalSmiles)
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
      if (_query)
         _settings.colName = searchIndigoColumn(inSpecs[0], _settings.colName, IndigoQueryMolValue.class);
      else
         _settings.colName = searchIndigoColumn(inSpecs[0], _settings.colName, IndigoMolValue.class);
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
      IndigoMoleculeSaverSettings s = new IndigoMoleculeSaverSettings();
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
