package com.ggasoftware.indigo.knime.compsep;

import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;

public class IndigoComponentSeparatorNodeDialog extends NodeDialogPane
{
   private IndigoComponentSeparatorSettings _settings = new IndigoComponentSeparatorSettings();
   
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);
   
   private final JTextField _newColPrefix = new JTextField(10);
   private final JCheckBox _limitComponentNumber = new JCheckBox("Limit component number");
   private final JSpinner _componentNumber = new JSpinner(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));

   private ChangeListener _limitListener = new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
         if(_limitComponentNumber.isSelected())
            _componentNumber.setEnabled(true);
         else
            _componentNumber.setEnabled(false);
      }
   };;;
   
   protected IndigoComponentSeparatorNodeDialog ()
   {
      _settings.registerDialogComponent(_molColumn, IndigoComponentSeparatorSettings.INPUT_PORT, _settings.colName);
      _settings.registerDialogComponent(_newColPrefix, _settings.newColPrefix);
      _settings.registerDialogComponent(_limitComponentNumber, _settings.limitComponentNumber);
      _settings.registerDialogComponent(_componentNumber, _settings.componentNumber);
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Molecule column", _molColumn);
      dialogPanel.addItem("New column prefix", _newColPrefix);
      dialogPanel.addItemsPanel("Component Separator Settings");
      dialogPanel.addItem(_limitComponentNumber, _componentNumber);
      
      _limitComponentNumber.addChangeListener(_limitListener);
      
      addTab("Standard settings", dialogPanel.getPanel());
   }
   
   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      try {
         _settings.loadSettingsFrom(settings);
         _settings.loadDialogSettings(specs);
         
         _limitListener.stateChanged(null);
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage());
      }
   }

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.saveDialogSettings();
      _settings.saveSettingsTo(settings);
   }   
}
