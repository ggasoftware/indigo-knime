package com.ggasoftware.indigo.knime;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

/**
 * <code>NodeDialog</code> for the "IndigoBasicProperties" Node.
 *
 * @author GGA Software Services LLC
 */
public class IndigoMoleculePropertiesNodeDialog extends NodeDialogPane {

    private final ColumnSelectionPanel m_selPanel;
    private final ColumnFilterPanel m_filterPanel;
	
    /**
     * New pane for configuring IndigoBasicProperties node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    @SuppressWarnings("unchecked")
	protected IndigoMoleculePropertiesNodeDialog() {
        super();
        
        m_selPanel = new ColumnSelectionPanel(IndigoValue.class);
        m_filterPanel = new ColumnFilterPanel(false);
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(m_selPanel, BorderLayout.NORTH);
        panel.add(m_filterPanel, BorderLayout.CENTER);
        
        DataTableSpec dummySpec = new DataTableSpec(IndigoMoleculePropertiesNodeModel.colSpecs.values().toArray(new DataColumnSpec[0]));
        Collection<String> selProps = IndigoMoleculePropertiesNodeModel.calculators.keySet();
        m_filterPanel.update(dummySpec, false, selProps);
        
        addTab("Properties and target column", panel);
    }

	@Override
	protected void saveSettingsTo(NodeSettingsWO settings)
			throws InvalidSettingsException {
        settings.addString("colName", m_selPanel.getSelectedColumn());
        settings.addStringArray("selectedProps", m_filterPanel.getIncludedColumnSet().toArray(new String[0]));
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings,
            final DataTableSpec[] specs) throws NotConfigurableException {
        String[] selProps = settings.getStringArray("selectedProps", (String[])null);
        m_selPanel.update(specs[0], settings.getString("colName", null));
        DataTableSpec dummySpec = new DataTableSpec(IndigoMoleculePropertiesNodeModel.colSpecs.values().toArray(new DataColumnSpec[0]));
        m_filterPanel.update(dummySpec, false, selProps);
    }
	
}

