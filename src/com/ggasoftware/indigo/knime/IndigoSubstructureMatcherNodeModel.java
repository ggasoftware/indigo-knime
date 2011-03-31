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
public class IndigoSubstructureMatcherNodeModel extends NodeModel
{
	IndigoSubstructureMatcherSettings _settings = new IndigoSubstructureMatcherSettings();

	/**
	 * Constructor for the node model.
	 */
	protected IndigoSubstructureMatcherNodeModel()
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
		IndigoObject query;
		
		if (_settings.loadFromFile)
		{
			query = IndigoCell.indigo.loadQueryMoleculeFromFile(_settings.queryFileName);
			query.aromatize();
		}
		else
			query = IndigoCell.indigo.loadSmarts(_settings.smarts);

		DataTableSpec inputTableSpec = inData[0].getDataTableSpec();

		BufferedDataContainer validOutputContainer = exec
		      .createDataContainer(inputTableSpec);
		BufferedDataContainer invalidOutputContainer = exec
		      .createDataContainer(inputTableSpec);

		int colIdx = inputTableSpec.findColumnIndex(_settings.colName);

		if (colIdx == -1)
			throw new Exception("column not found");

		CloseableRowIterator it = inData[0].iterator();
		int rowNumber = 1;

		while (it.hasNext())
		{
			DataRow inputRow = it.next();

			IndigoObject match = IndigoCell.indigo.substructureMatcher(
			      ((IndigoCell) (inputRow.getCell(colIdx))).getIndigoObject())
			      .match(query);

			if (match != null)
			{
				validOutputContainer.addRowToTable(inputRow);
			} else
			{
				invalidOutputContainer.addRowToTable(inputRow);
			}
			exec.checkCanceled();
			exec.setProgress(rowNumber / (double) inData[0].getRowCount(),
			      "Adding row " + rowNumber);
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
		IndigoSubstructureMatcherSettings s = new IndigoSubstructureMatcherSettings();
		s.loadSettings(settings);
		
		try
		{
			if (s.loadFromFile)
			{
				if (s.queryFileName == null || s.queryFileName.equals(""))
					throw new InvalidSettingsException("the query file name must be specified");
				IndigoCell.indigo.loadQueryMoleculeFromFile(s.queryFileName);
			}
			else
			{
				if (s.smarts == null || s.smarts.equals(""))
					throw new InvalidSettingsException("the SMARTS expression must be specified");
				IndigoCell.indigo.loadSmarts(s.smarts);
			}
		}
		catch (IndigoException e)
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
