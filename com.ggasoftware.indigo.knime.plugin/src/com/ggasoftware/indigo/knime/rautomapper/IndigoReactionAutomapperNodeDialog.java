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
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.cell.IndigoQueryReactionValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;
import com.ggasoftware.indigo.knime.rautomapper.IndigoReactionAutomapperNodeModel.AAMode;

public class IndigoReactionAutomapperNodeDialog extends NodeDialogPane {

   private final SettingsModelColumnName m_column = new SettingsModelColumnName(
         IndigoReactionAutomapperNodeModel.CFGKEY_COLUMN,
         IndigoReactionAutomapperNodeModel.DEFAULT_COLUMN);
   private final SettingsModelBoolean m_replace = new SettingsModelBoolean(
         IndigoReactionAutomapperNodeModel.CFGKEY_REPLACE,
         IndigoReactionAutomapperNodeModel.DEFAULT_REPLACE);
   private final SettingsModelColumnName m_newColumn = new SettingsModelColumnName(
         IndigoReactionAutomapperNodeModel.CFGKEY_NEWCOLUMN,
         IndigoReactionAutomapperNodeModel.DEFAULT_NEWCOLUMN);
   private final SettingsModelInteger m_mode = new SettingsModelInteger(
         IndigoReactionAutomapperNodeModel.CFGKEY_MODE,
         IndigoReactionAutomapperNodeModel.DEFAULT_MODE );
   
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _colName = new ColumnSelectionComboxBox(
         (Border) null,
         IndigoReactionValue.class, IndigoQueryReactionValue.class);
   private final JCheckBox _appendColumn = new JCheckBox("Append Column");
   private final JTextField _newColName = new JTextField(20);
   private final JComboBox _mode = new JComboBox(
         new String[] {
               AAMode.Discard.toString(),
               AAMode.Keep.toString(),
               AAMode.Alter.toString(),
               AAMode.Clear.toString()
         });
   
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
        p.add(new JLabel("Reaction column"), c);
        c.gridx = 1;
        p.add(_mode, c);

        _appendColumn.addChangeListener(new ChangeListener() {
           public void stateChanged (final ChangeEvent e) {
              if (_appendColumn.isSelected()) {
                 _newColName.setEnabled(true);
                 if ("".equals(_newColName.getText())) {
                    _newColName.setText(_colName.getSelectedColumn() + " (mapped)");
                 }
              }
              else {
                 _newColName.setEnabled(false);
              }
           }
        });
        _newColName.setEnabled(_appendColumn.isSelected());

        addTab("Standard settings", p);
    }

   
   
   @Override
   protected void loadSettingsFrom(NodeSettingsRO settings,
         DataTableSpec[] specs) throws NotConfigurableException {
      try {
         m_column.loadSettingsFrom(settings);
         _colName.update(specs[0], m_column.getStringValue());
         m_replace.loadSettingsFrom(settings);
         _appendColumn.setSelected(!m_replace.getBooleanValue());
         m_newColumn.loadSettingsFrom(settings);
         _newColName.setText(m_newColumn.getStringValue());
         m_mode.loadSettingsFrom(settings);
         _mode.setSelectedIndex(m_mode.getIntValue());
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage(), e);
      }
   }

   @Override
   protected void saveSettingsTo(NodeSettingsWO settings)
         throws InvalidSettingsException {
      m_column.setStringValue(_colName.getSelectedColumn());
      m_column.saveSettingsTo(settings);
      m_replace.setBooleanValue(!_appendColumn.isSelected());
      m_replace.saveSettingsTo(settings);
      m_newColumn.setStringValue(_newColName.getText());
      m_newColumn.saveSettingsTo(settings);
      m_mode.setIntValue(_mode.getSelectedIndex());
      m_mode.saveSettingsTo(settings);
   }
}

