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

package com.ggasoftware.indigo.knime.convert.base;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.util.ColumnSelectionComboxBox;

public class IndigoLoaderNodeDialog extends NodeDialogPane
{
   private ColumnSelectionComboxBox _colName;
   private final JCheckBox _treatXAsPseudoatom = new JCheckBox();
   private final JCheckBox _ignoreStereochemistryErrors = new JCheckBox();
   private final JCheckBox _appendColumn = new JCheckBox("Append Column");
   private final JTextField _newColName = new JTextField(20);
   IndigoLoaderSettings _settings = new IndigoLoaderSettings();

   /**
    * New pane for configuring IndigoMoleculeLoader node dialog. This is just a
    * suggestion to demonstrate possible default dialog components.
    */
   protected IndigoLoaderNodeDialog(String columnLabel, Class<? extends DataValue>[] filterValueClasses)
   {
      super();

      JPanel p = new JPanel(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();

      c.anchor = GridBagConstraints.WEST;
      c.insets = new Insets(2, 2, 2, 2);
      c.gridx = 0;
      c.gridy = 0;

      p.add(new JLabel(columnLabel + " column"), c);
      _colName = new ColumnSelectionComboxBox((Border) null, filterValueClasses);

      c.gridx = 1;
      p.add(_colName, c);

      c.gridy++;

      c.gridx = 0;
      p.add(_appendColumn, c);
      c.gridx = 1;
      p.add(_newColName, c);

      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Treat X as pseudoatom"), c);
      c.gridx = 1;
      p.add(_treatXAsPseudoatom, c);

      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Ignore stereochemistry errors"), c);
      c.gridx = 1;
      p.add(_ignoreStereochemistryErrors, c);

      _appendColumn.addChangeListener(new ChangeListener()
      {
         public void stateChanged (final ChangeEvent e)
         {
            if (_appendColumn.isSelected())
            {
               _newColName.setEnabled(true);
               if ("".equals(_newColName.getText()))
               {
                  _newColName.setText(_colName.getSelectedColumn()
                        + " (Indigo)");
               }
            }
            else
               _newColName.setEnabled(false);
         }
      });
      _newColName.setEnabled(_appendColumn.isSelected());

      addTab("Standard settings", p);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      _settings.loadSettingsForDialog(settings);

      _colName.update(specs[0], _settings.colName);
      _appendColumn.setSelected(!_settings.replaceColumn);
      _newColName.setEnabled(!_settings.replaceColumn);
      _newColName
            .setText(_settings.newColName != null ? _settings.newColName : "");
      _treatXAsPseudoatom.setSelected(_settings.treatXAsPseudoatom);
      _ignoreStereochemistryErrors
            .setSelected(_settings.ignoreStereochemistryErrors);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveSettingsTo (final NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.colName = _colName.getSelectedColumn();
      _settings.replaceColumn = !_appendColumn.isSelected();
      _settings.newColName = _newColName.getText();
      _settings.treatXAsPseudoatom = _treatXAsPseudoatom.isSelected();
      _settings.ignoreStereochemistryErrors = _ignoreStereochemistryErrors
            .isSelected();
      _settings.saveSettings(settings);
   }
}
