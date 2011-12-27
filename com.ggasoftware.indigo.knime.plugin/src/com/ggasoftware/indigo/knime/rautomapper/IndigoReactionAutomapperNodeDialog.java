package com.ggasoftware.indigo.knime.rautomapper;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.cell.IndigoQueryReactionValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;
import com.ggasoftware.indigo.knime.rautomapper.IndigoReactionAutomapperSettings.AAMode;

public class IndigoReactionAutomapperNodeDialog extends NodeDialogPane {

   private final IndigoReactionAutomapperSettings _settings = new IndigoReactionAutomapperSettings();

   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _colName = new ColumnSelectionComboxBox((Border) null, IndigoReactionValue.class, IndigoQueryReactionValue.class);
   private final JCheckBox _appendColumn = new JCheckBox("Append Column");
   private final JTextField _newColName = new JTextField(20);

   private final JComboBox _mode = new JComboBox(new String[] { AAMode.Discard.toString(), AAMode.Keep.toString(), AAMode.Alter.toString(),
         AAMode.Clear.toString() });
   
   private final JCheckBox _ignoreCharges = new JCheckBox("Ignore Charges");
   private final JCheckBox _ignoreIsotopes = new JCheckBox("Ignore Isotopes");
   private final JCheckBox _ignoreRadicals = new JCheckBox("Ignore Radicals");
   private final JCheckBox _ignoreValence = new JCheckBox("Ignore Valence");

   protected IndigoReactionAutomapperNodeDialog() {
      super();

      JPanel p = new JPanel(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();

      c.anchor = GridBagConstraints.WEST;
      c.insets = new Insets(2, 2, 2, 2);
      c.gridx = 0;
      c.gridy = 0;

      p.add(new JLabel("Reaction column"), c);

      c.gridx = 1;
      p.add(_colName, c);

      c.gridy++;

      c.gridx = 0;
      p.add(_appendColumn, c);
      c.gridx = 1;
      p.add(_newColName, c);

      c.gridy++;

      c.gridx = 0;
      p.add(new JLabel("Reaction AAM mode"), c);
      c.gridx = 1;
      p.add(_mode, c);

      _appendColumn.addChangeListener(new ChangeListener() {
         public void stateChanged(final ChangeEvent e) {
            if (_appendColumn.isSelected()) {
               _newColName.setEnabled(true);
               if ("".equals(_newColName.getText())) {
                  _newColName.setText(_colName.getSelectedColumn() + " (mapped)");
               }
            } else {
               _newColName.setEnabled(false);
            }
         }
      });
      _newColName.setEnabled(_appendColumn.isSelected());
      
      c.gridy++;
      c.gridx = 0;
      p.add(_ignoreCharges, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(_ignoreIsotopes, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(_ignoreRadicals, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(_ignoreValence, c);

      addTab("Standard settings", p);
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
