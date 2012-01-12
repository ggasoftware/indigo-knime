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
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
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
   
   public SettingsModelColumnName targetColName = new SettingsModelColumnName("targetColName", null);
   public SettingsModelColumnName queryColName = new SettingsModelColumnName("queryColName", null);
   public SettingsModelInteger mode = new SettingsModelInteger("mode", MoleculeMode.Normal.ordinal());
   public SettingsModelBoolean exact = new SettingsModelBoolean("exact", false);
   public SettingsModelBoolean align = new SettingsModelBoolean("align", false);
   public SettingsModelBoolean alignByQuery = new SettingsModelBoolean("alignByQuery", false);
   public SettingsModelBoolean highlight = new SettingsModelBoolean("highlight", false);
   public SettingsModelBoolean appendColumn = new SettingsModelBoolean("appendColumn", false);
   public SettingsModelString newColName = new SettingsModelString("newColName", null);
   
   public SettingsModelBoolean appendQueryKeyColumn = new SettingsModelBoolean("appendQueryKeyColumn", false);
   public SettingsModelString queryKeyColumn = new SettingsModelString("queryKeyColumn", null);

   public SettingsModelBoolean appendQueryMatchCountKeyColumn = new SettingsModelBoolean("appendQueryMatchCountKeyColumn", false);
   public SettingsModelString queryMatchCountKeyColumn = new SettingsModelString("queryMatchCountKeyColumn", null);
   
   public SettingsModelBoolean matchAllSelected = new SettingsModelBoolean("matchAllExceptSelected", false);
   public SettingsModelBoolean matchAnyAtLeastSelected = new SettingsModelBoolean("matchAnyAtLeastSelected", true);
   public SettingsModelInteger matchAnyAtLeast = new SettingsModelInteger("matchAnyAtLeast", 1);
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
