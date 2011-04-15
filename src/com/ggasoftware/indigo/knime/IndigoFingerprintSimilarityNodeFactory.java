package com.ggasoftware.indigo.knime;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "IndigoMoleculeSimilarity" Node.
 * 
 * 
 * @author GGA Software Services LLC
 */
public class IndigoFingerprintSimilarityNodeFactory extends
      NodeFactory<IndigoFingerprintSimilarityNodeModel>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public IndigoFingerprintSimilarityNodeModel createNodeModel ()
   {
      return new IndigoFingerprintSimilarityNodeModel();
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
   public NodeView<IndigoFingerprintSimilarityNodeModel> createNodeView (
         final int viewIndex, final IndigoFingerprintSimilarityNodeModel nodeModel)
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
      return new IndigoFingerprintSimilarityNodeDialog();
   }

}
