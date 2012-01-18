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
   private final IndigoMurckoScaffoldSettings _settings = new IndigoMurckoScaffoldSettings();
   
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);

   private final JCheckBox _appendColumn = new JCheckBox("Append Column");
   private final JTextField _newColName = new JTextField(20);
   
   private final JCheckBox _removeTerminalRings3 = new JCheckBox("Remove terminal 3-rings");
   private final JCheckBox _removeTerminalRings4 = new JCheckBox("Remove terminal 4-rings");

   private ChangeListener _changeListener = new ChangeListener() {
      public void stateChanged(final ChangeEvent e) {
         if (_appendColumn.isSelected()) {
            _newColName.setEnabled(true);
            if ("".equals(_newColName.getText())) {
               _newColName.setText(_molColumn.getSelectedColumn() + " (murcko)");
            }
         } else {
            _newColName.setEnabled(false);
         }
      }
   };
   
   public IndigoMurckoScaffoldNodeDialog ()
   {
      super();
      
      _registerDialogComponents();

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
      
      _appendColumn.addChangeListener(_changeListener );

      addTab("Standard settings", p);
   }
   

   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_molColumn, IndigoMurckoScaffoldSettings.INPUT_PORT, _settings.colName);
      _settings.registerDialogComponent(_appendColumn, _settings.appendColumn);
      _settings.registerDialogComponent(_newColName, _settings.newColName);
      _settings.registerDialogComponent(_removeTerminalRings3, _settings.removeTerminalRings3);
      _settings.registerDialogComponent(_removeTerminalRings4, _settings.removeTerminalRings4);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      try {
         _settings.loadSettingsFrom(settings);
         _settings.loadDialogSettings(specs);
         
         _changeListener.stateChanged(null);
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage());
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveSettingsTo (final NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.saveDialogSettings();
      _settings.saveSettingsTo(settings);
   }
}
