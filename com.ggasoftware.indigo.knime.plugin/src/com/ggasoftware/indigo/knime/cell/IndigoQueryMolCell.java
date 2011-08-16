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
public class IndigoQueryMolCell extends IndigoDataCell implements IndigoQueryMolValue
{
   private static class Serializer implements DataCellSerializer<IndigoQueryMolCell>
   {
      /**
       * {@inheritDoc}
       */
      public void serialize (final IndigoQueryMolCell cell,
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
      public IndigoQueryMolCell deserialize (final DataCellDataInput input)
            throws IOException
      {
         char c = input.readChar();
         String query;
         boolean smarts;
         
         if (c == 'S')
            smarts = true;
         else if (c == 'Q')
            smarts = false;
         else
            throw new IOException("cannot deserialize: bad 1-st symbol");
         
         query = input.readUTF();
         return new IndigoQueryMolCell(query, smarts);
      }
   }

   private static final DataCellSerializer<IndigoQueryMolCell> SERIALIZER = new Serializer();

   public static final DataCellSerializer<IndigoQueryMolCell> getCellSerializer ()
   {
      return SERIALIZER;
   }

   private String _query;
   private boolean _smarts;
   
   public static final DataType TYPE = DataType.getType(IndigoQueryMolCell.class);

   public String getSource ()
   {
      return _query;
   }
   
   public boolean isSmarts ()
   {
      return _smarts;
   }
   
   protected IndigoQueryMolCell(IndigoObject obj) {
      super(obj);
   }
   
   /**
    * @deprecated Please use
    * {@link IndigoQueryMolCell#fromString(String)}
    * or
    * {@link IndigoQueryMolCell#fromSmarts(String)}
    * instead
    */
   public IndigoQueryMolCell (String query, boolean smarts)
   {
      super(null);
      
      _query = query;
      _smarts = smarts;
      
      Indigo indigo = IndigoPlugin.getIndigo();
      try
      {
         IndigoPlugin.lock();
         if (smarts)
            _object = indigo.loadSmarts(query);
         else
         {
            _object = indigo.loadQueryMolecule(query);
            _object.aromatize();
         }
      }
      finally
      {
         IndigoPlugin.unlock();
      }
   }
   
   public static IndigoQueryMolCell fromString(String str) {
      Indigo indigo = IndigoPlugin.getIndigo();
      try {
         IndigoPlugin.lock();
         IndigoObject io = indigo.loadQueryMolecule(str);
         io.aromatize();
         IndigoQueryMolCell ret = new IndigoQueryMolCell(io);
         ret._query = str;
         ret._smarts = false;
         return ret;
      }
      finally {
         IndigoPlugin.unlock();
      }
   }
   
   public static IndigoQueryMolCell fromSmarts(String smarts) {
      Indigo indigo = IndigoPlugin.getIndigo();
      try {
         IndigoPlugin.lock();
         IndigoQueryMolCell ret = new IndigoQueryMolCell(indigo.loadSmarts(smarts));
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

}
