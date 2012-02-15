package com.ggasoftware.indigo.knime.rbuilder;

import java.util.HashMap;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.util.ColumnFilter;

import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolValue;
import com.ggasoftware.indigo.knime.common.IndigoNodeSettings;

public class IndigoReactionBuilderSettings extends IndigoNodeSettings{
   public static final int INPUT_PORT = 0;
   
   public final SettingsModelString reactantColName = new SettingsModelString("reactantColName", null);
   public final SettingsModelString productColName = new SettingsModelString("productColName", null);
   public final SettingsModelString catalystColName = new SettingsModelString("catalystColName", null);
   
   public final SettingsModelBoolean addReactants = new SettingsModelBoolean("addReactants", true);
   public final SettingsModelBoolean addProducts = new SettingsModelBoolean("addProducts", true);
   public final SettingsModelBoolean addCatalysts = new SettingsModelBoolean("addCatalysts", false);
   
   public final SettingsModelString newColName = new SettingsModelString("newColName", "Result reaction");
   
   private final HashMap<SettingsModelBoolean, SettingsModelString> _settingsColumnMap = new HashMap<SettingsModelBoolean, SettingsModelString>();;
   
   public final ColumnFilter columnFilter = new ColumnFilter() {
      @Override
      public boolean includeColumn(DataColumnSpec colSpec) {
         if(colSpec.getType().isCompatible(IndigoMolValue.class))
            return true;
         if(colSpec.getType().isCompatible(IndigoQueryMolValue.class))
            return true;
         return false;
      }
      @Override
      public String allFilteredMsg() {
         return "no 'IndigoMolValue' or 'IndigoQueryMolValue' was found";
      }
   };

   
   public IndigoReactionBuilderSettings() {
      addSettingsParameter(reactantColName);
      addSettingsParameter(productColName);
      addSettingsParameter(catalystColName);
      addSettingsParameter(addReactants);
      addSettingsParameter(addProducts);
      addSettingsParameter(addCatalysts);
      addSettingsParameter(newColName);
      
      _settingsColumnMap.put(addReactants, reactantColName);
      _settingsColumnMap.put(addProducts, productColName);
      _settingsColumnMap.put(addCatalysts, catalystColName);
   }

   public HashMap<SettingsModelBoolean, SettingsModelString> getSettingsColumnMap() {
      return _settingsColumnMap;
   }
   
}
