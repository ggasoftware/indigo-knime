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

import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;
import com.ggasoftware.indigo.knime.common.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.common.IndigoNodeSettings;
import com.ggasoftware.indigo.knime.common.IndigoNodeSettings.STRUCTURE_TYPE;

public class IndigoMoleculeFingerprinterNodeDialog extends NodeDialogPane
{
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _indigoColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class, IndigoReactionValue.class);
   private final JTextField _newColName = new JTextField(16);

   private final JSpinner _size = new JSpinner(new SpinnerNumberModel(IndigoMoleculeFingerprinterSettings.FP_DEFAULT, 
         IndigoMoleculeFingerprinterSettings.FP_MIN, 
         IndigoMoleculeFingerprinterSettings.FP_MAX, 1));
   
   private final JLabel _structureType = new JLabel();
   private DataTableSpec _indigoSpec;
   
   private final IndigoMoleculeFingerprinterSettings _settings = new IndigoMoleculeFingerprinterSettings();
   private ItemListener _changeListener = new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent arg0) {
         if ("".equals(_newColName.getText()))
            _newColName.setText(_indigoColumn.getSelectedColumn() + " (fingerprint)");
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

   /**
    * New pane for configuring the IndigoMoleculeFingerprinter node.
    */
   protected IndigoMoleculeFingerprinterNodeDialog()
   {
      super();
      
      _registerDialogComponents();
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Structure type", _structureType);
      dialogPanel.addItem("Indigo column", _indigoColumn);
      dialogPanel.addItem("New column name", _newColName);
      dialogPanel.addItemsPanel("Fingerprint Settings");
      dialogPanel.addItem("Fingerprint size in qwords:", _size);
      
      ((JSpinner.DefaultEditor)_size.getEditor()).getTextField().setColumns(2);

      _indigoColumn.addItemListener(_changeListener);
      _indigoColumn.addItemListener(_columnChangeListener);
      
      addTab("Standard settings", dialogPanel.getPanel());
   }
   
   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_indigoColumn, IndigoMoleculeFingerprinterSettings.INPUT_PORT, _settings.colName);
      _settings.registerDialogComponent(_newColName, _settings.newColName);
      _settings.registerDialogComponent(_size, _settings.fpSizeQWords);
   }
   
   /*
    * Returns current column selection state
    */
   private STRUCTURE_TYPE _getStructureType() {
      return IndigoNodeSettings.getStructureType(_indigoSpec, _indigoColumn.getSelectedColumn());
   }

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      STRUCTURE_TYPE stype = _getStructureType();

      if (stype.equals(STRUCTURE_TYPE.Unknown))
         throw new InvalidSettingsException("can not define the indigo column type");
      
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
         _indigoSpec = specs[IndigoMoleculeFingerprinterSettings.INPUT_PORT];
         _columnChangeListener.itemStateChanged(null);
         
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage());
      }
   }
}
