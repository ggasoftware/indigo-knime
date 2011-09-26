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

   private final ChangeListener _changeListener = new ChangeListener() {
      public void stateChanged (ChangeEvent e)
      {
         boolean enabled = _highlight.isSelected() || _align.isSelected();
         _newColName.setEnabled(enabled);
         _appendColumn.setEnabled(enabled);
         
         _alignByQuery.setEnabled(_align.isSelected());
         
         if (!enabled)
            _appendColumn.setSelected(false);
         
         if (_appendColumn.isEnabled())
            _newColName.setEnabled(_appendColumn.isSelected());
         
         if (_newColName.isEnabled() && _newColName.getText().length() < 1)
            _newColName.setText(_molColumn.getSelectedColumn() + " (matched)");
      }
   };
   
   /**
    * New pane for configuring the IndigoSmartsMatcher node.
    */
   protected IndigoSubstructureMatcherNodeDialog()
   {
      super();

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
      
      _align.addChangeListener(_changeListener);
      _highlight.addChangeListener(_changeListener);
      
      c.gridy++;
      c.gridx = 0;
      p.add(_appendColumn, c);
      c.gridx = 1;
      p.add(_newColName, c);
      
      _appendColumn.addChangeListener(_changeListener);
      _align.setSelected(false);
      _highlight.setSelected(false);
      _appendColumn.setEnabled(false);
      _newColName.setEnabled(false);
      
      addTab("Standard settings", p);
   }

   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      _settings.loadSettingsForDialog(settings);

      _molColumn.update(specs[0], _settings.colName);
      _molColumn2.update(specs[1], _settings.colName2);
      _newColName.setText(_settings.newColName);
      _align.setSelected(_settings.align);
      _alignByQuery.setSelected(_settings.alignByQuery);
      _exact.setSelected(_settings.exact);
      _highlight.setSelected(_settings.highlight);
      _appendColumn.setSelected(_settings.appendColumn);
      _mode.setSelectedItem(_settings.mode);
      _changeListener.stateChanged(null);
   }

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.colName = _molColumn.getSelectedColumn();
      _settings.colName2 = _molColumn2.getSelectedColumn();
      _settings.appendColumn = _appendColumn.isSelected();
      _settings.highlight = _highlight.isSelected();
      _settings.align = _align.isSelected();
      _settings.alignByQuery = _alignByQuery.isSelected();
      _settings.exact = _exact.isSelected();
      _settings.newColName = _newColName.getText();
      _settings.mode = (Mode)_mode.getSelectedItem();

      _settings.saveSettings(settings);
   }
}
