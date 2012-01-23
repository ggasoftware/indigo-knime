package com.ggasoftware.indigo.knime.fremover;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.knime.core.data.*;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.*;

import com.ggasoftware.indigo.*;
import com.ggasoftware.indigo.knime.IndigoNodeSettings;
import com.ggasoftware.indigo.knime.IndigoNodeSettings.STRUCTURE_TYPE;
import com.ggasoftware.indigo.knime.cell.IndigoDataCell;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;
import com.ggasoftware.indigo.knime.common.IndigoNodeModel;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

public class IndigoFeatureRemoverNodeModel extends IndigoNodeModel
{

   private final IndigoFeatureRemoverSettings _settings = new IndigoFeatureRemoverSettings();
   
   /**
    * Constructor for the node model.
    */
   protected IndigoFeatureRemoverNodeModel()
   {
      super(1, 1);
   }

   public static interface Remover
   {
      public void removeFeature (IndigoObject io);
   }
   
   public static final ArrayList<String> names = new ArrayList<String>();
   public static final Map<String, Remover> removers = new HashMap<String, Remover>();
   
   private static void addRemover (String name, Remover remover)
   {
      removers.put(name, remover);
      names.add(name);
   }
   
   static
   {
      addRemover("Isotopes", new Remover ()
      {
         public void removeFeature (IndigoObject io)
         {
            for (IndigoObject atom : io.iterateAtoms())
               atom.resetIsotope();
         }
      });
      addRemover("Chirality", new Remover ()
      {
         public void removeFeature (IndigoObject io)
         {
            io.clearStereocenters();
            for (IndigoObject bond : io.iterateBonds())
               if (bond.bondOrder() == 1)
                  bond.resetStereo();
         }
      });
      addRemover("Cis-trans", new Remover ()
      {
         public void removeFeature (IndigoObject io)
         {
            io.clearCisTrans();
         }
      });
      addRemover("Highlighting", new Remover ()
      {
         public void removeFeature (IndigoObject io)
         {
            io.unhighlight();
         }
      });
      addRemover("R-sites", new Remover ()
      {
         public void removeFeature (IndigoObject io)
         {
            for (IndigoObject atom : io.iterateAtoms())
               if (atom.isRSite())
                  atom.remove();
         }
      });
      addRemover("Pseudoatoms", new Remover ()
      {
         public void removeFeature (IndigoObject io)
         {
            for (IndigoObject atom : io.iterateAtoms())
               if (atom.isPseudoatom())
                  atom.remove();
         }
      });
      addRemover("Attachment points", new Remover ()
      {
         public void removeFeature (IndigoObject io)
         {
            io.clearAttachmentPoints();
         }
      });
      addRemover("Repeating units", new Remover ()
      {
         public void removeFeature (IndigoObject io)
         {
            for (IndigoObject ru : io.iterateRepeatingUnits())
               ru.remove();
         }
      });
      addRemover("Data S-groups", new Remover ()
      {
         public void removeFeature (IndigoObject io)
         {
            for (IndigoObject sg : io.iterateDataSGroups())
               sg.remove();
         }
      });
      addRemover("Minor components", new Remover ()
      {
         public void removeFeature (IndigoObject io)
         {
            int max_comp = -1, max_comp_size = 0;
            for (IndigoObject comp: io.iterateComponents())
               if (comp.countAtoms() > max_comp_size)
               {
                  max_comp_size = comp.countAtoms();
                  max_comp = comp.index();
               }
            if (max_comp == -1)
               return;
            
            IndigoObject maxComp = io.component(max_comp);
            HashSet<Integer> atomsRemain = new HashSet<Integer>();
            
            for(IndigoObject atom : maxComp.iterateAtoms())
               atomsRemain.add(atom.index());
            
            ArrayList<Integer> indices = new ArrayList<Integer>();
            
            for(IndigoObject atom : io.iterateAtoms())
               if(!atomsRemain.contains(atom.index()))
                  indices.add(atom.index());
            io.removeAtoms(toIntArray(indices));
         }
      });
   }
   
   static int[] toIntArray (List<Integer> list)
   {
      int[] arr = new int[list.size()];
      for (int i = 0; i < list.size(); i++)
         arr[i] = list.get(i).intValue();
      return arr;
   }

