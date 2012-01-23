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

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.ggasoftware.indigo.knime.IndigoNodeSettings;

public class IndigoSubstructureMatcherSettings extends IndigoNodeSettings {
   
   enum MoleculeMode {
      Normal, Tautomer, Resonance
   }
   enum ReactionMode {
      Standard, DaylightAAM
   }
   
   public final SettingsModelString targetColName = new SettingsModelString("targetColName", null);
   public final SettingsModelString queryColName = new SettingsModelString("queryColName", null);
   public final SettingsModelInteger mode = new SettingsModelInteger("mode", MoleculeMode.Normal.ordinal());
   public final SettingsModelBoolean exact = new SettingsModelBoolean("exact", false);
   public final SettingsModelBoolean align = new SettingsModelBoolean("align", false);
   public final SettingsModelBoolean alignByQuery = new SettingsModelBoolean("alignByQuery", false);
   public final SettingsModelBoolean highlight = new SettingsModelBoolean("highlight", false);
   public final SettingsModelBoolean appendColumn = new SettingsModelBoolean("appendColumn", false);
   public final SettingsModelString newColName = new SettingsModelString("newColName", null);
   
   public final SettingsModelBoolean appendQueryKeyColumn = new SettingsModelBoolean("appendQueryKeyColumn", false);
   public final SettingsModelString queryKeyColumn = new SettingsModelString("queryKeyColumn", null);

   public final SettingsModelBoolean appendQueryMatchCountKeyColumn = new SettingsModelBoolean("appendQueryMatchCountKeyColumn", false);
   public final SettingsModelString queryMatchCountKeyColumn = new SettingsModelString("queryMatchCountKeyColumn", null);
   
   public final SettingsModelBoolean matchAllSelected = new SettingsModelBoolean("matchAllExceptSelected", false);
   public final SettingsModelBoolean matchAnyAtLeastSelected = new SettingsModelBoolean("matchAnyAtLeastSelected", true);
   public final SettingsModelInteger matchAnyAtLeast = new SettingsModelInteger("matchAnyAtLeast", 1);
   /*
    * Parameter is not saved
    */
   public STRUCTURE_TYPE structureType;
   
   public IndigoSubstructureMatcherSettings() {
      addSettingsParameter(targetColName);
      addSettingsParameter(queryColName);
      addSettingsParameter(mode);
      addSettingsParameter(exact);
      addSettingsParameter(align);
      addSettingsParameter(alignByQuery);
      addSettingsParameter(highlight);
      addSettingsParameter(appendColumn);
      addSettingsParameter(newColName);
      addSettingsParameter(appendQueryKeyColumn);
      addSettingsParameter(queryKeyColumn);
      addSettingsParameter(appendQueryMatchCountKeyColumn);
      addSettingsParameter(queryMatchCountKeyColumn);
      addSettingsParameter(matchAllSelected);
      addSettingsParameter(matchAnyAtLeastSelected);
      addSettingsParameter(matchAnyAtLeast);
   }

   public int getTargetColIdx(DataTableSpec inPortSpec) {
      return searchColumnIdx(targetColName.getStringValue(), "target", inPortSpec);
   }
   
   public int getQueryColIdx(DataTableSpec inPortSpec) {
      return searchColumnIdx(queryColName.getStringValue(), "query", inPortSpec);
   }

   
}
