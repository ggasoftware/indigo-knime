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

package com.ggasoftware.indigo.knime.submatchcounter;

import org.knime.core.node.*;

public class IndigoSubstructureMatchCounterSettings
{
   public enum Uniqueness
   {
      Atoms, Bonds, None
   }

   public String colName;
   public String colName2;
   public String newColName = "Number of matches";
   Uniqueness uniqueness = Uniqueness.Atoms;
   public boolean highlight = false;
   public boolean appendColumn = false;
   public String newColName2;

   public void loadSettings (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      colName = settings.getString("colName");
      colName2 = settings.getString("colName2");
      newColName = settings.getString("newColName");
      uniqueness = Uniqueness.valueOf(settings.getString("uniqueness"));
      highlight = settings.getBoolean("highlight", false);
      appendColumn = settings.getBoolean("appendColumn", false);
      newColName2 = settings.getString("newColName2", null);
   }

   public void loadSettingsForDialog (final NodeSettingsRO settings)
   {
      colName = settings.getString("colName", null);
      colName2 = settings.getString("colName2", null);
      newColName = settings.getString("newColName", "Number of matches");
      uniqueness = Uniqueness.valueOf(settings.getString("uniqueness", Uniqueness.Atoms.name()));
      highlight = settings.getBoolean("highlight", false);
      appendColumn = settings.getBoolean("appendColumn", false);
      newColName2 = settings.getString("newColName2", null);
   }

   public void saveSettings (final NodeSettingsWO settings)
   {
      if (colName != null)
         settings.addString("colName", colName);
      if (colName2 != null)
         settings.addString("colName2", colName2);
      if (newColName != null)
         settings.addString("newColName", newColName);
      if (uniqueness != null)
         settings.addString("uniqueness", uniqueness.name());
      settings.addBoolean("highlight", highlight);
      settings.addBoolean("appendColumn", appendColumn);
      if (newColName2 != null)
         settings.addString("newColName2", newColName2);
   }
}
