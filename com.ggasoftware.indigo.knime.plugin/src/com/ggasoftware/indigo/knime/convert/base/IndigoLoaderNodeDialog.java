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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.common.IndigoDialogPanel;

public class IndigoLoaderNodeDialog extends NodeDialogPane
{
   private final IndigoLoaderSettings _settings;
   
   private final ColumnSelectionComboxBox _indigoColumn;
   private final JCheckBox _appendColumn = new JCheckBox("Append column");
   private final JTextField _newColName = new JTextField(20);
   
   private final JCheckBox _treatXAsPseudoatom = new JCheckBox("Treat X as pseudoatom");
   private final JCheckBox _ignoreStereochemistryErrors = new JCheckBox("Ignore stereochemistry errors");
   private final JCheckBox _treatStringAsSMARTS = new JCheckBox("Treat string as SMARTS");
   
   private DataTableSpec _indigoSpec;
   
   private final ChangeListener _changeListener = new ChangeListener() {
      public void stateChanged(final ChangeEvent e) {
         if (_appendColumn.isSelected()) {
            _newColName.setEnabled(true);
            if ("".equals(_newColName.getText())) {
               _newColName.setText(_indigoColumn.getSelectedColumn() + " (Indigo)");
            }
         } else
            _newColName.setEnabled(false);
      }
   };

   private ItemListener _smartsItemListener = new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
         _treatStringAsSMARTS.setEnabled(false);
         if (_indigoSpec != null) {
            DataColumnSpec colSpec = _indigoSpec.getColumnSpec(_indigoColumn.getSelectedColumn());
            if (colSpec != null) {
               if (colSpec.getType().getPreferredValueClass().equals(org.knime.core.data.StringValue.class))
                  _treatStringAsSMARTS.setEnabled(true);
               else {
                  _treatStringAsSMARTS.setEnabled(false);
                  _treatStringAsSMARTS.setSelected(false);
               }
            }
         }
      }
   };;;

   /**
    * New pane for configuring IndigoMoleculeLoader node dialog. This is just a
    * suggestion to demonstrate possible default dialog components.
    */
   protected IndigoLoaderNodeDialog(String columnLabel, Class<? extends DataValue>[] filterValueClasses, boolean query)
   {
      super();
      _settings = new IndigoLoaderSettings(query);
      
      _indigoColumn = new ColumnSelectionComboxBox((Border) null, filterValueClasses);
      _registerDialogComponents();
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Indigo column", _indigoColumn);
      dialogPanel.addItem(_appendColumn, _newColName);
      dialogPanel.addItemsPanel("Loader Settings");
      dialogPanel.addItem(_treatXAsPseudoatom);
      dialogPanel.addItem(_ignoreStereochemistryErrors);
      
      if(query) {
         dialogPanel.addItem(_treatStringAsSMARTS);
         _indigoColumn.addItemListener(_smartsItemListener);
      }

      _appendColumn.addChangeListener(_changeListener);
      addTab("Standard settings", dialogPanel.getPanel());
   }

   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_indigoColumn, IndigoLoaderSettings.INPUT_PORT, _settings.colName);
      _settings.registerDialogComponent(_appendColumn, _settings.appendColumn);
      _settings.registerDialogComponent(_newColName, _settings.newColName);
      _settings.registerDialogComponent(_treatXAsPseudoatom, _settings.treatXAsPseudoatom);
      _settings.registerDialogComponent(_ignoreStereochemistryErrors, _settings.ignoreStereochemistryErrors);
      if(_settings.query)
         _settings.registerDialogComponent(_treatStringAsSMARTS, _settings.treatStringAsSMARTS);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      try {
         _settings.loadSettingsFrom(settings);
         _settings.loadDialogSettings(specs);
         
         _indigoSpec = specs[IndigoLoaderSettings.INPUT_PORT];
         
         _changeListener.stateChanged(null);
         if(_settings.query)
            _smartsItemListener.itemStateChanged(null);
         
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage());
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveSettingsTo (final NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.saveDialogSettings();
      _settings.saveSettingsTo(settings);
   }
}
