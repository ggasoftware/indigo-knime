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

import com.ggasoftware.indigo.knime.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryReactionValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;
import com.ggasoftware.indigo.knime.submatcher.IndigoSubstructureMatcherSettings.Mode;

public class IndigoSubstructureMatcherNodeDialog extends NodeDialogPane
{
   private final IndigoSubstructureMatcherSettings _settings = new IndigoSubstructureMatcherSettings();
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _targetColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class, IndigoReactionValue.class);
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _queryColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoQueryMolValue.class, IndigoQueryReactionValue.class);
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

         updateNullableEdit(_queryKeyColumnName, _targetColumn.getSelectedColumn() + " (query row ID)");
         updateNullableEdit(_queryMatchCountKeyColumn, _targetColumn.getSelectedColumn() + " (queries matched)");
         updateNullableEdit(_newColName, _targetColumn.getSelectedColumn() + " (matched)");
      }
   };
   
   /**
    * New pane for configuring the IndigoSmartsMatcher node.
    */
   protected IndigoSubstructureMatcherNodeDialog()
   {
      super();
      
      _registerDialogComponents();
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Molecule column", _targetColumn);
      dialogPanel.addItem("Query molecule column", _queryColumn);
      dialogPanel.addItem(_appendColumn, _newColName);
      dialogPanel.addItemsPanel("Substructure Settings");
      dialogPanel.addItem("Mode: ", _mode);
      dialogPanel.addItem(_exact);
      dialogPanel.addItem(_highlight);
      dialogPanel.addItem(_align, _alignByQuery);
      
      ((JSpinner.DefaultEditor)_matchAnyAtLeast.getEditor()).getTextField().setColumns(4);

      JPanel p2 = new JPanel(new GridBagLayout());
      GridBagConstraints c2 = new GridBagConstraints();
      c2.anchor = GridBagConstraints.WEST;
      c2.insets = new Insets(2, 2, 2, 2);
      c2.gridy = 0;
      c2.gridx = 0;
      
      JLabel matchLabel = new JLabel("Match");
      IndigoDialogPanel.setDefaultFont(matchLabel);
      p2.add(matchLabel, c2);
      c2.gridx++;
      p2.add(_matchAnyAtLeastSelected, c2);
      c2.gridx++;
      p2.add(_matchAnyAtLeast, c2);
      c2.gridx++;
      JLabel queriesLabel = new JLabel(" queries");
      IndigoDialogPanel.setDefaultFont(queriesLabel);
      p2.add(queriesLabel, c2);
     
      c2.gridy++;
      c2.gridx = 1;
      c2.gridwidth = 3;
      p2.add(_matchAllExceptSelected, c2);
      
      ButtonGroup bg = new ButtonGroup();
      bg.add(_matchAllExceptSelected);
      bg.add(_matchAnyAtLeastSelected);
      
      
      dialogPanel.addItem(p2, new JPanel());
      
      dialogPanel.addItemsPanel("Column Key Settings");
      dialogPanel.addItem(_appendQueryKeyColumn, _queryKeyColumnName);
      dialogPanel.addItem(_appendQueryMatchCountKeyColumn, _queryMatchCountKeyColumn);
      
      /*
       * Add all change listeners
       */
      _appendQueryKeyColumn.addChangeListener(_changeListener);
      _appendQueryMatchCountKeyColumn.addChangeListener(_changeListener);
      
      _appendColumn.addChangeListener(_changeListener);
      _align.addChangeListener(_changeListener);
      _highlight.addChangeListener(_changeListener);
      
      _matchAllExceptSelected.addChangeListener(_changeListener);
      _matchAnyAtLeastSelected.addChangeListener(_changeListener);
      
      /*
       * Set fonts
       */
      IndigoDialogPanel.setDefaultFont(_align);
      IndigoDialogPanel.setDefaultFont(_alignByQuery);
      IndigoDialogPanel.setDefaultFont(_appendColumn);
      IndigoDialogPanel.setDefaultFont(_appendQueryKeyColumn);
      IndigoDialogPanel.setDefaultFont(_appendQueryMatchCountKeyColumn);
      IndigoDialogPanel.setDefaultFont(_matchAllExceptSelected);
      IndigoDialogPanel.setDefaultFont(_matchAnyAtLeastSelected);
      IndigoDialogPanel.setDefaultFont(_exact);
      IndigoDialogPanel.setDefaultFont(_highlight);
      
      _align.setSelected(false);
      _highlight.setSelected(false);
      _appendColumn.setEnabled(false);
      _newColName.setEnabled(false);
      
      addTab("Standard settings", dialogPanel.getPanel());
   }

   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_targetColumn, IndigoSubstructureMatcherNodeModel.INDIGO_TARGET_PORT, _settings.targetColName);
      _settings.registerDialogComponent(_queryColumn, IndigoSubstructureMatcherNodeModel.INDIGO_QUERY_PORT, _settings.queryColName);
      
      _settings.registerDialogComponent(_newColName, _settings.newColName);
      _settings.registerDialogComponent(_align, _settings.align);
      _settings.registerDialogComponent(_alignByQuery,_settings.alignByQuery);
      _settings.registerDialogComponent(_exact, _settings.exact);
      _settings.registerDialogComponent(_highlight, _settings.highlight);
      _settings.registerDialogComponent(_appendColumn, _settings.appendColumn);
      _settings.registerDialogComponent(_mode, _settings.mode);
      _settings.registerDialogComponent(_appendQueryKeyColumn, _settings.appendQueryKeyColumn);

      _settings.registerDialogComponent(_appendQueryKeyColumn, _settings.appendQueryKeyColumn);
      _settings.registerDialogComponent(_queryKeyColumnName, _settings.queryKeyColumn);

      _settings.registerDialogComponent(_appendQueryMatchCountKeyColumn, _settings.appendQueryMatchCountKeyColumn);
      _settings.registerDialogComponent(_queryMatchCountKeyColumn, _settings.queryMatchCountKeyColumn);

      _settings.registerDialogComponent(_matchAllExceptSelected, _settings.matchAllSelected);
      _settings.registerDialogComponent(_matchAnyAtLeastSelected, _settings.matchAnyAtLeastSelected);
      _settings.registerDialogComponent(_matchAnyAtLeast, _settings.matchAnyAtLeast);
      
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
