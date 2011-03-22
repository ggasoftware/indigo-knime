package com.ggasoftware.indigo.knime;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.Border;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;
import org.knime.core.node.util.ColumnSelectionComboxBox;
import org.knime.core.data.DataValue;

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
public class IndigoBasicPropertiesNodeDialog extends DefaultNodeSettingsPane {

	private final ColumnSelectionComboxBox m_colName = new ColumnSelectionComboxBox((Border)null, IndigoValue.class);
	
    /**
     * New pane for configuring IndigoBasicProperties node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected IndigoBasicPropertiesNodeDialog() {
        super();
        JPanel p = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(2, 2, 2, 2);
        c.gridx = 0;
        c.gridy = 0;
        p.add(new JLabel("Indigo column   "), c);
        c.gridx = 1;
        p.add(m_colName, c);
        
        addTab("Standard settings", p);
    }
}

