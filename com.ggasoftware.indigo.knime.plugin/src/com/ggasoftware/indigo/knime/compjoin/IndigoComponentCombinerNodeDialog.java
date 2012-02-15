package com.ggasoftware.indigo.knime.compjoin;

import javax.swing.JTextField;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DialogComponentColumnFilter;

import com.ggasoftware.indigo.knime.common.IndigoDialogPanel;

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
              true, _settings.columnFilter);
        
        dialogPanel.addItem(filterPanel.getComponentPanel());
        dialogPanel.addItemsPanel("Output Column Settings");
        dialogPanel.addItem("Result molecule column name", _newColName);
        
        addTab("Standard Settings", dialogPanel.getPanel());
        
    }

   @Override
   protected void saveSettingsTo(NodeSettingsWO settings)
         throws InvalidSettingsException {
      if(_settings.colNames.getIncludeList().isEmpty())
         throw new InvalidSettingsException("selected column list can not be empty");
      
      _settings.saveDialogSettings();
      _settings.saveSettingsTo(settings);
      filterPanel.saveSettingsTo(settings);
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

