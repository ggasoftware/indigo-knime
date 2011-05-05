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

package com.ggasoftware.indigo.knime.fpsim;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.knime.core.data.*;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.*;
import org.knime.core.data.vector.bitvector.*;
import org.knime.core.node.*;

import com.ggasoftware.indigo.knime.common.IndigoNodeModel;
import com.ggasoftware.indigo.knime.fpsim.IndigoFingerprintSimilaritySettings.Aggregation;
import com.ggasoftware.indigo.knime.fpsim.IndigoFingerprintSimilaritySettings.Metric;

public class IndigoFingerprintSimilarityNodeModel extends IndigoNodeModel
{
   IndigoFingerprintSimilaritySettings _settings = new IndigoFingerprintSimilaritySettings();

   /**
    * Constructor for the node model.
    */
   protected IndigoFingerprintSimilarityNodeModel()
   {
      super(2, 1);
   }

   protected DataTableSpec getDataTableSpec (DataTableSpec inSpec) throws InvalidSettingsException
   {
      DataColumnSpec[] specs = new DataColumnSpec[inSpec.getNumColumns() + 1];

      if (_settings.newColName == null || _settings.newColName.length() < 1)
         throw new InvalidSettingsException("No new column name specified");
      
      int i;

      for (i = 0; i < inSpec.getNumColumns(); i++)
         specs[i] = inSpec.getColumnSpec(i);

      specs[i] = new DataColumnSpecCreator(_settings.newColName,
            DoubleCell.TYPE).createSpec();

      return new DataTableSpec(specs);
   }

   protected float _calcSimilarity (BitVectorValue bitvector, BitVectorValue template) throws Exception
   {
      if (bitvector.length() != template.length())
         throw new Exception("fingerprint's length does not match the template");
      
      long a = template.cardinality();
      long b = bitvector.cardinality();
      long c = 0;
      
      long ia = template.nextSetBit(0), ib = bitvector.nextSetBit(0);
      
      while (ia != -1 && ib != -1)
      {
         if (ia < ib)
            ia = template.nextSetBit(ia + 1);
         else if (ia > ib)
            ib = bitvector.nextSetBit(ib + 1);
         else
         {
            c++;
            ia = template.nextSetBit(ia + 1);
            ib = bitvector.nextSetBit(ib + 1);
         }
      }
      
      if (c == 0)
         return 0;
      else if (_settings.metric == Metric.Tanimoto)
         return (float)c / (a + b - c);
      else if (_settings.metric == Metric.EuclidSub)
         return (float)c / a;
      else
         return (float)c / (_settings.tverskyAlpha * (a - c) +
                            _settings.tverskyBeta  * (b - c) + c); 
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute (final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception
   {
      DataTableSpec spec = getDataTableSpec(inData[0].getDataTableSpec());
      DataTableSpec spec2 = getDataTableSpec(inData[1].getDataTableSpec());

      BufferedDataContainer outputContainer = exec.createDataContainer(spec);

      int colIdx = spec.findColumnIndex(_settings.colName);
      if (colIdx == -1)
         throw new Exception("column not found");

      int colIdx2 = spec2.findColumnIndex(_settings.colName2);
      if (colIdx2 == -1)
         throw new Exception("second column not found");

      ArrayList<BitVectorValue> templates = new ArrayList<BitVectorValue>(); 
      
      {
         CloseableRowIterator it = inData[1].iterator();
         
         if (!it.hasNext())
            throw new Exception("no template fingerprint found in the data source");

         while (it.hasNext())
         {
            DataRow row = it.next();
            templates.add((BitVectorValue)row.getCell(colIdx2));
         }
      }
      
      CloseableRowIterator it = inData[0].iterator();
      int rowNumber = 1;

      while (it.hasNext())
      {
         DataRow inputRow = it.next();
         RowKey key = inputRow.getKey();
         DataCell[] cells = new DataCell[inputRow.getNumCells() + 1];
         BitVectorValue bitvector = (BitVectorValue)inputRow.getCell(colIdx);

         float result = 0;
         int count = 0;
         
         if (_settings.aggregation == Aggregation.Minimum)
            result = 1000000;
         
         for (BitVectorValue template : templates)
         {
            float sim = _calcSimilarity(bitvector, template);
            
            switch (_settings.aggregation)
            {
               case Minimum:
                  if (sim < result)
                     result = sim;
                  break;
               case Maximum:
                  if (sim > result)
                     result = sim;
                  break;
               case Average:
                  result += sim;
            }
            count++;
         }

         if (_settings.aggregation == Aggregation.Average)
            result /= count;
         
         int i;
         
         for (i = 0; i < inputRow.getNumCells(); i++)
            cells[i] = inputRow.getCell(i);
         cells[i++] = new DoubleCell(result);

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
      _settings.colName = searchIndigoColumn(inSpecs[0], _settings.colName, BitVectorValue.class);
      _settings.colName2 = searchIndigoColumn(inSpecs[1], _settings.colName2, BitVectorValue.class);
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
      _settings.loadSettingsForDialog(settings);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void validateSettings (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      IndigoFingerprintSimilaritySettings s = new IndigoFingerprintSimilaritySettings();
      s.loadSettings(settings);
      if (s.colName == null || s.colName.length() < 1)
         throw new InvalidSettingsException("column name must be specified");
      if (s.colName2 == null || s.colName2.length() < 1)
         throw new InvalidSettingsException("template column name must be specified");
      if (s.newColName == null || s.newColName.length() < 1)
         throw new InvalidSettingsException("new column name must be specified");
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
