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
public class IndigoMoleculeSimilarityNodeFactory extends
      NodeFactory<IndigoMoleculeSimilarityNodeModel>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IndigoMoleculeSimilarityNodeModel createNodeModel()
	{
		return new IndigoMoleculeSimilarityNodeModel();
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
	public NodeView<IndigoMoleculeSimilarityNodeModel> createNodeView(
	      final int viewIndex, final IndigoMoleculeSimilarityNodeModel nodeModel)
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
		return new IndigoMoleculeSimilarityNodeDialog();
	}

}
