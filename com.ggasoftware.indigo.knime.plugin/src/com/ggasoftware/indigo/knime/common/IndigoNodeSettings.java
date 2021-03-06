package com.ggasoftware.indigo.knime.common;

import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
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
import org.knime.core.node.defaultnodesettings.SettingsModelDouble;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryReactionValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;

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
   private class ComboStringDialogMap implements DialogMap {
      
      private final JComboBox _dialogComp;
      private final SettingsModelString _mapParam;

      public ComboStringDialogMap(JComboBox dialogComp, SettingsModelString mapParam) {
         this._dialogComp = dialogComp;
         this._mapParam = mapParam;
      }
      public void load(DataTableSpec[] specs) {
         String value = _mapParam.getStringValue(); 
         for (int i = 0; i < _dialogComp.getItemCount(); i++)
            if (_dialogComp.getItemAt(i).toString().equals(value)) {
               _dialogComp.setSelectedIndex(i);
               break;
            }
      }
      public void save() {
         _mapParam.setStringValue(_dialogComp.getSelectedItem().toString());
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
      private final SettingsModelString _mapParam;
      private boolean _optional = false;
      private boolean _disabled = false;

      public ColumnDialogMap(ColumnSelectionComboxBox dialogComp, int specPort, SettingsModelString mapParam) {
         this._dialogComp = dialogComp;
         this._specPort = specPort;
         this._mapParam = mapParam;
      }
      public void load(DataTableSpec[] specs) throws NotConfigurableException {
         _disabled = (_optional && specs[_specPort] == null);
         if (_disabled) {
            return;
         }
         _dialogComp.update(specs[_specPort],   _mapParam.getStringValue());
      }
      public void save() {
         if (_disabled) {
            return;
         }
         _mapParam.setStringValue(_dialogComp.getSelectedColumn());
      }
      public void setOptional (boolean optional) {
         _optional = optional;
      }
   }
   
   private class DoubleDialogMap implements DialogMap {

      private final JFormattedTextField _dialogComp;
      private final SettingsModelDouble _mapParam;

      public DoubleDialogMap(JFormattedTextField dialogComp, SettingsModelDouble mapParam) {
         this._dialogComp = dialogComp;
         this._mapParam = mapParam;
      }
      public void load(DataTableSpec[] specs) throws NotConfigurableException {
         _dialogComp.setValue(_mapParam.getDoubleValue());
      }
      public void save() {
         _mapParam.setDoubleValue(((Number)_dialogComp.getValue()).doubleValue());
      }

   }
   
   public class FloatDialogMap implements DialogMap {
      private final JFormattedTextField _dialogComp;
      private final SettingsModelFloat _mapParam;
      
      public FloatDialogMap(JFormattedTextField dialogComp, SettingsModelFloat mapParam) {
         _dialogComp = dialogComp;
         _mapParam = mapParam;
      }

      @Override
      public void load(DataTableSpec[] specs) throws NotConfigurableException {
         _dialogComp.setValue(_mapParam.getFloatValue());
      }

      @Override
      public void save() {
         _mapParam.setFloatValue(((Number)_dialogComp.getValue()).floatValue());
      }

   }
   
   public class DeprecatedSettingsModelBooleanInverse extends SettingsModelBoolean {

      private final String _configName;
      private final boolean _defaultValue;

      public DeprecatedSettingsModelBooleanInverse(String configName,
            boolean defaultValue) {
         super(configName, defaultValue);
         this._configName = configName;
         this._defaultValue = defaultValue;
      }
      
      @Override
      protected void loadSettingsForModel(NodeSettingsRO settings)
            throws InvalidSettingsException {
         setBooleanValue(!settings.getBoolean(_configName));
      }
      
      @Override
      protected void loadSettingsForDialog(NodeSettingsRO settings,
            PortObjectSpec[] specs) throws NotConfigurableException {
         setBooleanValue(!settings.getBoolean(_configName, _defaultValue));
      }
      
      @Override
      protected void saveSettingsForModel(NodeSettingsWO settings) {
         settings.addBoolean(_configName, !getBooleanValue());
      }
   }
   
   private final ArrayList<SettingsModel> _allSettings = new ArrayList<SettingsModel>();
   private final ArrayList<DialogMap> _allDialogSettings = new ArrayList<DialogMap>();
   public String warningMessage;
   
   
   protected void addSettingsParameter(SettingsModel param) {
      _allSettings.add(param);
   }
   
   public void loadSettingsFrom(NodeSettingsRO settings) throws InvalidSettingsException {
      loadSettingsFrom(settings, false);
   }
   
   public void loadSettingsFrom(NodeSettingsRO settings, boolean throwError) throws InvalidSettingsException {
      warningMessage = null;
      StringBuilder wMessage = new StringBuilder();
      
      for (SettingsModel param : _allSettings) {
         try {
            param.loadSettingsFrom(settings);
         } catch (InvalidSettingsException e) {
            wMessage.append(e.getMessage());
            wMessage.append('\n');
         }
      }
      loadAdditionalSettings(settings);
      if(wMessage.length() > 0 ) {
         wMessage.insert(0, "Not all the settings have been loaded: ");
         wMessage.append("Probably, these settings are new, and the default values have been used. Please, review the configuration and resave it.");
         warningMessage = wMessage.toString();
         if(throwError)
            throw new InvalidSettingsException(warningMessage);
      }
   }

   public void saveSettingsTo(NodeSettingsWO settings) {
      for (SettingsModel param : _allSettings) {
         param.saveSettingsTo(settings);
      }
      saveAdditionalSettings(settings);
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

   public void registerDialogComponent(JComboBox dialogComp, SettingsModelString mapParam) {
      _allDialogSettings.add(new ComboStringDialogMap(dialogComp, mapParam));
   }
   
   public void registerDialogComponent(JTextField dialogComp, SettingsModelString mapParam) {
      _allDialogSettings.add(new StringDialogMap(dialogComp, mapParam));
   }
   
   public void registerDialogComponent(JSpinner dialogComp, SettingsModelInteger mapParam) {
      _allDialogSettings.add(new SpinnerDialogMap(dialogComp, mapParam));
   }
   
   public void registerDialogComponent(ColumnSelectionComboxBox dialogComp, int specPort, SettingsModelString mapParam) {
      registerDialogComponent(dialogComp, specPort, mapParam, false);
   }

   public void registerDialogComponent(ColumnSelectionComboxBox dialogComp, int specPort, SettingsModelString mapParam, boolean optional) {
      ColumnDialogMap columnDialogMap = new ColumnDialogMap(dialogComp, specPort, mapParam);
      columnDialogMap.setOptional(optional);
      _allDialogSettings.add(columnDialogMap);
   }
   
   public void registerDialogComponent(JFormattedTextField dialogComp, SettingsModelDouble mapParam) {
      _allDialogSettings.add(new DoubleDialogMap(dialogComp, mapParam));
   }
   
   public void registerDialogComponent(JFormattedTextField dialogComp,
         SettingsModelFloat mapParam) {
      _allDialogSettings.add(new FloatDialogMap(dialogComp, mapParam));
      
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
   
   public enum STRUCTURE_TYPE {
      Reaction, Molecule, Unknown;
   }
   
   /*
    * Returns current column selection state
    */
   public static STRUCTURE_TYPE getStructureType(DataTableSpec tSpec, DataTableSpec qSpec, String tName, String qName) {
      STRUCTURE_TYPE result = STRUCTURE_TYPE.Unknown;
      
      int reactions = 0;
      int molecules = 0;
      
      if(tSpec != null) {
         if(tSpec.containsName(tName))
            if(tSpec.getColumnSpec(tName).getType().isCompatible(IndigoReactionValue.class))
               ++reactions;
            else if (tSpec.getColumnSpec(tName).getType().isCompatible(IndigoMolValue.class)) 
               ++molecules;
      }
      
      if(qSpec != null) {
         if(qSpec.containsName(qName))
            if(qSpec.getColumnSpec(qName).getType().isCompatible(IndigoQueryReactionValue.class))
               ++reactions;
            else if(qSpec.getColumnSpec(qName).getType().isCompatible(IndigoQueryMolValue.class))
               ++molecules;
      }
      if(reactions == 2)
         result = STRUCTURE_TYPE.Reaction;
      else if(molecules == 2)
         result = STRUCTURE_TYPE.Molecule;
      
      return result;
   }
   
   public static int searchColumnIdx(String colName, String errorMsg, DataTableSpec inPortSpec) {
      int colIdx = -1;

      if (colName == null)
         throw new RuntimeException(errorMsg + " column not found");

      colIdx = inPortSpec.findColumnIndex(colName);

      if (colIdx == -1)
         throw new RuntimeException(errorMsg + " column not found");
      
      return colIdx;
   }

   public static STRUCTURE_TYPE getStructureType(DataTableSpec tSpec, String tName) {
      STRUCTURE_TYPE result = STRUCTURE_TYPE.Unknown;

      if (tSpec != null) {
         if (tSpec.containsName(tName))
            if (tSpec.getColumnSpec(tName).getType().isCompatible(IndigoReactionValue.class))
               result = STRUCTURE_TYPE.Reaction;
            else if (tSpec.getColumnSpec(tName).getType().isCompatible(IndigoMolValue.class))
               result = STRUCTURE_TYPE.Molecule;
      }
      return result;
   }
   
   
}
