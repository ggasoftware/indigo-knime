package com.ggasoftware.indigo.knime.rsplitter;

import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.ggasoftware.indigo.knime.IndigoNodeSettings;

public class IndigoReactionSplitterSettings extends IndigoNodeSettings{
   
   public final SettingsModelString reactionColName = new SettingsModelString("reactionColName", null);
   
   public final SettingsModelString reactantColName = new SettingsModelString("reactantColName", "Reactants");
   public final SettingsModelString productColName = new SettingsModelString("productColName", "Products");
   public final SettingsModelString catalystColName = new SettingsModelString("catalystColName", "Catalysts");
   
   public final SettingsModelBoolean extractReactants = new SettingsModelBoolean("extractReactants", true);
   public final SettingsModelBoolean extractProducts = new SettingsModelBoolean("extractProducts", true);
   public final SettingsModelBoolean extractCatalysts = new SettingsModelBoolean("extractCatalysts", false);
   
   public IndigoReactionSplitterSettings() {
      addSettingsParameter(reactionColName);
      addSettingsParameter(reactantColName);
      addSettingsParameter(productColName);
      addSettingsParameter(catalystColName);
      addSettingsParameter(extractReactants);
      addSettingsParameter(extractProducts);
      addSettingsParameter(extractCatalysts);
   }
   
}
