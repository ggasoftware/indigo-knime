package com.ggasoftware.indigo.knime.rsplitter;

import java.util.HashMap;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.util.ColumnFilter;

import com.ggasoftware.indigo.knime.IndigoNodeSettings;
import com.ggasoftware.indigo.knime.cell.IndigoQueryReactionValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;

public class IndigoReactionSplitterSettings extends IndigoNodeSettings {
   
   public static final int INPUT_PORT = 0;
   
   public final SettingsModelString reactionColumn = new SettingsModelString("reactionColumn", null);
   
   public final SettingsModelString reactantColName = new SettingsModelString("reactantColName", "Reactants");
   public final SettingsModelString productColName = new SettingsModelString("productColName", "Products");
   public final SettingsModelString catalystColName = new SettingsModelString("catalystColName", "Catalysts");
   
   public final SettingsModelBoolean extractReactants = new SettingsModelBoolean("extractReactants", true);
   public final SettingsModelBoolean extractProducts = new SettingsModelBoolean("extractProducts", true);
   public final SettingsModelBoolean extractCatalysts = new SettingsModelBoolean("extractCatalysts", false);
   
   private final HashMap<SettingsModelBoolean, SettingsModelString> _settingsColumnMap = new HashMap<SettingsModelBoolean, SettingsModelString>();;
   
   public final ColumnFilter columnFilter = new ColumnFilter() {
      @Override
      public boolean includeColumn(DataColumnSpec colSpec) {
         if(colSpec.getType().isCompatible(IndigoReactionValue.class))
            return true;
         if(colSpec.getType().isCompatible(IndigoQueryReactionValue.class))
            return true;
         return false;
      }
      @Override
      public String allFilteredMsg() {
         return "no 'IndigoReactionValue' or 'IndigoQueryReactionValue' was found";
      }
   };
   
   public IndigoReactionSplitterSettings() {
      addSettingsParameter(reactionColumn);
      addSettingsParameter(reactantColName);
      addSettingsParameter(productColName);
      addSettingsParameter(catalystColName);
      addSettingsParameter(extractReactants);
      addSettingsParameter(extractProducts);
      addSettingsParameter(extractCatalysts);
      
      _settingsColumnMap.put(extractReactants, reactantColName);
      _settingsColumnMap.put(extractProducts, productColName);
      _settingsColumnMap.put(extractCatalysts, catalystColName);
   }
   
   public HashMap<SettingsModelBoolean, SettingsModelString> getSettingsColumnMap() {
      return _settingsColumnMap;
   }
   
}
