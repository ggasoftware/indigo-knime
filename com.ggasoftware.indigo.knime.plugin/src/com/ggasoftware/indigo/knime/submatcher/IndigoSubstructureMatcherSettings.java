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

import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;

import com.ggasoftware.indigo.knime.IndigoNodeSettings;

public class IndigoSubstructureMatcherSettings extends IndigoNodeSettings {
   
   enum Mode
   {
      Normal, Tautomer, Resonance
   }
   
   public SettingsModelColumnName targetColName = new SettingsModelColumnName("colName", null);
   public SettingsModelColumnName queryColName = new SettingsModelColumnName("colName2", null);
   public SettingsModelInteger mode = new SettingsModelInteger("mode", Mode.Normal.ordinal());
   public SettingsModelBoolean exact = new SettingsModelBoolean("exact", false);
   public SettingsModelBoolean align = new SettingsModelBoolean("align", false);
   public SettingsModelBoolean alignByQuery = new SettingsModelBoolean("alignByQuery", false);
   public SettingsModelBoolean highlight = new SettingsModelBoolean("highlight", false);
   public SettingsModelBoolean appendColumn = new SettingsModelBoolean("appendColumn", false);
   public SettingsModelColumnName newColName = new SettingsModelColumnName("newColName", null);
   
   public SettingsModelBoolean appendQueryKeyColumn = new SettingsModelBoolean("appendQueryKeyColumn", false);
   public SettingsModelColumnName queryKeyColumn = new SettingsModelColumnName("queryKeyColumn", null);

   public SettingsModelBoolean appendQueryMatchCountKeyColumn = new SettingsModelBoolean("appendQueryMatchCountKeyColumn", false);
   public SettingsModelColumnName queryMatchCountKeyColumn = new SettingsModelColumnName("queryMatchCountKeyColumn", null);
   
   public SettingsModelBoolean matchAllSelected = new SettingsModelBoolean("matchAllExceptSelected", false);
   public SettingsModelBoolean matchAnyAtLeastSelected = new SettingsModelBoolean("matchAnyAtLeastSelected", true);
   public SettingsModelInteger matchAnyAtLeast = new SettingsModelInteger("matchAnyAtLeast", 1);
   
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
   
}
