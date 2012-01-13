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
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

import com.ggasoftware.indigo.knime.IndigoNodeSettings;
import com.ggasoftware.indigo.knime.IndigoNodeSettings.STRUCTURE_TYPE;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolValue;
import com.ggasoftware.indigo.knime.submatchcounter.IndigoSubstructureMatchCounterSettings.Uniqueness;

public class IndigoSubstructureMatchCounterNodeDialog extends NodeDialogPane {
   private final IndigoSubstructureMatchCounterSettings _settings = new IndigoSubstructureMatchCounterSettings();
   
   
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _targetColumn = new ColumnSelectionComboxBox((Border) null, IndigoMolValue.class);
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _queryColumn = new ColumnSelectionComboxBox((Border) null, IndigoQueryMolValue.class);

   private final JLabel _structureType = new JLabel();
   private final JTextField _newColName = new JTextField(20);
   private final JComboBox _uniqueness = new JComboBox(new Object[] { Uniqueness.Atoms, Uniqueness.Bonds, Uniqueness.None });
   private final JCheckBox _highlight = new JCheckBox("Highlight all matches");
   private final JCheckBox _appendColumn = new JCheckBox("Append column");
   private final JTextField _appendColumnName = new JTextField(20);

   private DataTableSpec _targetSpec;
   private DataTableSpec _querySpec;
   
   private final ChangeListener _changeListener = new ChangeListener() {
      public void stateChanged (ChangeEvent e)
      {
         boolean enabled = _highlight.isSelected();
         _appendColumn.setEnabled(enabled);
         
         if (!enabled)
            _appendColumn.setSelected(false);
         
         if (_appendColumn.isEnabled())
            _appendColumnName.setEnabled(_appendColumn.isSelected());
         
         if (_appendColumnName.isEnabled() && _appendColumnName.getText().length() < 1)
            _appendColumnName.setText(_targetColumn.getSelectedColumn() + " (highlihghted)");
      }
   };
   
   private final ItemListener _columnChangeListener = new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
         STRUCTURE_TYPE stype = _getStructureType();
         switch(stype) {
            case Unknown:
               _structureType.setText("Unknown");
               break;
            case Reaction:
               _structureType.setText("Reaction");
               break;
            case Molecule:
               _structureType.setText("Molecule");
               break;
         }
      }
   };
   
   /*
    * Returns current column selection state
    */
   private STRUCTURE_TYPE _getStructureType() {
      return IndigoNodeSettings.getStructureType(_targetSpec, _querySpec, 
            _targetColumn.getSelectedColumn(), _queryColumn.getSelectedColumn());
   }
   
   /**
    * New pane for configuring the IndigoSmartsMatcher node.
    */
   protected IndigoSubstructureMatchCounterNodeDialog()
   {
      super();
      
      _registerDialogComponents();

      JPanel p = new JPanel(new GridBagLayout());

      GridBagConstraints c = new GridBagConstraints();

      c.anchor = GridBagConstraints.WEST;
      c.insets = new Insets(2, 2, 2, 2);
      c.gridy = 0;
      c.gridx = 0;
      p.add(new JLabel("Molecule column"), c);
      c.gridx = 1;
      p.add(_targetColumn, c);

      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Query molecule column"), c);
      c.gridx = 1;
      p.add(_queryColumn, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("New column name"), c);
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
      p.add(_appendColumnName, c);
      
      _highlight.addChangeListener(_changeListener);
      _appendColumn.addChangeListener(_changeListener);
      
      _highlight.setSelected(false);
      _appendColumn.setEnabled(false);
      _appendColumnName.setEnabled(false);
      
      addTab("Standard settings", p);
   }

   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_targetColumn, IndigoSubstructureMatchCounterNodeModel.TARGET_PORT, _settings.targetColName);
      _settings.registerDialogComponent(_queryColumn, IndigoSubstructureMatchCounterNodeModel.QUERY_PORT, _settings.queryColName);
      _settings.registerDialogComponent(_newColName, _settings.newColName);
      _settings.registerDialogComponent(_uniqueness, _settings.uniqueness);
      _settings.registerDialogComponent(_appendColumnName, _settings.appendColumnName);
      _settings.registerDialogComponent(_highlight, _settings.highlight);
      _settings.registerDialogComponent(_appendColumn, _settings.appendColumn);
   }
   
   @Override
   protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs) throws NotConfigurableException {
      try {
         _settings.loadSettingsFrom(settings);
         _settings.loadDialogSettings(specs);

         _changeListener.stateChanged(null);
         
         _targetSpec = specs[IndigoSubstructureMatchCounterNodeModel.TARGET_PORT];
         _querySpec = specs[IndigoSubstructureMatchCounterNodeModel.QUERY_PORT];
         /*
          * Update mode
          */
         _columnChangeListener.itemStateChanged(null);
         
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage());
      }
   }

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      
      STRUCTURE_TYPE stype = _getStructureType();
      
      if(stype.equals(STRUCTURE_TYPE.Unknown))
         throw new InvalidSettingsException("can not select reaction and molecule column in the same time!");
      
      _settings.saveDialogSettings();
      _settings.saveSettingsTo(settings);
   }

}
