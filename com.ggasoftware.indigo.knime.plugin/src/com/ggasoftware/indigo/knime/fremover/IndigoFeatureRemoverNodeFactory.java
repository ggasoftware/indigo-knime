package com.ggasoftware.indigo.knime.fremover;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;


public class IndigoFeatureRemoverNodeFactory extends
      NodeFactory<IndigoFeatureRemoverNodeModel>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public IndigoFeatureRemoverNodeModel createNodeModel()
   {
      return new IndigoFeatureRemoverNodeModel();
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
   public NodeView<IndigoFeatureRemoverNodeModel> createNodeView(
         final int viewIndex, final IndigoFeatureRemoverNodeModel nodeModel)
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
      return new IndigoFeatureRemoverNodeDialog();
   }

}
