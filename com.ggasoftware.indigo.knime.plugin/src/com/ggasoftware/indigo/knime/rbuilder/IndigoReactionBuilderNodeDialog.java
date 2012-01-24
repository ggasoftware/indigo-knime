package com.ggasoftware.indigo.knime.rbuilder;

import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.IndigoDialogPanel;


/**
 * 
 */
public class IndigoReactionBuilderNodeDialog extends NodeDialogPane {
   private final IndigoReactionBuilderSettings _settings = new IndigoReactionBuilderSettings();
   
   private final ColumnSelectionComboxBox _reactantColName = new ColumnSelectionComboxBox(
         (Border) null, _settings.columnFilter);
   private final ColumnSelectionComboxBox _productColName = new ColumnSelectionComboxBox(
         (Border) null, _settings.columnFilter);
   private final ColumnSelectionComboxBox _catalystColName = new ColumnSelectionComboxBox(
         (Border) null, _settings.columnFilter);
   
   private final JCheckBox _addReactants = new JCheckBox("Add reactants");
   private final JCheckBox _addProducts = new JCheckBox("Add products");
   private final JCheckBox _addCatalysts = new JCheckBox("Add catalysts");
   
   private final ArrayList<ChangeListener> _changeListeners = new ArrayList<ChangeListener>();
   
   /**
     */
   protected IndigoReactionBuilderNodeDialog() {
      super();
      
      _registerDialogComponents();
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem(_addReactants, _reactantColName);
      dialogPanel.addItem(_addProducts, _productColName);
      dialogPanel.addItem(_addCatalysts, _catalystColName);
      
      /*
       * Add change listeners
       */
      ChangeListener reactListener = new ChangeListener() {
         public void stateChanged(ChangeEvent e) {
            _reactantColName.setEnabled(_addReactants.isSelected());
         }
      };
      ChangeListener productListener = new ChangeListener() {
         public void stateChanged(ChangeEvent e) {
            _productColName.setEnabled(_addProducts.isSelected());
         }
      };
      ChangeListener catalystListener = new ChangeListener() {
         public void stateChanged(ChangeEvent e) {
            _catalystColName.setEnabled(_addCatalysts.isSelected());
         }
      };
      _changeListeners.add(reactListener);
      _changeListeners.add(productListener);
      _changeListeners.add(catalystListener);
      
      _addReactants.addChangeListener(reactListener);
      _addProducts.addChangeListener(productListener);
      _addCatalysts.addChangeListener(catalystListener);
      
      addTab("Standard Settings", dialogPanel.getPanel());
   }
   
   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_reactantColName, IndigoReactionBuilderSettings.INPUT_PORT, _settings.reactantColName);
      _settings.registerDialogComponent(_productColName, IndigoReactionBuilderSettings.INPUT_PORT, _settings.productColName);
      _settings.registerDialogComponent(_catalystColName, IndigoReactionBuilderSettings.INPUT_PORT, _settings.catalystColName);
      
      _settings.registerDialogComponent(_addReactants, _settings.addReactants);
      _settings.registerDialogComponent(_addProducts, _settings.addProducts);
      _settings.registerDialogComponent(_addCatalysts, _settings.addCatalysts);
   }

   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      try {
         _settings.loadSettingsFrom(settings);
         _settings.loadDialogSettings(specs);
         
         for(ChangeListener cL : _changeListeners) {
            cL.stateChanged(null);
         }
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
