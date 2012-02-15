package com.ggasoftware.indigo.knime.combchem;

import javax.swing.JTextField;
import javax.swing.border.Border;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryReactionValue;
import com.ggasoftware.indigo.knime.common.IndigoDialogPanel;



/**
 * 
 */
public class IndigoReactionGeneratorNodeDialog extends NodeDialogPane {
   private final IndigoReactionGeneratorSettings _settings = new IndigoReactionGeneratorSettings();
   
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _reactionColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoQueryReactionValue.class);
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn1 = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn2 = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);

   private final JTextField _newColName = new JTextField(20);
   
   
   /**
    */
   protected IndigoReactionGeneratorNodeDialog() {
      super();
      _registerDialogComponents();

      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Reaction column", _reactionColumn);
      dialogPanel.addItem("Reactans 1 column", _molColumn1);
      dialogPanel.addItem("Reactans 2 column", _molColumn2);
      dialogPanel.addItem("New column name", _newColName);
      
      addTab("Standard Settings", dialogPanel.getPanel());
   }
   
   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_molColumn1, IndigoReactionGeneratorSettings.MOL_PORT1,  _settings.molColumn1);
      _settings.registerDialogComponent(_molColumn2, IndigoReactionGeneratorSettings.MOL_PORT2,  _settings.molColumn2, true);
      _settings.registerDialogComponent(_reactionColumn, IndigoReactionGeneratorSettings.REACTION_PORT, _settings.reactionColumn);
      _settings.registerDialogComponent(_newColName, _settings.newColName);
   }

   @Override
   protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs) throws NotConfigurableException {
      try {
         _settings.loadSettingsFrom(settings);
         _settings.loadDialogSettings(specs);
         
         DataTableSpec molSpec2 = specs[IndigoReactionGeneratorSettings.MOL_PORT2];
         if (molSpec2 == null) {
            _molColumn2.setVisible(false);
         }
         
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
