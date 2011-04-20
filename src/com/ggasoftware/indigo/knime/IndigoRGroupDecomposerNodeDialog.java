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

package com.ggasoftware.indigo.knime;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.ColumnSelectionComboxBox;

public class IndigoRGroupDecomposerNodeDialog extends NodeDialogPane
{
   IndigoRGroupDecomposerSettings _settings = new IndigoRGroupDecomposerSettings();
   
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn2 = new ColumnSelectionComboxBox(
         (Border) null, IndigoQueryMolValue.class);
   private final JTextField _newColPrefix = new JTextField(10);
   private final JCheckBox _aromatize = new JCheckBox("Aromatize");
   private final JSpinner _maxRGroups = new JSpinner(new SpinnerNumberModel(6, 1, 32, 1));
   
   /**
    * New pane for configuring the IndigoRGroupDecomposer node.
    */
   protected IndigoRGroupDecomposerNodeDialog()
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
      p.add(new JLabel("Maximum R-Groups"), c);
      c.gridx = 1;
      ((JSpinner.DefaultEditor)_maxRGroups.getEditor()).getTextField().setColumns(2);
      p.add(_maxRGroups, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("R-Group column prefix"), c);
      c.gridx = 1;
      p.add(_newColPrefix, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(_aromatize, c);
      
      addTab("Standard settings", p);   }
   
   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      _settings.loadSettingsForDialog(settings);

      _molColumn.update(specs[0], _settings.colName);
      _molColumn2.update(specs[1], _settings.colName2);
      _newColPrefix.setText(_settings.newColPrefix);
      _aromatize.setSelected(_settings.aromatize);
      _maxRGroups.setValue(_settings.numRGroups);
   }

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.colName = _molColumn.getSelectedColumn();
      _settings.colName2 = _molColumn2.getSelectedColumn();
      _settings.newColPrefix = _newColPrefix.getText();
      _settings.aromatize = _aromatize.isSelected();
      _settings.numRGroups = ((Number)_maxRGroups.getValue()).intValue();
      
      _settings.saveSettings(settings);
   }   
}
