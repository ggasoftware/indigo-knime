package com.ggasoftware.indigo.knime;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "IndigoSmartsMatcher" Node.
 * 
 *
 * @author GGA Software Services LLC
 */
public class IndigoSmartsMatcherNodeFactory 
        extends NodeFactory<IndigoSmartsMatcherNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public IndigoSmartsMatcherNodeModel createNodeModel() {
        return new IndigoSmartsMatcherNodeModel();
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
    public NodeView<IndigoSmartsMatcherNodeModel> createNodeView(final int viewIndex,
            final IndigoSmartsMatcherNodeModel nodeModel) {
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
        return new IndigoSmartsMatcherNodeDialog();
    }

}

