package com.ggasoftware.indigo.knime;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

import com.ggasoftware.indigo.IndigoObject;

/**
 * <code>NodeFactory</code> for the "IndigoHydrogenAdder" Node.
 * 
 * 
 * @author GGA Software Services LLC
 */
public class IndigoHydrogenAdderNodeFactory extends
      NodeFactory<IndigoSimpleNodeModel>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IndigoSimpleNodeModel createNodeModel ()
	{
		return new IndigoSimpleNodeModel("unfold hydrogens", new IndigoSimpleNodeModel.Transformer()
		{
			@Override
			void transform (IndigoObject io)
			{
				io.unfoldHydrogens();
			}
		});
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
	public NodeView<IndigoSimpleNodeModel> createNodeView (
	      final int viewIndex, final IndigoSimpleNodeModel nodeModel)
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
