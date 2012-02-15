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

package com.ggasoftware.indigo.knime.valence;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;
import com.ggasoftware.indigo.knime.common.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.common.IndigoNodeSettings;
import com.ggasoftware.indigo.knime.common.IndigoNodeSettings.STRUCTURE_TYPE;

import javax.swing.JLabel;
import javax.swing.border.*;

public class IndigoValenceCheckerNodeDialog extends NodeDialogPane
{
   private final IndigoValenceCheckerSettings _settings = new IndigoValenceCheckerSettings();
   
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _indigoColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class, IndigoReactionValue.class);

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

   private final JLabel _structureType = new JLabel();
   private DataTableSpec _indigoSpec;
   
   protected IndigoValenceCheckerNodeDialog()
   {
      super();
      
      _settings.registerDialogComponent(_indigoColumn, 0, _settings.colName);
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Structure type", _structureType);
      dialogPanel.addItem("Indigo column", _indigoColumn);
      
      _indigoColumn.addItemListener(_columnChangeListener);

      addTab("Standard settings", dialogPanel.getPanel());
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
         
         _indigoSpec = specs[IndigoValenceCheckerSettings.INPUT_PORT];
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
         throws InvalidSettingsException
   {
      STRUCTURE_TYPE stype = _getStructureType();

      if (stype.equals(STRUCTURE_TYPE.Unknown))
         throw new InvalidSettingsException("can not define the indigo column type");
      
      _settings.saveDialogSettings();
      _settings.saveSettingsTo(settings);
   }
}
