package com.ggasoftware.indigo.knime.compjoin;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.node.defaultnodesettings.SettingsModelFilterString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.util.ColumnFilter;

import com.ggasoftware.indigo.knime.IndigoNodeSettings;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolValue;

public class IndigoComponentCombinerSettings extends IndigoNodeSettings {

   public static final int INPUT_PORT = 0;

   public final SettingsModelFilterString colNames = new SettingsModelFilterString(
         "colNames");
   public final SettingsModelString newColName = new SettingsModelString(
         "newColName", "Joined molecule");

   public final ColumnFilter columnFilter = new ColumnFilter() {
      @Override
      public boolean includeColumn(DataColumnSpec colSpec) {
         if(colSpec.getType().isCompatible(IndigoMolValue.class))
            return true;
         if(colSpec.getType().isCompatible(IndigoQueryMolValue.class))
            return true;
         return false;
      }
      @Override
      public String allFilteredMsg() {
         return "no 'IndigoMolValue' or 'IndigoQueryMolValue' was found";
      }
   };

   public IndigoComponentCombinerSettings() {
      addSettingsParameter(colNames);
      addSettingsParameter(newColName);
   }

}
