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

import java.io.IOException;

import org.knime.core.data.*;

import com.ggasoftware.indigo.Indigo;
import com.ggasoftware.indigo.IndigoObject;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

@SuppressWarnings("serial")
public class IndigoQueryReactionCell extends IndigoDataCell implements IndigoQueryReactionValue
{
   private static class Serializer implements DataCellSerializer<IndigoQueryReactionCell>
   {
      /**
       * {@inheritDoc}
       */
      public void serialize (final IndigoQueryReactionCell cell,
            final DataCellDataOutput out) throws IOException
      {
         if (cell.isSmarts())
            out.writeChar('S');
         else
            out.writeChar('Q');
         
         out.writeUTF(cell.getSource());
      }

      /**
       * {@inheritDoc}
       */
      public IndigoQueryReactionCell deserialize (final DataCellDataInput input)
            throws IOException
      {
         switch (input.readChar()) {
         case 'S':
            return IndigoQueryReactionCell.fromSmarts(input.readUTF());
         case 'Q':
            return IndigoQueryReactionCell.fromString(input.readUTF());
         default:
            throw new IOException("cannot deserialize: bad 1-st symbol");
         }
      }
   }

   private static final DataCellSerializer<IndigoQueryReactionCell> SERIALIZER = new Serializer();

   public static final DataCellSerializer<IndigoQueryReactionCell> getCellSerializer ()
   {
      return SERIALIZER;
   }

   private String _query;
   private boolean _smarts;
   
   public static final DataType TYPE = DataType.getType(IndigoQueryReactionCell.class);

   public String getSource ()
   {
      return _query;
   }
   
   public boolean isSmarts ()
   {
      return _smarts;
   }
   /**
    * @deprecated Please use
    * {@link IndigoQueryReactionCell#fromString(String)}
    * or
    * {@link IndigoQueryReactionCell#fromSmarts(String)}
    * instead
    */
   public IndigoQueryReactionCell(IndigoObject obj) {
      super(obj);
   }
   
   private IndigoQueryReactionCell(IndigoObject obj, String query, boolean smarts) {
      super(obj);
      _query = query;
      _smarts = smarts;
   }
   
   public static IndigoQueryReactionCell fromString(String str) {
      Indigo indigo = IndigoPlugin.getIndigo();
      try {
         IndigoPlugin.lock();
         IndigoObject io = indigo.loadQueryReaction(str);
         io.aromatize();
         IndigoQueryReactionCell ret = new IndigoQueryReactionCell(io);
         ret._query = str;
         ret._smarts = false;
         return ret;
      }
      finally {
         IndigoPlugin.unlock();
      }
   }
   
   public static IndigoQueryReactionCell fromSmarts(String smarts) {
      Indigo indigo = IndigoPlugin.getIndigo();
      try {
         IndigoPlugin.lock();
         IndigoQueryReactionCell ret = new IndigoQueryReactionCell(indigo.loadSmarts(smarts));
         ret._query = smarts;
         ret._smarts = true;
         return ret;
      }
      finally {
         IndigoPlugin.unlock();
      }
   }

   @Override
   public String toString ()
   {
      try
      {
         IndigoPlugin.lock();
         
         // Return the name if it is present
         if (_object.name() != null && _object.name().length() > 0)
            return _object.name();
         
         // Otherwise, return the unique Indigo's object ID
         return "<Indigo object #" + _object.self + ">";
      }
      finally
      {
         IndigoPlugin.unlock();
      }
   }

   @Override
   protected boolean equalsDataCell (DataCell dc)
   {
      return false;
   }

   @Override
   public int hashCode ()
   {
      return 0;
   }

   public IndigoQueryReactionCell clone() {
      try {
         IndigoPlugin.lock();
         IndigoQueryReactionCell ret = new IndigoQueryReactionCell(_object.clone(), _query, _smarts);
         return ret;
      }
      finally {
         IndigoPlugin.unlock();
      }
   }

}
