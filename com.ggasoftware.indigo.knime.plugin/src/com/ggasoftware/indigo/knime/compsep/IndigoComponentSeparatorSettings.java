package com.ggasoftware.indigo.knime.compsep;

import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.ggasoftware.indigo.knime.IndigoNodeSettings;

public class IndigoComponentSeparatorSettings extends IndigoNodeSettings
{
   public static final int INPUT_PORT = 0;
   
   public SettingsModelColumnName colName = new SettingsModelColumnName("colName", null);
   public SettingsModelString newColPrefix = new SettingsModelString("newColPrefix", "Component #");
   
   public IndigoComponentSeparatorSettings() {
      addSettingsParameter(colName);
      addSettingsParameter(newColPrefix);
   }
}
