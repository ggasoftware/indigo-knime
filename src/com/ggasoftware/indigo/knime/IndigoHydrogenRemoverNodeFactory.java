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
public class IndigoHydrogenRemoverNodeFactory extends
      NodeFactory<IndigoHydrogenRemoverNodeModel>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IndigoHydrogenRemoverNodeModel createNodeModel ()
	{
		return new IndigoHydrogenRemoverNodeModel();
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
	public NodeView<IndigoHydrogenRemoverNodeModel> createNodeView (
	      final int viewIndex, final IndigoHydrogenRemoverNodeModel nodeModel)
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
		return new IndigoSimpleNodeDialog("-H");
	}
}
