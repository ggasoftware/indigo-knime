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

package com.ggasoftware.indigo.knime.scaffold;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.cell.IndigoMolValue;

public class IndigoScaffoldFinderNodeDialog extends NodeDialogPane
{
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);

   private final JCheckBox _tryExact = new JCheckBox("Try exact method");
   private final JSpinner _exactIterations = new JSpinner();
   private final JSpinner _approxIterations = new JSpinner();
   
   private final JTextField _newColName = new JTextField(20);

   private final IndigoScaffoldFinderSettings _settings = new IndigoScaffoldFinderSettings();

   /**
    * New pane for configuring the IndigoScaffoldFinder node.
    */
   protected IndigoScaffoldFinderNodeDialog ()
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
      p.add(new JLabel("New column with scaffolds"), c);
      c.gridx = 1;
      p.add(_newColName, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(_tryExact, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Maximum number of iterations for exact method (0 to no limit)"), c);
      c.gridx = 1;
      ((JSpinner.DefaultEditor)_exactIterations.getEditor()).getTextField().setColumns(8);
      p.add(_exactIterations, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Number of iterations for approximate method"), c);
      c.gridx = 1;
      ((JSpinner.DefaultEditor)_approxIterations.getEditor()).getTextField().setColumns(8);
      p.add(_approxIterations, c);
      
      _tryExact.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged (ChangeEvent arg0)
         {
            _exactIterations.setEnabled(_tryExact.isSelected());
         }
      });
      
      addTab("Standard settings", p);
   }

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.colName = _molColumn.getSelectedColumn();
      _settings.newColName = _newColName.getText();
      _settings.tryExactMethod = _tryExact.isSelected();
      _settings.maxIterApprox = ((Number)_approxIterations.getValue()).intValue();
      _settings.maxIterExact = ((Number)_exactIterations.getValue()).intValue();

      _settings.saveSettings(settings);
   }
   
   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      _settings.loadSettingsForDialog(settings);

      _molColumn.update(specs[0], _settings.colName);
      _newColName.setText(_settings.newColName);
      _tryExact.setSelected(_settings.tryExactMethod);
      _exactIterations.setValue(_settings.maxIterExact);
      _approxIterations.setValue(_settings.maxIterApprox);
      
      _exactIterations.setEnabled(_tryExact.isSelected());
   }
}
