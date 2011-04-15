package com.ggasoftware.indigo.knime;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class IndigoMoleculeFingerprinterNodeFactory extends
      NodeFactory<IndigoMoleculeFingerprinterNodeModel>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public IndigoMoleculeFingerprinterNodeModel createNodeModel ()
   {
      return new IndigoMoleculeFingerprinterNodeModel();
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
   public NodeView<IndigoMoleculeFingerprinterNodeModel> createNodeView (
         final int viewIndex,
         final IndigoMoleculeFingerprinterNodeModel nodeModel)
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
      return new IndigoMoleculeFingerprinterNodeDialog();
   }
}
