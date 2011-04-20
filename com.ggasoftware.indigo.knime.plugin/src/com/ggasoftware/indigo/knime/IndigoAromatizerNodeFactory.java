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

package com.ggasoftware.indigo.knime;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

import com.ggasoftware.indigo.IndigoObject;

public class IndigoAromatizerNodeFactory extends
      NodeFactory<IndigoSimpleNodeModel>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public IndigoSimpleNodeModel createNodeModel ()
   {
      return new IndigoSimpleNodeModel("aromatize molecule",
            new IndigoSimpleNodeModel.Transformer()
            {
               @Override
               void transform (IndigoObject io)
               {
                  io.aromatize();
               }
            });
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getNrNodeViews ()
   {
      return 0;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NodeView<IndigoSimpleNodeModel> createNodeView (final int viewIndex,
         final IndigoSimpleNodeModel nodeModel)
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean hasDialog ()
   {
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NodeDialogPane createNodeDialogPane ()
   {
      return new IndigoSimpleNodeDialog("aromatized");
   }
}
