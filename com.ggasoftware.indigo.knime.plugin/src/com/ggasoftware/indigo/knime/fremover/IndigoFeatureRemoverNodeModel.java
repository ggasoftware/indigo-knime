package com.ggasoftware.indigo.knime.fremover;

import java.io.File;
import java.io.IOException;
import java.util.*;

import org.knime.core.data.*;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.*;

import com.ggasoftware.indigo.*;
import com.ggasoftware.indigo.knime.cell.IndigoMolCell;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.common.IndigoNodeModel;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

public class IndigoFeatureRemoverNodeModel extends IndigoNodeModel
{

   IndigoFeatureRemoverSettings _settings = new IndigoFeatureRemoverSettings();
   
   /**
    * Constructor for the node model.
    */
   protected IndigoFeatureRemoverNodeModel()
   {
      super(1, 1);
   }

   public static interface Remover
   {
      public IndigoObject removeFeature (IndigoObject io);
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
         public IndigoObject removeFeature (IndigoObject io)
         {
            for (IndigoObject atom : io.iterateAtoms())
               atom.resetIsotope();
            return io;
         }
      });
      addRemover("Chirality", new Remover ()
      {
         public IndigoObject removeFeature (IndigoObject io)
         {
            io.clearStereocenters();
            for (IndigoObject bond : io.iterateBonds())
               if (bond.bondOrder() == 1)
                  bond.resetStereo();
            return io;
         }
      });
      addRemover("Cis-trans", new Remover ()
      {
         public IndigoObject removeFeature (IndigoObject io)
         {
            io.clearCisTrans();
            return io;
         }
      });
      addRemover("Highlighting", new Remover ()
      {
         public IndigoObject removeFeature (IndigoObject io)
         {
            io.unhighlight();
            return io;
         }
      });
      addRemover("R-sites", new Remover ()
      {
         public IndigoObject removeFeature (IndigoObject io)
         {
            for (IndigoObject atom : io.iterateAtoms())
               if (atom.isRSite())
                  atom.remove();
            return io;
         }
      });
      addRemover("Pseudoatoms", new Remover ()
      {
         public IndigoObject removeFeature (IndigoObject io)
         {
            for (IndigoObject atom : io.iterateAtoms())
               if (atom.isPseudoatom())
                  atom.remove();
            return io;
         }
      });
      addRemover("Attachment points", new Remover ()
      {
         public IndigoObject removeFeature (IndigoObject io)
         {
            io.clearAttachmentPoints();
            return io;
         }
      });
      addRemover("Repeating units", new Remover ()
      {
         public IndigoObject removeFeature (IndigoObject io)
         {
            for (IndigoObject ru : io.iterateRepeatingUnits())
               ru.remove();
            return io;
         }
      });
      addRemover("Data S-groups", new Remover ()
      {
         public IndigoObject removeFeature (IndigoObject io)
         {
            for (IndigoObject sg : io.iterateDataSGroups())
               sg.remove();
            return io;
         }
      });
      addRemover("Minor components", new Remover ()
      {
         public IndigoObject removeFeature (IndigoObject io)
         {
            int max_comp = -1, max_comp_size = 0;
            
            for (IndigoObject comp: io.iterateComponents())
               if (comp.countAtoms() > max_comp_size)
               {
                  max_comp_size = comp.countAtoms();
                  max_comp = comp.index();
               }
            
            if (max_comp == -1)
               return io;
            return io.component(max_comp).clone();
         }
      });
   }
   
   protected DataTableSpec getDataTableSpec (DataTableSpec inputTableSpec) throws InvalidSettingsException
   {
      if (_settings.appendColumn)
         if (_settings.newColName == null || _settings.newColName.length() < 1)
            throw new InvalidSettingsException("New column name must be specified");
      
      DataColumnSpec[] specs;
      
      if (_settings.appendColumn)
         specs = new DataColumnSpec[inputTableSpec.getNumColumns() + 1];
      else
         specs = new DataColumnSpec[inputTableSpec.getNumColumns()];

      int i;
      
      for (i = 0; i < inputTableSpec.getNumColumns(); i++)
         specs[i] = inputTableSpec.getColumnSpec(i);
      
      if (_settings.appendColumn)
         specs[i] = new DataColumnSpecCreator(_settings.newColName, IndigoMolCell.TYPE).createSpec();
      
      return new DataTableSpec(specs);
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception
   {
      DataTableSpec inputTableSpec = inData[0].getDataTableSpec();

      BufferedDataContainer outputContainer = exec.createDataContainer(getDataTableSpec(inputTableSpec));

      int colIdx = inputTableSpec.findColumnIndex(_settings.colName);

      if (colIdx == -1)
         throw new Exception("column not found");
      
      CloseableRowIterator it = inData[0].iterator();
      int rowNumber = 1;

      while (it.hasNext())
      {
         DataRow inputRow = it.next();
         IndigoObject target;
         DataCell cell = inputRow.getCell(colIdx); 
         
         if (cell.isMissing())
            target = null;
         else
         {
            target = ((IndigoMolCell)(inputRow.getCell(colIdx))).getIndigoObject();
   
            try
            {
               IndigoPlugin.lock();
               target = target.clone();
               for (String s : _settings.selectedFeatures)
                  target = removers.get(s).removeFeature(target);
            }
            finally
            {
               IndigoPlugin.unlock();
            }
         }
         
         if (_settings.appendColumn)
         {
            DataCell[] cells = new DataCell[inputRow.getNumCells() + 1];
            int i;
            
            for (i = 0; i < inputRow.getNumCells(); i++)
               cells[i] = inputRow.getCell(i);
            if (target == null)
               cells[i] = cell;
            cells[i] = new IndigoMolCell(target);
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
                     cells[i] = new IndigoMolCell(target);
               }
               else
                  cells[i] = inputRow.getCell(i);
            }
            outputContainer.addRowToTable(new DefaultRow(inputRow.getKey(), cells));
         }
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
      IndigoFeatureRemoverSettings s = new IndigoFeatureRemoverSettings();
      s.loadSettings(settings);

      if (s.colName == null || s.colName.length() < 1)
         throw new InvalidSettingsException("column name must be specified");
      if (s.appendColumn && (s.newColName == null || s.newColName.length() < 1))
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
