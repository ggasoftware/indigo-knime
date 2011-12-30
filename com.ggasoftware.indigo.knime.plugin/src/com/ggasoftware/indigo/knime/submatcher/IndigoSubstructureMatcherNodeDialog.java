/****************************************************************************
 * Copyright (C) 2011 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 3 as published by the Free Software
 * Foundation.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 ***************************************************************************/

package com.ggasoftware.indigo.knime.submatcher;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolValue;
import com.ggasoftware.indigo.knime.submatcher.IndigoSubstructureMatcherSettings.Mode;

public class IndigoSubstructureMatcherNodeDialog extends NodeDialogPane
{
   private final IndigoSubstructureMatcherSettings _settings = new IndigoSubstructureMatcherSettings();
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn2 = new ColumnSelectionComboxBox(
         (Border) null, IndigoQueryMolValue.class);
   private final JComboBox _mode = new JComboBox(new Object[] {Mode.Normal, Mode.Tautomer, Mode.Resonance});
   private final JCheckBox _exact = new JCheckBox("Allow only exact matches");
   private final JCheckBox _highlight = new JCheckBox("Highlight matched structures");
   private final JCheckBox _align = new JCheckBox("Align matched structures");
   private final JCheckBox _alignByQuery = new JCheckBox("Align by query");
   private final JCheckBox _appendColumn = new JCheckBox("Append column");
   private final JTextField _newColName = new JTextField(20);
   private final JCheckBox _appendQueryKeyColumn = new JCheckBox("Append queries row ID column");
   private final JTextField _queryKeyColumnName = new JTextField(20);
   
   private final JCheckBox _appendQueryMatchCountKeyColumn = new JCheckBox("Append match count column");
   private final JTextField _queryMatchCountKeyColumn = new JTextField(20);
   
   private final JRadioButton _matchAllExceptSelected = new JRadioButton("All queries");
   private final JRadioButton _matchAnyAtLeastSelected = new JRadioButton("At least ");
   private final JSpinner _matchAnyAtLeast = new JSpinner(new SpinnerNumberModel(1, 0, Integer.MAX_VALUE, 1));

   private static void updateNullableEdit (JTextField field, String defValue)
   {
      if (field.isEnabled() && field.getText().length() < 1)
         field.setText(defValue);
      if (!field.isEnabled() && field.getText().length() > 0 && field.getText().equals(defValue))
         field.setText("");
   }
   
   private final ChangeListener _changeListener = new ChangeListener() {
      public void stateChanged (ChangeEvent e)
      {
         boolean enabled = _highlight.isSelected() || _align.isSelected();
         _newColName.setEnabled(enabled);
         _appendColumn.setEnabled(enabled);
        
         _alignByQuery.setEnabled(_align.isSelected());
         
         if (!enabled)
            _appendColumn.setSelected(false);
         
         _queryKeyColumnName.setEnabled(_appendQueryKeyColumn.isSelected());
         _queryMatchCountKeyColumn.setEnabled(_appendQueryMatchCountKeyColumn.isSelected());
         
         _matchAnyAtLeast.setEnabled(_matchAnyAtLeastSelected.isSelected());
         
         if (_appendColumn.isEnabled())
            _newColName.setEnabled(_appendColumn.isSelected());

         updateNullableEdit(_queryKeyColumnName, _molColumn.getSelectedColumn() + " (query row ID)");
         updateNullableEdit(_queryMatchCountKeyColumn, _molColumn.getSelectedColumn() + " (queries matched)");
         updateNullableEdit(_newColName, _molColumn.getSelectedColumn() + " (matched)");
      }
   };
   
