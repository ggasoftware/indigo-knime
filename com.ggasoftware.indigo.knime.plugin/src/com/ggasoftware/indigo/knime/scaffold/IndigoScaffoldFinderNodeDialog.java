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

package com.ggasoftware.indigo.knime.scaffold;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.cell.IndigoMolValue;

public class IndigoScaffoldFinderNodeDialog extends NodeDialogPane {
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox((Border) null, IndigoMolValue.class);

   private final JCheckBox _tryExact = new JCheckBox("Try exact method");
   private final JSpinner _exactIterations = new JSpinner();
   private final JSpinner _approxIterations = new JSpinner();

   private final JTextField _newColName = new JTextField(20);

   private final IndigoScaffoldFinderSettings _settings = new IndigoScaffoldFinderSettings();

   private ChangeListener _changeListener = new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent arg0) {
         _exactIterations.setEnabled(_tryExact.isSelected());
      }
   };

   /**
    * New pane for configuring the IndigoScaffoldFinder node.
    */
   protected IndigoScaffoldFinderNodeDialog() {
      super();

      _registerDialogComponents();
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Molecule column", _molColumn);
      dialogPanel.addItem("New column with scaffolds", _newColName);
      dialogPanel.addItemsPanel("Scaffold Finder Settings");
      dialogPanel.addItem(_tryExact);
      dialogPanel.addItem("Maximum number of iterations for exact method (0 to no limit)", _exactIterations);
      dialogPanel.addItem("Number of iterations for approximate method", _approxIterations);
      

      ((JSpinner.DefaultEditor) _exactIterations.getEditor()).getTextField().setColumns(8);
      ((JSpinner.DefaultEditor) _approxIterations.getEditor()).getTextField().setColumns(8);
      
      _tryExact.addChangeListener(_changeListener);

      addTab("Standard settings", dialogPanel.getPanel());
   }

   private void _registerDialogComponents() {
      _settings.registerDialogComponent(_molColumn, IndigoScaffoldFinderSettings.INPUT_PORT, _settings.colName);
      _settings.registerDialogComponent(_newColName, _settings.newColName);
      _settings.registerDialogComponent(_tryExact, _settings.tryExactMethod);
      _settings.registerDialogComponent(_exactIterations, _settings.maxIterExact);
      _settings.registerDialogComponent(_approxIterations, _settings.maxIterApprox);
   }

   @Override
   protected void saveSettingsTo(NodeSettingsWO settings) throws InvalidSettingsException {
      _settings.saveDialogSettings();
      _settings.saveSettingsTo(settings);
   }

   @Override
   protected void loadSettingsFrom(final NodeSettingsRO settings, final DataTableSpec[] specs) throws NotConfigurableException {
      try {
         _settings.loadSettingsFrom(settings);
         _settings.loadDialogSettings(specs);

         _changeListener.stateChanged(null);
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage());
      }
   }
}
