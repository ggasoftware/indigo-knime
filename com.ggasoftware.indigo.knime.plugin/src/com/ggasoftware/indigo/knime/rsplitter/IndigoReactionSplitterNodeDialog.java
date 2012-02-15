package com.ggasoftware.indigo.knime.rsplitter;

import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
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

import com.ggasoftware.indigo.knime.common.IndigoDialogPanel;


/**
 * <code>NodeDialog</code> for the "IndigoReactionSplitter" Node.
 * 
 * 
 */
public class IndigoReactionSplitterNodeDialog extends NodeDialogPane {

   private final IndigoReactionSplitterSettings _settings = new IndigoReactionSplitterSettings();
   
   private final ColumnSelectionComboxBox _reactionColumn = new ColumnSelectionComboxBox(
         (Border) null, _settings.columnFilter);
   
   private final JTextField _reactantColName = new JTextField(20);
   private final JTextField _productColName = new JTextField(20);
   private final JTextField _catalystColName = new JTextField(20);
   
   private final JCheckBox _extractReactants = new JCheckBox("Extract reactants");
   private final JCheckBox _extractProducts = new JCheckBox("Extract products");
   private final JCheckBox _extractCatalysts = new JCheckBox("Extract catalysts");
   
   private final ArrayList<ChangeListener> _changeListeners = new ArrayList<ChangeListener>();
   
   protected IndigoReactionSplitterNodeDialog() {
      super();
      
      _registerDialogComponents();
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Input reaction column", _reactionColumn);
      dialogPanel.addItemsPanel("Output Column Names");
      dialogPanel.addItem(_extractReactants, _reactantColName);
      dialogPanel.addItem(_extractProducts, _productColName);
      dialogPanel.addItem(_extractCatalysts, _catalystColName);
      
      /*
       * Add change listeners
       */
      ChangeListener reactListener = new ChangeListener() {
         public void stateChanged(ChangeEvent e) {
            _reactantColName.setEnabled(_extractReactants.isSelected());
         }
      };
      ChangeListener productListener = new ChangeListener() {
         public void stateChanged(ChangeEvent e) {
            _productColName.setEnabled(_extractProducts.isSelected());
         }
      };
      ChangeListener catalystListener = new ChangeListener() {
         public void stateChanged(ChangeEvent e) {
            _catalystColName.setEnabled(_extractCatalysts.isSelected());
         }
      };
      _changeListeners.add(reactListener);
      _changeListeners.add(productListener);
      _changeListeners.add(catalystListener);
      
      _extractReactants.addChangeListener(reactListener);
      _extractProducts.addChangeListener(productListener);
      _extractCatalysts.addChangeListener(catalystListener);
      
      addTab("Standard Settings", dialogPanel.getPanel());

   }
   
   private void _registerDialogComponents() {
      
      _settings.registerDialogComponent(_reactionColumn, IndigoReactionSplitterSettings.INPUT_PORT, _settings.reactionColumn);
      
      _settings.registerDialogComponent(_reactantColName, _settings.reactantColName);
      _settings.registerDialogComponent(_productColName, _settings.productColName);
      _settings.registerDialogComponent(_catalystColName, _settings.catalystColName);
      
      _settings.registerDialogComponent(_extractReactants, _settings.extractReactants);
      _settings.registerDialogComponent(_extractProducts, _settings.extractProducts);
      _settings.registerDialogComponent(_extractCatalysts, _settings.extractCatalysts);
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
         
         for(ChangeListener cL : _changeListeners) {
            cL.stateChanged(null);
         }
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage());
      }
   }
}
