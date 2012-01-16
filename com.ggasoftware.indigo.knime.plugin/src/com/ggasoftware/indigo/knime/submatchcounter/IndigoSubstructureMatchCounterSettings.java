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

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;
import com.ggasoftware.indigo.knime.IndigoNodeSettings;

public class IndigoSubstructureMatchCounterSettings extends IndigoNodeSettings
{
   public enum Uniqueness
   {
      Atoms, Bonds, None
   }

   public final SettingsModelColumnName targetColName = new SettingsModelColumnName("targetColName", null);
   public final SettingsModelColumnName queryColName = new SettingsModelColumnName("queryColName", null);
   public final SettingsModelString newColName = new SettingsModelString("newColName", "Number of matches");
   public final SettingsModelInteger uniqueness = new SettingsModelInteger("uniqueness", Uniqueness.Atoms.ordinal());
   public final SettingsModelBoolean highlight = new SettingsModelBoolean("highlight", false);
   public final SettingsModelBoolean appendColumn = new SettingsModelBoolean("appendColumn", false);
   public final SettingsModelString appendColumnName = new SettingsModelString("appendColumnName", null);
   /*
    * Parameter is not saved
    */
   public STRUCTURE_TYPE structureType;

   
   public IndigoSubstructureMatchCounterSettings() {
      addSettingsParameter(targetColName);
      addSettingsParameter(queryColName);
      addSettingsParameter(newColName);
      addSettingsParameter(uniqueness);
      addSettingsParameter(highlight);
      addSettingsParameter(appendColumn);
      addSettingsParameter(appendColumnName);
   }


   public int getTargetColumnIdx(DataTableSpec inPortSpec) {
      return searchColumnIdx(targetColName.getStringValue(), "target", inPortSpec);
   }


   public int getQueryColumnIdx(DataTableSpec inPortSpec) {
      return searchColumnIdx(queryColName.getStringValue(), "query", inPortSpec);
   }
}
