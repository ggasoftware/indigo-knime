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

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

public class IndigoRGroupDecomposerSettings
{
   public String colName;
   public String colName2 ;
   public String newColPrefix;
   public boolean aromatize = true;
   public int numRGroups = 0;

   public void loadSettings (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      colName = settings.getString("colName");
      colName2 = settings.getString("colName2");
      newColPrefix = settings.getString("newColPrefix");
      aromatize = settings.getBoolean("aromatize");
      numRGroups = settings.getInt("numRGroups");
   }

   public void loadSettingsForDialog (final NodeSettingsRO settings)
   {
      colName = settings.getString("colName", null);
      colName2 = settings.getString("colName2", null);
      newColPrefix = settings.getString("newColPrefix", "R-Group #");
      aromatize = settings.getBoolean("aromatize", true);
      numRGroups = settings.getInt("numRGroups", 9);
   }

   public void saveSettings (final NodeSettingsWO settings)
   {
      if (colName != null)
         settings.addString("colName", colName);
      if (colName2 != null)
         settings.addString("colName2", colName2);
      if (newColPrefix != null)
         settings.addString("newColPrefix", newColPrefix);
      if (numRGroups > 0)
         settings.addInt("numRGroups", numRGroups);
      settings.addBoolean("aromatize", aromatize);
   }
}