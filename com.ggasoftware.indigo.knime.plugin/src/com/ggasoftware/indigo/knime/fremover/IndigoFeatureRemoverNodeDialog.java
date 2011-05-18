package com.ggasoftware.indigo.knime.fremover;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

import com.ggasoftware.indigo.knime.cell.IndigoMolValue;

public class IndigoFeatureRemoverNodeDialog extends NodeDialogPane
{
   private final IndigoFeatureRemoverSettings _settings = new IndigoFeatureRemoverSettings();
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);

   private final JCheckBox _appendColumn = new JCheckBox("Append column");
   private final JTextField _newColName = new JTextField(20);
   
   private final Map<String, JCheckBox> _features = new HashMap<String, JCheckBox>();

   private final ChangeListener _changeListener = new ChangeListener() {
      public void stateChanged (ChangeEvent e)
      {
         if (_appendColumn.isEnabled())
            _newColName.setEnabled(_appendColumn.isSelected());
         
         if (_newColName.isEnabled() && _newColName.getText().length() < 1)
            _newColName.setText(_molColumn.getSelectedColumn() + " (featureless)");
      }
   };
   
   /**
    * New pane for configuring the IndigoSmartsMatcher node.
    */
   protected IndigoFeatureRemoverNodeDialog ()
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
      p.add(_appendColumn, c);
      c.gridx = 1;
      p.add(_newColName, c);
      
      _appendColumn.addChangeListener(_changeListener);

      _appendColumn.setEnabled(true);
      _newColName.setEnabled(false);
      
      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Remove the following features:"), c);

      for (String key : IndigoFeatureRemoverNodeModel.removers.keySet())
      {
         c.gridy++;
         c.gridx = 0;
         JCheckBox cb = new JCheckBox(key);
         _features.put(key, cb);
         p.add(cb, c);
      }
      
      addTab("Standard settings", p);
   }

   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      _settings.loadSettingsForDialog(settings);

      _molColumn.update(specs[0], _settings.colName);
      _newColName.setText(_settings.newColName);
      _appendColumn.setSelected(_settings.appendColumn);
      
      for (JCheckBox cb : _features.values())
         cb.setSelected(false);
      
      for (String s : _settings.selectedFeatures)
         _features.get(s).setSelected(true);
   }

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.colName = _molColumn.getSelectedColumn();
      _settings.appendColumn = _appendColumn.isSelected();
      _settings.newColName = _newColName.getText();

      ArrayList<String> al = new ArrayList<String>();
      
      for (String s : _features.keySet())
         if (_features.get(s).isSelected())
            al.add(s);
      
      _settings.selectedFeatures = (String[])al.toArray(new String[]{});
      
      _settings.saveSettings(settings);
   }
}
