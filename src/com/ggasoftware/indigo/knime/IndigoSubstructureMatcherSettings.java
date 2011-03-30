package com.ggasoftware.indigo.knime;

import org.knime.core.node.*;

public class IndigoSubstructureMatcherSettings
{
	public String colName;
	public String queryFileName;

	public void loadSettings (final NodeSettingsRO settings)
	      throws InvalidSettingsException
	{
		colName = settings.getString("colName");
		queryFileName = settings.getString("queryFileName");
	}

	public void loadSettingsForDialog (final NodeSettingsRO settings)
	{
		colName = settings.getString("colName", null);
		queryFileName = settings.getString("queryFileName", null);
	}

	public void saveSettings (final NodeSettingsWO settings)
	{
		settings.addString("colName", colName);
		settings.addString("queryFileName", queryFileName);
	}
}
