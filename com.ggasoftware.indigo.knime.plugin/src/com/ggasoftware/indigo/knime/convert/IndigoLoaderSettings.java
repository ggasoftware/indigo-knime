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

package com.ggasoftware.indigo.knime.convert;

import org.knime.core.node.*;

class IndigoLoaderSettings
{
   public String colName;
   public boolean replaceColumn = true;
   public String newColName;
   public boolean treatXAsPseudoatom = true;
   public boolean ignoreStereochemistryErrors = true;

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
      treatXAsPseudoatom = settings.getBoolean("treatXAsPseudoatom");
      ignoreStereochemistryErrors = settings
            .getBoolean("ignoreStereochemistryErrors");
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
      newColName = settings.getString("newColName", null);
      treatXAsPseudoatom = settings.getBoolean("treatXAsPseudoatom", true);
      ignoreStereochemistryErrors = settings.getBoolean("ignoreStereochemistryErrors", true);
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
      settings.addBoolean("treatXAsPseudoatom", treatXAsPseudoatom);
      settings.addBoolean("ignoreStereochemistryErrors",
            ignoreStereochemistryErrors);
   }
}
