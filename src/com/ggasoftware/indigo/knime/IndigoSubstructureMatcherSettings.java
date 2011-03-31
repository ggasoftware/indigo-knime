package com.ggasoftware.indigo.knime;

import org.knime.core.node.*;

public class IndigoSubstructureMatcherSettings
{
	public String colName;
	public boolean loadFromFile;
	public String queryFileName;
	public String smarts;

	public void loadSettings (final NodeSettingsRO settings)
	      throws InvalidSettingsException
	{
		colName = settings.getString("colName");
		queryFileName = settings.getString("queryFileName");
		loadFromFile = settings.getBoolean("loadFromFile");
		smarts = settings.getString("smarts");
	}

	public void loadSettingsForDialog (final NodeSettingsRO settings)
	{
		colName = settings.getString("colName", null);
		queryFileName = settings.getString("queryFileName", null);
		loadFromFile = settings.getBoolean("loadFromFile", false);
		smarts = settings.getString("smarts", "");
	}

	public void saveSettings (final NodeSettingsWO settings)
	{
		settings.addString("colName", colName);
		settings.addString("queryFileName", queryFileName);
		settings.addBoolean("loadFromFile", loadFromFile);
		if (smarts != null)
			settings.addString("smarts", smarts);
	}
}
