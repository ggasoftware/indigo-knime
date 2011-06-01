package com.ggasoftware.indigo.knime.compsep;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.cell.IndigoMolValue;

public class IndigoComponentSeparatorNodeDialog extends NodeDialogPane
{
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);
   private final JTextField _newColPrefix = new JTextField(10);

   private IndigoComponentSeparatorSettings _settings = new IndigoComponentSeparatorSettings();
   
   protected IndigoComponentSeparatorNodeDialog ()
   {
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
      p.add(new JLabel("New column prefix"), c);
      c.gridx = 1;
      p.add(_newColPrefix, c);
      
      addTab("Standard settings", p);
   }
   
   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      _settings.loadSettingsForDialog(settings);

      _molColumn.update(specs[0], _settings.colName);
      _newColPrefix.setText(_settings.newColPrefix);
   }

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.colName = _molColumn.getSelectedColumn();
      _settings.newColPrefix = _newColPrefix.getText();
      
      _settings.saveSettings(settings);
   }   
}
