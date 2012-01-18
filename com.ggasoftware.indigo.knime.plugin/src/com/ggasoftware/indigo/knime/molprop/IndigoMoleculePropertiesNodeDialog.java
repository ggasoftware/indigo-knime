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

package com.ggasoftware.indigo.knime.molprop;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.Border;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

import com.ggasoftware.indigo.knime.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;

public class IndigoMoleculePropertiesNodeDialog extends NodeDialogPane
{

   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _indigoColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);
   private final ColumnFilterPanel _filterPanel;
   
   private final IndigoMoleculePropertiesSettings _settings = new IndigoMoleculePropertiesSettings();

   /**
    * New pane for configuring IndigoBasicProperties node dialog. This is just a
    * suggestion to demonstrate possible default dialog components.
    */
   protected IndigoMoleculePropertiesNodeDialog()
   {
      super();

      _settings.registerDialogComponent(_indigoColumn, 0, _settings.colName);
      
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      dialogPanel.addItemsPanel("Column settings");
      dialogPanel.addItem("Indigo column", _indigoColumn);
      
      _filterPanel = new ColumnFilterPanel(false);
      JPanel panel = new JPanel(new BorderLayout(5, 5));
      panel.add(dialogPanel.getPanel(), BorderLayout.NORTH);
      panel.add(_filterPanel, BorderLayout.CENTER);

      DataTableSpec dummySpec = new DataTableSpec(IndigoMoleculePropertiesNodeModel.colSpecsArray);
      Collection<String> selProps = IndigoMoleculePropertiesNodeModel.names;
      _filterPanel.update(dummySpec, false, selProps);

      addTab("Properties and target column", panel);
   }

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.selectedProps.setStringArrayValue(_filterPanel.getIncludedColumnSet().toArray(new String[0]));
      
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
         DataTableSpec dummySpec = new DataTableSpec(IndigoMoleculePropertiesNodeModel.colSpecsArray);
         
         _filterPanel.update(dummySpec, false, _settings.selectedProps.getStringArrayValue());
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage());
      }
      
   }
}
