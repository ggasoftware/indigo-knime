package com.ggasoftware.indigo.knime;

import org.knime.core.node.*;

public class IndigoSubstructureMatcherSettings
{
   public String colName;
   public String colName2;

   public void loadSettings (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      colName = settings.getString("colName");
      colName2 = settings.getString("colName2");
   }

   public void loadSettingsForDialog (final NodeSettingsRO settings)
   {
      colName = settings.getString("colName", null);
      colName2 = settings.getString("colName2", null);
   }

   public void saveSettings (final NodeSettingsWO settings)
   {
      settings.addString("colName", colName);
      settings.addString("colName2", colName);
   }
}
