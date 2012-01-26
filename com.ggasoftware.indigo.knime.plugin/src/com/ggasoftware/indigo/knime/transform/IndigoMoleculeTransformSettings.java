package com.ggasoftware.indigo.knime.transform;

import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.ggasoftware.indigo.knime.IndigoNodeSettings;

public class IndigoMoleculeTransformSettings extends IndigoNodeSettings {
   public static final int REACTION_PORT = 0;
   public static final int MONOMER_PORT = 1;
   
   public final SettingsModelString reactionColumn = new SettingsModelString("colName", null);
   public final SettingsModelString monomerColumn = new SettingsModelString("colName2", null);
   public final SettingsModelBoolean appendColumn = new SettingsModelBoolean("appendColumn", false);
   public final SettingsModelString newColName = new SettingsModelString("newColName", null);
   
   public IndigoMoleculeTransformSettings() {
      addSettingsParameter(reactionColumn);
      addSettingsParameter(monomerColumn);
      addSettingsParameter(appendColumn);
      addSettingsParameter(newColName);
   }
}
