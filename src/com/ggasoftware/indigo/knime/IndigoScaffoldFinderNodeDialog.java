package com.ggasoftware.indigo.knime;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;
import javax.swing.border.Border;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.IndigoScaffoldFinderSettings.Method;

public class IndigoScaffoldFinderNodeDialog extends NodeDialogPane
{
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);

   private final JComboBox _method = new JComboBox(new Object[] {Method.Exact});
   private final JTextField _newColName = new JTextField(20);

   private final IndigoScaffoldFinderSettings _settings = new IndigoScaffoldFinderSettings();

   /**
    * New pane for configuring the IndigoScaffoldFinder node.
    */
   protected IndigoScaffoldFinderNodeDialog ()
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
      p.add(new JLabel("New column with scaffolds"), c);
      c.gridx = 1;
      p.add(_newColName, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Method"), c);
      c.gridx = 1;
      p.add(_method, c);
      
      addTab("Standard settings", p);
   }

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.colName = _molColumn.getSelectedColumn();
      _settings.newColName = _newColName.getText();
      _settings.method= ((Method)_method.getSelectedItem());

      _settings.saveSettings(settings);
   }
   
   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      _settings.loadSettingsForDialog(settings);

      _molColumn.update(specs[0], _settings.colName);
      _newColName.setText(_settings.newColName);
      _method.setSelectedItem(_settings.method);
   }
}
