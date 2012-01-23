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

package com.ggasoftware.indigo.knime.fpsim;

import org.knime.core.node.defaultnodesettings.SettingsModelDouble;
import org.knime.core.node.defaultnodesettings.SettingsModelInteger;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.ggasoftware.indigo.knime.IndigoNodeSettings;

public class IndigoFingerprintSimilaritySettings extends IndigoNodeSettings
{
   public static final int TARGET_PORT = 0;
   public static final int QUERY_PORT = 1;
   
   enum Metric
   {
      Tanimoto, EuclidSub, Tversky
   }

   enum Aggregation
   {
      Minimum, Maximum, Average
   }
   
   public final SettingsModelString targetColumn = new SettingsModelString("targetColumn", null);
   public final SettingsModelString queryColumn = new SettingsModelString("queryColumn", null);
   public final SettingsModelString newColName = new SettingsModelString("newColName", "similarity");
   public final SettingsModelInteger metric = new SettingsModelInteger("metric", Metric.Tanimoto.ordinal());
   public final SettingsModelDouble tverskyAlpha = new SettingsModelDouble("tverskyAlpha", 0.5f);
   public final SettingsModelDouble tverskyBeta = new SettingsModelDouble("tverskyBeta", 0.5f);
   public final SettingsModelInteger aggregation = new SettingsModelInteger("aggregation", Aggregation.Average.ordinal());

   
   public IndigoFingerprintSimilaritySettings() {
      addSettingsParameter(targetColumn);
      addSettingsParameter(queryColumn);
      addSettingsParameter(newColName);
      addSettingsParameter(metric);
      addSettingsParameter(aggregation);
      addSettingsParameter(tverskyAlpha);
      addSettingsParameter(tverskyBeta);
   }


}
