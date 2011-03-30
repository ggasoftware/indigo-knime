package com.ggasoftware.indigo.knime;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.*;
import org.knime.core.data.container.*;
import org.knime.core.node.*;

import com.ggasoftware.indigo.*;

/**
 * This is the model implementation of a generic transformer of Indigo objects.
 * 
 * 
 * @author GGA Software Services LLC
 */
public class IndigoSimpleNodeModel extends NodeModel
{

	public static abstract class Transformer
	{
		abstract void transform (IndigoObject io);
	}
	
	// the logger instance
	private static final NodeLogger logger = NodeLogger
	      .getLogger(IndigoSimpleNodeModel.class);

	IndigoSimpleSettings _settings = new IndigoSimpleSettings();
	Transformer _transformer;
	String _message;

	protected IndigoSimpleNodeModel (String message, Transformer transformer)
	{
		super(1, 1);
		_message = message;
		_transformer = transformer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected BufferedDataTable[] execute (final BufferedDataTable[] inData,
	      final ExecutionContext exec) throws Exception
	{

		ColumnRearranger crea = createRearranger(inData[0].getDataTableSpec());

		return new BufferedDataTable[] { exec.createColumnRearrangeTable(
		      inData[0], crea, exec) };
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void reset ()
	{
	}

	class Converter implements CellFactory
	{
		int _colIndex;
		private final DataColumnSpec[] m_colSpec;

		Converter(final DataTableSpec inSpec, final DataColumnSpec cs,
		      final IndigoSimpleSettings settings, final int colIndex)
		{
			_colIndex = colIndex;

			DataType type = IndigoCell.TYPE;

			if (settings.replaceColumn)
			{
				m_colSpec = new DataColumnSpec[] { new DataColumnSpecCreator(
				      settings.colName, type).createSpec() };
			} else
			{
				m_colSpec = new DataColumnSpec[] { new DataColumnSpecCreator(
				      DataTableSpec
				            .getUniqueColumnName(inSpec, settings.newColName),
				      type).createSpec() };
			}
		}

		public DataCell getCell (final DataRow row)
		{
			DataCell cell = row.getCell(_colIndex);
			if (cell.isMissing())
			{
				return cell;
			} else
			{
				IndigoValue iv = (IndigoValue) cell;
				try
				{
					IndigoObject io = iv.getIndigoObject().clone();
					_transformer.transform(io);
					return new IndigoCell(io);
				} catch (IndigoException ex)
				{
					logger.error("Could not " + _message + ": " + ex.getMessage(), ex);
					return DataType.getMissingCell();
				}
			}
		}

		@Override
		public DataCell[] getCells (DataRow row)
		{
			return new DataCell[] { getCell(row) };
		}

		@Override
		public DataColumnSpec[] getColumnSpecs ()
		{
			return m_colSpec;
		}

		@Override
		public void setProgress (int curRowNr, int rowCount, RowKey lastKey,
		      ExecutionMonitor exec)
		{
		}
	}

	private ColumnRearranger createRearranger (final DataTableSpec inSpec)
	{
		ColumnRearranger crea = new ColumnRearranger(inSpec);

		DataType type = IndigoCell.TYPE;

		DataColumnSpec cs;
		if (_settings.replaceColumn)
		{
			cs = new DataColumnSpecCreator(_settings.colName, type).createSpec();
		} else
		{
			String name = DataTableSpec.getUniqueColumnName(inSpec,
			      _settings.newColName);
			cs = new DataColumnSpecCreator(name, type).createSpec();
		}

		Converter conv = new Converter(inSpec, cs, _settings,
		      inSpec.findColumnIndex(_settings.colName));

		if (_settings.replaceColumn)
		{
			crea.replace(conv, _settings.colName);
		} else
		{
			crea.append(conv);
		}

		return crea;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DataTableSpec[] configure (final DataTableSpec[] inSpecs)
	      throws InvalidSettingsException
	{

		if (_settings.colName == null)
		{
			for (DataColumnSpec cs : inSpecs[0])
			{
				if (cs.getType().isCompatible(IndigoValue.class))
				{
					if (_settings.colName != null)
					{
						setWarningMessage("Selected column '" + _settings.colName
						      + "' as Indigo column");
					} else
					{
						_settings.colName = cs.getName();
					}
				}
			}
			if (_settings.colName == null)
			{
				throw new InvalidSettingsException(
				      "No Indigo column in input table");
			}
		} else
		{
			if (!inSpecs[0].containsName(_settings.colName))
			{
				throw new InvalidSettingsException("Column '" + _settings.colName
				      + "' does not exist in input table");
			}
			if (!inSpecs[0].getColumnSpec(_settings.colName).getType()
			      .isCompatible(IndigoValue.class))
			{
				throw new InvalidSettingsException("Column '" + _settings.colName
				      + "' does not contain Indigo molecules");
			}
		}

		return new DataTableSpec[] { createRearranger(inSpecs[0]).createSpec() };
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
		IndigoSimpleSettings s = new IndigoSimpleSettings();
		s.loadSettings(settings);
		if (!s.replaceColumn
		      && ((s.newColName == null) || (s.newColName.length() < 1)))
		{
			throw new InvalidSettingsException("No name for new column given");
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
