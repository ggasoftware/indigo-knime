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

package com.ggasoftware.indigo.knime.bondreplacer;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

import com.ggasoftware.indigo.IndigoObject;
import com.ggasoftware.indigo.knime.bondreplacer.IndigoBondReplacerNodeSettings.BondOrder;
import com.ggasoftware.indigo.knime.common.IndigoSimpleNodeDialog;
import com.ggasoftware.indigo.knime.common.IndigoSimpleNodeModel;

public class IndigoBondReplacerNodeFactory extends
      NodeFactory<IndigoSimpleNodeModel>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public IndigoSimpleNodeModel createNodeModel ()
   {
      final IndigoBondReplacerNodeSettings settings = new IndigoBondReplacerNodeSettings();
      return new IndigoSimpleNodeModel("replace bonds", settings,
            new IndigoSimpleNodeModel.Transformer()
            {
               @Override
               public void transform (IndigoObject io, boolean reaction)
               {
                  String bondOrderString = settings.bondOrder.getStringValue();
                  int newOrder;
                  if (bondOrderString.equals(BondOrder.Single.toString()))
                     newOrder = 1;
                  else if (bondOrderString.equals(BondOrder.Double.toString()))
                     newOrder = 2;
                  else if (bondOrderString.equals(BondOrder.Triple.toString()))
                     newOrder = 3;
                  else if (bondOrderString.equals(BondOrder.Aromatic.toString()))
                     newOrder = 4;
                  else
                     throw new RuntimeException("Unknown bond order type: " + bondOrderString);
                     
                  for (IndigoObject bond : io.iterateBonds()) {
                     if (settings.replaceHighlighted.getBooleanValue() && !bond.isHighlighted())
                        continue;
                     bond.setBondOrder(newOrder);
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
      final IndigoBondReplacerNodeSettings settings = new IndigoBondReplacerNodeSettings();
      return new IndigoSimpleNodeDialog("bonds replaced", settings, false) {
         private final JComboBox _bondOrder = new JComboBox(new Object[] {
               BondOrder.Single, BondOrder.Double, BondOrder.Triple,
               BondOrder.Aromatic });
         private final JCheckBox _replaceHighlighted = new JCheckBox(
               "Replace only highlighted bonds");

         {
            settings.registerDialogComponent(_bondOrder, settings.bondOrder);
            settings.registerDialogComponent(_replaceHighlighted, settings.replaceHighlighted);
            
            // Initialize
            _dialogPanel.addItemsPanel("Replacer settings");
            _dialogPanel.addItem("Bond order", _bondOrder);
            _dialogPanel.addItem(_replaceHighlighted);
            
            addTabDialog();
         }
      };
   }

}
