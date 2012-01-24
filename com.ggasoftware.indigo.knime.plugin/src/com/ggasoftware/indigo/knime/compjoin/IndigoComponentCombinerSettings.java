package com.ggasoftware.indigo.knime.compjoin;

import org.knime.core.node.defaultnodesettings.SettingsModelFilterString;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.ggasoftware.indigo.knime.IndigoNodeSettings;

public class IndigoComponentCombinerSettings extends IndigoNodeSettings {

   public static final int INPUT_PORT = 0;

   public final SettingsModelFilterString colNames = new SettingsModelFilterString(
         "colNames");
   public final SettingsModelString newColName = new SettingsModelString(
         "newColName", "Joined molecule");

   public IndigoComponentCombinerSettings() {
      addSettingsParameter(colNames);
      addSettingsParameter(newColName);
   }

}
