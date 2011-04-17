package com.ggasoftware.indigo.knime;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.ColumnSelectionComboxBox;

public class IndigoRGroupDecomposerNodeDialog extends NodeDialogPane
{
   IndigoRGroupDecomposerSettings _settings = new IndigoRGroupDecomposerSettings();
   
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn2 = new ColumnSelectionComboxBox(
         (Border) null, IndigoQueryMolValue.class);
   private final JTextField _newColPrefix = new JTextField(10);
   private final JCheckBox _aromatize = new JCheckBox("Aromatize");
   
   /**
    * New pane for configuring the IndigoRGroupDecomposer node.
    */
   protected IndigoRGroupDecomposerNodeDialog()
   {
      super();

      JPanel p = new JPanel(new GridBagLayout());

      GridBagConstraints c = new GridBagConstraints();

      c.anchor = GridBagConstraints.WEST;
      c.insets = new Insets(2, 2, 2, 2);
      c.gridy = 0;
      c.gridx = 0;
      p.add(new JLabel("Molecule column"), c);
      c.gridx = 1;
      p.add(_molColumn, c);

      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Query molecule column"), c);
      c.gridx = 1;
      p.add(_molColumn2, c);

      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("R-Group column prefix"), c);
      c.gridx = 1;
      p.add(_newColPrefix, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(_aromatize, c);
      
      addTab("Standard settings", p);   }
   
   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      _settings.loadSettingsForDialog(settings);

      _molColumn.update(specs[0], _settings.colName);
      _molColumn2.update(specs[1], _settings.colName2);
      _newColPrefix.setText(_settings.newColPrefix);
      _aromatize.setSelected(_settings.aromatize);
   }

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.colName = _molColumn.getSelectedColumn();
      _settings.colName2 = _molColumn2.getSelectedColumn();
      _settings.newColPrefix = _newColPrefix.getText();
      _settings.aromatize = _aromatize.isSelected();
      
      _settings.saveSettings(settings);
   }   
}
