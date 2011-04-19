package com.ggasoftware.indigo.knime;

import org.knime.core.node.*;

public class IndigoScaffoldFinderSettings
{
   public String colName;
   public String newColName;
   public boolean tryExactMethod = true;
   public int maxIterExact = -1;
   public int maxIterApprox = -1;

   /**
    * Loads the settings from the given node settings object.
    * 
    * @param settings
    *           node settings
    * @throws InvalidSettingsException
    *            if some settings are missing
    */
   public void loadSettings (final NodeSettingsRO settings) throws InvalidSettingsException
   {
      colName = settings.getString("colName");
      newColName = settings.getString("newColName");
      tryExactMethod = settings.getBoolean("tryExactMethod");
      maxIterExact = settings.getInt("maxIterExact");
      maxIterApprox = settings.getInt("maxIterApprox");
   }

   /**
    * Loads the settings from the given node settings object.
    * 
    * @param settings
    *           node settings
    */
   public void loadSettingsForDialog (final NodeSettingsRO settings)
   {
      colName = settings.getString("colName", null);
      newColName = settings.getString("newColName", "Scaffold");
      tryExactMethod = settings.getBoolean("tryExactMethod", true);
      maxIterExact = settings.getInt("maxIterExact", 50000);
      maxIterApprox = settings.getInt("maxIterApprox", 10000);
   }

   /**
    * Saves the settings to the given node settings object.
    * 
    * @param settings
    *           node settings
    */
   public void saveSettings (final NodeSettingsWO settings)
   {
      if (colName != null)
         settings.addString("colName", colName);
      if (newColName != null)
         settings.addString("newColName", newColName);
      settings.addBoolean("tryExactMethod", tryExactMethod);
      if (maxIterExact >= 0)
         settings.addInt("maxIterExact", maxIterExact);
      if (maxIterApprox >= 0)
         settings.addInt("maxIterApprox", maxIterApprox);
   }
}
