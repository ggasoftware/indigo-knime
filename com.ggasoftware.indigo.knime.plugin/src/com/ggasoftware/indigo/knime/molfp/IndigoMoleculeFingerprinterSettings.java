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

package com.ggasoftware.indigo.knime.molfp;

import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.ggasoftware.indigo.knime.common.IndigoNodeSettings;

public class IndigoMoleculeFingerprinterSettings extends IndigoNodeSettings
{
   public static final int INPUT_PORT = 0;
   
   public static final int FP_DEFAULT = 8;
   public static final int FP_MIN = 1;
   public static final int FP_MAX = 1000000;
   
   public final SettingsModelString colName = new SettingsModelString("colName", null);
   public final SettingsModelString newColName = new SettingsModelString("newColName", null);
   public final SettingsModelIntegerBounded fpSizeQWords = new SettingsModelIntegerBounded("fpSizeQWords", FP_DEFAULT, FP_MIN, FP_MAX);
   
   /*
    * Parameter is not saved
    */
   public STRUCTURE_TYPE structureType;
   
   public IndigoMoleculeFingerprinterSettings() {
      addSettingsParameter(colName);
      addSettingsParameter(newColName);
      addSettingsParameter(fpSizeQWords);
   }
}
