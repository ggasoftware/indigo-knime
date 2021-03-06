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

package com.ggasoftware.indigo.knime.common.transformer;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;
import com.ggasoftware.indigo.knime.common.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.common.IndigoNodeSettings;
import com.ggasoftware.indigo.knime.common.IndigoNodeSettings.STRUCTURE_TYPE;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class IndigoTransformerNodeDialog extends NodeDialogPane
{
   public static interface DialogComponents {
      public void loadDialogComponents(IndigoDialogPanel dialogPanel, IndigoTransformerSettings settings);
   }

   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _indigoColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class, IndigoReactionValue.class);

   private final JCheckBox _appendColumn = new JCheckBox("Append column");
   private final JTextField _newColName = new JTextField(20);
   private final IndigoTransformerSettings _settings;
   private final String _desc;
   
   private final JLabel _structureType = new JLabel();
   private DataTableSpec _indigoSpec;
   
   ChangeListener _changeListener = new ChangeListener()
   {
      public void stateChanged (final ChangeEvent e)
      {
         if (_appendColumn.isSelected())
         {
            _newColName.setEnabled(true);
            if ("".equals(_newColName.getText()))
            {
               _newColName.setText(_indigoColumn.getSelectedColumn() + " ("
                     + _desc + ")");
            }
         }
         else
         {
            _newColName.setEnabled(false);
         }
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

   public IndigoTransformerNodeDialog (String desc, IndigoTransformerSettings settings, DialogComponents dialogComponents)
   {
      super();
      _desc = desc;
      _settings = settings;
      
      _registerDialogComponents();

      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Structure type", _structureType);
      dialogPanel.addItem("Indigo column", _indigoColumn);
      dialogPanel.addItem(_appendColumn, _newColName);

      _indigoColumn.addItemListener(_columnChangeListener);
      _appendColumn.addChangeListener(_changeListener);
      
      if(dialogComponents != null)
         dialogComponents.loadDialogComponents(dialogPanel, _settings);

      addTab("Standard settings", dialogPanel.getPanel());
   }
   
   public IndigoTransformerNodeDialog (String desc, IndigoTransformerSettings settings) {
      this(desc, settings, null);
   }
   
   public IndigoTransformerNodeDialog (String desc)
   {
      this(desc, new IndigoTransformerSettings(), null);
   }

   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_indigoColumn, IndigoTransformerSettings.INPUT_PORT, _settings.colName);
      _settings.registerDialogComponent(_appendColumn, _settings.appendColumn);
      _settings.registerDialogComponent(_newColName, _settings.newColName);
   }
   
   /*
    * Returns current column selection state
    */
   private STRUCTURE_TYPE _getStructureType() {
      return IndigoNodeSettings.getStructureType(_indigoSpec, _indigoColumn.getSelectedColumn());
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
         
         _indigoSpec = specs[IndigoTransformerSettings.INPUT_PORT];
         _changeListener.stateChanged(null);
         _columnChangeListener.itemStateChanged(null);
         
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage());
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveSettingsTo (final NodeSettingsWO settings)
      throws InvalidSettingsException {
      STRUCTURE_TYPE stype = _getStructureType();

      if (stype.equals(STRUCTURE_TYPE.Unknown))
         throw new InvalidSettingsException("can not define the indigo column type");

      _settings.saveDialogSettings();
      _settings.saveSettingsTo(settings);
   }
}
