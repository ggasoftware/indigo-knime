package com.ggasoftware.indigo.knime;

import java.io.File;
import java.io.IOException;

import org.knime.chem.types.CMLCell;
import org.knime.chem.types.CMLCellFactory;
import org.knime.chem.types.SdfCell;
import org.knime.chem.types.SdfCellFactory;
import org.knime.chem.types.SmilesCell;
import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.CellFactory;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import com.ggasoftware.indigo.IndigoException;
import com.ggasoftware.indigo.knime.IndigoMoleculeSaverSettings.Format;

/**
 * This is the model implementation of IndigoMoleculeSaver.
 * 
 *
 * @author GGA Software Services LLC
 */
public class IndigoMoleculeSaverNodeModel extends NodeModel {
    
	IndigoMoleculeSaverSettings m_settings = new IndigoMoleculeSaverSettings();

    private static final NodeLogger LOGGER =
        NodeLogger.getLogger(IndigoMoleculeSaverNodeModel.class);
	
    /**
     * Constructor for the node model.
     */
    protected IndigoMoleculeSaverNodeModel() {
        super(1, 1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected BufferedDataTable[] execute(final BufferedDataTable[] inData,
            final ExecutionContext exec) throws Exception {
        ColumnRearranger crea = createRearranger(inData[0].getDataTableSpec());

        return new BufferedDataTable[]{exec.createColumnRearrangeTable(
                inData[0], crea, exec)};

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void reset() {
        // TODO: generated method stub
    }
    
    class Converter implements CellFactory
    {
    	int _colIndex;
        private final DataColumnSpec[] m_colSpec;
    	
        Converter(final DataTableSpec inSpec, final DataColumnSpec cs, final IndigoMoleculeSaverSettings settings, final int colIndex) {
            _colIndex = colIndex;
            
            DataType type = null;
            
            if (m_settings.destFormat == Format.SDF)
                type = SdfCell.TYPE;
            else if (m_settings.destFormat == Format.Smiles)
                type = SmilesCell.TYPE;
            else 
                type = CMLCell.TYPE;
            
            if (settings.replaceColumn) {
                m_colSpec = new DataColumnSpec[]{
                		new DataColumnSpecCreator(settings.colName, type).createSpec()};
            } else {
                m_colSpec = new DataColumnSpec[]{
                		new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec,
                				settings.newColName), type).createSpec()};
            }
        }
        
        public DataCell getCell(final DataRow row) {
            DataCell cell = row.getCell(_colIndex);
            if (cell.isMissing()) {
                return cell;
            } else {
            	IndigoValue iv = (IndigoValue)cell;
            	try {
            		if (m_settings.destFormat == Format.SDF)
            			return SdfCellFactory.create(iv.getIndigoObject().molfile());
            		if (m_settings.destFormat == Format.Smiles)
            			return new SmilesCell(iv.getIndigoObject().smiles());
            		return CMLCellFactory.create(iv.getIndigoObject().cml());
            	}
            	catch (IndigoException ex) {
                    LOGGER.error("Could not convert molecule: " + ex.getMessage(), ex);
                    return DataType.getMissingCell();
            	}
            }
        }

		@Override
		public DataCell[] getCells(DataRow row) {
			return new DataCell[]{getCell(row)};
		}

		@Override
		public DataColumnSpec[] getColumnSpecs() {
			return m_colSpec;
		}

		@Override
		public void setProgress(int curRowNr, int rowCount, RowKey lastKey,
				ExecutionMonitor exec) {
			// TODO Auto-generated method stub
			
		}
    }
    

    private ColumnRearranger createRearranger(final DataTableSpec inSpec) {
        ColumnRearranger crea = new ColumnRearranger(inSpec);

        DataType type = null;
        if (m_settings.destFormat == Format.SDF) {
            type = SdfCell.TYPE;
        } else if (m_settings.destFormat == Format.Smiles) {
            type = SmilesCell.TYPE;
        } else if (m_settings.destFormat == Format.CML) {
            type = CMLCell.TYPE;
        }

        DataColumnSpec cs;
        if (m_settings.replaceColumn) {
            cs =
                    new DataColumnSpecCreator(m_settings.colName, type)
                            .createSpec();
        } else {
            String name =
                    DataTableSpec.getUniqueColumnName(inSpec, m_settings
                            .newColName);
            cs = new DataColumnSpecCreator(name, type).createSpec();
        }

        Converter conv =
                new Converter(inSpec, cs, m_settings, inSpec.findColumnIndex(m_settings.colName));

        if (m_settings.replaceColumn) {
            crea.replace(conv, m_settings.colName);
        } else {
            crea.append(conv);
        }

        return crea;
    }
    
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {

        if (m_settings.colName == null) {
            for (DataColumnSpec cs : inSpecs[0]) {
                if (cs.getType().isCompatible(IndigoValue.class)) {
                    if (m_settings.colName != null) {
                        setWarningMessage("Selected column '"
                                + m_settings.colName + "' as Indigo column");
                    } else {
                        m_settings.colName = cs.getName();
                    }
                }
            }
            if (m_settings.colName == null) {
                throw new InvalidSettingsException(
                        "No Indigo column in input table");
            }
        } else {
            if (!inSpecs[0].containsName(m_settings.colName)) {
                throw new InvalidSettingsException("Column '"
                        + m_settings.colName
                        + "' does not exist in input table");
            }
            if (!inSpecs[0].getColumnSpec(m_settings.colName).getType()
                    .isCompatible(IndigoValue.class)) {
                throw new InvalidSettingsException("Column '"
                        + m_settings.colName
                        + "' does not contain Indigo molecules");
            }
        }

        return new DataTableSpec[]{createRearranger(inSpecs[0]).createSpec()};
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {
    	m_settings.saveSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadValidatedSettingsFrom(final NodeSettingsRO settings)
            throws InvalidSettingsException {
    	m_settings.loadSettings(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettings(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        IndigoMoleculeSaverSettings s = new IndigoMoleculeSaverSettings();
        s.loadSettings(settings);
        if (!s.replaceColumn
                && ((s.newColName == null) || (s.newColName.length() < 1))) {
            throw new InvalidSettingsException("No name for new column given");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        // TODO: generated method stub
    }

}

