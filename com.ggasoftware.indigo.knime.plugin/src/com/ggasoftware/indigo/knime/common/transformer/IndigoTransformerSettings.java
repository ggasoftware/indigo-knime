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

package com.ggasoftware.indigo.knime.common.transformer;

import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.ggasoftware.indigo.knime.common.IndigoNodeSettings;

public class IndigoTransformerSettings extends IndigoNodeSettings
{
   public static final int INPUT_PORT = 0;
   
   public final SettingsModelString colName = new SettingsModelString("colName", null);
   public final DeprecatedSettingsModelBooleanInverse appendColumn = new DeprecatedSettingsModelBooleanInverse("replaceColumn", false);
   public final SettingsModelString newColName = new SettingsModelString("newColName", null);
   
   /*
    * Parameter is not saved
    */
   public STRUCTURE_TYPE structureType;

   public IndigoTransformerSettings() {
      addSettingsParameter(colName);
      addSettingsParameter(appendColumn);
      addSettingsParameter(newColName);
   }
}
