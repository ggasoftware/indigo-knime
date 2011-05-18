package com.ggasoftware.indigo.knime.fremover;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

public class IndigoFeatureRemoverSettings
{
   public String colName;
   public boolean appendColumn = false;
   public String newColName;
   public String[] selectedFeatures;

   public void loadSettings (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      colName = settings.getString("colName");
      newColName = settings.getString("newColName", null);
      appendColumn = settings.getBoolean("appendColumn");
      selectedFeatures = settings.getStringArray("selectedFeatures");
   }

   public void loadSettingsForDialog (final NodeSettingsRO settings)
   {
      colName = settings.getString("colName", null);
      newColName = settings.getString("newColName", null);
      appendColumn = settings.getBoolean("appendColumn", false);
      selectedFeatures = settings.getStringArray("selectedFeatures", new String[]{});
   }

   public void saveSettings (final NodeSettingsWO settings)
   {
      if (colName != null)
         settings.addString("colName", colName);
      if (newColName != null)
         settings.addString("newColName", newColName);
      settings.addBoolean("appendColumn", appendColumn);
      if (selectedFeatures != null)
         settings.addStringArray("selectedFeatures", selectedFeatures);
   }
}
