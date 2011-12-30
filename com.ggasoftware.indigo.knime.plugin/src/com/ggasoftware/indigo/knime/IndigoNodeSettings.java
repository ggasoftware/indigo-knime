package com.ggasoftware.indigo.knime;

import java.util.ArrayList;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.defaultnodesettings.SettingsModel;
/*
 * Class for basic settings handling
 */
public class IndigoNodeSettings {
   
   private final ArrayList<SettingsModel> _allSettings = new ArrayList<SettingsModel>();
   
   protected void addSettingsParameter(SettingsModel param) {
      _allSettings.add(param);
   }
   
   public void loadSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
      for (SettingsModel param : _allSettings) {
         param.loadSettingsFrom(settings);
      }
      loadAdditionalSettings(settings);
   }

   public void saveSettingsTo(NodeSettingsWO settings) {
      for (SettingsModel param : _allSettings) {
         param.saveSettingsTo(settings);
      }
      saveAdditionalSettings(settings);
   }

   public void validateSettings(NodeSettingsRO settings) throws InvalidSettingsException {
      for (SettingsModel param : _allSettings) {
         param.validateSettings(settings);
      }
      validateAdditionalSettings(settings);
   }
   /*
    * Additional settings processing
    */
   protected void loadAdditionalSettings(NodeSettingsRO settings) throws InvalidSettingsException{
   }
   protected void saveAdditionalSettings(NodeSettingsWO settings) {
   }
   public void validateAdditionalSettings(NodeSettingsRO settings) throws InvalidSettingsException {
   }
   
   
   
}
