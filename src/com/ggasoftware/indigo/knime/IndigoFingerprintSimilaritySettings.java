package com.ggasoftware.indigo.knime;

import org.knime.core.node.*;

public class IndigoFingerprintSimilaritySettings
{
   enum Metric
   {
      Tanimoto, EuclidSub, Tversky
   }

   public String colName;
   public String colName2;
   public String newColName;
   public Metric metric;
   public float tverskyAlpha;
   public float tverskyBeta;

   public void loadSettings (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      colName = settings.getString("colName");
      colName2 = settings.getString("colName2");
      newColName = settings.getString("newColName");
      metric = Metric.valueOf(settings.getString("metric"));
      tverskyAlpha = settings.getFloat("tverskyAlpha");
      tverskyBeta = settings.getFloat("tverskyBeta");
   }

   public void loadSettingsForDialog (final NodeSettingsRO settings)
   {
      colName = settings.getString("colName", null);
      colName2 = settings.getString("colName2", null);
      newColName = settings.getString("newColName", "similarity");
      metric = Metric.valueOf(settings.getString("metric",
            Metric.Tanimoto.name()));
      tverskyAlpha = settings.getFloat("tverskyAlpha", 0.5f);
      tverskyBeta = settings.getFloat("tverskyBeta", 0.5f);
   }

   public void saveSettings (final NodeSettingsWO settings)
   {
      if (colName != null)
         settings.addString("colName", colName);
      if (colName2 != null)
         settings.addString("colName2", colName2);
      if (metric != null)
         settings.addString("metric", metric.name());
      if (newColName != null)
         settings.addString("newColName", newColName);
      settings.addFloat("tverskyAlpha", tverskyAlpha);
      settings.addFloat("tverskyBeta", tverskyBeta);
   }
}
