package com.ggasoftware.indigo.knime.murcko;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

import com.ggasoftware.indigo.knime.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class IndigoMurckoScaffoldNodeDialog extends NodeDialogPane
{
   private final IndigoMurckoScaffoldSettings _settings = new IndigoMurckoScaffoldSettings();
   
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);

   private final JCheckBox _appendColumn = new JCheckBox("Append Column");
   private final JTextField _newColName = new JTextField(20);
   
   private final JCheckBox _removeTerminalRings3 = new JCheckBox("Remove terminal 3-rings");
   private final JCheckBox _removeTerminalRings4 = new JCheckBox("Remove terminal 4-rings");

   private ChangeListener _changeListener = new ChangeListener() {
      public void stateChanged(final ChangeEvent e) {
         if (_appendColumn.isSelected()) {
            _newColName.setEnabled(true);
            if ("".equals(_newColName.getText())) {
               _newColName.setText(_molColumn.getSelectedColumn() + " (murcko)");
            }
         } else {
            _newColName.setEnabled(false);
         }
      }
   };
   
   public IndigoMurckoScaffoldNodeDialog ()
   {
      super();
      
      _registerDialogComponents();
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Molecule column", _molColumn);
      dialogPanel.addItem(_appendColumn, _newColName);
      dialogPanel.addItemsPanel("Murcko Scaffold Settings");
      dialogPanel.addItem(_removeTerminalRings3);
      dialogPanel.addItem(_removeTerminalRings4);
      
      IndigoDialogPanel.setDefaultFont(_appendColumn);
      IndigoDialogPanel.setDefaultFont(_removeTerminalRings3);
      IndigoDialogPanel.setDefaultFont(_removeTerminalRings4);

      _appendColumn.addChangeListener(_changeListener );

      addTab("Standard settings", dialogPanel.getPanel());
   }
   

   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_molColumn, IndigoMurckoScaffoldSettings.INPUT_PORT, _settings.colName);
      _settings.registerDialogComponent(_appendColumn, _settings.appendColumn);
      _settings.registerDialogComponent(_newColName, _settings.newColName);
      _settings.registerDialogComponent(_removeTerminalRings3, _settings.removeTerminalRings3);
      _settings.registerDialogComponent(_removeTerminalRings4, _settings.removeTerminalRings4);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      try {
         _settings.loadSettingsFrom(settings);
         _settings.loadDialogSettings(specs);
         
         _changeListener.stateChanged(null);
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage());
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveSettingsTo (final NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.saveDialogSettings();
      _settings.saveSettingsTo(settings);
   }
}
