package com.ggasoftware.indigo.knime.murcko;

import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.ggasoftware.indigo.knime.common.IndigoNodeSettings;

public class IndigoMurckoScaffoldSettings extends IndigoNodeSettings
{
   public static final int INPUT_PORT = 0;
   
   public final SettingsModelString colName = new SettingsModelString("colName", null);
   public final SettingsModelBoolean appendColumn = new SettingsModelBoolean("appendColumn", false);
   public final SettingsModelString newColName = new SettingsModelString("newColName", null);
   public final SettingsModelBoolean removeTerminalRings3 = new SettingsModelBoolean("removeTerminalRings3", false);
   public final SettingsModelBoolean removeTerminalRings4 = new SettingsModelBoolean("removeTerminalRings4", false);
   
   public IndigoMurckoScaffoldSettings() {
      addSettingsParameter(colName);
      addSettingsParameter(appendColumn);
      addSettingsParameter(newColName);
      addSettingsParameter(removeTerminalRings3);
      addSettingsParameter(removeTerminalRings4);
   }

}
