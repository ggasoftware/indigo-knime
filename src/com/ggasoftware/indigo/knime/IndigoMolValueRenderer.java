package com.ggasoftware.indigo.knime;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import com.ggasoftware.indigo.*;

import org.knime.chem.types.*;
import org.knime.core.data.renderer.*;
import org.knime.core.node.NodeLogger;

public class IndigoMolValueRenderer extends AbstractPainterDataValueRenderer
{
	private static final long serialVersionUID = -4924582235032651081L;

	private static final NodeLogger LOGGER = NodeLogger
	      .getLogger(IndigoMolValueRenderer.class);

	private static final Font NO_2D_FONT = new Font(Font.SANS_SERIF,
	      Font.ITALIC, 12);

	IndigoObject _object;

	private static IndigoRenderer renderer = new IndigoRenderer(IndigoPlugin.getIndigo());

	/**
	 * Instantiates new renderer.
	 */
	public IndigoMolValueRenderer()
	{
	}

	/**
	 * Sets the string object for the cell being rendered.
	 * 
	 * @param value
	 *           the string value for this cell; if value is <code>null</code> it
	 *           sets the text value to an empty string
	 * @see javax.swing.JLabel#setText
	 * 
	 */
	@Override
	protected void setValue (final Object value)
	{
		if (value instanceof IndigoMolValue)
		{ // when used directly on CDKCell
			_object = ((IndigoMolValue) value).getIndigoObject();
		}
		else if (value instanceof SmilesValue)
		{
			_object = IndigoPlugin.getIndigo().loadMolecule(((SmilesValue) value)
			      .getSmilesValue());
		}
		else if (value instanceof MolValue)
		{
			_object = IndigoPlugin.getIndigo().loadMolecule(((MolValue) value)
			      .getMolValue());
		}
		else if (value instanceof SdfValue)
		{
			_object = IndigoPlugin.getIndigo().loadMolecule(((SdfValue) value)
			      .getSdfValue());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintComponent (final Graphics g)
	{
		super.paintComponent(g);
		if (_object == null)
		{
			g.setFont(NO_2D_FONT);
			g.drawString("?", 2, 14);
			return;
		}
		Dimension d = getSize();
		byte[] buf;

		try
		{
			IndigoPlugin.lock();
		
			Indigo indigo = IndigoPlugin.getIndigo();
			
			indigo.setOption("render-image-size", d.width, d.height);
			indigo.setOption("render-output-format", "png");
			indigo.setOption("render-bond-length", IndigoPlugin.getDefault().bondLength());
			indigo.setOption("render-implicit-hydrogens-visible", IndigoPlugin.getDefault().showImplicitHydrogens());
			indigo.setOption("render-coloring", IndigoPlugin.getDefault().coloring());
			buf = renderer.renderToBuffer(_object);
		}
		finally
		{
			IndigoPlugin.unlock();
		}

		try
		{
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(buf));
			g.drawImage(img, 0, 0, null);
		}
		catch (Exception e)
		{
			LOGGER.debug(e.getMessage(), e);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription ()
	{
		return "Indigo Molecule";
	}

	@Override
	public Dimension getPreferredSize ()
	{
		return new Dimension(IndigoPlugin.getDefault().molImageWidth(), IndigoPlugin.getDefault().molImageHeight());
	}
}
