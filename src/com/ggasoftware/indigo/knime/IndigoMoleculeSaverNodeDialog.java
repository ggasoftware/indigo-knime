package com.ggasoftware.indigo.knime;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.knime.chem.types.*;
import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.IndigoMoleculeSaverSettings.Format;

public class IndigoMoleculeSaverNodeDialog extends NodeDialogPane
{

   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class, IndigoQueryMolValue.class);
   private JComboBox _destFormat;
   private final JCheckBox _appendColumn = new JCheckBox("Append Column");
   private final JTextField _newColName = new JTextField(20);
   private final JCheckBox _generateCoords = new JCheckBox("Generate coordinates if needed");
   private final IndigoMoleculeSaverSettings _settings = new IndigoMoleculeSaverSettings();

   /**
    * New pane for configuring the IndigoMoleculeSaver node.
    */
   protected IndigoMoleculeSaverNodeDialog (boolean query)
   {
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
      p.add(new JLabel("Destination format   "), c);
      c.gridx = 1;
      if (query)
         _destFormat = new JComboBox(new Object[] {Format.Mol, Format.Smiles});
      else
         _destFormat = new JComboBox(new Object[] {
               Format.Mol, Format.Smiles, Format.CanonicalSmiles, Format.CML });
      p.add(_destFormat, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(_generateCoords, c);
      _generateCoords.setSelected(true);

      _destFormat.addItemListener(new ItemListener()
      {
         @Override
         public void itemStateChanged (ItemEvent arg0)
         {
            Format selected = (Format)_destFormat.getSelectedItem(); 
            if (selected == Format.CML || selected == Format.Mol)
               _generateCoords.setEnabled(true);
            else
               _generateCoords.setEnabled(false);
         }
      });
      
      _appendColumn.addChangeListener(new ChangeListener()
      {
         public void stateChanged (final ChangeEvent e)
         {
            if (_appendColumn.isSelected())
            {
               _newColName.setEnabled(true);
               if ("".equals(_newColName.getText()))
               {
                  _newColName.setText(_molColumn.getSelectedColumn()
                        + " (saved)");
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
      _appendColumn.setSelected(!_settings.replaceColumn);
      _newColName.setEnabled(!_settings.replaceColumn);
      _newColName.setText(_settings.newColName);
      _destFormat.setSelectedItem(_settings.destFormat);
      _generateCoords.setSelected(_settings.generateCoords);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveSettingsTo (final NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.colName = _molColumn.getSelectedColumn();
      _settings.replaceColumn = !_appendColumn.isSelected();
      _settings.newColName = _newColName.getText();
      _settings.destFormat = ((Format)_destFormat.getSelectedItem());
      _settings.generateCoords = _generateCoords.isSelected();
      _settings.saveSettings(settings);
   }
}
