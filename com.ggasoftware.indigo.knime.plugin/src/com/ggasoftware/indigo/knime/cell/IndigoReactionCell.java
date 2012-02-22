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
import java.nio.ByteBuffer;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataCellDataInput;
import org.knime.core.data.DataCellDataOutput;
import org.knime.core.data.DataCellSerializer;
import org.knime.core.data.DataType;

import com.ggasoftware.indigo.IndigoException;
import com.ggasoftware.indigo.IndigoObject;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

@SuppressWarnings("serial")
public class IndigoReactionCell extends IndigoDataCell implements IndigoReactionValue
{
   private static class Serializer implements
 DataCellSerializer<IndigoReactionCell> {
      /**
       * {@inheritDoc}
       */
      public void serialize(final IndigoReactionCell cell, final DataCellDataOutput out) throws IOException {
         byte[] buf = cell._getBuffer();
         out.writeInt(buf.length);
         out.write(buf);
      }

      /**
       * {@inheritDoc}
       */
      public IndigoReactionCell deserialize(final DataCellDataInput input) throws IOException {
         int buf_len = input.readInt();
         byte[] buf = new byte[buf_len];
         input.readFully(buf);
         return new IndigoReactionCell(buf);
      }
   }

   private static final DataCellSerializer<IndigoReactionCell> SERIALIZER = new Serializer();

   public static final DataCellSerializer<IndigoReactionCell> getCellSerializer ()
   {
      return SERIALIZER;
   }

   public static final DataType TYPE = DataType.getType(IndigoReactionCell.class);

   public IndigoReactionCell (IndigoObject obj)
   {
      super();

      // Try to serialize to check unexpected configurations: extraordinary charge or etc.
      try
      {
         IndigoPlugin.lock();
         _byteBuffer = ByteBuffer.wrap(obj.serialize());
      }
      finally
      {
         IndigoPlugin.unlock();
      }

   }

   public IndigoReactionCell(byte[] buf) {
      super(buf);
   }

   @Override
   public String toString ()
   {
      try
      {
         IndigoPlugin.lock();
         IndigoObject _object = getIndigoObject();
         
         // Return a SMILES string if it can be calculated
         try
         {
            return _object.smiles();
         }
         catch (IndigoException e)
         {
            // If SMILES is not an option, return the unique Indigo's object ID
            return "<Indigo object #" + _object.self + ">";
         }
      }
      catch (IndigoException e)
      {
         return null;
      } finally {
         IndigoPlugin.unlock();
      }
   }

   @Override
   protected boolean equalsDataCell (DataCell dc)
   {
      IndigoDataCell other = (IndigoDataCell)dc;
      
      try
      {
         IndigoPlugin.lock();
         IndigoObject self_obj = getIndigoObject();
         IndigoObject other_obj = other.getIndigoObject();
         
         IndigoObject match = IndigoPlugin.getIndigo().exactMatch(self_obj, other_obj);
         
         if (match != null)
            return true;
      }
      catch (IndigoException e)
      {
         // ignore the exception; default to the false result
      }
      finally
      {
         IndigoPlugin.unlock();
      }
      
      return false;
   }

   @Override
   public int hashCode ()
   {
      return 0;
   }

   @Override
   public IndigoObject getIndigoObject() throws IndigoException{
      byte[] buf = _getBuffer();
      IndigoObject res;
      try {
         IndigoPlugin.lock();
         res = IndigoPlugin.getIndigo().unserialize(buf);
      } finally {
         IndigoPlugin.unlock();
      }
      return res;
   }
   

}
//@SuppressWarnings("serial")
//public class IndigoReactionCell extends IndigoDataCell implements IndigoReactionValue {
//   
//   private static final NodeLogger LOGGER = NodeLogger.getLogger(IndigoReactionCell.class);
//   
//   private static class Serializer implements DataCellSerializer<IndigoReactionCell> {
//      /**
//       * {@inheritDoc}
//       */
//      public void serialize (final IndigoReactionCell cell, final DataCellDataOutput out) throws IOException {
//         try {
//            IndigoPlugin.lock();
//            BASE64Encoder encoder = new BASE64Encoder();
//            String str = encoder.encodeBuffer(cell.getIndigoObject().serialize());
//            out.writeUTF(str);            
//         }
//         catch (IndigoException ex) {
//            LOGGER.error("Error while serializing Indigo object", ex);
//            throw new IOException(ex.getMessage());
//         }
//         finally {
//            IndigoPlugin.unlock();
//         }
//      }
//      
//      /**
//       * {@inheritDoc}
//       */
//      public IndigoReactionCell deserialize (final DataCellDataInput input) throws IOException {
//         String str = input.readUTF();
//         
//         BASE64Decoder decoder = new BASE64Decoder();
//         byte[] buf = decoder.decodeBuffer(str);
//         try {
//            IndigoPlugin.lock();
//            return new IndigoReactionCell(IndigoPlugin.getIndigo().unserialize(buf));
//         }
//         catch (IndigoException ex) {
//            LOGGER.error("Error while unserializing Indigo object", ex);
//            throw new IOException(ex.getMessage());
//         }
//         finally {
//            IndigoPlugin.unlock();
//         }
//      }
//   }
//   
//   private static final DataCellSerializer<IndigoReactionCell> SERIALIZER = new Serializer();
//   
//   public static final DataCellSerializer<IndigoReactionCell> getCellSerializer() {
//      return SERIALIZER;
//   }
//   
//   public static final DataType TYPE = DataType.getType(IndigoReactionCell.class);
//   
//   public IndigoReactionCell(IndigoObject obj)
//   {
//      super(obj);
//      
//      // Try to serialize to check unexpected configurations: extraordinary charge or etc.
//      try
//      {
//         IndigoPlugin.lock();
//         obj.serialize();
//      }
//      finally
//      {
//         IndigoPlugin.unlock();
//      }
//   }
//   
//   @Override
//   public String toString() {
//      try {
//         IndigoPlugin.lock();
//         
//         // Return the name if it is present
//         if (_object.name() != null && _object.name().length() > 0) return _object.name();
//         
//         // Otherwise, return a SMILES string if it can be calculated
//         try {
//            return _object.smiles();
//         }
//         catch (IndigoException e) {
//            // If SMILES is not an option, return the unique Indigo's object ID
//            return "<Indigo object #" + _object.self + ">";
//         }
//      }
//      finally {
//         IndigoPlugin.unlock();
//      }
//   }
//   
//   @Override
//   protected boolean equalsDataCell(DataCell dc) {
//      try {
//         IndigoPlugin.lock();
//         
//         return IndigoPlugin.getIndigo().exactMatch(_object, ((IndigoDataCell)dc)._object) != null;
//      }
//      catch (IndigoException e) {
//         // ignore the exception; default to the false result
//      }
//      finally {
//         IndigoPlugin.unlock();
//      }
//      
//      return false;
//   }
//   
//   @Override
//   public int hashCode() {
//      return 0;
//   }
//   
//}
