package com.ggasoftware.indigo.knime.areplacer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.knime.core.data.*;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.*;

import com.ggasoftware.indigo.IndigoException;
import com.ggasoftware.indigo.IndigoObject;
import com.ggasoftware.indigo.knime.cell.IndigoMolCell;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.common.IndigoNodeModel;
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
      DataTableSpec spec = getDataTableSpec(inData[0].getDataTableSpec());
      BufferedDataContainer outputContainer = exec.createDataContainer(spec);

      int colIdx = spec.findColumnIndex(_settings.colName);

      if (colIdx == -1)
         throw new Exception("column not found");

      CloseableRowIterator it = inData[0].iterator();
      int rowNumber = 1;

      // Split atoms to replace into a set
      HashSet<String> atomToReplace = new HashSet<String>();
      if (_settings.replaceSpecificAtom)
      {
         String[] atoms = _settings.specificAtom.split("\\s*,\\s*");
         atomToReplace.addAll(Arrays.asList(atoms));
      }
     
      while (it.hasNext())
      {
         DataRow inputRow = it.next();
         RowKey key = inputRow.getKey();
         DataCell[] cells;
         int i;
         
         if (_settings.replaceColumn)
            cells = new DataCell[inputRow.getNumCells()];
         else
            cells = new DataCell[inputRow.getNumCells() + 1];
         
         for (i = 0; i < inputRow.getNumCells(); i++)
            cells[i] = inputRow.getCell(i);
         
         if (inputRow.getCell(colIdx).isMissing())
         {
            if (!_settings.replaceColumn)
               cells[i] = DataType.getMissingCell();
         }
         else
         {
            IndigoObject io = ((IndigoMolCell) (inputRow.getCell(colIdx))).getIndigoObject();
   
            try
            {
               IndigoPlugin.lock();
               IndigoObject mol = io.clone();
   
               List<Integer> atoms = new ArrayList<Integer>(); 
               
               boolean replaceAllAtoms = !_settings.replaceHighlighted && !_settings.replaceSpecificAtom;

               if (!_settings.replaceAttachmentPoints || !replaceAllAtoms)
               {
                  for (IndigoObject atom : mol.iterateAtoms())
                  {
                     if (_settings.replaceHighlighted && !atom.isHighlighted())
                        continue;
                     if (_settings.replaceSpecificAtom && !atomToReplace.contains(atom.symbol()))
                        continue;
                     atoms.add(atom.index());
                  }
                  
                  for (int idx : atoms)
                  {
                     IndigoObject atom = mol.getAtom(idx);
                     if (_settings.newAtomLabel.matches("R\\d*"))
                        atom.setRSite(_settings.newAtomLabel);
                     else
                        atom.resetAtom(_settings.newAtomLabel);
                  }
               }
               
               if (_settings.replaceAttachmentPoints)
               {
                  List<Integer> atomsWithAttach = new ArrayList<Integer>();
                  int maxOrder = mol.countAttachmentPoints();
                  
                  for (int order = 1; order <= maxOrder; order++)
                     for (IndigoObject atomWithAttachment: mol.iterateAttachmentPoints(order))
                        atomsWithAttach.add(atomWithAttachment.index());
                  mol.clearAttachmentPoints();
                  List<Integer> newAtoms = new ArrayList<Integer>();
                  for (int idx : atomsWithAttach)
                  {
                     IndigoObject atom = mol.getAtom(idx);
                     IndigoObject newAtom;
                     if (_settings.newAtomLabel.matches("R\\d*"))
                        newAtom = mol.addRSite(_settings.newAtomLabel);
                     else
                        newAtom = mol.addAtom(_settings.newAtomLabel);
                     atom.addBond(newAtom, 1);
                     newAtoms.add(newAtom.index());
                  }
                  
                  // Layout added atoms if coordinates are present
                  try
                  {
                     if (mol.hasCoord())
                        mol.getSubmolecule(newAtoms).layout();
                  }
                  catch (IndigoException ex)
                  {
                     LOGGER.warn("Layout exception: " + ex.getMessage());
                  }
               }
               
               if (_settings.replaceColumn)
                  cells[colIdx] = new IndigoMolCell(mol);
               else
                  cells[i] = new IndigoMolCell(mol);
            }
            finally
            {
               IndigoPlugin.unlock();
            }
         }

         outputContainer.addRowToTable(new DefaultRow(key, cells));
         exec.checkCanceled();
         exec.setProgress(rowNumber / (double) inData[0].getRowCount(),
               "Adding row " + rowNumber);

         rowNumber++;
      }

      outputContainer.close();
      return new BufferedDataTable[] { outputContainer.getTable() };
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
      if (!_settings.replaceColumn && (_settings.newColName == null || _settings.newColName.length() < 1))
         throw new InvalidSettingsException("New column name must be specified");
      
      DataColumnSpec[] specs;
      
      if (_settings.replaceColumn)
         specs = new DataColumnSpec[inSpec.getNumColumns()];
      else
         specs = new DataColumnSpec[inSpec.getNumColumns() + 1];

      int i;

      for (i = 0; i < inSpec.getNumColumns(); i++)
         specs[i] = inSpec.getColumnSpec(i);

      if (!_settings.replaceColumn)
         specs[i] = new DataColumnSpecCreator(_settings.newColName, IndigoMolCell.TYPE).createSpec();

      return new DataTableSpec(specs);
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
         throws InvalidSettingsException
   {
      _settings.colName = searchIndigoColumn(inSpecs[0], _settings.colName, IndigoMolValue.class);
      return new DataTableSpec[] { getDataTableSpec(inSpecs[0]) };
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveSettingsTo(final NodeSettingsWO settings)
   {
      _settings.saveSettings(settings);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      _settings.loadSettings(settings);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void validateSettings(final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      IndigoAtomReplacerSettings s = new IndigoAtomReplacerSettings();
      s.loadSettings(settings);
      if (s.colName == null || s.colName.length() < 1)
         throw new InvalidSettingsException("No column name given");
      if (!s.replaceColumn && ((s.newColName == null) || (s.newColName.length() < 1)))
         throw new InvalidSettingsException("No name for new column given");
      if (s.newAtomLabel == null || s.newAtomLabel.length() < 1)
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
