package com.ggasoftware.indigo.knime;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

public class IndigoMoleculeSaverSettings
{
	public enum Format
	{
		SDF, Smiles, CanonicalSmiles, CML
	}

	public String colName = "Molecule";
	public Format destFormat = Format.Smiles;
	public boolean replaceColumn = true;
	public String newColName;

	/**
	 * Loads the settings from the given node settings object.
	 * 
	 * @param settings
	 *           node settings
	 * @throws InvalidSettingsException
	 *            if some settings are missing
	 */
	public void loadSettings (final NodeSettingsRO settings)
	      throws InvalidSettingsException
	{
		colName = settings.getString("colName");
		replaceColumn = settings.getBoolean("replaceColumn");
		newColName = settings.getString("newColName");
		destFormat = Format.valueOf(settings.getString("destFormat"));
	}

	/**
	 * Loads the settings from the given node settings object.
	 * 
	 * @param settings
	 *           node settings
	 */
	public void loadSettingsForDialog (final NodeSettingsRO settings)
	{
		colName = settings.getString("colName", null);
		replaceColumn = settings.getBoolean("replaceColumn", true);
		newColName = settings.getString("newColName", "");
		destFormat = Format.valueOf(settings.getString("destFormat",
		      Format.SDF.name()));
	}

	/**
	 * Saves the settings to the given node settings object.
	 * 
	 * @param settings
	 *           node settings
	 */
	public void saveSettings (final NodeSettingsWO settings)
	{
		settings.addString("colName", colName);
		settings.addBoolean("replaceColumn", replaceColumn);
		settings.addString("newColName", newColName);
		settings.addString("destFormat", destFormat.name());
	}
}
