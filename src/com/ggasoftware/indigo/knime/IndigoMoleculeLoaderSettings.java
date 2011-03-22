package com.ggasoftware.indigo.knime;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

public class IndigoMoleculeLoaderSettings {
	public String colName;
	public boolean replaceColumn = true;
	public String newColName = "";
	public boolean treatXAsPseudoatom = true;
	public boolean ignoreStereochemistryErrors = true;
	
    /**
     * Loads the settings from the given node settings object.
     *
     * @param settings node settings
     * @throws InvalidSettingsException if some settings are missing
     */
    public void loadSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        colName = settings.getString("colName");
        replaceColumn = settings.getBoolean("replaceColumn");
        newColName = settings.getString("newColName");
        treatXAsPseudoatom = settings.getBoolean("treatXAsPseudoatom");
        ignoreStereochemistryErrors = settings.getBoolean("ignoreStereochemistryErrors");
    }

    /**
     * Loads the settings from the given node settings object.
     *
     * @param settings node settings
     */
    public void loadSettingsForDialog(final NodeSettingsRO settings) {
        colName = settings.getString("colName", null);
        replaceColumn = settings.getBoolean("replaceColumn", true);
        newColName = settings.getString("newColName", "");
        treatXAsPseudoatom = settings.getBoolean("treatXAsPseudoatom", true);
        ignoreStereochemistryErrors = settings.getBoolean("ignoreStereochemistryErrors", true);
    }

    /**
     * Saves the settings to the given node settings object.
     *
     * @param settings node settings
     */
    public void saveSettings(final NodeSettingsWO settings) {
        settings.addString("colName", colName);
        settings.addBoolean("replaceColumn", replaceColumn);
        settings.addString("newColName", newColName);
        settings.addBoolean("treatXAsPseudoatom", treatXAsPseudoatom);
        settings.addBoolean("ignoreStereochemistryErrors", ignoreStereochemistryErrors);
    }
}
