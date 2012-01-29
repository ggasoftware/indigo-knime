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

package com.ggasoftware.indigo.knime.common;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

import com.ggasoftware.indigo.knime.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.IndigoNodeSettings;
import com.ggasoftware.indigo.knime.IndigoNodeSettings.STRUCTURE_TYPE;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class IndigoSimpleNodeDialog extends NodeDialogPane
{

   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _indigoColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class, IndigoReactionValue.class);

   private final JCheckBox _appendColumn = new JCheckBox("Append column");
   private final JTextField _newColName = new JTextField(20);
   private final IndigoSimpleSettings _settings;
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

   protected final IndigoDialogPanel _dialogPanel;

   public IndigoSimpleNodeDialog (String desc, IndigoSimpleSettings settings, boolean needAddTab)
   {
      super();
      _desc = desc;
      _settings = settings;
      
      _registerDialogComponents();

      _dialogPanel = new IndigoDialogPanel();
      
      _dialogPanel.addItemsPanel("Column Settings");
      _dialogPanel.addItem("Structure type", _structureType);
      _dialogPanel.addItem("Indigo column", _indigoColumn);
      _dialogPanel.addItem(_appendColumn, _newColName);

      _indigoColumn.addItemListener(_columnChangeListener);
      _appendColumn.addChangeListener(_changeListener);

      if (needAddTab)
         addTabDialog();
   }
   
   protected void addTabDialog ()
   {
      addTab("Standard settings", _dialogPanel.getPanel());
   }
   
   public IndigoSimpleNodeDialog (String desc)
   {
      this(desc, new IndigoSimpleSettings(), true);
   }

   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_indigoColumn, IndigoSimpleSettings.INPUT_PORT, _settings.colName);
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
         
         _indigoSpec = specs[IndigoSimpleSettings.INPUT_PORT];
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
