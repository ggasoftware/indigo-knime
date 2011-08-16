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

package com.ggasoftware.indigo.knime.cell;

import javax.swing.Icon;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.renderer.DataValueRendererFamily;
import org.knime.core.data.renderer.DefaultDataValueRendererFamily;
import org.knime.core.data.renderer.MultiLineStringValueRenderer;

public interface IndigoQueryReactionValue extends IndigoDataValue
{
   public static final UtilityFactory UTILITY = new IndigoQueryReactionUtilityFactory();

   /** Implementations of the meta information of this value class. */
   public static class IndigoQueryReactionUtilityFactory extends UtilityFactory
   {
      /** Singleton icon to be used to display this cell type. */
      private static final Icon ICON = loadIcon(
            com.ggasoftware.indigo.knime.cell.IndigoMolValue.class, "../indigo.png");

      /** Only subclasses are allowed to instantiate this class. */
      protected IndigoQueryReactionUtilityFactory()
      {
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Icon getIcon ()
      {
         return ICON;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      protected DataValueRendererFamily getRendererFamily (
            final DataColumnSpec spec)
      {
         return new DefaultDataValueRendererFamily(
               new IndigoDataValueRenderer(),
               new MultiLineStringValueRenderer("SMILES string"));
      }
   }
}
