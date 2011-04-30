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

package com.ggasoftware.indigo.knime.hremover;

import java.util.ArrayList;
import java.util.List;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

import com.ggasoftware.indigo.IndigoObject;
import com.ggasoftware.indigo.knime.common.IndigoSimpleNodeDialog;
import com.ggasoftware.indigo.knime.common.IndigoSimpleNodeModel;

public class IndigoHydrogenRemoverNodeFactory extends
      NodeFactory<IndigoSimpleNodeModel>
{

   static int[] toIntArray (List<Integer> list)
   {
      int[] arr = new int[list.size()];
      for (int i = 0; i < list.size(); i++)
         arr[i] = list.get(i).intValue();
      return arr;
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public IndigoSimpleNodeModel createNodeModel ()
   {
      return new IndigoSimpleNodeModel("fold hydrogens",
            new IndigoSimpleNodeModel.Transformer()
            {
               @Override
               public void transform (IndigoObject io)
               {
                  ArrayList<Integer> indices = new ArrayList<Integer>();

                  for (IndigoObject atom : io.iterateAtoms())
                  {
                     if (atom.isPseudoatom() || atom.isRSite())
                        continue;
                     if (atom.atomicNumber() == 1 && atom.isotope() == 0)
                     {
                        boolean has_stereo = false;
                        for (IndigoObject nei : atom.iterateNeighbors())
                        {
                           if (nei.bond().bondStereo() != 0)
                              has_stereo = true;
                        }
                        if (!has_stereo)
                           indices.add(atom.index());
                     }
                  }

                  if (indices.size() > 0)
                  {
                     io.removeAtoms(toIntArray(indices));
                  }
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
      return new IndigoSimpleNodeDialog("-H");
   }
}
