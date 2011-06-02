package com.ggasoftware.indigo.knime.murcko;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

import com.ggasoftware.indigo.knime.cell.IndigoMolValue;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class IndigoMurckoScaffoldNodeDialog extends NodeDialogPane
{
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);

   private final JCheckBox _appendColumn = new JCheckBox("Append Column");
   private final JTextField _newColName = new JTextField(20);
   private final IndigoMurckoScaffoldSettings _settings = new IndigoMurckoScaffoldSettings();
   private final JCheckBox _removeTerminalRings3 = new JCheckBox("Remove terminal 3-rings");
   private final JCheckBox _removeTerminalRings4 = new JCheckBox("Remove terminal 4-rings");
   
   public IndigoMurckoScaffoldNodeDialog ()
   {
      super();

      JPanel p = new JPanel(new GridBagLayout());

      GridBagConstraints c = new GridBagConstraints();

      c.anchor = GridBagConstraints.WEST;
      c.insets = new Insets(2, 2, 2, 2);
      c.gridx = 0;
      c.gridy = 0;
      p.add(new JLabel("Indigo column   "), c);
      c.gridx = 1;
      p.add(_molColumn, c);

      c.gridy++;
      c.gridx = 0;
      p.add(_appendColumn, c);
      c.gridx = 1;
      p.add(_newColName, c);

      c.gridy++;
      c.gridx = 0;
      p.add(_removeTerminalRings3, c);
      c.gridy++;
      c.gridx = 0;
      p.add(_removeTerminalRings4, c);
      
      _appendColumn.addChangeListener(new ChangeListener()
      {
         public void stateChanged (final ChangeEvent e)
         {
            if (_appendColumn.isSelected())
            {
               _newColName.setEnabled(true);
               if ("".equals(_newColName.getText()))
               {
                  _newColName.setText(_molColumn.getSelectedColumn() + " (murcko)");
               }
            }
            else
            {
               _newColName.setEnabled(false);
            }
         }
      });
      _newColName.setEnabled(_appendColumn.isSelected());

      addTab("Standard settings", p);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      _settings.loadSettingsForDialog(settings);

      _molColumn.update(specs[0], _settings.colName);
      _appendColumn.setSelected(_settings.appendColumn);
      _newColName.setEnabled(_settings.appendColumn);
      _newColName.setText(_settings.newColName);
      _removeTerminalRings3.setSelected(_settings.removeTerminalRings3);
      _removeTerminalRings4.setSelected(_settings.removeTerminalRings4);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveSettingsTo (final NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.colName = _molColumn.getSelectedColumn();
      _settings.appendColumn = _appendColumn.isSelected();
      _settings.newColName = _newColName.getText();
      _settings.removeTerminalRings3 = _removeTerminalRings3.isSelected();
      _settings.removeTerminalRings4 = _removeTerminalRings4.isSelected();
      
      _settings.saveSettings(settings);
   }
}
