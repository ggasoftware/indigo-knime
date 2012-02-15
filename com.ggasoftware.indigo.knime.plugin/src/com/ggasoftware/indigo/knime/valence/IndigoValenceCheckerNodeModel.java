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
import org.knime.core.data.def.*;
import org.knime.core.node.*;

import com.ggasoftware.indigo.knime.cell.IndigoDataCell;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;
import com.ggasoftware.indigo.knime.common.IndigoNodeModel;
import com.ggasoftware.indigo.knime.common.IndigoNodeSettings;
import com.ggasoftware.indigo.knime.common.IndigoNodeSettings.STRUCTURE_TYPE;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

public class IndigoValenceCheckerNodeModel extends IndigoNodeModel
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
      int colIdx = inputTableSpec.findColumnIndex(_settings.colName.getStringValue());

      if (colIdx == -1)
         throw new InvalidSettingsException("column not found");

      DataColumnSpec invalidOutputColumnSpec = new DataColumnSpecCreator(
            _settings.colName.getStringValue(), StringCell.TYPE).createSpec();
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

      BufferedDataTable bufferedDataTable = inData[IndigoValenceCheckerSettings.INPUT_PORT];
      
      DataTableSpec inputTableSpec = bufferedDataTable.getDataTableSpec();
      DataTableSpec[] outputSpecs = getDataTableSpecs(inputTableSpec);

      BufferedDataContainer validOutputContainer = exec
            .createDataContainer(outputSpecs[0]);
      BufferedDataContainer invalidOutputContainer = exec
            .createDataContainer(outputSpecs[1]);

      int colIdx = inputTableSpec.findColumnIndex(_settings.colName.getStringValue());

      if (colIdx == -1)
         throw new Exception("column not found");

      int rowNumber = 1;

      for (DataRow inputRow : bufferedDataTable)
      {
         RowKey key = inputRow.getKey();
         DataCell[] cells;

         cells = new DataCell[inputRow.getNumCells()];

         String str = null;
         if (!inputRow.getCell(colIdx).isMissing())
            try {
               IndigoPlugin.lock();
               str = ((IndigoDataCell) (inputRow.getCell(colIdx))).getIndigoObject().checkBadValence();
            } finally {
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
            if(inputRow.getCell(colIdx).isMissing())
               invalidOutputContainer.addRowToTable(inputRow);
            else
               validOutputContainer.addRowToTable(inputRow);
         }
         exec.checkCanceled();
         exec.setProgress(rowNumber / (double) bufferedDataTable.getRowCount(),
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
   
   private STRUCTURE_TYPE _defineStructureType(DataTableSpec tSpec) {
      STRUCTURE_TYPE stype = IndigoNodeSettings.getStructureType(tSpec, _settings.colName.getStringValue());
      return stype;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected DataTableSpec[] configure (final DataTableSpec[] inSpecs)
         throws InvalidSettingsException
   {
      DataTableSpec inSpec = inSpecs[IndigoValenceCheckerSettings.INPUT_PORT];

      searchMixedIndigoColumn(inSpec, _settings.colName, IndigoMolValue.class, IndigoReactionValue.class);

      STRUCTURE_TYPE stype = _defineStructureType(inSpec);

      if (stype.equals(STRUCTURE_TYPE.Unknown))
         throw new InvalidSettingsException("can not define structure type: reaction or molecule columns");
      
      /*
       * Set loading parameters warning message
       */
      if(_settings.warningMessage != null) {
         setWarningMessage(_settings.warningMessage);
      }

      return getDataTableSpecs(inSpecs[0]);
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