   /**
    * New pane for configuring the IndigoSmartsMatcher node.
    */
   protected IndigoSubstructureMatcherNodeDialog()
   {
      super();
      
      //addDialogComponent()

      JPanel p = new JPanel(new GridBagLayout());

      GridBagConstraints c = new GridBagConstraints();

      c.anchor = GridBagConstraints.WEST;
      c.insets = new Insets(2, 2, 2, 2);
      c.gridy = 0;
      c.gridx = 0;
      p.add(new JLabel("Molecule column"), c);
      c.gridx = 1;
      p.add(_molColumn, c);

      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Query molecule column"), c);
      c.gridx = 1;
      p.add(_molColumn2, c);

      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Mode: "), c);
      c.gridx = 1;
      p.add(_mode, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(_exact, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(_highlight, c);

      c.gridy++;
      c.gridx = 0;
      p.add(_align, c);
      c.gridx = 1;
      p.add(_alignByQuery, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(_appendColumn, c);
      c.gridx = 1;
      p.add(_newColName, c);
      
      _appendColumn.addChangeListener(_changeListener);
      _align.addChangeListener(_changeListener);
      _highlight.addChangeListener(_changeListener);
      
      ((JSpinner.DefaultEditor)_matchAnyAtLeast.getEditor()).getTextField().setColumns(4);

      JPanel p2 = new JPanel(new GridBagLayout());
      GridBagConstraints c2 = new GridBagConstraints();
      c2.anchor = GridBagConstraints.WEST;
      c2.insets = new Insets(2, 2, 2, 2);
      c2.gridy = 0;
      c2.gridx = 0;
      
      p2.add(new JLabel("Match"), c2);
      c2.gridx++;
      p2.add(_matchAnyAtLeastSelected, c2);
      c2.gridx++;
      p2.add(_matchAnyAtLeast, c2);
      c2.gridx++;
      p2.add(new JLabel(" queries"), c2);
     
      c2.gridy++;
      c2.gridx = 1;
      c2.gridwidth = 3;
      p2.add(_matchAllExceptSelected, c2);
      
      ButtonGroup bg = new ButtonGroup();
      bg.add(_matchAllExceptSelected);
      bg.add(_matchAnyAtLeastSelected);
      _matchAllExceptSelected.addChangeListener(_changeListener);
      _matchAnyAtLeastSelected.addChangeListener(_changeListener);
      
      c.gridy++;
      c.gridx = 0;
      c.gridwidth = 2;
      p.add(p2, c);
      c.gridwidth = 1;

      c.gridy++;
      c.gridx = 0;
      p.add(_appendQueryKeyColumn, c);
      c.gridx = 1;
      p.add(_queryKeyColumnName, c);
      
      _appendQueryKeyColumn.addChangeListener(_changeListener);
      
      c.gridy++;
      c.gridx = 0;
      p.add(_appendQueryMatchCountKeyColumn, c);
      c.gridx = 1;
      p.add(_queryMatchCountKeyColumn, c);
      
      _appendQueryMatchCountKeyColumn.addChangeListener(_changeListener);
      
      _align.setSelected(false);
      _highlight.setSelected(false);
      _appendColumn.setEnabled(false);
      _newColName.setEnabled(false);
      
      addTab("Standard settings", p);
   }

   @Override
   protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs) throws NotConfigurableException {
      try {
         _settings.loadSettingsFrom(settings);

         _molColumn.update(specs[IndigoSubstructureMatcherNodeModel.INDIGO_TARGET_PORT], _settings.targetColName.getStringValue());
         _molColumn2.update(specs[IndigoSubstructureMatcherNodeModel.INDIGO_QUERY_PORT], _settings.queryColName.getStringValue());
         
         _newColName.setText(_settings.newColName.getStringValue());
         _align.setSelected(_settings.align.getBooleanValue());
         _alignByQuery.setSelected(_settings.alignByQuery.getBooleanValue());
         _exact.setSelected(_settings.exact.getBooleanValue());
         _highlight.setSelected(_settings.highlight.getBooleanValue());
         _appendColumn.setSelected(_settings.appendColumn.getBooleanValue());
         _mode.setSelectedIndex(_settings.mode.getIntValue());
         _appendQueryKeyColumn.setSelected(_settings.appendQueryKeyColumn.getBooleanValue());

         _appendQueryKeyColumn.setSelected(_settings.appendQueryKeyColumn.getBooleanValue());
         _queryKeyColumnName.setText(_settings.queryKeyColumn.getStringValue());

         _appendQueryMatchCountKeyColumn.setSelected(_settings.appendQueryMatchCountKeyColumn.getBooleanValue());
         _queryMatchCountKeyColumn.setText(_settings.queryMatchCountKeyColumn.getStringValue());

         _matchAllExceptSelected.setSelected(_settings.matchAllSelected.getBooleanValue());
         _matchAnyAtLeastSelected.setSelected(_settings.matchAnyAtLeastSelected.getBooleanValue());
         _matchAnyAtLeast.setValue(_settings.matchAnyAtLeast.getIntValue());

         _changeListener.stateChanged(null);
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage());
      }
   }

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.targetColName.setStringValue(_molColumn.getSelectedColumn());
      _settings.queryColName.setStringValue(_molColumn2.getSelectedColumn());
      _settings.appendColumn.setBooleanValue(_appendColumn.isSelected());
      _settings.highlight.setBooleanValue(_highlight.isSelected());
      _settings.align.setBooleanValue(_align.isSelected());
      _settings.alignByQuery.setBooleanValue(_alignByQuery.isSelected());
      _settings.exact.setBooleanValue(_exact.isSelected());
      _settings.newColName.setStringValue(_newColName.getText());
      _settings.mode.setIntValue(((Mode)_mode.getSelectedItem()).ordinal());

      _settings.appendQueryKeyColumn.setBooleanValue(_appendQueryKeyColumn.isSelected());
      _settings.queryKeyColumn.setStringValue(_queryKeyColumnName.getText());

      _settings.appendQueryMatchCountKeyColumn.setBooleanValue(_appendQueryMatchCountKeyColumn.isSelected());
      _settings.queryMatchCountKeyColumn.setStringValue(_queryMatchCountKeyColumn.getText());
      
      _settings.matchAllSelected.setBooleanValue(_matchAllExceptSelected.isSelected());
      _settings.matchAnyAtLeastSelected.setBooleanValue(_matchAnyAtLeastSelected.isSelected());
      _settings.matchAnyAtLeast.setIntValue((Integer)_matchAnyAtLeast.getValue());

      _settings.saveSettingsTo(settings);
   }
}
