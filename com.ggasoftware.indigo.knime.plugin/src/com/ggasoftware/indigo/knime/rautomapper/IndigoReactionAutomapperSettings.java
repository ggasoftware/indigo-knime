package com.ggasoftware.indigo.knime.rautomapper;

import java.util.HashMap;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;

import com.ggasoftware.indigo.knime.IndigoNodeSettings;

public class IndigoReactionAutomapperSettings extends IndigoNodeSettings {
   public static enum AAMode {
      Discard, Keep, Alter, Clear
   }
   
   static final String CFGKEY_IGNORE_CHARGES = "ignore_charges";
   static final String CFGKEY_IGNORE_ISOTOPES = "ignore_isotopes";
   static final String CFGKEY_IGNORE_RADICALS = "ignore_radicals";
   static final String CFGKEY_IGNORE_VALENCE = "ignore_valence";

   public final SettingsModelColumnName reactionColumn = new SettingsModelColumnName("column", null);
   public final DeprecatedSettingsModelBooleanInverse appendColumn = new DeprecatedSettingsModelBooleanInverse("replaceColumn", false);
   public final SettingsModelColumnName newColName = new SettingsModelColumnName("newColumn", null);
   public final SettingsModelInteger mode = new SettingsModelInteger("mode", AAMode.Discard.ordinal());
   
   public final SettingsModelBoolean ignoreCharges = new SettingsModelBoolean(CFGKEY_IGNORE_CHARGES, false);
   public final SettingsModelBoolean ignoreIsotopes = new SettingsModelBoolean(CFGKEY_IGNORE_ISOTOPES, false);
   public final SettingsModelBoolean ignoreRadicals = new SettingsModelBoolean(CFGKEY_IGNORE_RADICALS, false);
   public final SettingsModelBoolean ignoreValence = new SettingsModelBoolean(CFGKEY_IGNORE_VALENCE, false);
   
   private final HashMap<String, SettingsModelBoolean> _ignoreFlags = new HashMap<String, SettingsModelBoolean>();

   public IndigoReactionAutomapperSettings() {
      addSettingsParameter(reactionColumn);
      addSettingsParameter(appendColumn);
      addSettingsParameter(newColName);
      addSettingsParameter(mode);
      addSettingsParameter(ignoreCharges);
      addSettingsParameter(ignoreIsotopes);
      addSettingsParameter(ignoreRadicals);
      addSettingsParameter(ignoreValence);
      
      _ignoreFlags.put(CFGKEY_IGNORE_CHARGES, ignoreCharges);
      _ignoreFlags.put(CFGKEY_IGNORE_ISOTOPES, ignoreIsotopes);
      _ignoreFlags.put(CFGKEY_IGNORE_RADICALS, ignoreRadicals);
      _ignoreFlags.put(CFGKEY_IGNORE_VALENCE, ignoreValence);
   }
   
   public String getAAMParameters() {
      StringBuilder result = new StringBuilder();
      /*
       * Append mode
       */
      result.append(AAMode.values()[mode.getIntValue()].name().toLowerCase());
      /*
       * Append ignore flags
       */
      for (String ignoreFlagKey : _ignoreFlags.keySet()) {
         SettingsModelBoolean ignoreFlag = _ignoreFlags.get(ignoreFlagKey);
         
         if(ignoreFlag.getBooleanValue()) {
            result.append(" ");
            result.append(ignoreFlagKey);
         }
      }
      return result.toString();
   }

   public int getColumnIdx(DataTableSpec inPortSpec) {
      return searchColumnIdx(reactionColumn.getStringValue(), "reaction" , inPortSpec);
   }

}
