package com.ggasoftware.indigo.knime;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.data.container.*;
import org.knime.core.node.*;

import com.ggasoftware.indigo.*;

/**
 * This is the model implementation of IndigoSmartsMatcher.
 * 
 * 
 * @author GGA Software Services LLC
 */
public class IndigoSmartsMatcherNodeModel extends NodeModel
{
	IndigoSmartsMatcherSettings _settings = new IndigoSmartsMatcherSettings();

	/**
	 * Constructor for the node model.
	 */
	protected IndigoSmartsMatcherNodeModel()
	{
		super(1, 2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute (final BufferedDataTable[] inData,
	      final ExecutionContext exec) throws Exception
	{
		IndigoObject smarts = IndigoCell.indigo.loadSmarts(_settings.smarts);

		DataTableSpec inputTableSpec = inData[0].getDataTableSpec();

		BufferedDataContainer validOutputContainer = exec.createDataContainer(inputTableSpec);
		BufferedDataContainer invalidOutputContainer = exec.createDataContainer(inputTableSpec);

		int colIdx = inputTableSpec.findColumnIndex(_settings.colName);

		if (colIdx == -1)
			throw new Exception("column not found");

		CloseableRowIterator it = inData[0].iterator();
		int rowNumber = 1;

		while (it.hasNext())
		{
			DataRow inputRow = it.next();

			IndigoObject match = IndigoCell.indigo.substructureMatcher(
					((IndigoCell)(inputRow.getCell(colIdx))).getIndigoObject()).match(smarts);

			if (match != null)
			{
				validOutputContainer.addRowToTable(inputRow);
			}
			else
			{
				invalidOutputContainer.addRowToTable(inputRow);
			}
			exec.checkCanceled();
			exec.setProgress(rowNumber / (double) inData[0].getRowCount(), "Adding row " + rowNumber);
			rowNumber++;
		}

		validOutputContainer.close();
		invalidOutputContainer.close();
		return new BufferedDataTable[] { validOutputContainer.getTable(),
		      invalidOutputContainer.getTable() };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset ()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure (final DataTableSpec[] inSpecs)
	      throws InvalidSettingsException
	{

		return new DataTableSpec[] { inSpecs[0], inSpecs[0] };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo (final NodeSettingsWO settings)
	{
		_settings.saveSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom (final NodeSettingsRO settings)
	      throws InvalidSettingsException
	{
		_settings.loadSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings (final NodeSettingsRO settings)
	      throws InvalidSettingsException
	{
		IndigoSmartsMatcherSettings s = new IndigoSmartsMatcherSettings();
		s.loadSettings(settings);
		try
		{
			IndigoCell.indigo.loadSmarts(s.smarts);
		} catch (IndigoException e)
		{
			throw new InvalidSettingsException(e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals (final File internDir,
	      final ExecutionMonitor exec) throws IOException,
	      CanceledExecutionException
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals (final File internDir,
	      final ExecutionMonitor exec) throws IOException,
	      CanceledExecutionException
	{
	}

}
