package com.ggasoftware.indigo.knime.fremover;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

import com.ggasoftware.indigo.knime.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.IndigoNodeSettings;
import com.ggasoftware.indigo.knime.IndigoNodeSettings.STRUCTURE_TYPE;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;

public class IndigoFeatureRemoverNodeDialog extends NodeDialogPane
{
   private final IndigoFeatureRemoverSettings _settings = new IndigoFeatureRemoverSettings();
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _indigoColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class, IndigoReactionValue.class);

   private final JCheckBox _appendColumn = new JCheckBox("Append column");
   private final JTextField _newColName = new JTextField(20);
   
   private final Map<String, JCheckBox> _features = new HashMap<String, JCheckBox>();

   private final JLabel _structureType = new JLabel();
   private DataTableSpec _indigoSpec;
   private final ChangeListener _changeListener = new ChangeListener() {
      public void stateChanged (ChangeEvent e)
      {
         if (_appendColumn.isEnabled())
            _newColName.setEnabled(_appendColumn.isSelected());
         
         if (_newColName.isEnabled() && _newColName.getText().length() < 1)
            _newColName.setText(_indigoColumn.getSelectedColumn() + " (featureless)");
      }
   };
   
   private final ItemListener _columnChangeListener = new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
         STRUCTURE_TYPE stype = _getStructureType();
         switch(stype) {
            case Unknown:
               _structureType.setText("Unknown");
               break;
            case Reaction:
               _structureType.setText("Reaction");
               break;
            case Molecule:
               _structureType.setText("Molecule");
               break;
         }
      }
   };
   /**
    * 
    */
   protected IndigoFeatureRemoverNodeDialog ()
   {
      super();
      
      _registerDialogComponents();
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Structure type", _structureType);
      dialogPanel.addItem("Indigo column", _indigoColumn);
      dialogPanel.addItem(_appendColumn, _newColName);
      dialogPanel.addItemsPanel("Remove the following features:");

      for (String key : IndigoFeatureRemoverNodeModel.names) {
         JCheckBox cb = new JCheckBox(key);
         _features.put(key, cb);
         dialogPanel.addItem(cb);
      }
      
      _appendColumn.addChangeListener(_changeListener);
      _indigoColumn.addItemListener(_columnChangeListener);
      
      addTab("Standard settings", dialogPanel.getPanel());
   }

   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_indigoColumn, IndigoFeatureRemoverSettings.INPUT_PORT, _settings.colName);
      _settings.registerDialogComponent(_newColName, _settings.newColName);
      _settings.registerDialogComponent(_appendColumn, _settings.appendColumn);
   }
   
   /*
    * Returns current column selection state
    */
   private STRUCTURE_TYPE _getStructureType() {
      return IndigoNodeSettings.getStructureType(_indigoSpec, _indigoColumn.getSelectedColumn());
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
         
         _indigoSpec = specs[IndigoFeatureRemoverSettings.INPUT_PORT];
         _columnChangeListener.itemStateChanged(null);
         
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage());
      }
   }

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      
      STRUCTURE_TYPE stype = _getStructureType();

      if (stype.equals(STRUCTURE_TYPE.Unknown))
         throw new InvalidSettingsException("can not define the indigo column type");
      
      ArrayList<String> al = new ArrayList<String>();
      
      for (String s : _features.keySet())
         if (_features.get(s).isSelected())
            al.add(s);
      
      _settings.selectedFeatures.setStringArrayValue((String[])al.toArray(new String[]{}));
      
      _settings.saveDialogSettings();
      _settings.saveSettingsTo(settings);
   }
}
