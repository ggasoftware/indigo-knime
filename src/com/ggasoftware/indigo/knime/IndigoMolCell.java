package com.ggasoftware.indigo.knime;

import com.ggasoftware.indigo.*;
import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.node.NodeLogger;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("serial")
public class IndigoMolCell extends DataCell implements IndigoMolValue
{
   private static final NodeLogger LOGGER = NodeLogger
         .getLogger(IndigoMolCell.class);

   private static class Serializer implements
         DataCellSerializer<IndigoMolCell>
   {
      /**
       * {@inheritDoc}
       */
      public void serialize (final IndigoMolCell cell,
            final DataCellDataOutput out) throws IOException
      {
         try
         {
            BASE64Encoder encoder = new BASE64Encoder();
            String str = encoder.encodeBuffer(cell.getIndigoObject()
                  .serialize());
            out.writeUTF(str);
         }
         catch (IndigoException ex)
         {
            LOGGER.error("Error while serializing Indigo object", ex);
            throw new IOException(ex.getMessage());
         }
      }

      /**
       * {@inheritDoc}
       */
      public IndigoMolCell deserialize (final DataCellDataInput input)
            throws IOException
      {
         String str = input.readUTF();

         try
         {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] buf = decoder.decodeBuffer(str);
            try
            {
               IndigoPlugin.lock();
               return new IndigoMolCell(IndigoPlugin.getIndigo().unserialize(
                     buf));
            }
            finally
            {
               IndigoPlugin.unlock();
            }
         }
         catch (IndigoException ex)
         {
            LOGGER.error("Error while deserializing Indigo object", ex);
            throw new IOException(ex.getMessage());
         }
      }
   }

   private static final DataCellSerializer<IndigoMolCell> SERIALIZER = new Serializer();

   public static final DataCellSerializer<IndigoMolCell> getCellSerializer ()
   {
      return SERIALIZER;
   }

   private IndigoObject _object;
   public static final DataType TYPE = DataType.getType(IndigoMolCell.class);

   public IndigoMolCell (IndigoObject obj)
   {
      _object = obj;
   }

   @Override
   public String toString ()
   {
      return null;
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

   @Override
   public IndigoObject getIndigoObject ()
   {
      return _object;
   }
}
