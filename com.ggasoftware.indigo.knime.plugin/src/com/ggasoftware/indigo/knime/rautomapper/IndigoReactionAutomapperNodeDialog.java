package com.ggasoftware.indigo.knime.rautomapper;

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
   
   private final JCheckBox _ignoreCharges = new JCheckBox("Ignore Charges");
   private final JCheckBox _ignoreIsotopes = new JCheckBox("Ignore Isotopes");
   private final JCheckBox _ignoreRadicals = new JCheckBox("Ignore Radicals");
   private final JCheckBox _ignoreValence = new JCheckBox("Ignore Valence");

   protected IndigoReactionAutomapperNodeDialog() {
      super();
      
      _registerDialogComponents();
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column settings");
      dialogPanel.addItem("Reaction column", _colName);
      dialogPanel.addItem(_appendColumn, _newColName);
      
      dialogPanel.addItemsPanel("Automapping settings");
      dialogPanel.addItem("Reaction AAM mode", _mode);
      dialogPanel.addItem(_ignoreCharges);
      dialogPanel.addItem(_ignoreIsotopes);
      dialogPanel.addItem(_ignoreRadicals);
      dialogPanel.addItem(_ignoreValence);
      
      IndigoDialogPanel.setDefaultFont(_appendColumn);
      IndigoDialogPanel.setDefaultFont(_ignoreCharges);
      IndigoDialogPanel.setDefaultFont(_ignoreIsotopes);
      IndigoDialogPanel.setDefaultFont(_ignoreRadicals);
      IndigoDialogPanel.setDefaultFont(_ignoreValence);
      IndigoDialogPanel.addColumnChangeListener(_appendColumn, _colName, _newColName, " (mapped)");

      addTab("Standard settings", dialogPanel.getPanel());
   }

   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_colName, IndigoReactionAutomapperNodeModel.INPUT_PORT, _settings.m_column);
      _settings.registerDialogComponent(_newColName, _settings.m_newColumn);
      _settings.registerDialogComponent(_appendColumn, _settings.m_appendColumn);
      _settings.registerDialogComponent(_mode, _settings.m_mode);
      /*
       * Ignore flags
       */
      _settings.registerDialogComponent(_ignoreCharges, _settings.m_ignoreCharges);
      _settings.registerDialogComponent(_ignoreIsotopes, _settings.m_ignoreIsotopes);
      _settings.registerDialogComponent(_ignoreRadicals, _settings.m_ignoreRadicals);
      _settings.registerDialogComponent(_ignoreValence, _settings.m_ignoreValence);
      
      
   }

   @Override
   protected void loadSettingsFrom(NodeSettingsRO settings, DataTableSpec[] specs) throws NotConfigurableException {
      try {
         _settings.loadSettingsFrom(settings);
         _settings.loadDialogSettings(specs);
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage(), e);
      }

   }

   @Override
   protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
      _settings.saveDialogSettings();
      _settings.saveSettingsTo(settings);
   }
}
