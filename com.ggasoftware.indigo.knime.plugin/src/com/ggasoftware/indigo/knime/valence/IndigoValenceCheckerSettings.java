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

package com.ggasoftware.indigo.knime.valence;

import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;

import com.ggasoftware.indigo.knime.IndigoNodeSettings;

public class IndigoValenceCheckerSettings extends IndigoNodeSettings
{
   public final SettingsModelColumnName colName = new SettingsModelColumnName("colName", null);

   public IndigoValenceCheckerSettings() {
      addSettingsParameter(colName);
   }
}
