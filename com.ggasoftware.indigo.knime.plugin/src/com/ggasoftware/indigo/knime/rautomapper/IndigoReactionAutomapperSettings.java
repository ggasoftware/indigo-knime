package com.ggasoftware.indigo.knime.rautomapper;

import java.util.ArrayList;
import java.util.HashMap;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;

public class IndigoReactionAutomapperSettings {
   public static enum AAMode {
      Discard, Keep, Alter, Clear
   }

   static final String CFGKEY_COLUMN = "column";
   static final String CFGKEY_REPLACE = "replaceColumn";
   static final String CFGKEY_NEWCOLUMN = "newColumn";
   static final String CFGKEY_MODE = "mode";
   
   static final String CFGKEY_IGNORE_CHARGES = "ignore_charges";
   static final String CFGKEY_IGNORE_ISOTOPES = "ignore_isotopes";
   static final String CFGKEY_IGNORE_RADICALS = "ignore_radicals";
   static final String CFGKEY_IGNORE_VALENCE = "ignore_valence";

   static final String DEFAULT_COLUMN = null;
   static final boolean DEFAULT_REPLACE = true;
   static final String DEFAULT_NEWCOLUMN = null;
   static final int DEFAULT_MODE = AAMode.Discard.ordinal();

   public final SettingsModelColumnName m_column = new SettingsModelColumnName(CFGKEY_COLUMN, DEFAULT_COLUMN);
   public final SettingsModelBoolean m_replace = new SettingsModelBoolean(CFGKEY_REPLACE, DEFAULT_REPLACE);
   public final SettingsModelColumnName m_newColumn = new SettingsModelColumnName(CFGKEY_NEWCOLUMN, DEFAULT_NEWCOLUMN);
   public final SettingsModelInteger m_mode = new SettingsModelInteger(CFGKEY_MODE, DEFAULT_MODE);
   
   public final SettingsModelBoolean m_ignoreCharges = new SettingsModelBoolean(CFGKEY_IGNORE_CHARGES, false);
   public final SettingsModelBoolean m_ignoreIsotopes = new SettingsModelBoolean(CFGKEY_IGNORE_ISOTOPES, false);
   public final SettingsModelBoolean m_ignoreRadicals = new SettingsModelBoolean(CFGKEY_IGNORE_RADICALS, false);
   public final SettingsModelBoolean m_ignoreValence = new SettingsModelBoolean(CFGKEY_IGNORE_VALENCE, false);
   
   private final ArrayList<SettingsModel> _allSettings = new ArrayList<SettingsModel>();
   private final HashMap<String, SettingsModelBoolean> _ignoreFlags = new HashMap<String, SettingsModelBoolean>();

   public IndigoReactionAutomapperSettings() {
      _allSettings.add(m_column);
      _allSettings.add(m_replace);
      _allSettings.add(m_newColumn);
      _allSettings.add(m_mode);
      _allSettings.add(m_ignoreCharges);
      _allSettings.add(m_ignoreIsotopes);
      _allSettings.add(m_ignoreRadicals);
      _allSettings.add(m_ignoreValence);
      
      _ignoreFlags.put(CFGKEY_IGNORE_CHARGES, m_ignoreCharges);
      _ignoreFlags.put(CFGKEY_IGNORE_ISOTOPES, m_ignoreIsotopes);
      _ignoreFlags.put(CFGKEY_IGNORE_RADICALS, m_ignoreRadicals);
      _ignoreFlags.put(CFGKEY_IGNORE_VALENCE, m_ignoreValence);
   }

   public void loadSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
      for (SettingsModel param : _allSettings) {
         param.loadSettingsFrom(settings);
      }
   }

   public void saveSettingsTo(NodeSettingsWO settings) {
      for (SettingsModel param : _allSettings) {
         param.saveSettingsTo(settings);
      }
   }

   public void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
      for (SettingsModel param : _allSettings) {
         param.validateSettings(settings);
      }
   }

   public String getAAMParameters() {
      StringBuilder result = new StringBuilder();
      /*
       * Append mode
       */
      result.append(AAMode.values()[m_mode.getIntValue()].name().toLowerCase());
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

}
