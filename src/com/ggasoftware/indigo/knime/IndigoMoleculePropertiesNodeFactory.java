package com.ggasoftware.indigo.knime;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "IndigoBasicProperties" Node.
 * 
 * 
 * @author GGA Software Services LLC
 */
public class IndigoMoleculePropertiesNodeFactory extends
      NodeFactory<IndigoMoleculePropertiesNodeModel>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IndigoMoleculePropertiesNodeModel createNodeModel ()
	{
		return new IndigoMoleculePropertiesNodeModel();
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
	public NodeView<IndigoMoleculePropertiesNodeModel> createNodeView (
	      final int viewIndex, final IndigoMoleculePropertiesNodeModel nodeModel)
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
		return new IndigoMoleculePropertiesNodeDialog();
	}

}
