package com.ggasoftware.indigo.knime;

import org.knime.core.node.*;

public class IndigoValenceCheckerSettings
{
   public String colName = "Molecule";

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
   }
}
