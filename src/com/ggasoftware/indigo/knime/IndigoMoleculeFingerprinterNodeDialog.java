package com.ggasoftware.indigo.knime;

import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import javax.swing.*;
import javax.swing.border.*;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.*;



/**
 * <code>NodeDialog</code> for the "IndigoMoleculeFingerprinter" Node.
 * 
 * 
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more
 * complex dialog please derive directly from
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author
 */
public class IndigoMoleculeFingerprinterNodeDialog extends NodeDialogPane
{
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);
   private final JTextField _newColName = new JTextField(16);

   private final JFormattedTextField _size = new JFormattedTextField(NumberFormat.getIntegerInstance());
   private final IndigoMoleculeFingerprinterSettings _settings = new IndigoMoleculeFingerprinterSettings();
   
   /**
    * New pane for configuring the IndigoMoleculeFingerprinter node.
    */
   protected IndigoMoleculeFingerprinterNodeDialog()
   {
      JPanel p = new JPanel(new GridBagLayout());

      GridBagConstraints c = new GridBagConstraints();

      c.anchor = GridBagConstraints.WEST;
      c.insets = new Insets(2, 2, 2, 2);

      c.gridx = 0;
      c.gridy = 0;
      p.add(new JLabel("Indigo column   "), c);
      c.gridx = 1;
      p.add(_molColumn, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("New column name"), c);
      c.gridx = 1;
      p.add(_newColName, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Fingerprint size in qwords:"), c);
      c.gridx = 1;
      _size.setColumns(3);
      p.add(_size, c);

      _molColumn.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged (ItemEvent arg0)
         {
           if ("".equals(_newColName.getText()))
                 _newColName.setText(_molColumn.getSelectedColumn() + " (fingerprint)");
         }
      });      
      
      addTab("Standard settings", p);
   }

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.fpSizeQWords = ((Number)_size.getValue()).intValue();
      _settings.colName = _molColumn.getSelectedColumn();
      _settings.newColName = _newColName.getText();
      _settings.saveSettings(settings);
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      _settings.loadSettingsForDialog(settings);
      _size.setValue(_settings.fpSizeQWords);
      _molColumn.update(specs[0], _settings.colName);
      _newColName.setText(_settings.newColName);
      if ("".equals(_newColName.getText()))
         _newColName.setText(_molColumn.getSelectedColumn() + " (fingerprint)");
   }
}
