package com.ggasoftware.indigo.knime;

import com.ggasoftware.indigo.*;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.knime.core.data.DataCellDataInput;
import org.knime.core.data.DataCellDataOutput;
import org.knime.core.data.DataCellSerializer;
import org.knime.core.data.DataType;
import org.knime.core.data.DataCell;
import org.knime.core.node.NodeLogger;

public class IndigoCell extends DataCell implements IndigoValue {

    private static final NodeLogger LOGGER =
        NodeLogger.getLogger(IndigoCell.class);
    
    private static class IndigoSerializer implements DataCellSerializer<IndigoCell> {
        /**
         * {@inheritDoc}
         */
        public void serialize(final IndigoCell cell, final DataCellDataOutput out)
                throws IOException {
            try {
            	out.writeUTF(cell.getIndigoObject().molfile());
            } catch (IndigoException ex) {
                LOGGER.error("Error while serializing Indigo object", ex);
                throw new IOException(ex.getMessage());
            }
        }

        /**
         * {@inheritDoc}
         */
        public IndigoCell deserialize(final DataCellDataInput input)
                throws IOException {
            String molfile = input.readUTF();

            try {
            	return new IndigoCell(IndigoCell.indigo.loadMolecule(molfile));
            } catch (IndigoException ex) {
                LOGGER.error("Error while deserializing Indigo object", ex);
                throw new IOException(ex.getMessage());
            }
        }
    }
	
    private static final DataCellSerializer<IndigoCell> SERIALIZER =
        new IndigoSerializer();
    
    public static final DataCellSerializer<IndigoCell> getCellSerializer() {
        return SERIALIZER;
    }
    
	private static Indigo createIndigoInstance ()
	{
		return new Indigo();
	}
	
	public static Indigo indigo = createIndigoInstance();
	
	private IndigoObject _object;
	public static final DataType TYPE = DataType.getType(IndigoCell.class);
	
	public IndigoCell (IndigoObject obj)
	{
		_object = obj;
	}
	
	@Override
	public String toString() {
		return null;
	}

	@Override
	protected boolean equalsDataCell(DataCell dc) {
		return false;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public IndigoObject getIndigoObject() {
		return _object;
	}

}
