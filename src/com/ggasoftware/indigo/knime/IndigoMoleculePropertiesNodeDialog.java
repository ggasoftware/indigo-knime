package com.ggasoftware.indigo.knime;

import java.awt.*;
import java.util.*;

import javax.swing.*;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.defaultnodesettings.*;
import org.knime.core.node.util.*;
import org.knime.ext.chem.cdk.molprops.MolPropsNodeModel;

/**
 * <code>NodeDialog</code> for the "IndigoBasicProperties" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author GGA Software Services LLC
 */
public class IndigoMoleculePropertiesNodeDialog extends NodeDialogPane {

	//private final ColumnSelectionComboxBox m_colName = new ColumnSelectionComboxBox((Border)null, IndigoValue.class);
	
    private final ColumnSelectionPanel m_selPanel;

    private final ColumnFilterPanel m_filterPanel;
	
    /**
     * New pane for configuring IndigoBasicProperties node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
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

