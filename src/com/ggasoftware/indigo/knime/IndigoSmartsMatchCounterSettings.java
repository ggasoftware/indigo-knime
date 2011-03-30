package com.ggasoftware.indigo.knime;

import org.knime.core.node.*;

public class IndigoSmartsMatchCounterSettings
{
	public enum Uniqueness
	{
		Atoms, Bonds, None
	}
	
	public String colName;
	public String smarts;
	public String newColName;
	Uniqueness uniqueness;

	public void loadSettings (final NodeSettingsRO settings)
	      throws InvalidSettingsException
	{
		colName = settings.getString("colName");
		newColName = settings.getString("newColName");
		smarts = settings.getString("smarts");
		uniqueness = Uniqueness.valueOf(settings.getString("uniqueness"));
	}

	public void loadSettingsForDialog (final NodeSettingsRO settings)
	{
		colName = settings.getString("colName", null);
		newColName = settings.getString("newColName", "Number of matches");
		smarts = settings.getString("smarts", "");
		uniqueness = Uniqueness.valueOf(settings.getString("uniqueness", Uniqueness.Atoms.name()));
	}

	public void saveSettings (final NodeSettingsWO settings)
	{
		if (colName != null)
			settings.addString("colName", colName);
		if (newColName != null)
			settings.addString("newColName", newColName);
		if (smarts != null)
			settings.addString("smarts", smarts);
		if (uniqueness != null) 
			settings.addString("uniqueness", uniqueness.name());
	}
}
