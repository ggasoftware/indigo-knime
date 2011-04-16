package com.ggasoftware.indigo.knime;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;


public class IndigoQueryMoleculeSaverNodeFactory extends
      NodeFactory<IndigoMoleculeSaverNodeModel>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public IndigoMoleculeSaverNodeModel createNodeModel ()
   {
      return new IndigoMoleculeSaverNodeModel(true);
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
   public NodeView<IndigoMoleculeSaverNodeModel> createNodeView (
         final int viewIndex, final IndigoMoleculeSaverNodeModel nodeModel)
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
      return new IndigoMoleculeSaverNodeDialog(true);
   }

}
