package com.ggasoftware.indigo.knime;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.knime.chem.types.CMLValue;
import org.knime.chem.types.SdfValue;
import org.knime.chem.types.SmilesValue;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.IndigoMoleculeSaverSettings.Format;

/**
 * <code>NodeDialog</code> for the "IndigoMoleculeSaver" Node.
 * 
 *
 * This node dialog derives from {@link DefaultNodeSettingsPane} which allows
 * creation of a simple dialog with standard components. If you need a more 
 * complex dialog please derive directly from 
 * {@link org.knime.core.node.NodeDialogPane}.
 * 
 * @author GGA Software Services LLC
 */
public class IndigoMoleculeSaverNodeDialog extends NodeDialogPane {

    private final ColumnSelectionComboxBox m_molColumn =
        new ColumnSelectionComboxBox((Border)null, IndigoValue.class);

    private final JComboBox m_destFormat =
        new JComboBox(new Object[]{Format.SDF, Format.Smiles, Format.CML});

    private final JCheckBox m_replaceColumn = new JCheckBox();

    private final JLabel m_newColNameLabel =
    	new JLabel("   New column name   ");

    private final JTextField m_newColName = new JTextField(20);

    private final IndigoMoleculeSaverSettings m_settings = new IndigoMoleculeSaverSettings();
    
    /**
     * New pane for configuring the IndigoMoleculeSaver node.
     */
    protected IndigoMoleculeSaverNodeDialog() {

        JPanel p = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(2, 2, 2, 2);
        c.gridx = 0;
        c.gridy = 0;
        p.add(new JLabel("Indigo column   "), c);
        c.gridx = 1;
        p.add(m_molColumn, c);

        c.gridx = 0;
        c.gridy++;
        p.add(new JLabel("Replace column   "), c);
        c.gridx = 1;
        p.add(m_replaceColumn, c);

        m_replaceColumn.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                m_newColNameLabel.setEnabled(!m_replaceColumn.isSelected());
                m_newColName.setEnabled(!m_replaceColumn.isSelected());
            }
        });

        c.gridx = 0;
        c.gridy++;
        p.add(m_newColNameLabel, c);
        c.gridx = 1;
        p.add(m_newColName, c);

        c.gridx = 0;
        c.gridy++;
        p.add(new JLabel("Destination format   "), c);
        c.gridx = 1;
        p.add(m_destFormat, c);

        m_destFormat.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(final JList list,
                    final Object value, final int index,
                    final boolean isSelected, final boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index,
                        isSelected, cellHasFocus);
                if (value == Format.SDF) {
                    setIcon(SdfValue.UTILITY.getIcon());
                    setText("SDF");
                } else if (value == Format.Smiles) {
                    setIcon(SmilesValue.UTILITY.getIcon());
                    setText("Smiles");
                } else if (value == Format.CML) {
                    setIcon(CMLValue.UTILITY.getIcon());
                    setText("CML");
                } else {
                    setIcon(null);
                    setText("");
                }
                return this;
            }
        });

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
        m_replaceColumn.setSelected(m_settings.replaceColumn);
        m_newColNameLabel.setEnabled(!m_settings.replaceColumn);
        m_newColName.setEnabled(!m_settings.replaceColumn);
        m_newColName.setText(m_settings.newColName);
        m_destFormat.setSelectedItem(m_settings.destFormat);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings)
            throws InvalidSettingsException {
        m_settings.colName = m_molColumn.getSelectedColumn();
        m_settings.replaceColumn = m_replaceColumn.isSelected();
        m_settings.newColName = m_newColName.getText();
        m_settings.destFormat = ((Format)m_destFormat.getSelectedItem());
        m_settings.saveSettings(settings);
    }    
}
