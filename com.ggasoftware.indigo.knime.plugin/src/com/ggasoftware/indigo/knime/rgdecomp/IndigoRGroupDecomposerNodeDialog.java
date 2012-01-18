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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.ColumnSelectionComboxBox;

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

      JPanel p = new JPanel(new GridBagLayout());

      GridBagConstraints c = new GridBagConstraints();

      c.anchor = GridBagConstraints.WEST;
      c.insets = new Insets(2, 2, 2, 2);
      c.gridy = 0;
      c.gridx = 0;
      p.add(new JLabel("Molecule column"), c);
      c.gridx = 1;
      p.add(_molColumn, c);

      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Query molecule column"), c);
      c.gridx = 1;
      p.add(_scafColumn, c);

      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("R-Group column prefix"), c);
      c.gridx = 1;
      p.add(_newColPrefix, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Scaffold column name"), c);
      c.gridx = 1;
      p.add(_newScafColName, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(_aromatize, c);
      
      addTab("Standard settings", p);
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
