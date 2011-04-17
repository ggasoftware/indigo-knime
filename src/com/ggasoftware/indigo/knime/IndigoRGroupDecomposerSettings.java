package com.ggasoftware.indigo.knime;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

public class IndigoRGroupDecomposerSettings
{
   public String colName;
   public String colName2;
   public String newColPrefix;
   public boolean aromatize;

   public void loadSettings (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      colName = settings.getString("colName");
      colName2 = settings.getString("colName2");
      newColPrefix = settings.getString("newColPrefix");
      aromatize = settings.getBoolean("aromatize");
   }

   public void loadSettingsForDialog (final NodeSettingsRO settings)
   {
      colName = settings.getString("colName", null);
      colName2 = settings.getString("colName2", null);
      newColPrefix = settings.getString("newColPrefix", "R-Group #");
      aromatize = settings.getBoolean("aromatize", true);
   }

   public void saveSettings (final NodeSettingsWO settings)
   {
      settings.addString("colName", colName);
      settings.addString("colName2", colName2);
      settings.addString("newColPrefix", newColPrefix);
      settings.addBoolean("aromatize", aromatize);
   }
}
