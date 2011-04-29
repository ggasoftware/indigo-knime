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

package com.ggasoftware.indigo.knime.convert.molsaver;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

public class IndigoMoleculeSaverSettings
{
   public enum Format
   {
      Mol, Smiles, CanonicalSmiles, CML
   }

   public String colName;
   public Format destFormat = Format.Smiles;
   public boolean replaceColumn = true;
   public String newColName;
   public boolean generateCoords = true;

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
      colName = settings.getString("colName");
      replaceColumn = settings.getBoolean("replaceColumn");
      newColName = settings.getString("newColName");
      destFormat = Format.valueOf(settings.getString("destFormat"));
      generateCoords = settings.getBoolean("generateCoords");
   }

   /**
    * Loads the settings from the given node settings object.
    * 
    * @param settings
    *           node settings
    */
   public void loadSettingsForDialog (final NodeSettingsRO settings)
   {
      colName = settings.getString("colName", null);
      replaceColumn = settings.getBoolean("replaceColumn", true);
      newColName = settings.getString("newColName", "");
      destFormat = Format.valueOf(settings.getString("destFormat", Format.Mol.name()));
      generateCoords = settings.getBoolean("generateCoords", true);
   }

   /**
    * Saves the settings to the given node settings object.
    * 
    * @param settings
    *           node settings
    */
   public void saveSettings (final NodeSettingsWO settings)
   {
      if (colName != null)
         settings.addString("colName", colName);
      settings.addBoolean("replaceColumn", replaceColumn);
      if (newColName != null)
         settings.addString("newColName", newColName);
      settings.addString("destFormat", destFormat.name());
      settings.addBoolean("generateCoords", generateCoords);
   }
}
