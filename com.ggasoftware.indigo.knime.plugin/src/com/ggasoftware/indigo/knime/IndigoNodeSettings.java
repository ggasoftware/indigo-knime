package com.ggasoftware.indigo.knime;

import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.util.ColumnSelectionComboxBox;

/*
 * Class for basic settings handling
 */
public class IndigoNodeSettings {
   


   private interface DialogMap {
      abstract void load(final DataTableSpec[] specs) throws NotConfigurableException;
      abstract void save();
   }
   
   private class CheckDialogMap implements DialogMap{
      private final JCheckBox _dialogComp;
      private final SettingsModelBoolean _mapParam;
      
      public CheckDialogMap(JCheckBox dialogComp, SettingsModelBoolean mapParam) {
         _dialogComp = dialogComp;
         _mapParam = mapParam;
      }
      public void load(DataTableSpec[] specs) {
         _dialogComp.setSelected(_mapParam.getBooleanValue());
      }
      public void save() {
         _mapParam.setBooleanValue(_dialogComp.isSelected());
      }
      
   }
   private class RadioDialogMap implements DialogMap {
      private final JRadioButton _dialogComp;
      private final SettingsModelBoolean _mapParam;
      
      public RadioDialogMap(JRadioButton dialogComp, SettingsModelBoolean mapParam) {
         _dialogComp = dialogComp;
         _mapParam = mapParam;
      }
      public void load(DataTableSpec[] specs) {
         _dialogComp.setSelected(_mapParam.getBooleanValue());
      }
      public void save() {
         _mapParam.setBooleanValue(_dialogComp.isSelected());
      }
      
   }
   private class ComboDialogMap implements DialogMap {
      
      private final JComboBox _dialogComp;
      private final SettingsModelInteger _mapParam;

      public ComboDialogMap(JComboBox dialogComp, SettingsModelInteger mapParam) {
         this._dialogComp = dialogComp;
         this._mapParam = mapParam;
      }
      public void load(DataTableSpec[] specs) {
         _dialogComp.setSelectedIndex(_mapParam.getIntValue());
      }
      public void save() {
         _mapParam.setIntValue(_dialogComp.getSelectedIndex());
      }
      
   }
   
   private class StringDialogMap implements DialogMap {
      private final JTextField _dialogComp;
      private final SettingsModelString _mapParam;

      public StringDialogMap(JTextField dialogComp, SettingsModelString mapParam) {
         this._dialogComp = dialogComp;
         this._mapParam = mapParam;
      }
      public void load(DataTableSpec[] specs) {
         _dialogComp.setText(_mapParam.getStringValue());
      }
      public void save() {
         _mapParam.setStringValue(_dialogComp.getText());
      }

   }
   
   private class SpinnerDialogMap implements DialogMap {

      private final JSpinner _dialogComp;
      private final SettingsModelInteger _mapParam;
      public SpinnerDialogMap(JSpinner dialogComp, SettingsModelInteger mapParam) {
         this._dialogComp = dialogComp;
         this._mapParam = mapParam;
      }
      public void load(DataTableSpec[] specs) {
         _dialogComp.setValue(_mapParam.getIntValue());
      }
      public void save() {
         _mapParam.setIntValue((Integer)_dialogComp.getValue());
      }
   }
   
   private class ColumnDialogMap implements DialogMap {

      private final ColumnSelectionComboxBox _dialogComp;
      private final int _specPort;
      private final SettingsModelColumnName _mapParam;

      public ColumnDialogMap(ColumnSelectionComboxBox dialogComp, int specPort, SettingsModelColumnName mapParam) {
         this._dialogComp = dialogComp;
         this._specPort = specPort;
         this._mapParam = mapParam;
      }
      public void load(DataTableSpec[] specs) throws NotConfigurableException {
         _dialogComp.update(specs[_specPort],   _mapParam.getStringValue());
      }
      public void save() {
         _mapParam.setStringValue(_dialogComp.getSelectedColumn());
      }

   }
   
   private final ArrayList<SettingsModel> _allSettings = new ArrayList<SettingsModel>();
   private final ArrayList<DialogMap> _allDialogSettings = new ArrayList<DialogMap>();
   
   
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
   
   
   public void registerDialogComponent(JCheckBox dialogComp, SettingsModelBoolean mapParam) {
      _allDialogSettings.add(new CheckDialogMap(dialogComp, mapParam));
   }
   
   public void registerDialogComponent(JRadioButton dialogComp, SettingsModelBoolean mapParam) {
      _allDialogSettings.add(new RadioDialogMap(dialogComp, mapParam));
   }
   
   public void registerDialogComponent(JComboBox dialogComp, SettingsModelInteger mapParam) {
      _allDialogSettings.add(new ComboDialogMap(dialogComp, mapParam));
   }
   
   public void registerDialogComponent(JTextField dialogComp, SettingsModelString mapParam) {
      _allDialogSettings.add(new StringDialogMap(dialogComp, mapParam));
   }
   
   public void registerDialogComponent(JSpinner dialogComp, SettingsModelInteger mapParam) {
      _allDialogSettings.add(new SpinnerDialogMap(dialogComp, mapParam));
   }
   
   public void registerDialogComponent(ColumnSelectionComboxBox dialogComp, int specPort, SettingsModelColumnName mapParam) {
      _allDialogSettings.add(new ColumnDialogMap(dialogComp, specPort, mapParam));
   }
   
   public void loadDialogSettings(final DataTableSpec[] specs) throws NotConfigurableException {
      for(DialogMap dialogMap : _allDialogSettings) {
         dialogMap.load(specs);
      }
   }
   public void saveDialogSettings() {
      for(DialogMap dialogMap: _allDialogSettings) {
         dialogMap.save();
      }
   }
   
}