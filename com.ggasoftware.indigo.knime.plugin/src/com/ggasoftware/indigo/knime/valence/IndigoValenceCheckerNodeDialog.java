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

import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

import com.ggasoftware.indigo.knime.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;

import javax.swing.border.*;

public class IndigoValenceCheckerNodeDialog extends NodeDialogPane
{
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _indigoColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);

   IndigoValenceCheckerSettings _settings = new IndigoValenceCheckerSettings();

   protected IndigoValenceCheckerNodeDialog()
   {
      super();
      
      _settings.registerDialogComponent(_indigoColumn, 0, _settings.colName);
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Indigo column", _indigoColumn);

      addTab("Standard settings", dialogPanel.getPanel());
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
