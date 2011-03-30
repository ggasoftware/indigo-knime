package com.ggasoftware.indigo.knime;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

/**
 * <code>NodeFactory</code> for the "IndigoSubstructureMatchCounter" Node.
 * 
 *
 * @author GGA Software Services LLC
 */
public class IndigoSubstructureMatchCounterNodeFactory 
        extends NodeFactory<IndigoSubstructureMatchCounterNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public IndigoSubstructureMatchCounterNodeModel createNodeModel() {
        return new IndigoSubstructureMatchCounterNodeModel();
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
    public NodeView<IndigoSubstructureMatchCounterNodeModel> createNodeView(final int viewIndex,
            final IndigoSubstructureMatchCounterNodeModel nodeModel) {
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
        return new IndigoSubstructureMatchCounterNodeDialog();
    }
}

