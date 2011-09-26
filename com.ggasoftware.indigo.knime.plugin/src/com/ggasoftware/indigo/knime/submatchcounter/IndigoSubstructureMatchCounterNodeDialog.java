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

package com.ggasoftware.indigo.knime.submatchcounter;

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
import com.ggasoftware.indigo.knime.submatchcounter.IndigoSubstructureMatchCounterSettings.Uniqueness;

public class IndigoSubstructureMatchCounterNodeDialog extends NodeDialogPane
{
   private final IndigoSubstructureMatchCounterSettings _settings = new IndigoSubstructureMatchCounterSettings();
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn2 = new ColumnSelectionComboxBox(
         (Border) null, IndigoQueryMolValue.class);
   private final JTextField _newColName = new JTextField(20);

   private final JComboBox _uniqueness = new JComboBox(new Object[] {
         Uniqueness.Atoms, Uniqueness.Bonds, Uniqueness.None });
   
   private final JCheckBox _highlight = new JCheckBox("Highlight matches");
   private final JCheckBox _appendColumn = new JCheckBox("Append column");
   private final JTextField _newColName2 = new JTextField(20);
   
   private final ChangeListener _changeListener = new ChangeListener() {
      public void stateChanged (ChangeEvent e)
      {
         boolean enabled = _highlight.isSelected();
         _appendColumn.setEnabled(enabled);
         
         if (!enabled)
            _appendColumn.setSelected(false);
         
         if (_appendColumn.isEnabled())
            _newColName2.setEnabled(_appendColumn.isSelected());
         
         if (_newColName2.isEnabled() && _newColName2.getText().length() < 1)
            _newColName2.setText(_molColumn.getSelectedColumn() + " (highlihghted)");
      }
   };
   
   /**
    * New pane for configuring the IndigoSmartsMatcher node.
    */
   protected IndigoSubstructureMatchCounterNodeDialog()
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
      p.add(new JLabel("New column"), c);
      c.gridx = 1;
      p.add(_newColName, c);

      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Uniqueness"), c);
      c.gridx = 1;
      p.add(_uniqueness, c);

      c.gridy++;
      c.gridx = 0;
      p.add(_highlight, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(_appendColumn, c);
      c.gridx = 1;
      p.add(_newColName2, c);
      
      _highlight.addChangeListener(_changeListener);
      _appendColumn.addChangeListener(_changeListener);
      
      _highlight.setSelected(false);
      _appendColumn.setEnabled(false);
      _newColName2.setEnabled(false);
      
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
      _uniqueness.setSelectedItem(_settings.uniqueness);
      _newColName2.setText(_settings.newColName2);
      _highlight.setSelected(_settings.highlight);
      _appendColumn.setSelected(_settings.appendColumn);
      _changeListener.stateChanged(null);
   }

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.colName = _molColumn.getSelectedColumn();
      _settings.colName2 = _molColumn2.getSelectedColumn();
      _settings.newColName = _newColName.getText();
      _settings.uniqueness = ((Uniqueness)_uniqueness.getSelectedItem());
      _settings.highlight = _highlight.isSelected();
      _settings.appendColumn = _appendColumn.isSelected();
      _settings.newColName2 = _newColName2.getText();
      _settings.saveSettings(settings);
   }
}
