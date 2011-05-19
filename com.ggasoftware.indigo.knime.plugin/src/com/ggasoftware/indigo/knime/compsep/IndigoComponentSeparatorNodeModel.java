package com.ggasoftware.indigo.knime.compsep;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.knime.core.data.*;
import org.knime.core.data.container.CloseableRowIterator;
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
   private static final NodeLogger LOGGER = NodeLogger.getLogger(IndigoComponentSeparatorNodeModel.class);

   protected DataTableSpec calcDataTableSpec (DataTableSpec inSpec)
   {
      DataColumnSpec[] specs = new DataColumnSpec[inSpec.getNumColumns() + _settings.maxComponents ];

      int i;

      for (i = 0; i < inSpec.getNumColumns(); i++)
         specs[i] = inSpec.getColumnSpec(i);

      for (i = 1; i <= _settings.maxComponents; i++)
         specs[inSpec.getNumColumns() + i - 1] =
            new DataColumnSpecCreator(_settings.newColPrefix + i, IndigoMolCell.TYPE).createSpec();

      return new DataTableSpec(specs);
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
         final ExecutionContext exec) throws Exception
   {
      DataTableSpec spec = calcDataTableSpec(inData[0].getDataTableSpec());
      BufferedDataContainer outputContainer = exec.createDataContainer(spec);

      int colIdx = inData[0].getDataTableSpec().findColumnIndex(_settings.colName);
      if (colIdx == -1)
         throw new Exception("column not found");

      CloseableRowIterator it = inData[0].iterator();
      while (it.hasNext())
      {
         DataRow inputRow = it.next();
         RowKey key = inputRow.getKey();
         DataCell[] cells = new DataCell[inputRow.getNumCells() + _settings.maxComponents];
         int i;

         for (i = 0; i < inputRow.getNumCells(); i++)
            cells[i] = inputRow.getCell(i);

         for (i = 0; i < _settings.maxComponents; i++)
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
               {
                  if (i >= _settings.maxComponents)
                  {
                     LOGGER.warn("component index " + i + " is out of range for the given settings");
                     break;
                  }
                  cells[inputRow.getNumCells() + i] = new IndigoMolCell(collection.get(i));
               }
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
      _settings.colName = searchIndigoColumn(inSpecs[0], _settings.colName, IndigoMolValue.class);
      return new DataTableSpec[] { calcDataTableSpec(inSpecs[0]) };
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
   protected void validateSettings (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      IndigoComponentSeparatorSettings s = new IndigoComponentSeparatorSettings();
      s.loadSettings(settings);

      if (s.colName == null || s.colName.length() < 1)
         throw new InvalidSettingsException("column name must be specified");
      if (s.newColPrefix == null || s.newColPrefix.length() < 1)
         throw new InvalidSettingsException("prefix must be specified");
      if (s.maxComponents < 1)
         throw new InvalidSettingsException("R-Groups number should be greater than zero");
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
