package com.ggasoftware.indigo.knime;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class IndigoScaffoldFinderNodeFactory extends
      NodeFactory<IndigoScaffoldFinderNodeModel>
{
   /**
    * {@inheritDoc}
    */
   @Override
   public IndigoScaffoldFinderNodeModel createNodeModel ()
   {
      return new IndigoScaffoldFinderNodeModel();
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
   public NodeView<IndigoScaffoldFinderNodeModel> createNodeView (
         final int viewIndex, final IndigoScaffoldFinderNodeModel nodeModel)
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
      return new IndigoScaffoldFinderNodeDialog();
   }
}
