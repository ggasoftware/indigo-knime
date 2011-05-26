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

package com.ggasoftware.indigo.knime.molprop;

import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.data.container.*;
import org.knime.core.data.def.*;
import org.knime.core.node.*;

import com.ggasoftware.indigo.*;
import com.ggasoftware.indigo.knime.cell.IndigoMolCell;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.common.IndigoNodeModel;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

import java.io.*;
import java.util.*;

public class IndigoMoleculePropertiesNodeModel extends IndigoNodeModel
{

   private final IndigoMoleculePropertiesSettings _settings = new IndigoMoleculePropertiesSettings();

   public static interface PropertyCalculator
   {
      DataType type ();

      DataCell calculate (IndigoObject io);
   }

   public static abstract class IntPropertyCalculator implements
         PropertyCalculator
   {
      public DataType type ()
      {
         return IntCell.TYPE;
      }
   }

   public static abstract class DoublePropertyCalculator implements
         PropertyCalculator
   {
      public DataType type ()
      {
         return DoubleCell.TYPE;
      }
   }

   public static abstract class StringPropertyCalculator implements
         PropertyCalculator
   {
      public DataType type ()
      {
         return StringCell.TYPE;
      }
   }

   public static final ArrayList<String> names = new ArrayList<String>();
   public static final Map<String, PropertyCalculator> calculators = new HashMap<String, PropertyCalculator>();
   public static final Map<String, DataColumnSpec> colSpecs = new HashMap<String, DataColumnSpec>();
   public static DataColumnSpec[] colSpecsArray;  

   private static void putCalculator (String name, PropertyCalculator pc)
   {
      calculators.put(name, pc);
      names.add(name);
   }
   
   static
   {
      putCalculator("Molecular formula", new StringPropertyCalculator()
      {
         public DataCell calculate (IndigoObject io)
         {
            return new StringCell(io.grossFormula());
         }
      });
      
      putCalculator("Molecular weight", new DoublePropertyCalculator()
      {
         public DataCell calculate (IndigoObject io)
         {
            return new DoubleCell(io.molecularWeight());
         }
      });

      putCalculator("Most abundant mass", new DoublePropertyCalculator()
      {
         public DataCell calculate (IndigoObject io)
         {
            return new DoubleCell(io.mostAbundantMass());
         }
      });

      putCalculator("Monoisotopic mass", new DoublePropertyCalculator()
      {
         public DataCell calculate (IndigoObject io)
         {
            return new DoubleCell(io.monoisotopicMass());
         }
      });

      putCalculator("Number of connected components",
            new IntPropertyCalculator()
            {
               public DataCell calculate (IndigoObject io)
               {
                  return new IntCell(io.countComponents());
               }
            });

      
      putCalculator("Total number of atoms", new IntPropertyCalculator()
      {
         public DataCell calculate (IndigoObject io)
         {
            return new IntCell(io.countHeavyAtoms() + io.countHydrogens());
         }
      });

      putCalculator("Number of hydrogens", new IntPropertyCalculator ()
      {
         public DataCell calculate (IndigoObject io)
         {
            return new IntCell(io.countHydrogens());
         }
      });
      
      putCalculator("Number of heavy atoms", new IntPropertyCalculator()
      {
         public DataCell calculate (IndigoObject io)
         {
            return new IntCell(io.countHeavyAtoms());
         }
      });

      putCalculator("Number of heteroatoms", new IntPropertyCalculator ()
      {
         public DataCell calculate (IndigoObject io)
         {
            int count = 0;
            
            for (IndigoObject atom : io.iterateAtoms())
            {
               if (atom.isRSite() || atom.isPseudoatom())
                  continue;
               
               int number = atom.atomicNumber();
               
               if (number > 1 && number != 6)
                  count++;
            }
               
            return new IntCell(count);
         }
      });
      

      putCalculator("Number of carbons", new IntPropertyCalculator ()
      {
         public DataCell calculate (IndigoObject io)
         {
            int count = 0;
            
            for (IndigoObject atom : io.iterateAtoms())
            {
               if (atom.isRSite() || atom.isPseudoatom())
                  continue;
               
               int number = atom.atomicNumber();
               
               if (number == 6)
                  count++;
            }
               
            return new IntCell(count);
         }
      });      
      
      putCalculator("Number of aromatic atoms", new IntPropertyCalculator()
      {
         public DataCell calculate (IndigoObject io)
         {
            int count = 0;

            io = io.clone();
            io.aromatize();
            for (IndigoObject atom : io.iterateAtoms())
               for (IndigoObject nei : atom.iterateNeighbors())
                  if (nei.bond().bondOrder() == 4)
                  {
                     count++;
                     break;
                  }
            return new IntCell(count);
         }
      });

      putCalculator("Number of aliphatic atoms", new IntPropertyCalculator()
      {
         public DataCell calculate (IndigoObject io)
         {
            int count = 0;

            io = io.clone();
            io.aromatize();
            for (IndigoObject atom : io.iterateAtoms())
            {
               boolean aromatic = false;
               
               for (IndigoObject nei : atom.iterateNeighbors())
                  if (nei.bond().bondOrder() == 4)
                  {
                     aromatic = true;
                     break;
                  }
               if (!aromatic)
                  count++;
            }
            return new IntCell(count);
         }
      });
      
      putCalculator("Number of pseudoatoms", new IntPropertyCalculator()
      {
         public DataCell calculate (IndigoObject io)
         {
            return new IntCell(io.countPseudoatoms());
         }
      });

      
      putCalculator("Number of visible atoms", new IntPropertyCalculator()
      {
         public DataCell calculate (IndigoObject io)
         {
            return new IntCell(io.countAtoms());
         }
      });

      putCalculator("Number of chiral centers", new IntPropertyCalculator()
      {
         public DataCell calculate (IndigoObject io)
         {
            return new IntCell(io.countStereocenters());
         }
      });
      
      putCalculator("Number of R-sites", new IntPropertyCalculator()
      {
         public DataCell calculate (IndigoObject io)
         {
            return new IntCell(io.countRSites());
         }
      });
      
      putCalculator("Number of bonds", new IntPropertyCalculator()
      {
         public DataCell calculate (IndigoObject io)
         {
            return new IntCell(io.countBonds());
         }
      });

      putCalculator("Number of aromatic bonds", new IntPropertyCalculator()
      {
         public DataCell calculate (IndigoObject io)
         {
            int count = 0;

            io = io.clone();
            io.aromatize();
            for (IndigoObject bond : io.iterateBonds())
               if (bond.bondOrder() == 4)
                  count++;
            return new IntCell(count);
         }
      });

      putCalculator("Number of aliphatic bonds", new IntPropertyCalculator()
      {
         public DataCell calculate (IndigoObject io)
         {
            int count = 0;

            io = io.clone();
            io.aromatize();
            for (IndigoObject bond : io.iterateBonds())
               if (bond.bondOrder() != 4)
                  count++;
            return new IntCell(count);
         }
      });

      putCalculator("Number of cis/trans bonds", new IntPropertyCalculator()
      {
         public DataCell calculate (IndigoObject io)
         {
            int count = 0;

            for (IndigoObject bond : io.iterateBonds())
            {
               int stereo = bond.bondStereo();
               if (stereo == Indigo.CIS || stereo == Indigo.TRANS)
                  count++;
            }
            return new IntCell(count);
         }
      });
      
      putCalculator("Number of aromatic rings", new IntPropertyCalculator()
      {
         public DataCell calculate (IndigoObject io)
         {
            int count = 0;
            
            io = io.clone();
            io.aromatize();
            
            for (IndigoObject ring : io.iterateSSSR())
            {
               boolean arom = true;
               
               for (IndigoObject bond : ring.iterateBonds())
                  if (bond.bondOrder() != 4)
                     arom = false;
               
               if (arom)
                  count++;
            }
            
            return new IntCell(count);
         }
      });
      
      putCalculator("Number of aliphatic rings", new IntPropertyCalculator()
      {
         public DataCell calculate (IndigoObject io)
         {
            int count = 0;
            
            io = io.clone();
            io.aromatize();
            
            for (IndigoObject ring : io.iterateSSSR())
            {
               for (IndigoObject bond : ring.iterateBonds())
                  if (bond.bondOrder() != 4)
                  {
                     count++;
                     break;
                  }
            }
            
            return new IntCell(count);
         }
      });
    
      colSpecsArray = new DataColumnSpec[names.size()];
      
      for (int i = 0; i < names.size(); i++)
      {
         String key = names.get(i);
         DataType type = ((PropertyCalculator) calculators.get(key)).type();
         DataColumnSpec spec = new DataColumnSpecCreator(key, type).createSpec(); 
         colSpecs.put(key, spec);
         colSpecsArray[i] = spec;
      }
   }

