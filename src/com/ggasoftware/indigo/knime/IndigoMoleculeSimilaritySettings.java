package com.ggasoftware.indigo.knime;

import org.knime.core.node.*;

public class IndigoMoleculeSimilaritySettings
{
   enum Metric
   {
      Tanimoto, EuclidSub, Tversky
   }

   public String colName;
   public boolean loadFromFile = false;
   public String fileName;
   public String smiles;
   public String newColName;
   public Metric metric;
   public float tverskyAlpha;
   public float tverskyBeta;

   public void loadSettings (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      colName = settings.getString("colName");
      loadFromFile = settings.getBoolean("loadFromFile");
      fileName = settings.getString("fileName");
      smiles = settings.getString("smiles");
      newColName = settings.getString("newColName");
      metric = Metric.valueOf(settings.getString("metric"));
      tverskyAlpha = settings.getFloat("tverskyAlpha");
      tverskyBeta = settings.getFloat("tverskyBeta");
   }

   public void loadSettingsForDialog (final NodeSettingsRO settings)
   {
      colName = settings.getString("colName", null);
      loadFromFile = settings.getBoolean("loadFromFile", false);
      fileName = settings.getString("fileName", null);
      smiles = settings.getString("smiles", "");
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
      settings.addBoolean("loadFromFile", loadFromFile);
      if (fileName != null)
         settings.addString("fileName", fileName);
      if (smiles != null)
         settings.addString("smiles", smiles);
      if (metric != null)
         settings.addString("metric", metric.name());
      if (newColName != null)
         settings.addString("newColName", newColName);
      settings.addFloat("tverskyAlpha", tverskyAlpha);
      settings.addFloat("tverskyBeta", tverskyBeta);
   }
}
