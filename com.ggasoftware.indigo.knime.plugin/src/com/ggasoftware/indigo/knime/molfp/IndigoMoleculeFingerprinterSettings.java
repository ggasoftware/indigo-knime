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

package com.ggasoftware.indigo.knime.molfp;

import org.knime.core.node.*;

public class IndigoMoleculeFingerprinterSettings
{
   public int fpSizeQWords = 8;
   public String colName;
   public String newColName;

   /**
    * Loads the settings from the given node settings object.
    * 
    * @param settings
    *           node settings
    * @throws InvalidSettingsException
    *            if some settings are missing
    */
   public void loadSettings (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      fpSizeQWords = settings.getInt("fpSizeQWords");
      colName = settings.getString("colName");
      newColName = settings.getString("newColName");
   }

   /**
    * Loads the settings from the given node settings object.
    * 
    * @param settings
    *           node settings
    * @throws InvalidSettingsException
    */
   public void loadSettingsForDialog (final NodeSettingsRO settings)
   {
      fpSizeQWords = settings.getInt("fpSizeQWords", 8);
      colName = settings.getString("colName", null);
      newColName = settings.getString("newColName", null);
   }

   /**
    * Saves the settings to the given node settings object.
    * 
    * @param settings
    *           node settings
    */
   public void saveSettings (final NodeSettingsWO settings)
   {
      settings.addInt("fpSizeQWords", fpSizeQWords);
      settings.addString("colName", colName);
      settings.addString("newColName", newColName);
   }
}
