package com.ggasoftware.indigo.knime.areplacer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.knime.core.data.*;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.*;

import com.ggasoftware.indigo.IndigoException;
import com.ggasoftware.indigo.IndigoObject;
import com.ggasoftware.indigo.knime.cell.IndigoDataCell;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;
import com.ggasoftware.indigo.knime.common.IndigoNodeModel;
import com.ggasoftware.indigo.knime.common.IndigoNodeSettings;
import com.ggasoftware.indigo.knime.common.IndigoNodeSettings.STRUCTURE_TYPE;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

public class IndigoAtomReplacerNodeModel extends IndigoNodeModel
{
   IndigoAtomReplacerSettings _settings = new IndigoAtomReplacerSettings();

   protected IndigoAtomReplacerNodeModel()
   {
      super(1, 1);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception
   {
      BufferedDataTable bufferedDataTable = inData[IndigoAtomReplacerSettings.INPUT_PORT];
      _defineStructureType(bufferedDataTable.getDataTableSpec());
      
      DataTableSpec spec = getDataTableSpec(bufferedDataTable.getDataTableSpec());
      BufferedDataContainer outputContainer = exec.createDataContainer(spec);

      int colIdx = spec.findColumnIndex(_settings.colName.getStringValue());

      if (colIdx == -1)
         throw new Exception("column not found");

      int rowNumber = 1;

      // Split atoms to replace into a set
      HashSet<String> atomToReplace = new HashSet<String>();
      if (_settings.replaceSpecificAtom.getBooleanValue())
      {
         String[] atoms = _settings.specificAtom.getStringValue().split("\\s*,\\s*");
         atomToReplace.addAll(Arrays.asList(atoms));
      }
     
      for (DataRow inputRow : bufferedDataTable)
      {
         RowKey key = inputRow.getKey();
         DataCell[] cells;
         int i;
         
         if (_settings.appendColumn.getBooleanValue())
            cells = new DataCell[inputRow.getNumCells() + 1];
         else
            cells = new DataCell[inputRow.getNumCells()];
         
         for (i = 0; i < inputRow.getNumCells(); i++)
            cells[i] = inputRow.getCell(i);
         
         if (inputRow.getCell(colIdx).isMissing())
         {
            if (_settings.appendColumn.getBooleanValue())
               cells[i] = DataType.getMissingCell();
         }
         else
         {
   
            try
            {
               IndigoPlugin.lock();
               IndigoObject io = ((IndigoDataCell) (inputRow.getCell(colIdx))).getIndigoObject();
               
//               io = io.clone();
   
               boolean replaceAllAtoms = !_settings.replaceHighlighted.getBooleanValue() && !_settings.replaceSpecificAtom.getBooleanValue();

               if (!_settings.replaceAttachmentPoints.getBooleanValue() || !replaceAllAtoms)
               {
                  if(_settings.structureType.equals(STRUCTURE_TYPE.Reaction))
                     for(IndigoObject mol : io.iterateMolecules())
                        _replaceSpecificAtoms(atomToReplace, mol);
                  else
                     _replaceSpecificAtoms(atomToReplace, io);
               }
               
               if (_settings.replaceAttachmentPoints.getBooleanValue())
               {
                  if(_settings.structureType.equals(STRUCTURE_TYPE.Reaction))
                     for(IndigoObject mol : io.iterateMolecules())
                        _replaceAttachmentPoints(mol);
                  else
                     _replaceAttachmentPoints(io);
                     
               }
               
               if (_settings.appendColumn.getBooleanValue())
                  cells[i] = _createNewDataCell(io, _settings.structureType);
               else
                  cells[colIdx] = _createNewDataCell(io, _settings.structureType);
            }
            finally
            {
               IndigoPlugin.unlock();
            }
         }

         outputContainer.addRowToTable(new DefaultRow(key, cells));
         exec.checkCanceled();
         exec.setProgress(rowNumber / (double) bufferedDataTable.getRowCount(),
               "Adding row " + rowNumber);

         rowNumber++;
      }

      outputContainer.close();
      return new BufferedDataTable[] { outputContainer.getTable() };
   }

   private void _replaceAttachmentPoints(IndigoObject molStructure) {
      List<Integer> atomsWithAttach = new ArrayList<Integer>();
      int maxOrder = molStructure.countAttachmentPoints();
      
      for (int order = 1; order <= maxOrder; order++)
         for (IndigoObject atomWithAttachment: molStructure.iterateAttachmentPoints(order))
            atomsWithAttach.add(atomWithAttachment.index());
      
      molStructure.clearAttachmentPoints();
      List<Integer> newAtoms = new ArrayList<Integer>();
      for (int idx : atomsWithAttach)
      {
         IndigoObject atom = molStructure.getAtom(idx);
         IndigoObject newAtom;
         if (_settings.newAtomLabel.getStringValue().matches("R\\d*"))
            newAtom = molStructure.addRSite(_settings.newAtomLabel.getStringValue());
         else
            newAtom = molStructure.addAtom(_settings.newAtomLabel.getStringValue());
         atom.addBond(newAtom, 1);
         newAtoms.add(newAtom.index());
      }
      
      // Layout added atoms if coordinates are present
      try
      {
         if (molStructure.hasCoord())
            molStructure.getSubmolecule(newAtoms).layout();
      }
      catch (IndigoException ex)
      {
         LOGGER.warn("Layout exception: " + ex.getMessage());
      }
   }

   private void _replaceSpecificAtoms(HashSet<String> atomToReplace, IndigoObject molStructure) {
      List<Integer> atoms = new ArrayList<Integer>();
      
      for (IndigoObject atom : molStructure.iterateAtoms()) {
         if (_settings.replaceHighlighted.getBooleanValue() && !atom.isHighlighted())
            continue;
         if (_settings.replaceSpecificAtom.getBooleanValue() && !atomToReplace.contains(atom.symbol()))
            continue;
         atoms.add(atom.index());
      }

      for (int idx : atoms) {
         IndigoObject atom = molStructure.getAtom(idx);
         if (_settings.newAtomLabel.getStringValue().matches("R\\d*"))
            atom.setRSite(_settings.newAtomLabel.getStringValue());
         else
            atom.resetAtom(_settings.newAtomLabel.getStringValue());
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void reset()
   {
   }

   protected DataTableSpec getDataTableSpec (DataTableSpec inSpec) throws InvalidSettingsException
   {
      if (_settings.appendColumn.getBooleanValue() && (_settings.newColName.getStringValue() == null || _settings.newColName.getStringValue().length() < 1))
         throw new InvalidSettingsException("New column name must be specified");
      
      DataColumnSpec[] specs;
      
      if (_settings.appendColumn.getBooleanValue())
         specs = new DataColumnSpec[inSpec.getNumColumns() + 1];
      else
         specs = new DataColumnSpec[inSpec.getNumColumns()];

      int i;

      for (i = 0; i < inSpec.getNumColumns(); i++)
         specs[i] = inSpec.getColumnSpec(i);

      if (_settings.appendColumn.getBooleanValue())
         specs[i] = _createNewColumnSpec(_settings.newColName.getStringValue(), _settings.structureType);

      return new DataTableSpec(specs);
   }
   
   private STRUCTURE_TYPE _defineStructureType(DataTableSpec tSpec) {
      STRUCTURE_TYPE stype = IndigoNodeSettings.getStructureType(tSpec, _settings.colName.getStringValue());
      _settings.structureType = stype;
      return stype;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
         throws InvalidSettingsException
   {
      DataTableSpec inSpec = inSpecs[IndigoAtomReplacerSettings.INPUT_PORT];
      searchMixedIndigoColumn(inSpec, _settings.colName, IndigoMolValue.class, IndigoReactionValue.class);
      
      STRUCTURE_TYPE stype = _defineStructureType(inSpec);
      
      if(stype.equals(STRUCTURE_TYPE.Unknown)) 
         throw new InvalidSettingsException("can not define structure type: reaction or molecule columns");
      /*
       * Set loading parameters warning message
       */
      if(_settings.warningMessage != null) {
         setWarningMessage(_settings.warningMessage);
      }
      return new DataTableSpec[] { getDataTableSpec(inSpec) };
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveSettingsTo(final NodeSettingsWO settings)
   {
      _settings.saveSettingsTo(settings);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      _settings.loadSettingsFrom(settings);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void validateSettings(final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      IndigoAtomReplacerSettings s = new IndigoAtomReplacerSettings();
      s.loadSettingsFrom(settings);
      if (s.colName.getStringValue() == null || s.colName.getStringValue().length() < 1)
         throw new InvalidSettingsException("No column name given");
      if (s.appendColumn.getBooleanValue() && ((s.newColName.getStringValue() == null) || (s.newColName.getStringValue().length() < 1)))
         throw new InvalidSettingsException("No name for new column given");
      if (s.newAtomLabel.getStringValue() == null || s.newAtomLabel.getStringValue().length() < 1)
         throw new InvalidSettingsException("newAtomLabel label must not be empty");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadInternals(final File internDir,
         final ExecutionMonitor exec) throws IOException,
         CanceledExecutionException
   {
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveInternals(final File internDir,
         final ExecutionMonitor exec) throws IOException,
         CanceledExecutionException
   {
   }

   private static final NodeLogger LOGGER = NodeLogger.getLogger(IndigoAtomReplacerNodeModel.class);
}
