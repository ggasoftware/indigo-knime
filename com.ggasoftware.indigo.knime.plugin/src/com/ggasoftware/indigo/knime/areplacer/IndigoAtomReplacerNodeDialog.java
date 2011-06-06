package com.ggasoftware.indigo.knime.areplacer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
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

import com.ggasoftware.indigo.knime.cell.IndigoMolValue;

public class IndigoAtomReplacerNodeDialog extends NodeDialogPane
{
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);

   private final JCheckBox _appendColumn = new JCheckBox("Append Column");
   private final JTextField _newColName = new JTextField(20);
   private final IndigoAtomReplacerSettings _settings = new IndigoAtomReplacerSettings();
   private final JTextField _newAtomLabel = new JTextField(4);

   /**
    * New pane for configuring the IndigoAtomReplacer node.
    */
   protected IndigoAtomReplacerNodeDialog()
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
      p.add(new JLabel("New atom label:"), c);
      c.gridx = 1;
      p.add(_newAtomLabel, c);
      
      _appendColumn.addChangeListener(new ChangeListener()
      {
         public void stateChanged (final ChangeEvent e)
         {
            if (_appendColumn.isSelected())
            {
               _newColName.setEnabled(true);
               if ("".equals(_newColName.getText()))
               {
                  _newColName.setText(_molColumn.getSelectedColumn() + " (replaced atoms)");
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
      _newAtomLabel.setText(_settings.newAtomLabel);
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
      _settings.newAtomLabel = _newAtomLabel.getText();
      _settings.saveSettings(settings);
   }
}
