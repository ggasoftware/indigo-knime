package com.ggasoftware.indigo.knime.compsep;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

public class IndigoComponentSeparatorSettings
{
   public String colName;
   public String newColPrefix = "Component #";

   public void loadSettings(final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      colName = settings.getString("colName");
      newColPrefix = settings.getString("newColPrefix");
   }

   public void loadSettingsForDialog(final NodeSettingsRO settings)
   {
      colName = settings.getString("colName", null);
      newColPrefix = settings.getString("newColPrefix", "Component #");
   }

   public void saveSettings(final NodeSettingsWO settings)
   {
      if (colName != null)
         settings.addString("colName", colName);
      if (newColPrefix != null)
         settings.addString("newColPrefix", newColPrefix);
   }
}
