package com.ggasoftware.indigo.knime;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import com.ggasoftware.indigo.*;

import org.knime.chem.types.*;
import org.knime.core.data.renderer.*;
import org.knime.core.node.NodeLogger;

public class IndigoValueRenderer extends AbstractPainterDataValueRenderer
{

	private static final long serialVersionUID = -4924582235032651081L;

	private static final NodeLogger LOGGER = NodeLogger
	      .getLogger(IndigoValueRenderer.class);

	private static final Font NO_2D_FONT = new Font(Font.SANS_SERIF,
	      Font.ITALIC, 12);

	IndigoObject _object;

	private static IndigoRenderer renderer = new IndigoRenderer(
	      IndigoCell.indigo);

	/**
	 * Instantiates new renderer.
	 */
	public IndigoValueRenderer()
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
		if (value instanceof IndigoValue)
		{ // when used directly on CDKCell
			_object = ((IndigoValue) value).getIndigoObject();
		} else if (value instanceof SmilesValue)
		{
			_object = IndigoCell.indigo.loadMolecule(((SmilesValue) value)
			      .getSmilesValue());
		} else if (value instanceof MolValue)
		{
			_object = IndigoCell.indigo.loadMolecule(((MolValue) value)
			      .getMolValue());
		} else if (value instanceof SdfValue)
		{
			_object = IndigoCell.indigo.loadMolecule(((SdfValue) value)
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

		IndigoCell.indigo.setOption("render-image-size", d.width, d.height);
		IndigoCell.indigo.setOption("render-output-format", "png");
		IndigoCell.indigo.setOption("render-coloring", true);
		byte[] buf = renderer.renderToBuffer(_object);

		try
		{
			BufferedImage img = ImageIO.read(new ByteArrayInputStream(buf));
			g.drawImage(img, 0, 0, null);
		} catch (Exception e)
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

	/**
	 * @return new Dimension(80, 80);
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize ()
	{
		return new Dimension(200, 150);
	}
}
