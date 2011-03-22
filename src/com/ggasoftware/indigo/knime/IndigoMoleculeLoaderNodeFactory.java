package com.ggasoftware.indigo.knime;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "IndigoMoleculeLoader" Node.
 * 
 *
 * @author 
 */
public class IndigoMoleculeLoaderNodeFactory 
        extends NodeFactory<IndigoMoleculeLoaderNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public IndigoMoleculeLoaderNodeModel createNodeModel() {
        return new IndigoMoleculeLoaderNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<IndigoMoleculeLoaderNodeModel> createNodeView(final int viewIndex,
            final IndigoMoleculeLoaderNodeModel nodeModel) {
    	return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
        return new IndigoMoleculeLoaderNodeDialog();
    }

}

