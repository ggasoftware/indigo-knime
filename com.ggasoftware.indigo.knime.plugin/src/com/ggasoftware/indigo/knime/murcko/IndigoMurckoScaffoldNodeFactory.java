package com.ggasoftware.indigo.knime.murcko;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class IndigoMurckoScaffoldNodeFactory extends
      NodeFactory<IndigoMurckoScaffoldNodeModel>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public IndigoMurckoScaffoldNodeModel createNodeModel()
   {
      return new IndigoMurckoScaffoldNodeModel();
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
   public NodeView<IndigoMurckoScaffoldNodeModel> createNodeView(
         final int viewIndex, final IndigoMurckoScaffoldNodeModel nodeModel)
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
      return new IndigoMurckoScaffoldNodeDialog();
   }
}
