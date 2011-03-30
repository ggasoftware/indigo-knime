package com.ggasoftware.indigo.knime;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "SmartsMatchCounter" Node.
 * 
 *
 * @author GGA Software Services LLC
 */
public class IndigoSmartsMatchCounterNodeFactory 
        extends NodeFactory<IndigoSmartsMatchCounterNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public IndigoSmartsMatchCounterNodeModel createNodeModel() {
        return new IndigoSmartsMatchCounterNodeModel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<IndigoSmartsMatchCounterNodeModel> createNodeView(final int viewIndex,
            final IndigoSmartsMatchCounterNodeModel nodeModel) {
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
        return new IndigoSmartsMatchCounterNodeDialog();
    }

}

