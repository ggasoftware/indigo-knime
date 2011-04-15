package com.ggasoftware.indigo.knime;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.knime.chem.types.*;
import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.IndigoMoleculeSaverSettings.Format;

/**
 * <code>NodeDialog</code> for the "IndigoMoleculeSaver" Node.
 * 
 * @author GGA Software Services LLC
 */
public class IndigoMoleculeSaverNodeDialog extends NodeDialogPane
{

   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);
   private final JComboBox _destFormat = new JComboBox(new Object[] {
         Format.SDF, Format.Smiles, Format.CanonicalSmiles, Format.CML });
   private final JCheckBox _appendColumn = new JCheckBox("Append Column");
   private final JTextField _newColName = new JTextField(20);
   private final IndigoMoleculeSaverSettings _settings = new IndigoMoleculeSaverSettings();

   /**
    * New pane for configuring the IndigoMoleculeSaver node.
    */
   @SuppressWarnings("serial")
   protected IndigoMoleculeSaverNodeDialog()
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
      p.add(_destFormat, c);

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

      _destFormat.setRenderer(new DefaultListCellRenderer()
      {
         @Override
         public Component getListCellRendererComponent (final JList list,
               final Object value, final int index, final boolean isSelected,
               final boolean cellHasFocus)
         {
            super.getListCellRendererComponent(list, value, index, isSelected,
                  cellHasFocus);
            if (value == Format.SDF)
            {
               setIcon(SdfValue.UTILITY.getIcon());
               setText("SDF");
            }
            else if (value == Format.Smiles)
            {
               setIcon(SmilesValue.UTILITY.getIcon());
               setText("SMILES");
            }
            else if (value == Format.CanonicalSmiles)
            {
               setIcon(SmilesValue.UTILITY.getIcon());
               setText("Canonical SMILES");
            }
            else if (value == Format.CML)
            {
               setIcon(CMLValue.UTILITY.getIcon());
               setText("CML");
            }
            else
            {
               setIcon(null);
               setText("");
            }
            return this;
         }
      });

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
      _settings.destFormat = ((Format) _destFormat.getSelectedItem());
      _settings.saveSettings(settings);
   }
}
