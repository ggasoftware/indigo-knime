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
public class IndigoBasicPropertiesNodeFactory 
        extends NodeFactory<IndigoBasicPropertiesNodeModel> {

    /**
     * {@inheritDoc}
     */
    @Override
    public IndigoBasicPropertiesNodeModel createNodeModel() {
        return new IndigoBasicPropertiesNodeModel();
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
    public NodeView<IndigoBasicPropertiesNodeModel> createNodeView(final int viewIndex,
            final IndigoBasicPropertiesNodeModel nodeModel) {
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
        return new IndigoBasicPropertiesNodeDialog();
    }

}

