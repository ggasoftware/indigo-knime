package com.ggasoftware.indigo.knime.compsep;

import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.ggasoftware.indigo.knime.common.IndigoNodeSettings;

public class IndigoComponentSeparatorSettings extends IndigoNodeSettings
{
   public static final int INPUT_PORT = 0;
   
   public final SettingsModelString colName = new SettingsModelString("colName", null);
   public final SettingsModelString newColPrefix = new SettingsModelString("newColPrefix", "Component #");
   public final SettingsModelBoolean limitComponentNumber = new SettingsModelBoolean("limitComponentNumber", false);
   public final SettingsModelIntegerBounded componentNumber = new SettingsModelIntegerBounded("componentNumber", 1, 0, Integer.MAX_VALUE);
   
   public IndigoComponentSeparatorSettings() {
      addSettingsParameter(colName);
      addSettingsParameter(newColPrefix);
      addSettingsParameter(limitComponentNumber);
      addSettingsParameter(componentNumber);
   }
}
