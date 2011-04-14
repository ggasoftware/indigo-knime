package com.ggasoftware.indigo.knime;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "IndigoSubstructureMatcher" Node.
 * 
 * 
 * @author GGA Software Services LLC
 */
public class IndigoSubstructureMatcherNodeFactory extends
      NodeFactory<IndigoSubstructureMatcherNodeModel>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public IndigoSubstructureMatcherNodeModel createNodeModel ()
   {
      return new IndigoSubstructureMatcherNodeModel();
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
   public NodeView<IndigoSubstructureMatcherNodeModel> createNodeView (
         final int viewIndex, final IndigoSubstructureMatcherNodeModel nodeModel)
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
      return new IndigoSubstructureMatcherNodeDialog();
   }
}
