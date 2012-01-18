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

package com.ggasoftware.indigo.knime.rgdecomp;

import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolValue;

public class IndigoRGroupDecomposerNodeDialog extends NodeDialogPane
{
   IndigoRGroupDecomposerSettings _settings = new IndigoRGroupDecomposerSettings();
   
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _scafColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoQueryMolValue.class);
   
   private final JTextField _newColPrefix = new JTextField(10);
   private final JTextField _newScafColName = new JTextField(10);
   private final JCheckBox _aromatize = new JCheckBox("Aromatize");
   
   /**
    * New pane for configuring the IndigoRGroupDecomposer node.
    */
   protected IndigoRGroupDecomposerNodeDialog()
   {
      super();
      
      _registerDialogComponents();
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Molecule column", _molColumn);
      dialogPanel.addItem("Scaffold query column", _scafColumn);
      dialogPanel.addItemsPanel("R-Group Decomposer Settings");
      dialogPanel.addItem("R-Group column prefix", _newColPrefix);
      dialogPanel.addItem("Scaffold column name", _newScafColName);
      dialogPanel.addItem(_aromatize);
      
      IndigoDialogPanel.setDefaultFont(_aromatize);
      
      addTab("Standard settings", dialogPanel.getPanel());
   }

   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_molColumn, IndigoRGroupDecomposerSettings.MOL_PORT, _settings.molColumn);
      _settings.registerDialogComponent(_scafColumn, IndigoRGroupDecomposerSettings.SCAF_PORT, _settings.scaffoldColumn);
      _settings.registerDialogComponent(_newColPrefix, _settings.newColPrefix);
      _settings.registerDialogComponent(_newScafColName, _settings.newScafColName);
      _settings.registerDialogComponent(_aromatize, _settings.aromatize);
   }
   
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

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.saveDialogSettings();
      _settings.saveSettingsTo(settings);
   }   
}