   /**
    * Constructor for the node model.
    */
   protected IndigoMoleculePropertiesNodeModel()
   {
      super(1, 1);
   }

   protected DataTableSpec getDataTableSpec (DataTableSpec inSpec) throws InvalidSettingsException
   {
      if (_settings.selectedProps == null)
         throw new InvalidSettingsException("properties not selected");
      
      DataColumnSpec[] specs = new DataColumnSpec[inSpec.getNumColumns()
            + _settings.selectedProps.length];

      int i;

      for (i = 0; i < inSpec.getNumColumns(); i++)
         specs[i] = inSpec.getColumnSpec(i);

      for (String key : _settings.selectedProps)
         specs[i++] = colSpecs.get(key);

      return new DataTableSpec(specs);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute (final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception
   {
      DataTableSpec spec = getDataTableSpec(inData[0].getDataTableSpec());

      BufferedDataContainer outputContainer = exec.createDataContainer(spec);

      int colIdx = inData[0].getDataTableSpec().findColumnIndex(_settings.colName);

      if (colIdx == -1)
         throw new Exception("column not found");

      CloseableRowIterator it = inData[0].iterator();
      int rowNumber = 1;

      while (it.hasNext())
      {
         DataRow inputRow = it.next();
         RowKey key = inputRow.getKey();
         DataCell[] cells = new DataCell[inputRow.getNumCells()
               + _settings.selectedProps.length];
         IndigoObject io = ((IndigoMolCell) (inputRow.getCell(colIdx)))
               .getIndigoObject();
         int i;

         for (i = 0; i < inputRow.getNumCells(); i++)
            cells[i] = inputRow.getCell(i);

         try
         {
            IndigoPlugin.lock();
            for (String prop : _settings.selectedProps)
               cells[i++] = calculators.get(prop).calculate(io);
         }
         finally
         {
            IndigoPlugin.unlock();
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
      _settings.colName = searchIndigoColumn(inSpecs[0], _settings.colName, IndigoMolValue.class);
      return new DataTableSpec[] { getDataTableSpec(inSpecs[0]) };
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
      IndigoMoleculePropertiesSettings s = new IndigoMoleculePropertiesSettings();
      s.loadSettings(settings);
      if (s.colName == null || s.colName.length() < 1)
         throw new InvalidSettingsException("column name must be specified");
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
