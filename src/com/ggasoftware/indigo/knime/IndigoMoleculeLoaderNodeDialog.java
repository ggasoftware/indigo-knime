package com.ggasoftware.indigo.knime;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.util.ColumnSelectionComboxBox;
import org.knime.chem.types.MolValue;
import org.knime.chem.types.SdfValue;
import org.knime.chem.types.SmilesValue;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <code>NodeDialog</code> for the "IndigoMoleculeLoader" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author 
 */
public class IndigoMoleculeLoaderNodeDialog extends NodeDialogPane {

	private final ColumnSelectionComboxBox m_colName =
        new ColumnSelectionComboxBox((Border)null, SdfValue.class, MolValue.class, SmilesValue.class);
	
    private final JCheckBox m_treatXAsPseudoatom = new JCheckBox();
    
    private final JCheckBox m_ignoreStereochemistryErrors = new JCheckBox();

    private final JCheckBox m_appendColumn = new JCheckBox("Append Column");
    
    private final JTextField m_newColName = new JTextField(20);
    
    IndigoMoleculeLoaderSettings m_settings = new IndigoMoleculeLoaderSettings();
    
    /**
     * New pane for configuring IndigoMoleculeLoader node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected IndigoMoleculeLoaderNodeDialog() {
        super();
        
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(2, 2, 2, 2);
        c.gridx = 0;
        c.gridy = 0;
        p.add(new JLabel("Molecule column   "), c);
        c.gridx = 1;
        p.add(m_colName, c);

        c.gridy++;
        
        c.gridx = 0;
        p.add(m_appendColumn, c);
        c.gridx = 1;
        p.add(m_newColName, c);
        
        c.gridy++;
        c.gridx = 0;
        p.add(new JLabel("Treat X as pseudoatom"), c);
        c.gridx = 1;
        p.add(m_treatXAsPseudoatom, c);
        
        c.gridy++;
        c.gridx = 0;
        p.add(new JLabel("Ignore stereochemistry errors"), c);
        c.gridx = 1;
        p.add(m_ignoreStereochemistryErrors, c);

        m_appendColumn.addChangeListener(new ChangeListener() {
            public void stateChanged(final ChangeEvent e) {
                if (m_appendColumn.isSelected()) {
                    m_newColName.setEnabled(true);
                    if ("".equals(m_newColName.getText())) {
                        m_newColName.setText(
                                m_colName.getSelectedColumn() + " (Indigo)");
                    }
                } else {
                    m_newColName.setEnabled(false);
                }
                
            }  
        });
        m_newColName.setEnabled(m_appendColumn.isSelected());
        
        addTab("Standard settings", p);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsFrom(final NodeSettingsRO settings,
            final DataTableSpec[] specs) throws NotConfigurableException {
        m_settings.loadSettingsForDialog(settings);

        m_colName.update(specs[0], m_settings.colName);
        m_appendColumn.setSelected(!m_settings.replaceColumn);
        m_newColName.setEnabled(!m_settings.replaceColumn);
        m_newColName.setText(m_settings.newColName != null ? m_settings.newColName : "");
        m_treatXAsPseudoatom.setSelected(m_settings.treatXAsPseudoatom);
        m_ignoreStereochemistryErrors.setSelected(m_settings.ignoreStereochemistryErrors);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings)
            throws InvalidSettingsException {
        m_settings.colName = m_colName.getSelectedColumn();
        m_settings.replaceColumn =  !m_appendColumn.isSelected();
        m_settings.newColName = m_newColName.getText();
        m_settings.saveSettings(settings);
        m_settings.treatXAsPseudoatom = m_treatXAsPseudoatom.isSelected();
        m_settings.ignoreStereochemistryErrors = m_ignoreStereochemistryErrors.isSelected();
    }    
}

