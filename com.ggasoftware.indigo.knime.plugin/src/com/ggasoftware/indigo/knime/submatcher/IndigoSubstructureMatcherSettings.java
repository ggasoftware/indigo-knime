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

package com.ggasoftware.indigo.knime.submatcher;

import org.knime.core.node.*;

public class IndigoSubstructureMatcherSettings
{
   enum Mode
   {
      Normal, Tautomer, Resonance
   }
   
   public String colName;
   public String colName2;
   public Mode mode = Mode.Normal;
   public boolean exact = false;
   public boolean align = false;
   public boolean alignByQuery = false;
   public boolean highlight = false;
   public boolean appendColumn = false;
   public String newColName;

   public void loadSettings (final NodeSettingsRO settings)
         throws InvalidSettingsException
   {
      colName = settings.getString("colName");
      colName2 = settings.getString("colName2");
      newColName = settings.getString("newColName", null);
      align = settings.getBoolean("align");
      alignByQuery = settings.getBoolean("alignByQuery", false);
      exact = settings.getBoolean("exact", false);
      highlight = settings.getBoolean("highlight", false);
      appendColumn = settings.getBoolean("appendColumn", false);
      mode = Mode.valueOf(settings.getString("mode"));
   }

   public void loadSettingsForDialog (final NodeSettingsRO settings)
   {
      colName = settings.getString("colName", null);
      colName2 = settings.getString("colName2", null);
      newColName = settings.getString("newColName", null);
      mode = Mode.valueOf(settings.getString("mode", Mode.Normal.name()));
      align = settings.getBoolean("align", false);
      alignByQuery = settings.getBoolean("alignByQuery", false);
      exact = settings.getBoolean("exact", false);
      highlight = settings.getBoolean("highlight", false);
      appendColumn = settings.getBoolean("appendColumn", false);
   }

   public void saveSettings (final NodeSettingsWO settings)
   {
      if (colName != null)
         settings.addString("colName", colName);
      if (colName2 != null)
         settings.addString("colName2", colName2);
      if (newColName != null)
         settings.addString("newColName", newColName);
      if (mode != null)
         settings.addString("mode", mode.name());
      settings.addBoolean("appendColumn", appendColumn);
      settings.addBoolean("exact", exact);
      settings.addBoolean("align", align);
      settings.addBoolean("alignByQuery", alignByQuery);
      settings.addBoolean("highlight", highlight);
   }
}
