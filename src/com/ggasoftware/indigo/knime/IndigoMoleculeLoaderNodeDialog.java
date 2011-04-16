package com.ggasoftware.indigo.knime;

import java.awt.*;
import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.node.util.*;
import org.knime.chem.types.CMLValue;
import org.knime.chem.types.MolValue;
import org.knime.chem.types.SdfValue;
import org.knime.chem.types.SmartsValue;
import org.knime.chem.types.SmilesValue;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

/**
 * <code>NodeDialog</code> for the "IndigoMoleculeLoader" Node.
 * 
 * @author GGA Software Services LLC
 */
public class IndigoMoleculeLoaderNodeDialog extends NodeDialogPane
{
   private ColumnSelectionComboxBox _colName;
   private final JCheckBox _treatXAsPseudoatom = new JCheckBox();
   private final JCheckBox _ignoreStereochemistryErrors = new JCheckBox();
   private final JCheckBox _appendColumn = new JCheckBox("Append Column");
   private final JTextField _newColName = new JTextField(20);
   IndigoMoleculeLoaderSettings _settings = new IndigoMoleculeLoaderSettings();

   /**
    * New pane for configuring IndigoMoleculeLoader node dialog. This is just a
    * suggestion to demonstrate possible default dialog components.
    */
   @SuppressWarnings("unchecked")
   protected IndigoMoleculeLoaderNodeDialog (boolean query)
   {
      super();

      JPanel p = new JPanel(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();

      c.anchor = GridBagConstraints.WEST;
      c.insets = new Insets(2, 2, 2, 2);
      c.gridx = 0;
      c.gridy = 0;
      if (query)
      {
         p.add(new JLabel("Query molecule column"), c);
         _colName = new ColumnSelectionComboxBox(
               (Border) null, SdfValue.class, MolValue.class, SmilesValue.class, SmartsValue.class);
      }
      else
      {
         p.add(new JLabel("Molecule column   "), c);
         _colName = new ColumnSelectionComboxBox(
               (Border) null, SdfValue.class, MolValue.class, SmilesValue.class, CMLValue.class);
      }
      c.gridx = 1;
      p.add(_colName, c);

      c.gridy++;

      c.gridx = 0;
      p.add(_appendColumn, c);
      c.gridx = 1;
      p.add(_newColName, c);

      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Treat X as pseudoatom"), c);
      c.gridx = 1;
      p.add(_treatXAsPseudoatom, c);

      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Ignore stereochemistry errors"), c);
      c.gridx = 1;
      p.add(_ignoreStereochemistryErrors, c);

      _appendColumn.addChangeListener(new ChangeListener()
      {
         public void stateChanged (final ChangeEvent e)
         {
            if (_appendColumn.isSelected())
            {
               _newColName.setEnabled(true);
               if ("".equals(_newColName.getText()))
               {
                  _newColName.setText(_colName.getSelectedColumn()
                        + " (Indigo)");
               }
            }
            else
               _newColName.setEnabled(false);
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

      _colName.update(specs[0], _settings.colName);
      _appendColumn.setSelected(!_settings.replaceColumn);
      _newColName.setEnabled(!_settings.replaceColumn);
      _newColName
            .setText(_settings.newColName != null ? _settings.newColName : "");
      _treatXAsPseudoatom.setSelected(_settings.treatXAsPseudoatom);
      _ignoreStereochemistryErrors
            .setSelected(_settings.ignoreStereochemistryErrors);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveSettingsTo (final NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.colName = _colName.getSelectedColumn();
      _settings.replaceColumn = !_appendColumn.isSelected();
      _settings.newColName = _newColName.getText();
      _settings.treatXAsPseudoatom = _treatXAsPseudoatom.isSelected();
      _settings.ignoreStereochemistryErrors = _ignoreStereochemistryErrors
            .isSelected();
      _settings.saveSettings(settings);
   }
}
