package com.ggasoftware.indigo.knime;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Spring;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.node.util.ColumnSelectionComboxBox;

public class IndigoDialogPanel {
   
   public static final int DEFAULT_PAD = 5;
   public static final int COLUMNS_NUMBER = 2;
   
   private JPanel _resultPanel;
   private JPanel _currentPanel;
   
   private final ArrayList<JPanel> _settingsPanels = new ArrayList<JPanel>();
   
   public IndigoDialogPanel() {
   }
   
   public JPanel getPanel() {
      if(_resultPanel == null) {
         JPanel allSettingsPanel = new JPanel(new GridBagLayout());
         GridBagConstraints c = new GridBagConstraints();
         c.anchor = GridBagConstraints.NORTHWEST;
         c.fill = GridBagConstraints.HORIZONTAL;
         c.gridheight = 1;
         c.gridwidth = 1;
         c.insets = new Insets(0, 0, 0, 0);
         c.ipadx = 0;
         c.ipady = 0;
         c.gridx = 0;
         c.gridy = GridBagConstraints.RELATIVE;
         /*
          * Add all sub panels
          */
         for (JPanel jPanel : _settingsPanels) {
            makeCompactGrid(jPanel, COLUMNS_NUMBER);
            allSettingsPanel.add(jPanel, c);
         }
         _resultPanel = new JPanel(new BorderLayout());
         _resultPanel.add(allSettingsPanel, BorderLayout.PAGE_START);
      }
      return _resultPanel;
   }
   
   public void addItemsPanel(String name) {
      _currentPanel = new JPanel(new SpringLayout());
      _settingsPanels.add(_currentPanel);
      
      if(name != null) {
         _currentPanel.setBorder(BorderFactory.createTitledBorder(name));
      }
   }
   
   public void addItem(String itemLabel, JComponent itemComponent) {
      JLabel label = new JLabel(itemLabel);
      label.setFont(new Font("Serif", Font.PLAIN, 12));
      addItem(label, itemComponent);
   }

   public void addItem(JComponent leftComponent, JComponent rightComponent) {
      if(_currentPanel == null) {
         throw new RuntimeException("internal error: could not add to empty panel: call addItemsPanel() first");
      }
      if(leftComponent == null || rightComponent == null)
         throw new RuntimeException("internal error: can not add null components");
      /*
       * Refresh common panel every time
       */
      _resultPanel = null;
      
      /*
       * Add left component
       */
      JPanel leftPanel = new JPanel(new BorderLayout());
      leftPanel.add(leftComponent, BorderLayout.WEST);
      _currentPanel.add(leftPanel);
      
      /*
       * Add right component
       */
      JPanel rightPanel = new JPanel(new BorderLayout());
      rightPanel.add(rightComponent, BorderLayout.EAST);
      _currentPanel.add(rightPanel);
      
   }
   
   /*
    * Used by makeCompactGrid.
    */
   protected static SpringLayout.Constraints getConstraintsForCell(int row, int col, Container parent, int cols) {
      SpringLayout layout = (SpringLayout) parent.getLayout();
      Component c = parent.getComponent(row * cols + col);
      return layout.getConstraints(c);
   }
   
   /**
    * Aligns the first <code>rows</code> * <code>cols</code> components of
    * <code>parent</code> in a grid. Each component in a column is as wide as
    * the maximum preferred width of the components in that column; height is
    * similarly determined for each row. The parent is made just big enough to
    * fit them all.
    * 
    */
   protected static void makeCompactGrid(Container parent, int cols) {
      int rows = parent.getComponentCount() / cols;
      SpringLayout layout;
      try {
         layout = (SpringLayout) parent.getLayout();
      } catch (ClassCastException exc) {
         System.err.println("The first argument to makeCompactGrid must use SpringLayout.");
         return;
      }
      
      // Align all cells in each column and make them the same width.
      Spring x = Spring.constant(DEFAULT_PAD);
      for (int c = 0; c < cols; c++) {
         Spring width = Spring.constant(0);
         for (int r = 0; r < rows; r++) {
            width = Spring.max(width, getConstraintsForCell(r, c, parent, cols).getWidth());
         }
         for (int r = 0; r < rows; r++) {
            SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
            constraints.setX(x);
            constraints.setWidth(width);
         }
         x = Spring.sum(x, Spring.sum(width, Spring.constant(DEFAULT_PAD)));
      }
      
      // Align all cells in each row and make them the same height.
      Spring y = Spring.constant(DEFAULT_PAD);
      for (int r = 0; r < rows; r++) {
         Spring height = Spring.constant(0);
         for (int c = 0; c < cols; c++) {
            height = Spring.max(height, getConstraintsForCell(r, c, parent, cols).getHeight());
         }
         for (int c = 0; c < cols; c++) {
            SpringLayout.Constraints constraints = getConstraintsForCell(r, c, parent, cols);
            constraints.setY(y);
            constraints.setHeight(height);
         }
         y = Spring.sum(y, Spring.sum(height, Spring.constant(DEFAULT_PAD)));
      }
      
      // Set the parent's size.
      SpringLayout.Constraints pCons = layout.getConstraints(parent);
      pCons.setConstraint(SpringLayout.SOUTH, y);
      pCons.setConstraint(SpringLayout.EAST, x);
   }

   public static void addColumnChangeListener(final JCheckBox appendColumn, final ColumnSelectionComboxBox colName, final JTextField newColName, final String suffix) {
      appendColumn.addChangeListener(new ChangeListener() {
         public void stateChanged(final ChangeEvent e) {
            if (appendColumn.isSelected()) {
               newColName.setEnabled(true);
               if ("".equals(newColName.getText())) {
                  newColName.setText(colName.getSelectedColumn() + suffix);
               }
            } else {
               newColName.setEnabled(false);
            }
         }
      });
      newColName.setEnabled(appendColumn.isSelected());
   }
   
}
