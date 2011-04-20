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
import javax.swing.*;

import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.node.util.*;
import javax.swing.border.*;

public class IndigoValenceCheckerNodeDialog extends NodeDialogPane
{

   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox m_molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);

   IndigoValenceCheckerSettings m_settings = new IndigoValenceCheckerSettings();

   protected IndigoValenceCheckerNodeDialog()
   {
      super();

      JPanel p = new JPanel(new GridBagLayout());

      GridBagConstraints c = new GridBagConstraints();

      c.anchor = GridBagConstraints.WEST;
      c.insets = new Insets(2, 2, 2, 2);
      c.gridx = 0;
      c.gridy = 0;
      p.add(new JLabel("Indigo column   "), c);
      c.gridx = 1;
      p.add(m_molColumn, c);

      addTab("Standard settings", p);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      m_settings.loadSettingsForDialog(settings);

      m_molColumn.update(specs[0], m_settings.colName);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveSettingsTo (final NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      m_settings.colName = m_molColumn.getSelectedColumn();
      m_settings.saveSettings(settings);
   }
}
