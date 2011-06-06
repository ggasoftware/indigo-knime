package com.ggasoftware.indigo.knime.areplacer;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

public class IndigoAtomReplacerSettings
{
   public String colName;
   public boolean replaceColumn = true;
   public String newColName;
   public String newAtomLabel = "*";

   public void loadSettings(final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      colName = settings.getString("colName");
      replaceColumn = settings.getBoolean("replaceColumn");
      newColName = settings.getString("newColName");
      newAtomLabel = settings.getString("newAtomLabel");
   }

   public void loadSettingsForDialog(final NodeSettingsRO settings)
   {
      colName = settings.getString("colName", null);
      replaceColumn = settings.getBoolean("replaceColumn", true);
      newColName = settings.getString("newColName", null);
      newAtomLabel = settings.getString("newAtomLabel", "*");
   }

   public void saveSettings(final NodeSettingsWO settings)
   {
      if (colName != null)
         settings.addString("colName", colName);
      settings.addBoolean("replaceColumn", replaceColumn);
      if (newColName != null)
         settings.addString("newColName", newColName);
      if (newAtomLabel != null)
         settings.addString("newAtomLabel", newAtomLabel);
   }
}
