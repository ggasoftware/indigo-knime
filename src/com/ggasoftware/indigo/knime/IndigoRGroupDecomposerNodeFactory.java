package com.ggasoftware.indigo.knime;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "IndigoRGroupDecomposer" Node.
 * 
 * 
 * @author
 */
public class IndigoRGroupDecomposerNodeFactory extends
      NodeFactory<IndigoRGroupDecomposerNodeModel>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public IndigoRGroupDecomposerNodeModel createNodeModel ()
   {
      return new IndigoRGroupDecomposerNodeModel();
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
   public NodeView<IndigoRGroupDecomposerNodeModel> createNodeView (
         final int viewIndex, final IndigoRGroupDecomposerNodeModel nodeModel)
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
      return new IndigoRGroupDecomposerNodeDialog();
   }

}
