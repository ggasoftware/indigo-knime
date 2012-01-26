package com.ggasoftware.indigo.knime.transform;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.util.ColumnFilter;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryReactionValue;



/**
 * 
 */
public class IndigoMoleculeTransformNodeDialog extends NodeDialogPane {
   private final IndigoMoleculeTransformSettings _settings = new IndigoMoleculeTransformSettings();
   
   private final ColumnSelectionComboxBox _reactionColumn = new ColumnSelectionComboxBox(
         (Border) null, new ColumnFilter() {
            public boolean includeColumn(DataColumnSpec colSpec) {
               if(colSpec.getType().isCompatible(IndigoQueryReactionValue.class))
                  return true;
               return false;
            }
            public String allFilteredMsg() {
               return "no 'IndigoQueryReactionValue' column type was found at port " + IndigoMoleculeTransformSettings.REACTION_PORT ;
            }
         });
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, new ColumnFilter() {
            public boolean includeColumn(DataColumnSpec colSpec) {
               if(colSpec.getType().isCompatible(IndigoMolValue.class))
                  return true;
               return false;
            }
            public String allFilteredMsg() {
               return "no 'IndigoMolValue' column type was found at port " + IndigoMoleculeTransformSettings.MOL_PORT;
            }
         });
   private final JCheckBox _appendColumn = new JCheckBox("Append column");
   private final JTextField _newColName = new JTextField(20);
   
   
   ChangeListener _changeListener = new ChangeListener() {
      public void stateChanged(final ChangeEvent e) {
         if (_appendColumn.isSelected()) {
            _newColName.setEnabled(true);
            if ("".equals(_newColName.getText())) {
               _newColName.setText(_molColumn.getSelectedColumn()
                     + " (transformed)");
            }
         } else {
            _newColName.setEnabled(false);
         }
      }
   };

   /**
    */
   protected IndigoMoleculeTransformNodeDialog() {
      super();
      _registerDialogComponents();

      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Molecule column", _molColumn);
      dialogPanel.addItem("Query Reaction column", _reactionColumn);
      dialogPanel.addItem(_appendColumn, _newColName);
      
      _appendColumn.addChangeListener(_changeListener);
      
      addTab("Standard Settings", dialogPanel.getPanel());
   }
   
   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_molColumn, IndigoMoleculeTransformSettings.MOL_PORT,  _settings.molColumn);
      _settings.registerDialogComponent(_reactionColumn, IndigoMoleculeTransformSettings.REACTION_PORT,  _settings.reactionColumn);
      _settings.registerDialogComponent(_appendColumn, _settings.appendColumn);
      _settings.registerDialogComponent(_newColName, _settings.newColName);
   }

   @Override
   protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs) throws NotConfigurableException {
      try {
         _settings.loadSettingsFrom(settings);
         _settings.loadDialogSettings(specs);

         _changeListener.stateChanged(null);
         
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