   protected DataTableSpec getDataTableSpec (DataTableSpec inputTableSpec) throws InvalidSettingsException
   {
      if (_settings.appendColumn.getBooleanValue())
         if (_settings.newColName.getStringValue() == null || _settings.newColName.getStringValue().length() < 1)
            throw new InvalidSettingsException("New column name must be specified");
      
      DataColumnSpec[] specs;
      
      if (_settings.appendColumn.getBooleanValue())
         specs = new DataColumnSpec[inputTableSpec.getNumColumns() + 1];
      else
         specs = new DataColumnSpec[inputTableSpec.getNumColumns()];

      int i;
      
      for (i = 0; i < inputTableSpec.getNumColumns(); i++)
         specs[i] = inputTableSpec.getColumnSpec(i);
      
      if (_settings.appendColumn.getBooleanValue())
         specs[i] = _createNewColumnSpec(_settings.newColName.getStringValue(), _settings.structureType);
      
      return new DataTableSpec(specs);
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception
   {
      BufferedDataTable bufferedDataTable = inData[IndigoFeatureRemoverSettings.INPUT_PORT];
      _defineStructureType(bufferedDataTable.getDataTableSpec());
      
      DataTableSpec inputTableSpec = bufferedDataTable.getDataTableSpec();

      BufferedDataContainer outputContainer = exec.createDataContainer(getDataTableSpec(inputTableSpec));

      int colIdx = inputTableSpec.findColumnIndex(_settings.colName.getStringValue());

      if (colIdx == -1)
         throw new Exception("column not found");
      
      int rowNumber = 1;

      for (DataRow inputRow : bufferedDataTable)
      {
         IndigoObject target;
         DataCell cell = inputRow.getCell(colIdx); 
         
         if (cell.isMissing())
            target = null;
         else
         {
            target = ((IndigoDataCell)(inputRow.getCell(colIdx))).getIndigoObject();
   
            try
            {
               IndigoPlugin.lock();
               target = target.clone();
               String[] features = _settings.selectedFeatures.getStringArrayValue();
               if (features != null)
                  for (String s : features) {
                     Remover fRem = removers.get(s);
                     if(_settings.structureType.equals(STRUCTURE_TYPE.Reaction))
                        for(IndigoObject mol : target.iterateMolecules())
                           fRem.removeFeature(mol);
                     else
                        fRem.removeFeature(target);
                        
                  }
            }
            finally
            {
               IndigoPlugin.unlock();
            }
         }
         
         if (_settings.appendColumn.getBooleanValue())
         {
            DataCell[] cells = new DataCell[inputRow.getNumCells() + 1];
            int i;
            
            for (i = 0; i < inputRow.getNumCells(); i++)
               cells[i] = inputRow.getCell(i);
            if (target == null)
               cells[i] = cell;
            cells[i] = _createNewDataCell(target, _settings.structureType);
            outputContainer.addRowToTable(new DefaultRow(inputRow.getKey(), cells));
         }
         else
         {
            DataCell[] cells = new DataCell[inputRow.getNumCells()];
            int i;
            
            for (i = 0; i < inputRow.getNumCells(); i++)
            {
               if (i == colIdx)
               {
                  if (target == null)
                     cells[i] = cell;
                  else
                     cells[i] = _createNewDataCell(target, _settings.structureType);
               }
               else
                  cells[i] = inputRow.getCell(i);
            }
            outputContainer.addRowToTable(new DefaultRow(inputRow.getKey(), cells));
         }
         exec.checkCanceled();
         exec.setProgress(rowNumber / (double) bufferedDataTable.getRowCount(),
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
      DataTableSpec inSpec = inSpecs[IndigoFeatureRemoverSettings.INPUT_PORT];

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
      IndigoFeatureRemoverSettings s = new IndigoFeatureRemoverSettings();
      s.loadSettingsFrom(settings);

      if (s.colName.getStringValue() == null || s.colName.getStringValue().length() < 1)
         throw new InvalidSettingsException("column name must be specified");
      if (s.appendColumn.getBooleanValue() && (s.newColName.getStringValue() == null || s.newColName.getStringValue().length() < 1))
         throw new InvalidSettingsException("new column name must be specified");
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
}
