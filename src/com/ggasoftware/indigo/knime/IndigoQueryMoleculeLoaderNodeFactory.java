package com.ggasoftware.indigo.knime;

import org.knime.core.node.*;

public class IndigoQueryMoleculeLoaderNodeFactory extends
      NodeFactory<IndigoMoleculeLoaderNodeModel>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public IndigoMoleculeLoaderNodeModel createNodeModel ()
   {
      return new IndigoMoleculeLoaderNodeModel(true);
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
   public NodeView<IndigoMoleculeLoaderNodeModel> createNodeView (
         final int viewIndex, final IndigoMoleculeLoaderNodeModel nodeModel)
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
      return new IndigoMoleculeLoaderNodeDialog(true);
   }
}
