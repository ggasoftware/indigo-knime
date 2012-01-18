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

import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

import com.ggasoftware.indigo.knime.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;

public class IndigoMoleculeFingerprinterNodeDialog extends NodeDialogPane
{
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);
   private final JTextField _newColName = new JTextField(16);

   private final JSpinner _size = new JSpinner(new SpinnerNumberModel(IndigoMoleculeFingerprinterSettings.FP_DEFAULT, 
         IndigoMoleculeFingerprinterSettings.FP_MIN, 
         IndigoMoleculeFingerprinterSettings.FP_MAX, 1));
   
   private final IndigoMoleculeFingerprinterSettings _settings = new IndigoMoleculeFingerprinterSettings();
   private ItemListener _changeListener = new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent arg0) {
         if ("".equals(_newColName.getText()))
            _newColName.setText(_molColumn.getSelectedColumn() + " (fingerprint)");
      }
   };

   /**
    * New pane for configuring the IndigoMoleculeFingerprinter node.
    */
   protected IndigoMoleculeFingerprinterNodeDialog()
   {
      super();
      
      _registerDialogComponents();
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Indigo column", _molColumn);
      dialogPanel.addItem("New column name", _newColName);
      dialogPanel.addItemsPanel("Fingerprint Settings");
      dialogPanel.addItem("Fingerprint size in qwords:", _size);
      
      ((JSpinner.DefaultEditor)_size.getEditor()).getTextField().setColumns(2);

      _molColumn.addItemListener(_changeListener);      
      
      addTab("Standard settings", dialogPanel.getPanel());
   }
   
   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_molColumn, IndigoMoleculeFingerprinterSettings.INPUT_PORT, _settings.colName);
      _settings.registerDialogComponent(_newColName, _settings.newColName);
      _settings.registerDialogComponent(_size, _settings.fpSizeQWords);
   }

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.saveDialogSettings();
      _settings.saveSettingsTo(settings);
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
         
         _changeListener.itemStateChanged(null);
         
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage());
      }
   }
}
