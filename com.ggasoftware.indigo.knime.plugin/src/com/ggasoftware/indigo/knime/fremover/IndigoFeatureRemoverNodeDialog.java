package com.ggasoftware.indigo.knime.fremover;

import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

import com.ggasoftware.indigo.knime.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;

public class IndigoFeatureRemoverNodeDialog extends NodeDialogPane
{
   private final IndigoFeatureRemoverSettings _settings = new IndigoFeatureRemoverSettings();
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _indigoColumn = new ColumnSelectionComboxBox(
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
            _newColName.setText(_indigoColumn.getSelectedColumn() + " (featureless)");
      }
   };
   
   /**
    * New pane for configuring the IndigoSmartsMatcher node.
    */
   protected IndigoFeatureRemoverNodeDialog ()
   {
      super();
      
      _registerDialogComponents();
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Indigo column", _indigoColumn);
      dialogPanel.addItem(_appendColumn, _newColName);
      dialogPanel.addItemsPanel("Remove the following features:");

      for (String key : IndigoFeatureRemoverNodeModel.names) {
         JCheckBox cb = new JCheckBox(key);
         _features.put(key, cb);
         IndigoDialogPanel.setDefaultFont(cb);
         dialogPanel.addItem(cb);
      }
      
      IndigoDialogPanel.setDefaultFont(_appendColumn);
      _appendColumn.addChangeListener(_changeListener);
      
      addTab("Standard settings", dialogPanel.getPanel());
   }

   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_indigoColumn, IndigoFeatureRemoverSettings.INPUT_PORT, _settings.colName);
      _settings.registerDialogComponent(_newColName, _settings.newColName);
      _settings.registerDialogComponent(_appendColumn, _settings.appendColumn);
   }

   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
 {
      try {
         _settings.loadSettingsFrom(settings);
         _settings.loadDialogSettings(specs);
         
         _changeListener.stateChanged(null);

         for (JCheckBox cb : _features.values())
            cb.setSelected(false);

         String[] selected = _settings.selectedFeatures.getStringArrayValue();
         
         if(selected != null)
            for (String s : selected)
               _features.get(s).setSelected(true);
         
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage());
      }
   }

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      
      ArrayList<String> al = new ArrayList<String>();
      
      for (String s : _features.keySet())
         if (_features.get(s).isSelected())
            al.add(s);
      
      _settings.selectedFeatures.setStringArrayValue((String[])al.toArray(new String[]{}));
      
      _settings.saveDialogSettings();
      _settings.saveSettingsTo(settings);
   }
}
