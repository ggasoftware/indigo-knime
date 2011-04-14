package com.ggasoftware.indigo.knime;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "IndigoMoleculeSaver" Node.
 * 
 * 
 * @author GGA Software Services LLC
 */
public class IndigoMoleculeSaverNodeFactory extends
      NodeFactory<IndigoMoleculeSaverNodeModel>
{

   /**
    * {@inheritDoc}
    */
   @Override
   public IndigoMoleculeSaverNodeModel createNodeModel ()
   {
      return new IndigoMoleculeSaverNodeModel();
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
      return new IndigoMoleculeSaverNodeDialog();
   }

}
