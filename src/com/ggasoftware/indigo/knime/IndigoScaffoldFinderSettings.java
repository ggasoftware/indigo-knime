package com.ggasoftware.indigo.knime;

import org.knime.core.node.*;

public class IndigoScaffoldFinderSettings
{
   public enum Method
   {
      Exact, Approximate
   }
   
   public String colName;
   public String newColName;
   public Method method = Method.Exact;

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
      method = Method.valueOf(settings.getString("method"));
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
      method = Method.valueOf(settings.getString("method", Method.Exact.name()));
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
      settings.addString("newColName", newColName);
      settings.addString("method", method.name());
   }
}
