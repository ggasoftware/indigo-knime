package com.ggasoftware.indigo.knime.compsep;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.knime.core.data.*;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.*;

import com.ggasoftware.indigo.IndigoObject;
import com.ggasoftware.indigo.knime.cell.*;
import com.ggasoftware.indigo.knime.common.IndigoNodeModel;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

public class IndigoComponentSeparatorNodeModel extends IndigoNodeModel
{

   protected IndigoComponentSeparatorNodeModel()
   {
      super(1, 1);
   }
   
   private final IndigoComponentSeparatorSettings _settings = new IndigoComponentSeparatorSettings();

   protected DataTableSpec calcDataTableSpec (DataTableSpec inSpec, int ncolumns)
   {
      DataColumnSpec[] specs = new DataColumnSpec[inSpec.getNumColumns() + ncolumns];

      int i;

      for (i = 0; i < inSpec.getNumColumns(); i++)
         specs[i] = inSpec.getColumnSpec(i);

      for (i = 1; i <= ncolumns; i++)
         specs[inSpec.getNumColumns() + i - 1] =
            new DataColumnSpecCreator(_settings.newColPrefix.getStringValue() + i, IndigoMolCell.TYPE).createSpec();

      return new DataTableSpec(specs);
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception
   {
      int colIdx = inData[0].getDataTableSpec().findColumnIndex(_settings.colName.getStringValue());
      if (colIdx == -1)
         throw new Exception("column not found");

      int maxcomp = 0;
      
      for (DataRow inputRow : inData[0])
      {
         DataCell cell = inputRow.getCell(colIdx);
         
         if (!cell.isMissing())
         {
            try
            {
               IndigoPlugin.lock();
            
               IndigoObject target = ((IndigoMolCell)(inputRow.getCell(colIdx))).getIndigoObject();
               int ncomp = target.countComponents();
               
               if (maxcomp < ncomp)
                  maxcomp = ncomp;
            }
            finally
            {
               IndigoPlugin.unlock();
            }
         }
         
      }
      
      DataTableSpec spec = calcDataTableSpec(inData[0].getDataTableSpec(), maxcomp);
      BufferedDataContainer outputContainer = exec.createDataContainer(spec);

      for (DataRow inputRow : inData[0])
      {
         RowKey key = inputRow.getKey();
         DataCell[] cells = new DataCell[inputRow.getNumCells() + maxcomp];
         int i;

         for (i = 0; i < inputRow.getNumCells(); i++)
            cells[i] = inputRow.getCell(i);

         for (i = 0; i < maxcomp; i++)
            cells[inputRow.getNumCells() + i] =  DataType.getMissingCell();
         
         DataCell cell = inputRow.getCell(colIdx);
         
         if (!cell.isMissing())
         {
            IndigoObject target = ((IndigoMolCell)(inputRow.getCell(colIdx))).getIndigoObject();
            try
            {
               IndigoPlugin.lock();
               
               ArrayList<IndigoObject> collection = new ArrayList<IndigoObject>();
               
               for (IndigoObject comp : target.iterateComponents())
                  collection.add(comp.clone());
               
               Collections.sort(collection, new Comparator<IndigoObject>()
               {
                  @Override
                  public int compare(IndigoObject io1, IndigoObject io2)
                  {
                      return io2.countAtoms() - io1.countAtoms();
                  }
               });
               
               for (i = 0; i < collection.size(); i++)
                  cells[inputRow.getNumCells() + i] = new IndigoMolCell(collection.get(i));
            }
            finally
            {
               IndigoPlugin.unlock();
            }
         }
         
         outputContainer.addRowToTable(new DefaultRow(key, cells));
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
      _settings.colName.setStringValue(searchIndigoColumn(inSpecs[0], _settings.colName.getStringValue(), IndigoMolValue.class));
      return new DataTableSpec[1];
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
   protected void validateSettings (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      IndigoComponentSeparatorSettings s = new IndigoComponentSeparatorSettings();
      s.loadSettingsFrom(settings);

      if (s.colName.getStringValue() == null || s.colName.getStringValue().length() < 1)
         throw new InvalidSettingsException("column name must be specified");
      if (s.newColPrefix.getStringValue() == null || s.newColPrefix.getStringValue().length() < 1)
         throw new InvalidSettingsException("prefix must be specified");
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
