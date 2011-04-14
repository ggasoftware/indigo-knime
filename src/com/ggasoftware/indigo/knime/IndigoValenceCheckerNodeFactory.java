package com.ggasoftware.indigo.knime;

import org.knime.core.node.*;

/**
 * <code>NodeFactory</code> for the "IndigoValenceChecker" Node.
 * 
 * 
 * @author GGA Software Services LLC
 */
public class IndigoValenceCheckerNodeFactory extends
      NodeFactory<IndigoValenceCheckerNodeModel>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public IndigoValenceCheckerNodeModel createNodeModel ()
   {
      return new IndigoValenceCheckerNodeModel();
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
   public NodeView<IndigoValenceCheckerNodeModel> createNodeView (
         final int viewIndex, final IndigoValenceCheckerNodeModel nodeModel)
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
      return new IndigoValenceCheckerNodeDialog();
   }

}
