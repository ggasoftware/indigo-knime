package com.ggasoftware.indigo.knime;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "IndigoAromatizer" Node.
 * 
 * 
 * @author GGA Software Services LLC
 */
public class IndigoAromatizerNodeFactory extends
      NodeFactory<IndigoAromatizerNodeModel>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IndigoAromatizerNodeModel createNodeModel ()
	{
		return new IndigoAromatizerNodeModel();
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
	public NodeView<IndigoAromatizerNodeModel> createNodeView (
	      final int viewIndex, final IndigoAromatizerNodeModel nodeModel)
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
		return new IndigoAromatizerNodeDialog();
	}

}
