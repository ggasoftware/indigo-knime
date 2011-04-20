package com.ggasoftware.indigo.knime;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;

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
                  setWarningMessage("Selected column '" + colName + "' as " + cls.getName());
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
}
