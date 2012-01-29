package com.ggasoftware.indigo.knime.combchem;

import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.ggasoftware.indigo.knime.IndigoNodeSettings;

public class IndigoReactionGeneratorSettings extends IndigoNodeSettings {
   public static final int REACTION_PORT = 0;
   public static final int MOL_PORT1 = 1;
   public static final int MOL_PORT2 = 2;
   
   public final SettingsModelString molColumn1 = new SettingsModelString("molColName1", null);
   public final SettingsModelString molColumn2 = new SettingsModelString("molColName2", null);
   public final SettingsModelString reactionColumn = new SettingsModelString("reactionColName", null);
   public final SettingsModelString newColName = new SettingsModelString("newColName", "Reaction");
   
   public IndigoReactionGeneratorSettings() {
      addSettingsParameter(reactionColumn);
      addSettingsParameter(molColumn1);
      addSettingsParameter(molColumn2);
      addSettingsParameter(newColName);
   }
}
