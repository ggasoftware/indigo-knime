package com.ggasoftware.indigo.knime;

import org.knime.core.node.*;

public class IndigoMoleculeFingerprinterSettings
{
   public int fpSizeQWords;
   public String colName;
   public String newColName;

   /**
    * Loads the settings from the given node settings object.
    * 
    * @param settings
    *           node settings
    * @throws InvalidSettingsException
    *            if some settings are missing
    */
   public void loadSettings (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      fpSizeQWords = settings.getInt("fpSizeQWords");
      colName = settings.getString("colName");
      newColName = settings.getString("newColName");
   }

   /**
    * Loads the settings from the given node settings object.
    * 
    * @param settings
    *           node settings
    * @throws InvalidSettingsException
    */
   public void loadSettingsForDialog (final NodeSettingsRO settings)
   {
      fpSizeQWords = settings.getInt("fpSizeQWords", 8);
      colName = settings.getString("colName", null);
      newColName = settings.getString("newColName", null);
   }

   /**
    * Saves the settings to the given node settings object.
    * 
    * @param settings
    *           node settings
    */
   public void saveSettings (final NodeSettingsWO settings)
   {
      settings.addInt("fpSizeQWords", fpSizeQWords);
      settings.addString("colName", colName);
      settings.addString("newColName", newColName);
   }
}
