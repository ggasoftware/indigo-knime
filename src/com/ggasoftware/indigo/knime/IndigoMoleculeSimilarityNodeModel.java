package com.ggasoftware.indigo.knime;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.data.container.CloseableRowIterator;
import org.knime.core.data.def.*;
import org.knime.core.node.*;

import com.ggasoftware.indigo.*;
import com.ggasoftware.indigo.knime.IndigoMoleculeSimilaritySettings.Metric;

/**
 * This is the model implementation of IndigoMoleculeSimilarity.
 * 
 * 
 * @author GGA Software Services LLC
 */
public class IndigoMoleculeSimilarityNodeModel extends NodeModel
{
	IndigoMoleculeSimilaritySettings _settings = new IndigoMoleculeSimilaritySettings();
	
	/**
	 * Constructor for the node model.
	 */
	protected IndigoMoleculeSimilarityNodeModel()
	{
		super(1, 1);
	}
	
	protected DataTableSpec getDataTableSpec (DataTableSpec inSpec)
	{
		DataColumnSpec[] specs = new DataColumnSpec[inSpec.getNumColumns() + 1];

		int i;

		for (i = 0; i < inSpec.getNumColumns(); i++)
			specs[i] = inSpec.getColumnSpec(i);

		specs[i] = new DataColumnSpecCreator(_settings.newColName, DoubleCell.TYPE).createSpec();

		return new DataTableSpec(specs);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
	      final ExecutionContext exec) throws Exception
	{
		DataTableSpec spec = getDataTableSpec(inData[0].getDataTableSpec());

		BufferedDataContainer outputContainer = exec.createDataContainer(spec);

		int colIdx = spec.findColumnIndex(_settings.colName);

		if (colIdx == -1)
			throw new Exception("column not found");

		CloseableRowIterator it = inData[0].iterator();
		int rowNumber = 1;

	
		String metric = "";
		
		if (_settings.metric == Metric.Tanimoto)
			metric = "tanimoto";
		else if (_settings.metric == Metric.EuclidSub)
			metric = "euclid-sub";
		else if (_settings.metric == Metric.Tversky)
			metric = "tversky " + _settings.tverskyAlpha + " " + _settings.tverskyBeta;
		
		try
		{
			IndigoPlugin.lock();
			Indigo indigo = IndigoPlugin.getIndigo();
			IndigoObject mol;
			if (_settings.loadFromFile)
				mol = indigo.loadMoleculeFromFile(_settings.fileName);
			else
				mol = indigo.loadMolecule(_settings.smiles);
	
			mol.aromatize();
			
			while (it.hasNext())
			{
				DataRow inputRow = it.next();
				RowKey key = inputRow.getKey();
				DataCell[] cells = new DataCell[inputRow.getNumCells() + 1];
				IndigoObject io = ((IndigoMolCell) (inputRow.getCell(colIdx))).getIndigoObject().clone();
				int i;
	
				io.aromatize();
				float similarity = indigo.similarity(mol, io, metric);
				
				for (i = 0; i < inputRow.getNumCells(); i++)
					cells[i] = inputRow.getCell(i);
				cells[i++] = new DoubleCell(similarity);
	
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
	protected void reset()
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
	      throws InvalidSettingsException
	{
		return new DataTableSpec[]{getDataTableSpec(inSpecs[0])};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo(final NodeSettingsWO settings)
	{
		_settings.saveSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
	      throws InvalidSettingsException
	{
		_settings.loadSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void validateSettings(final NodeSettingsRO settings)
	      throws InvalidSettingsException
	{
		IndigoMoleculeSimilaritySettings s = new IndigoMoleculeSimilaritySettings();
		s.loadSettings(settings);
		if (s.newColName == null || s.newColName.length() < 1)
			throw new InvalidSettingsException("new column name must me specified");
		try
		{
			if (s.loadFromFile)
			{
				if (s.fileName == null || s.fileName.equals(""))
					throw new InvalidSettingsException("the file name must be specified");
				IndigoPlugin.getIndigo().loadMoleculeFromFile(s.fileName);
			}
			else
			{
				if (s.smiles == null || s.smiles.equals(""))
					throw new InvalidSettingsException("the SMILES expression must be specified");
				IndigoPlugin.getIndigo().loadMolecule(s.smiles);
			}
		} catch (IndigoException e)
		{
			throw new InvalidSettingsException(e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadInternals(final File internDir,
	      final ExecutionMonitor exec) throws IOException,
	      CanceledExecutionException
	{
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveInternals(final File internDir,
	      final ExecutionMonitor exec) throws IOException,
	      CanceledExecutionException
	{
	}
}
