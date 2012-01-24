package com.ggasoftware.indigo.knime.compjoin;

import javax.swing.JTextField;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnFilter;
import org.knime.core.node.util.ColumnFilter;

import com.ggasoftware.indigo.knime.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolValue;

public class IndigoComponentCombinerNodeDialog extends NodeDialogPane {
   private final IndigoComponentCombinerSettings _settings = new IndigoComponentCombinerSettings();
   
   private final DialogComponentColumnFilter filterPanel;
   private final JTextField _newColName = new JTextField(20);

    protected IndigoComponentCombinerNodeDialog() {
        super();
        
        _settings.registerDialogComponent(_newColName, _settings.newColName);
        
        IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
        dialogPanel.addItemsPanel("Include Columns");
        
        /*
         * Add filter panel
         */
        filterPanel = new DialogComponentColumnFilter(_settings.colNames, 
              IndigoComponentCombinerSettings.INPUT_PORT, 
              true, new ColumnFilter() {
               @Override
               public boolean includeColumn(DataColumnSpec colSpec) {
                  if(colSpec.getType().isCompatible(IndigoMolValue.class))
                     return true;
                  if(colSpec.getType().isCompatible(IndigoQueryMolValue.class))
                     return true;
                  return false;
               }
               @Override
               public String allFilteredMsg() {
                  return "no 'IndigoMolValue' or 'IndigoQueryMolValue' was found";
               }
            });
        dialogPanel.addItem(filterPanel.getComponentPanel());
        dialogPanel.addItemsPanel("Output Column Settings");
        dialogPanel.addItem("Result molecule column name", _newColName);
        
        addTab("Standard Settings", dialogPanel.getPanel());
        
    }

   @Override
   protected void saveSettingsTo(NodeSettingsWO settings)
         throws InvalidSettingsException {
      _settings.saveDialogSettings();
      _settings.saveSettingsTo(settings);
   }
   
   @Override
   protected void loadSettingsFrom(NodeSettingsRO settings,
         DataTableSpec[] specs) throws NotConfigurableException {
      try {
         _settings.loadSettingsFrom(settings);
         _settings.loadDialogSettings(specs);
         filterPanel.loadSettingsFrom(settings, specs);
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage());
      }
   }
}

