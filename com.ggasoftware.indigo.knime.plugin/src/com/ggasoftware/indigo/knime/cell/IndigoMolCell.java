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

import org.knime.core.data.*;
import org.knime.core.node.NodeLogger;

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
            IndigoPlugin.lock();
            IndigoObject io = cell.getIndigoObject();
            try
            {
               if (!io.hasCoord())
                  io.layout();
               io.markEitherCisTrans();
            }
            catch (IndigoException e)
            {
            }
            
            out.writeUTF(io.molfile());
         }
         catch (IndigoException ex)
         {
            LOGGER.error("Error while serializing Indigo object", ex);
            throw new IOException(ex.getMessage());
         }
         finally
         {
            IndigoPlugin.unlock();
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
            IndigoPlugin.lock();
            return new IndigoMolCell(IndigoPlugin.getIndigo().loadMolecule(str));
         }
         catch (IndigoException ex)
         {
            LOGGER.error("Error while deserializing Indigo object", ex);
            throw new IOException(ex.getMessage());
         }
         finally
         {
            IndigoPlugin.unlock();
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
      try
      {
         IndigoPlugin.lock();
         
         // Return the name if it is present
         if (_object.name() != null && _object.name().length() > 0)
            return _object.name();
         
         // Otherwise, return a SMILES string if it can be calculated
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
      finally
      {
         IndigoPlugin.unlock();
      }
   }

   @Override
   protected boolean equalsDataCell (DataCell dc)
   {
      IndigoMolCell other = (IndigoMolCell)dc;
      
      try
      {
         IndigoPlugin.lock();
         
         IndigoObject match = IndigoPlugin.getIndigo().exactMatch(_object, other._object);
         
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
   public IndigoObject getIndigoObject ()
   {
      return _object;
   }
}
