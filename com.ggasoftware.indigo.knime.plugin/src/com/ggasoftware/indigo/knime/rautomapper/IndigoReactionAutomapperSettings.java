package com.ggasoftware.indigo.knime.rautomapper;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
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

   static final String DEFAULT_COLUMN = null;
   static final boolean DEFAULT_REPLACE = true;
   static final String DEFAULT_NEWCOLUMN = null;
   static final int DEFAULT_MODE = AAMode.Discard.ordinal();

   public final SettingsModelColumnName m_column = new SettingsModelColumnName(CFGKEY_COLUMN, DEFAULT_COLUMN);
   public final SettingsModelBoolean m_replace = new SettingsModelBoolean(CFGKEY_REPLACE, DEFAULT_REPLACE);
   public final SettingsModelColumnName m_newColumn = new SettingsModelColumnName(CFGKEY_NEWCOLUMN, DEFAULT_NEWCOLUMN);
   public final SettingsModelInteger m_mode = new SettingsModelInteger(CFGKEY_MODE, DEFAULT_MODE);

   public IndigoReactionAutomapperSettings() {
   }

   public void loadSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
      m_column.loadSettingsFrom(settings);
      m_replace.loadSettingsFrom(settings);
      m_newColumn.loadSettingsFrom(settings);
      m_mode.loadSettingsFrom(settings);
   }

   public void saveSettingsTo(NodeSettingsWO settings) {
      m_column.saveSettingsTo(settings);
      m_replace.saveSettingsTo(settings);
      m_newColumn.saveSettingsTo(settings);
      m_mode.saveSettingsTo(settings);
   }

   public void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
      m_column.validateSettings(settings);
      m_replace.validateSettings(settings);
      m_newColumn.validateSettings(settings);
      m_mode.validateSettings(settings);
   }

}
