package com.ggasoftware.indigo.knime.compsep;

import javax.swing.JTextField;
import javax.swing.border.Border;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.IndigoDialogPanel;
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
      _settings.registerDialogComponent(_molColumn, IndigoComponentSeparatorSettings.INPUT_PORT, _settings.colName);
      _settings.registerDialogComponent(_newColPrefix, _settings.newColPrefix);
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Molecule column", _molColumn);
      dialogPanel.addItem("New column prefix", _newColPrefix);
      
      addTab("Standard settings", dialogPanel.getPanel());
   }
   
   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      try {
         _settings.loadSettingsFrom(settings);
         _settings.loadDialogSettings(specs);
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
