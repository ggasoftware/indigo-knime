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

import com.ggasoftware.indigo.knime.cell.IndigoQueryReactionValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;
import com.ggasoftware.indigo.knime.common.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.rautomapper.IndigoReactionAutomapperSettings.AAMode;

public class IndigoReactionAutomapperNodeDialog extends NodeDialogPane {

   private final IndigoReactionAutomapperSettings _settings = new IndigoReactionAutomapperSettings();

   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _colName = new ColumnSelectionComboxBox((Border) null, IndigoReactionValue.class, IndigoQueryReactionValue.class);
   private final JCheckBox _appendColumn = new JCheckBox("Append column");
   private final JTextField _newColName = new JTextField(20);

   private final JComboBox _mode = new JComboBox(new String[] { AAMode.Discard.toString(), AAMode.Keep.toString(), AAMode.Alter.toString(),
         AAMode.Clear.toString() });
   
   private final JCheckBox _ignoreCharges = new JCheckBox("Ignore charges");
   private final JCheckBox _ignoreIsotopes = new JCheckBox("Ignore isotopes");
   private final JCheckBox _ignoreRadicals = new JCheckBox("Ignore radicals");
   private final JCheckBox _ignoreValence = new JCheckBox("Ignore valence");
   private final JCheckBox _highlightReactingCenters = new JCheckBox("Highlight Reacting Centers");

   protected IndigoReactionAutomapperNodeDialog() {
      super();
      
      _registerDialogComponents();
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column settings");
      dialogPanel.addItem("Reaction column", _colName);
      dialogPanel.addItem(_appendColumn, _newColName);
      
      dialogPanel.addItemsPanel("Automapping settings");
      dialogPanel.addItem("Reaction AAM mode", _mode);
      dialogPanel.addItem(_highlightReactingCenters);
      dialogPanel.addItemsPanel("Match rules settings");
      dialogPanel.addItem(_ignoreCharges);
      dialogPanel.addItem(_ignoreIsotopes);
      dialogPanel.addItem(_ignoreRadicals);
      dialogPanel.addItem(_ignoreValence);
      
      IndigoDialogPanel.addColumnChangeListener(_appendColumn, _colName, _newColName, " (mapped)");

      addTab("Standard settings", dialogPanel.getPanel());
   }

   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_colName, IndigoReactionAutomapperNodeModel.INPUT_PORT, _settings.reactionColumn);
      _settings.registerDialogComponent(_newColName, _settings.newColName);
      _settings.registerDialogComponent(_appendColumn, _settings.appendColumn);
      _settings.registerDialogComponent(_mode, _settings.mode);
      /*
       * Ignore flags
       */
      _settings.registerDialogComponent(_ignoreCharges, _settings.ignoreCharges);
      _settings.registerDialogComponent(_ignoreIsotopes, _settings.ignoreIsotopes);
      _settings.registerDialogComponent(_ignoreRadicals, _settings.ignoreRadicals);
      _settings.registerDialogComponent(_ignoreValence, _settings.ignoreValence);
      
      _settings.registerDialogComponent(_highlightReactingCenters, _settings.highlightReactingCenters);
      
      
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
