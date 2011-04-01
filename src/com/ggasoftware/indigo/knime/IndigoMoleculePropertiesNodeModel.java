package com.ggasoftware.indigo.knime;

import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.data.container.*;
import org.knime.core.data.def.*;
import org.knime.core.node.*;
import com.ggasoftware.indigo.*;
import java.io.*;
import java.util.*;

public class IndigoMoleculePropertiesNodeModel extends NodeModel
{

	private final IndigoMoleculePropertiesSettings _settings = new IndigoMoleculePropertiesSettings();

	public static interface PropertyCalculator
	{
		DataType type ();

		DataCell calculate (IndigoObject io);
	}

	public static abstract class IntPropertyCalculator implements
	      PropertyCalculator
	{
		public DataType type ()
		{
			return IntCell.TYPE;
		}
	}

	public static abstract class DoublePropertyCalculator implements
	      PropertyCalculator
	{
		public DataType type ()
		{
			return DoubleCell.TYPE;
		}
	}

	public static abstract class StringPropertyCalculator implements
	      PropertyCalculator
	{
		public DataType type ()
		{
			return StringCell.TYPE;
		}
	}

	public static final Map<String, PropertyCalculator> calculators = new HashMap<String, PropertyCalculator>();
	public static final Map<String, DataColumnSpec> colSpecs = new HashMap<String, DataColumnSpec>();

	static
	{
		calculators.put("Atoms count", new IntPropertyCalculator() {
			public DataCell calculate (IndigoObject io)
			{
				return new IntCell(io.countAtoms());
			}
		});

		calculators.put("Aromatic atoms count", new IntPropertyCalculator() {
			public DataCell calculate (IndigoObject io)
			{
				int count = 0;

				for (IndigoObject atom : io.iterateAtoms())
					for (IndigoObject nei : atom.iterateNeighbors())
						if (nei.bond().bondOrder() == 4)
						{
							count++;
							break;
						}
				return new IntCell(count);
			}
		});

		calculators.put("Aromatic bonds count", new IntPropertyCalculator() {
			public DataCell calculate (IndigoObject io)
			{
				int count = 0;

				for (IndigoObject bond : io.iterateBonds())
					if (bond.bondOrder() == 4)
						count++;
				return new IntCell(count);
			}
		});

		calculators.put("Bonds count", new IntPropertyCalculator() {
			public DataCell calculate (IndigoObject io)
			{
				return new IntCell(io.countBonds());
			}
		});

		calculators.put("Heavy atoms count", new IntPropertyCalculator() {
			public DataCell calculate (IndigoObject io)
			{
				return new IntCell(io.countHeavyAtoms());
			}
		});

		calculators.put("Number of chiral centers", new IntPropertyCalculator() {
			public DataCell calculate (IndigoObject io)
			{
				return new IntCell(io.countStereocenters());
			}
		});

		calculators.put("Number of cis-trans bonds", new IntPropertyCalculator() {
			public DataCell calculate (IndigoObject io)
			{
				int count = 0;

				for (IndigoObject bond : io.iterateBonds())
				{
					int stereo = bond.bondStereo();
					if (stereo == Indigo.CIS || stereo == Indigo.TRANS)
						count++;
				}
				return new IntCell(count);
			}
		});

		calculators.put("Number of connected components",
		      new IntPropertyCalculator() {
			      public DataCell calculate (IndigoObject io)
			      {
				      return new IntCell(io.countComponents());
			      }
		      });

		calculators.put("Number of pseudoatoms", new IntPropertyCalculator() {
			public DataCell calculate (IndigoObject io)
			{
				return new IntCell(io.countPseudoatoms());
			}
		});

		calculators.put("Number of R-sites", new IntPropertyCalculator() {
			public DataCell calculate (IndigoObject io)
			{
				return new IntCell(io.countRSites());
			}
		});

		// TODO: Hydrogen count
		/*
		 * calculators.put("Hydrogen count", new IntPropertyCalculator () { public
		 * DataCell calculate (IndigoObject io) { return new
		 * IntCell(io.countAtoms() - io.countHeavyAtoms()); }});
		 */

		calculators.put("Molecular weight", new DoublePropertyCalculator() {
			public DataCell calculate (IndigoObject io)
			{
				return new DoubleCell(io.molecularWeight());
			}
		});

		calculators.put("Monoisotopic mass", new DoublePropertyCalculator() {
			public DataCell calculate (IndigoObject io)
			{
				return new DoubleCell(io.monoisotopicMass());
			}
		});

		calculators.put("Most abundant mass", new DoublePropertyCalculator() {
			public DataCell calculate (IndigoObject io)
			{
				return new DoubleCell(io.mostAbundantMass());
			}
		});

		calculators.put("Molecular formula", new StringPropertyCalculator() {
			public DataCell calculate (IndigoObject io)
			{
				return new StringCell(io.grossFormula());
			}
		});

		for (String key : calculators.keySet())
		{
			DataType type = ((PropertyCalculator) calculators.get(key)).type();
			colSpecs.put(key, new DataColumnSpecCreator(key, type).createSpec());
		}
	}

	/**
	 * Constructor for the node model.
	 */
	protected IndigoMoleculePropertiesNodeModel()
	{
		super(1, 1);
	}

	protected DataTableSpec getDataTableSpec (DataTableSpec inSpec)
	{
		DataColumnSpec[] specs = new DataColumnSpec[inSpec.getNumColumns()
		      + _settings.selectedProps.length];

		int i;

		for (i = 0; i < inSpec.getNumColumns(); i++)
			specs[i] = inSpec.getColumnSpec(i);

		for (String key : _settings.selectedProps)
			specs[i++] = colSpecs.get(key);

		return new DataTableSpec(specs);
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

		while (it.hasNext())
		{
			DataRow inputRow = it.next();
			RowKey key = inputRow.getKey();
			DataCell[] cells = new DataCell[inputRow.getNumCells()
			      + _settings.selectedProps.length];
			IndigoObject io = ((IndigoCell) (inputRow.getCell(colIdx)))
			      .getIndigoObject();
			int i;

			for (i = 0; i < inputRow.getNumCells(); i++)
				cells[i] = inputRow.getCell(i);

			for (String prop : _settings.selectedProps)
				cells[i++] = calculators.get(prop).calculate(io);

			outputContainer.addRowToTable(new DefaultRow(key, cells));
			exec.checkCanceled();
			exec.setProgress(rowNumber / (double) inData[0].getRowCount(),
			      "Adding row " + rowNumber);

			rowNumber++;
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
		// TODO Code executed on reset.
		// Models build during execute are cleared here.
		// Also data handled in load/saveInternals will be erased here.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure (final DataTableSpec[] inSpecs)
	      throws InvalidSettingsException
	{
		return new DataTableSpec[] { getDataTableSpec(inSpecs[0]) };
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
