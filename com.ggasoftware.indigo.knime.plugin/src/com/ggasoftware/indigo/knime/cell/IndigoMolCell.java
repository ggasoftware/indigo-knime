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

import com.ggasoftware.indigo.*;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.knime.core.data.*;

@SuppressWarnings("serial")
public class IndigoMolCell extends IndigoDataCell implements IndigoMolValue
{

   private static class Serializer implements
 DataCellSerializer<IndigoMolCell> {
      /**
       * {@inheritDoc}
       */
      public void serialize(final IndigoMolCell cell, final DataCellDataOutput out) throws IOException {
         byte[] buf = cell._getBuffer();
         out.writeInt(buf.length);
         out.write(buf);
      }

      /**
       * {@inheritDoc}
       */
      public IndigoMolCell deserialize(final DataCellDataInput input) throws IOException {
         int buf_len = input.readInt();
         byte[] buf = new byte[buf_len];
         input.readFully(buf);
         return new IndigoMolCell(buf);
      }
   }

   private static final DataCellSerializer<IndigoMolCell> SERIALIZER = new Serializer();

   public static final DataCellSerializer<IndigoMolCell> getCellSerializer ()
   {
      return SERIALIZER;
   }

   public static final DataType TYPE = DataType.getType(IndigoMolCell.class);

   public IndigoMolCell (IndigoObject obj)
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

   public IndigoMolCell(byte[] buf) {
      super(buf);
   }

   @Override
   public String toString ()
   {
      try
      {
         IndigoPlugin.lock();
         IndigoObject obj = getIndigoObject();
         
         // Return a SMILES string if it can be calculated
         try
         {
            return obj.smiles();
         }
         catch (IndigoException e)
         {
            // If SMILES is not an option, return the unique Indigo's object ID
            return "<Indigo object #" + obj.self + ">";
         }
      }
      catch (IndigoException e)
      {
         return null;
      }
      finally
      {
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
//public class IndigoMolCell extends IndigoDataCell implements IndigoMolValue
//{
//   private static final NodeLogger LOGGER = NodeLogger
//         .getLogger(IndigoMolCell.class);
//
//   private static class Serializer implements
//         DataCellSerializer<IndigoMolCell>
//   {
//      /**
//       * {@inheritDoc}
//       */
//      public void serialize (final IndigoMolCell cell,
//            final DataCellDataOutput out) throws IOException
//      {
//         try
//         {
//            IndigoPlugin.lock();
//            BASE64Encoder encoder = new BASE64Encoder();
//            String str = encoder.encodeBuffer(cell.getIndigoObject().serialize());
//            out.writeUTF(str);            
//         }
//         catch (IndigoException ex)
//         {
//            LOGGER.error("Error while serializing Indigo object", ex);
//            throw new IOException(ex.getMessage());
//         }
//         finally
//         {
//            IndigoPlugin.unlock();
//         }
//      }
//
//      /**
//       * {@inheritDoc}
//       */
//      public IndigoMolCell deserialize (final DataCellDataInput input)
//            throws IOException
//      {
//         String str = input.readUTF();
//
//         BASE64Decoder decoder = new BASE64Decoder();
//         byte[] buf = decoder.decodeBuffer(str);
//         try
//         {
//            IndigoPlugin.lock();
//            return new IndigoMolCell(IndigoPlugin.getIndigo().unserialize(buf));
//         }
//         catch (IndigoException ex)
//         {
//            LOGGER.error("Error while unserializing Indigo object: " + ex.getMessage(), ex);
//            throw new IOException(ex.getMessage());
//         }
//         finally
//         {
//            IndigoPlugin.unlock();
//         }
//      }
//   }
//
//   private static final DataCellSerializer<IndigoMolCell> SERIALIZER = new Serializer();
//
//   public static final DataCellSerializer<IndigoMolCell> getCellSerializer ()
//   {
//      return SERIALIZER;
//   }
//
//   public static final DataType TYPE = DataType.getType(IndigoMolCell.class);
//
//   public IndigoMolCell (IndigoObject obj)
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
//   public String toString ()
//   {
//      try
//      {
//         IndigoPlugin.lock();
//         
//         // Return a SMILES string if it can be calculated
//         try
//         {
//            return _object.smiles();
//         }
//         catch (IndigoException e)
//         {
//            // If SMILES is not an option, return the unique Indigo's object ID
//            return "<Indigo object #" + _object.self + ">";
//         }
//      }
//      finally
//      {
//         IndigoPlugin.unlock();
//      }
//   }
//
//   @Override
//   protected boolean equalsDataCell (DataCell dc)
//   {
//      IndigoMolCell other = (IndigoMolCell)dc;
//      
//      try
//      {
//         IndigoPlugin.lock();
//         
//         IndigoObject match = IndigoPlugin.getIndigo().exactMatch(_object, other._object);
//         
//         if (match != null)
//            return true;
//      }
//      catch (IndigoException e)
//      {
//         // ignore the exception; default to the false result
//      }
//      finally
//      {
//         IndigoPlugin.unlock();
//      }
//      
//      return false;
//   }
//
//   @Override
//   public int hashCode ()
//   {
//      return 0;
//   }
//
//}