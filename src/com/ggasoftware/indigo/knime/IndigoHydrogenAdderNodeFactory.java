package com.ggasoftware.indigo.knime;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "IndigoHydrogenAdder" Node.
 * 
 * 
 * @author GGA Software Services LLC
 */
public class IndigoHydrogenAdderNodeFactory extends
      NodeFactory<IndigoHydrogenAdderNodeModel>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IndigoHydrogenAdderNodeModel createNodeModel ()
	{
		return new IndigoHydrogenAdderNodeModel();
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
	public NodeView<IndigoHydrogenAdderNodeModel> createNodeView (
	      final int viewIndex, final IndigoHydrogenAdderNodeModel nodeModel)
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
		return new IndigoSimpleNodeDialog("H");
	}

}
