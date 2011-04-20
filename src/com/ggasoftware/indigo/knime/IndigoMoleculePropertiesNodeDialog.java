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

package com.ggasoftware.indigo.knime;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

public class IndigoMoleculePropertiesNodeDialog extends NodeDialogPane
{

   private final ColumnSelectionPanel _selPanel;
   private final ColumnFilterPanel _filterPanel;
   
   private final IndigoMoleculePropertiesSettings _settings = new IndigoMoleculePropertiesSettings();

   /**
    * New pane for configuring IndigoBasicProperties node dialog. This is just a
    * suggestion to demonstrate possible default dialog components.
    */
   @SuppressWarnings("unchecked")
   protected IndigoMoleculePropertiesNodeDialog()
   {
      super();

      _selPanel = new ColumnSelectionPanel(IndigoMolValue.class);
      _filterPanel = new ColumnFilterPanel(false);
      JPanel panel = new JPanel(new BorderLayout(5, 5));
      panel.add(_selPanel, BorderLayout.NORTH);
      panel.add(_filterPanel, BorderLayout.CENTER);

      DataTableSpec dummySpec = new DataTableSpec(
            IndigoMoleculePropertiesNodeModel.colSpecs.values().toArray(
                  new DataColumnSpec[0]));
      Collection<String> selProps = IndigoMoleculePropertiesNodeModel.calculators
            .keySet();
      _filterPanel.update(dummySpec, false, selProps);

      addTab("Properties and target column", panel);
   }

   @Override
   protected void saveSettingsTo (NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.colName = _selPanel.getSelectedColumn();
      _settings.selectedProps = _filterPanel.getIncludedColumnSet().toArray(new String[0]);
      _settings.saveSettings(settings);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      _settings.loadSettingsForDialog(settings);
      
      DataTableSpec dummySpec = new DataTableSpec(
            IndigoMoleculePropertiesNodeModel.colSpecs.values().toArray(
                  new DataColumnSpec[0]));
      
      _filterPanel.update(dummySpec, false, _settings.selectedProps);
      _selPanel.update(specs[0], _settings.colName);
   }
}
