package com.ggasoftware.indigo.knime.areplacer;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class IndigoAtomReplacerNodeFactory extends
      NodeFactory<IndigoAtomReplacerNodeModel>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public IndigoAtomReplacerNodeModel createNodeModel()
   {
      return new IndigoAtomReplacerNodeModel();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int getNrNodeViews()
   {
      return 0;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NodeView<IndigoAtomReplacerNodeModel> createNodeView(
         final int viewIndex, final IndigoAtomReplacerNodeModel nodeModel)
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean hasDialog()
   {
      return true;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public NodeDialogPane createNodeDialogPane()
   {
      return new IndigoAtomReplacerNodeDialog();
   }
}
