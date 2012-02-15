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

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryReactionValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;
import com.ggasoftware.indigo.knime.common.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.convert.base.IndigoSaverSettings.Format;

public class IndigoSaverNodeDialog extends NodeDialogPane
{

   private final IndigoSaverSettings _settings = new IndigoSaverSettings();
   
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _indigoColumn = new ColumnSelectionComboxBox(
         (Border) null,
         IndigoMolValue.class, IndigoQueryMolValue.class, IndigoReactionValue.class, IndigoQueryReactionValue.class);
   private final JComboBox _destFormat;
   private final JCheckBox _appendColumn = new JCheckBox("Append column");
   private final JTextField _newColName = new JTextField(20);
   private final JCheckBox _generateCoords = new JCheckBox("Generate coordinates if needed");
   
   private final ChangeListener _changeListener = new ChangeListener() {
      public void stateChanged(final ChangeEvent e) {
         if (_appendColumn.isSelected()) {
            _newColName.setEnabled(true);
            if ("".equals(_newColName.getText())) {
               _newColName.setText(_indigoColumn.getSelectedColumn() + " (saved)");
            }
         } else {
            _newColName.setEnabled(false);
         }
      }
   };

   private final ItemListener _formatListener = new ItemListener()
   {
      @Override
      public void itemStateChanged (ItemEvent arg0)
      {
         Format selected = (Format)_destFormat.getSelectedItem(); 
         if (selected == Format.CML || selected == Format.Mol || selected == Format.SDF || selected == Format.Rxn)
            _generateCoords.setEnabled(true);
         else
            _generateCoords.setEnabled(false);
      }
   };

   /**
    * New pane for configuring the IndigoMoleculeSaver node.
    */
   protected IndigoSaverNodeDialog (Object[] formats)
   {
      
      _destFormat = new JComboBox(formats);
      _registerDialogComponents();
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Indigo column", _indigoColumn);
      dialogPanel.addItem(_appendColumn, _newColName);
      dialogPanel.addItemsPanel("Saver Settings");
      dialogPanel.addItem("Destination format", _destFormat);
      dialogPanel.addItem(_generateCoords);

      _destFormat.addItemListener(_formatListener );
      _appendColumn.addChangeListener(_changeListener );

      addTab("Standard settings", dialogPanel.getPanel());
   }

   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_indigoColumn, IndigoSaverSettings.INPUT_PORT, _settings.colName);
      _settings.registerDialogComponent(_appendColumn, _settings.appendColumn);
      _settings.registerDialogComponent(_newColName, _settings.newColName);
      _settings.registerDialogComponent(_generateCoords, _settings.generateCoords);
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
         _destFormat.setSelectedItem(Format.valueOf(_settings.destFormat.getStringValue()));
         
         _changeListener.stateChanged(null);
         _formatListener.itemStateChanged(null);
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
      _settings.destFormat.setStringValue(_destFormat.getSelectedItem().toString());
      
      _settings.saveSettingsTo(settings);
   }
}
