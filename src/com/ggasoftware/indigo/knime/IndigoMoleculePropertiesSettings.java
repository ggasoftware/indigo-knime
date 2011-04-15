package com.ggasoftware.indigo.knime;

import org.knime.core.node.*;

public class IndigoMoleculePropertiesSettings
{
   public String colName;
   public String[] selectedProps;

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
      colName = settings.getString("colName");
      selectedProps = settings.getStringArray("selectedProps");
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
      colName = settings.getString("colName", null);
      selectedProps = settings.getStringArray("selectedProps", new String[]{});
   }

   /**
    * Saves the settings to the given node settings object.
    * 
    * @param settings
    *           node settings
    */
   public void saveSettings (final NodeSettingsWO settings)
   {
      settings.addString("colName", colName);
      settings.addStringArray("selectedProps", selectedProps);
   }
}
