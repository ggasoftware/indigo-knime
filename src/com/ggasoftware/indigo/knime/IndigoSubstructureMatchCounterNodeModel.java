package com.ggasoftware.indigo.knime;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.data.container.*;
import org.knime.core.data.def.*;

import com.ggasoftware.indigo.*;

import com.ggasoftware.indigo.IndigoException;

/**
 * This is the model implementation of IndigoSubstructureMatchCounter.
 * 
 * 
 * @author GGA Software Services LLC
 */
public class IndigoSubstructureMatchCounterNodeModel extends NodeModel
{
	IndigoSubstructureMatchCounterSettings _settings = new IndigoSubstructureMatchCounterSettings();

	protected IndigoSubstructureMatchCounterNodeModel()
	{
		super(1, 1);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute (final BufferedDataTable[] inData,
	      final ExecutionContext exec) throws Exception
	{
		DataTableSpec spec = getDataTableSpec(inData[0].getDataTableSpec());

		BufferedDataContainer outputContainer = exec.createDataContainer(spec);

		int colIdx = spec.findColumnIndex(_settings.colName);

		if (colIdx == -1)
			throw new Exception("column not found");

		CloseableRowIterator it = inData[0].iterator();
		int rowNumber = 1;

		try
		{
			IndigoPlugin.lock();
			IndigoObject query;
			Indigo indigo = IndigoPlugin.getIndigo();
			
			if (_settings.loadFromFile)
			{
				query = indigo.loadQueryMoleculeFromFile(_settings.queryFileName);
				query.aromatize();
			}
			else
				query = indigo.loadSmarts(_settings.smarts);
			
			indigo.setOption("embedding-uniqueness", _settings.uniqueness.name().toLowerCase());
			
			while (it.hasNext())
			{
				DataRow inputRow = it.next();
				RowKey key = inputRow.getKey();
				DataCell[] cells = new DataCell[inputRow.getNumCells() + 1];
				IndigoObject io = ((IndigoMolCell) (inputRow.getCell(colIdx))).getIndigoObject();
				int i;
	
				for (i = 0; i < inputRow.getNumCells(); i++)
					cells[i] = inputRow.getCell(i);
	
				IndigoObject matcher = indigo.substructureMatcher(io);
				
				cells[i++] = new IntCell(matcher.countMatches(query));
	
				outputContainer.addRowToTable(new DefaultRow(key, cells));
				exec.checkCanceled();
				exec.setProgress(rowNumber / (double) inData[0].getRowCount(),
				      "Adding row " + rowNumber);
	
				rowNumber++;
			}
		}
		finally
		{
			IndigoPlugin.unlock();
		}

		outputContainer.close();
		return new BufferedDataTable[] { outputContainer.getTable() };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset ()
	{
	}

	protected DataTableSpec getDataTableSpec (DataTableSpec inSpec)
	{
		DataColumnSpec[] specs = new DataColumnSpec[inSpec.getNumColumns() + 1];

		int i;

		for (i = 0; i < inSpec.getNumColumns(); i++)
			specs[i] = inSpec.getColumnSpec(i);

		specs[i] = new DataColumnSpecCreator(_settings.newColName, IntCell.TYPE).createSpec();

		return new DataTableSpec(specs);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure (final DataTableSpec[] inSpecs)
	      throws InvalidSettingsException
	{
		return new DataTableSpec[]{getDataTableSpec(inSpecs[0])};
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
		IndigoSubstructureMatchCounterSettings s = new IndigoSubstructureMatchCounterSettings();
		s.loadSettings(settings);
		try
		{
			IndigoPlugin.lock();
			if (s.loadFromFile)
			{
				if (s.queryFileName == null || s.queryFileName.equals(""))
					throw new InvalidSettingsException("the query file name must be specified");
				IndigoPlugin.getIndigo().loadQueryMoleculeFromFile(s.queryFileName);
			}
			else
			{
				if (s.smarts == null || s.smarts.equals(""))
					throw new InvalidSettingsException("the SMARTS expression must be specified");
				IndigoPlugin.getIndigo().loadSmarts(s.smarts);
			}
		}
		catch (IndigoException e)
		{
			throw new InvalidSettingsException(e.getMessage());
		}
		finally
		{
			IndigoPlugin.unlock();
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
