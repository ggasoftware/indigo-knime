package com.ggasoftware.indigo.knime;

import org.knime.core.node.*;

public class IndigoSubstructureMatchCounterSettings
{
   public enum Uniqueness
   {
      Atoms, Bonds, None
   }

   public String colName;
   public String colName2;
   public String newColName;
   Uniqueness uniqueness;

   public void loadSettings (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      colName = settings.getString("colName");
      colName2 = settings.getString("colName2");
      newColName = settings.getString("newColName");
      uniqueness = Uniqueness.valueOf(settings.getString("uniqueness"));
   }

   public void loadSettingsForDialog (final NodeSettingsRO settings)
   {
      colName = settings.getString("colName", null);
      colName2 = settings.getString("colName2", null);
      newColName = settings.getString("newColName", "Number of matches");
      uniqueness = Uniqueness.valueOf(settings.getString("uniqueness", Uniqueness.Atoms.name()));
   }

   public void saveSettings (final NodeSettingsWO settings)
   {
      if (colName != null)
         settings.addString("colName", colName);
      if (colName2 != null)
         settings.addString("colName2", colName2);
      if (newColName != null)
         settings.addString("newColName", newColName);
      if (uniqueness != null)
         settings.addString("uniqueness", uniqueness.name());
   }
}
