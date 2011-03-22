package com.ggasoftware.indigo.knime;

import org.knime.core.node.defaultnodesettings.DefaultNodeSettingsPane;
import org.knime.core.node.defaultnodesettings.DialogComponentNumber;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

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
public class IndigoValenceCheckerNodeDialog extends DefaultNodeSettingsPane {

    /**
     * New pane for configuring IndigoValenceChecker node dialog.
     * This is just a suggestion to demonstrate possible default dialog
     * components.
     */
    protected IndigoValenceCheckerNodeDialog() {
        super();
        
        addDialogComponent(new DialogComponentNumber(
                new SettingsModelIntegerBounded(
                    IndigoValenceCheckerNodeModel.CFGKEY_COUNT,
                    IndigoValenceCheckerNodeModel.DEFAULT_COUNT,
                    Integer.MIN_VALUE, Integer.MAX_VALUE),
                    "Counter:", /*step*/ 1, /*componentwidth*/ 5));
                    
    }
}

