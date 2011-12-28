package com.ggasoftware.indigo.knime;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class IndigoDialogPanel{
   
   private final JPanel _panel;
   private final GridBagConstraints _constraints;
   
   public IndigoDialogPanel() {
      
      _panel = new JPanel(new GridBagLayout());
      _constraints = new GridBagConstraints();
      
      _constraints.anchor = GridBagConstraints.WEST;
      _constraints.insets = new Insets(2, 2, 2, 2);
      _constraints.gridx = 0;
      _constraints.gridy = 0;
   }
   
   public JPanel getPanel() {
      return _panel;
   }

   public void addItem(String itemLabel, JComponent itemComponent) {
      addItem(new JLabel(itemLabel), itemComponent);
   }

   public void addItem(JComponent leftComponent, JComponent rightComponent) {
      /*
       * Add left component
       */
      if(leftComponent != null) {
         _constraints.gridx = 0;
         _panel.add(leftComponent, _constraints);
      }
      /*
       * Add right component
       */
      if(rightComponent != null) {
         _constraints.gridx = 1;
         _panel.add(rightComponent, _constraints);
      }
      
      if(leftComponent != null || rightComponent != null)
         _constraints.gridy++;
   }
   
}
