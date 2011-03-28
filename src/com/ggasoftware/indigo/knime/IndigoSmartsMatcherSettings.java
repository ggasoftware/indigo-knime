package com.ggasoftware.indigo.knime;

import org.knime.core.node.*;

public class IndigoSmartsMatcherSettings
{
	public String colName;
	public String smarts;

	public void loadSettings (final NodeSettingsRO settings)
	      throws InvalidSettingsException
	{
		colName = settings.getString("colName");
		smarts = settings.getString("smarts");
	}

	public void loadSettingsForDialog (final NodeSettingsRO settings)
	{
		colName = settings.getString("colName", null);
		smarts = settings.getString("smarts", null);
	}

	public void saveSettings (final NodeSettingsWO settings)
	{
		settings.addString("colName", colName);
		settings.addString("smarts", smarts);
	}
}
