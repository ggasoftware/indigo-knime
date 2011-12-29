package com.ggasoftware.indigo.knime.rautomapper;

import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.cell.IndigoQueryReactionValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;
import com.ggasoftware.indigo.knime.rautomapper.IndigoReactionAutomapperSettings.AAMode;

public class IndigoReactionAutomapperNodeDialog extends NodeDialogPane {

   private final IndigoReactionAutomapperSettings _settings = new IndigoReactionAutomapperSettings();

   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _colName = new ColumnSelectionComboxBox((Border) null, IndigoReactionValue.class, IndigoQueryReactionValue.class);
   private final JCheckBox _appendColumn = new JCheckBox("Append column");
   private final JTextField _newColName = new JTextField(20);

   private final JComboBox _mode = new JComboBox(new String[] { AAMode.Discard.toString(), AAMode.Keep.toString(), AAMode.Alter.toString(),
         AAMode.Clear.toString() });
   
   private final JCheckBox _ignoreCharges = new JCheckBox();
   private final JCheckBox _ignoreIsotopes = new JCheckBox();
   private final JCheckBox _ignoreRadicals = new JCheckBox();
   private final JCheckBox _ignoreValence = new JCheckBox();

   protected IndigoReactionAutomapperNodeDialog() {
      super();
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column settings");
      dialogPanel.addItem("Reaction column", _colName);
      dialogPanel.addItem(_appendColumn, _newColName);
      
      dialogPanel.addItemsPanel("Automapping settings");
      dialogPanel.addItem("Reaction AAM mode", _mode);
      dialogPanel.addItem("Ignore Charges", _ignoreCharges);
      dialogPanel.addItem("Ignore Isotopes", _ignoreIsotopes);
      dialogPanel.addItem("Ignore Radicals", _ignoreRadicals);
      dialogPanel.addItem("Ignore Valence", _ignoreValence);
      
      _appendColumn.setFont(new Font("Serif", Font.PLAIN, 12));
      IndigoDialogPanel.addColumnChangeListener(_appendColumn, _colName, _newColName, " (mapped)");

      addTab("Standard settings", dialogPanel.getPanel());
   }

   @Override
   protected void loadSettingsFrom(NodeSettingsRO settings, DataTableSpec[] specs) throws NotConfigurableException {
      try {
         _settings.loadSettingsFrom(settings);

         _colName.update(specs[IndigoReactionAutomapperNodeModel.INPUT_PORT], _settings.m_column.getStringValue());
         _appendColumn.setSelected(!_settings.m_replace.getBooleanValue());
         _newColName.setText(_settings.m_newColumn.getStringValue());
         _mode.setSelectedIndex(_settings.m_mode.getIntValue());
         /*
          * Ignore flags
          */
         _ignoreCharges.setSelected(_settings.m_ignoreCharges.getBooleanValue());
         _ignoreIsotopes.setSelected(_settings.m_ignoreIsotopes.getBooleanValue());
         _ignoreRadicals.setSelected(_settings.m_ignoreRadicals.getBooleanValue());
         _ignoreValence.setSelected(_settings.m_ignoreValence.getBooleanValue());
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage(), e);
      }

   }

   @Override
   protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
      _settings.m_column.setStringValue(_colName.getSelectedColumn());
      _settings.m_replace.setBooleanValue(!_appendColumn.isSelected());
      _settings.m_newColumn.setStringValue(_newColName.getText());
      _settings.m_mode.setIntValue(_mode.getSelectedIndex());
      /*
       * Ignore flags
       */
      _settings.m_ignoreCharges.setBooleanValue(_ignoreCharges.isSelected());
      _settings.m_ignoreIsotopes.setBooleanValue(_ignoreIsotopes.isSelected());
      _settings.m_ignoreRadicals.setBooleanValue(_ignoreRadicals.isSelected());
      _settings.m_ignoreValence.setBooleanValue(_ignoreValence.isSelected());
      
      _settings.saveSettingsTo(settings);
   }
}
