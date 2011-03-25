package com.ggasoftware.indigo.knime;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import javax.swing.border.*;

/**
 * <code>NodeDialog</code> for the "IndigoValenceChecker" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author GGA Software Services LLC
 */
public class IndigoValenceCheckerNodeDialog extends NodeDialogPane {

    private final ColumnSelectionComboxBox m_molColumn =
        new ColumnSelectionComboxBox((Border)null, IndigoValue.class);
    
    IndigoValenceCheckerSettings m_settings = new IndigoValenceCheckerSettings();
    
    protected IndigoValenceCheckerNodeDialog() {
        super();
        
        JPanel p = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(2, 2, 2, 2);
        c.gridx = 0;
        c.gridy = 0;
        p.add(new JLabel("Indigo column   "), c);
        c.gridx = 1;
        p.add(m_molColumn, c);

        addTab("Standard settings", p);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings,
            final DataTableSpec[] specs) throws NotConfigurableException {
        m_settings.loadSettingsForDialog(settings);

        m_molColumn.update(specs[0], m_settings.colName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings)
            throws InvalidSettingsException {
        m_settings.colName = m_molColumn.getSelectedColumn();
        m_settings.saveSettings(settings);
    } 
}
