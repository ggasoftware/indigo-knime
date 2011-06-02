package com.ggasoftware.indigo.knime.murcko;

import org.knime.core.node.*;

public class IndigoMurckoScaffoldSettings
{
   public String colName = null;
   public boolean appendColumn = false;
   public String newColName;
   public boolean removeTerminalRings3 = false;
   public boolean removeTerminalRings4 = false;

   public void loadSettings(final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      colName = settings.getString("colName");
      appendColumn = settings.getBoolean("appendColumn");
      newColName = settings.getString("newColName");
      removeTerminalRings3 = settings.getBoolean("removeTerminalRings3");
      removeTerminalRings4 = settings.getBoolean("removeTerminalRings4");
   }
   
   public void loadSettingsForDialog (final NodeSettingsRO settings)
   {
      colName = settings.getString("colName", null);
      appendColumn = settings.getBoolean("appendColumn", false);
      newColName = settings.getString("newColName", null);
      removeTerminalRings3 = settings.getBoolean("removeTerminalRings3", false);
      removeTerminalRings4 = settings.getBoolean("removeTerminalRings4", false);
   }
   
   public void saveSettings (final NodeSettingsWO settings)
   {
      if (colName != null)
         settings.addString("colName", colName);
      settings.addBoolean("appendColumn", appendColumn);
      if (newColName != null)
         settings.addString("newColName", newColName);
      settings.addBoolean("removeTerminalRings3", removeTerminalRings3);
      settings.addBoolean("removeTerminalRings4", removeTerminalRings4);
   }
}
