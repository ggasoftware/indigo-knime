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

package com.ggasoftware.indigo.knime.molfp;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

import com.ggasoftware.indigo.knime.cell.IndigoMolValue;

public class IndigoMoleculeFingerprinterNodeDialog extends NodeDialogPane
{
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);
   private final JTextField _newColName = new JTextField(16);

   private final JSpinner _size = new JSpinner(new SpinnerNumberModel(8, 1, 1000000, 1));
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
      ((JSpinner.DefaultEditor)_size.getEditor()).getTextField().setColumns(2);
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
