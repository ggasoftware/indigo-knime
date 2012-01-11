package com.ggasoftware.indigo.knime.common;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;

public abstract class IndigoNodeModel extends NodeModel
{
   protected IndigoNodeModel (int nrInDataPorts, int nrOutDataPorts)
   {
      super(nrInDataPorts, nrOutDataPorts);
   }

   protected String searchIndigoColumn (DataTableSpec spec, String colName, Class<? extends DataValue> cls)
   throws InvalidSettingsException
   {
      if (colName == null)
      {
         for (DataColumnSpec cs : spec)
         {
            if (cs.getType().isCompatible(cls))
            {
               if (colName != null)
               {
                  setWarningMessage("Selected default column '" + colName + "' as " + cls.getName());
                  break;
               }
               else
                  colName = cs.getName();
            }
         }
         if (colName == null)
            throw new InvalidSettingsException("No " + cls.getName() + " found in the input table");
      }
      else
      {
         if (!spec.containsName(colName))
            throw new InvalidSettingsException("Column '" + colName + "' does not exist in input table");
         if (!spec.getColumnSpec(colName).getType().isCompatible(cls))
            throw new InvalidSettingsException("Column '" + colName + "' is not a " + cls.getName());
      }
      return colName;
   }

   protected void searchMixedIndigoColumn(DataTableSpec spec, SettingsModelColumnName colNameModel, Class<? extends DataValue> class1,
         Class<? extends DataValue> class2) throws InvalidSettingsException {
      
      String colName = colNameModel.getStringValue();
      if(colName == null || colName.length() < 1)
         colName = null;
      
      if (colName == null) {
         for (DataColumnSpec cs : spec) {
            if (cs.getType().isCompatible(class1)) {
               colName = cs.getName();
               setWarningMessage("Indigo column autoselect: selected column '" + colName + "' as " + class1.getName());
               colNameModel.setStringValue(colName);
               break;
            } else if(cs.getType().isCompatible(class2)) {
               colName = cs.getName();
               setWarningMessage("Indigo column autoselect: selected column '" + colName + "' as " + class2.getName());
               colNameModel.setStringValue(colName);
               break;
            }
         }
         if (colName == null)
            throw new InvalidSettingsException("No " + class1.getName() + " or " + class2.getName() + " found in the input table");
      } else {
         if (!spec.containsName(colName))
            throw new InvalidSettingsException("Column '" + colName + "' does not exist in input table");
         
         if (! (spec.getColumnSpec(colName).getType().isCompatible(class1) || spec.getColumnSpec(colName).getType().isCompatible(class2)))
            throw new InvalidSettingsException("Column '" + colName + "' is not a " + class1.getName() + " or " + class2.getName());
      }
   }
}
