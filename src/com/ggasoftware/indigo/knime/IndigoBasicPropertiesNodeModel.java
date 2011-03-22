package com.ggasoftware.indigo.knime;

import java.io.IOException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.RowKey;
import org.knime.core.data.container.CellFactory;
import org.knime.core.data.container.ColumnRearranger;
import org.knime.core.data.def.IntCell;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NodeLogger;

import com.ggasoftware.indigo.IndigoException;

import java.io.File;

public class IndigoBasicPropertiesNodeModel extends NodeModel {
    
    private static final NodeLogger LOGGER =
        NodeLogger.getLogger(IndigoBasicPropertiesNodeModel.class);
    
    private final IndigoBasicPropertiesSettings m_settings = new IndigoBasicPropertiesSettings();

    
    /**
     * Constructor for the node model.
     */
    protected IndigoBasicPropertiesNodeModel() {
    
        // TODO one incoming port and one outgoing port is assumed
        super(1, 1);
    }
    
    class Converter implements CellFactory
    {
    	int _colIndex;
        private final DataColumnSpec[] m_colSpec;
    	
        Converter(final DataTableSpec inSpec, final DataColumnSpec cs, final int colIndex) {
            _colIndex = colIndex;
            
            m_colSpec = new DataColumnSpec[]{
            		new DataColumnSpecCreator(DataTableSpec.getUniqueColumnName(inSpec,
            				"Heavy atoms count"), IntCell.TYPE).createSpec()};
        }
        
        public DataCell getCell(final DataRow row) {
            DataCell cell = row.getCell(_colIndex);
            if (cell.isMissing()) {
                return cell;
            } else {
            	try {
            		return new IntCell(((IndigoCell)cell).getIndigoObject().countHeavyAtoms());
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

        DataColumnSpec cs;
        cs = (new DataColumnSpecCreator("IndigoObject", IndigoCell.TYPE)).createSpec();
        
        Converter conv = new Converter(inSpec, cs, inSpec.findColumnIndex(m_settings.colName));

        crea.append(conv);

        return crea;
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
        // TODO Code executed on reset.
        // Models build during execute are cleared here.
        // Also data handled in load/saveInternals will be erased here.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected DataTableSpec[] configure(final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {
        
        // TODO: check if user settings are available, fit to the incoming
        // table structure, and the incoming types are feasible for the node
        // to execute. If the node can execute in its current state return
        // the spec of its output data table(s) (if you can, otherwise an array
        // with null elements), or throw an exception with a useful user message

        return new DataTableSpec[]{null};
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
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
        
        // TODO load internal data. 
        // Everything handed to output ports is loaded automatically (data
        // returned by the execute method, models loaded in loadModelContent,
        // and user settings set through loadSettingsFrom - is all taken care 
        // of). Load here only the other internals that need to be restored
        // (e.g. data used by the views).

    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveInternals(final File internDir,
            final ExecutionMonitor exec) throws IOException,
            CanceledExecutionException {
       
        // TODO save internal models. 
        // Everything written to output ports is saved automatically (data
        // returned by the execute method, models saved in the saveModelContent,
        // and user settings saved through saveSettingsTo - is all taken care 
        // of). Save here only the other internals that need to be preserved
        // (e.g. data used by the views).

    }

}

