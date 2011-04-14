package com.ggasoftware.indigo.knime;

import org.knime.core.node.*;

public class IndigoSubstructureMatchCounterSettings
{
   public enum Uniqueness
   {
      Atoms, Bonds, None
   }

   public String colName;
   public String queryFileName;
   public String newColName;
   Uniqueness uniqueness;
   public boolean loadFromFile;
   public String smarts;

   public void loadSettings (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      colName = settings.getString("colName");
      newColName = settings.getString("newColName");
      queryFileName = settings.getString("queryFileName");
      uniqueness = Uniqueness.valueOf(settings.getString("uniqueness"));
      loadFromFile = settings.getBoolean("loadFromFile");
      smarts = settings.getString("smarts");
   }

   public void loadSettingsForDialog (final NodeSettingsRO settings)
   {
      colName = settings.getString("colName", null);
      newColName = settings.getString("newColName", "Number of matches");
      queryFileName = settings.getString("queryFileName", null);
      uniqueness = Uniqueness.valueOf(settings.getString("uniqueness",
            Uniqueness.Atoms.name()));
      loadFromFile = settings.getBoolean("loadFromFile", false);
      smarts = settings.getString("smarts", "");
   }

   public void saveSettings (final NodeSettingsWO settings)
   {
      if (colName != null)
         settings.addString("colName", colName);
      if (newColName != null)
         settings.addString("newColName", newColName);
      if (queryFileName != null)
         settings.addString("queryFileName", queryFileName);
      if (uniqueness != null)
         settings.addString("uniqueness", uniqueness.name());
      settings.addBoolean("loadFromFile", loadFromFile);
      if (smarts != null)
         settings.addString("smarts", smarts);
   }
}
