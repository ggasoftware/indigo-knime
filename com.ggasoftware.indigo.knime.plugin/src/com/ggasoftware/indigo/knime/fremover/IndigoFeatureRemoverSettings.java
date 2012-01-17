package com.ggasoftware.indigo.knime.fremover;

import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.defaultnodesettings.SettingsModelStringArray;

import com.ggasoftware.indigo.knime.IndigoNodeSettings;

public class IndigoFeatureRemoverSettings extends IndigoNodeSettings
{
   public static final int INPUT_PORT = 0;
   
   public final SettingsModelColumnName colName = new SettingsModelColumnName("colName", null);
   public final SettingsModelBoolean appendColumn = new SettingsModelBoolean("appendColumn", false);
   public final SettingsModelString newColName = new SettingsModelString("newColName", null);
   public final SettingsModelStringArray selectedFeatures = new SettingsModelStringArray("selectedFeatures", null);
   
   public IndigoFeatureRemoverSettings() {
      addSettingsParameter(colName);
      addSettingsParameter(appendColumn);
      addSettingsParameter(newColName);
      addSettingsParameter(selectedFeatures);
   }

}
