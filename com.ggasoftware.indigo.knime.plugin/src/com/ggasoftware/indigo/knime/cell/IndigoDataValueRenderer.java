/****************************************************************************
 * Copyright (C) 2011 GGA Software Services LLC
 *
 * This file may be distributed and/or modified under the terms of the
 * GNU General Public License version 3 as published by the Free Software
 * Foundation.
 *
 * This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
 * WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 ***************************************************************************/

package com.ggasoftware.indigo.knime.cell;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;

import javax.imageio.ImageIO;

import com.ggasoftware.indigo.*;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

import org.knime.chem.types.MolValue;
import org.knime.chem.types.RxnValue;
import org.knime.chem.types.SdfValue;
import org.knime.chem.types.SmartsValue;
import org.knime.chem.types.SmilesValue;
import org.knime.core.data.renderer.*;
import org.knime.core.node.NodeLogger;

@SuppressWarnings("serial")
public class IndigoDataValueRenderer extends AbstractPainterDataValueRenderer
{
   private static final NodeLogger LOGGER = NodeLogger
         .getLogger(IndigoDataValueRenderer.class);

   private static final Font NO_2D_FONT = new Font(Font.SANS_SERIF, Font.ITALIC, 12);

   IndigoObject _object = null;

   private static IndigoRenderer renderer = null;

   /**
    * Instantiates new renderer.
    */
   public IndigoDataValueRenderer()
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
      _object = null;
      
      if (value instanceof IndigoDataValue)
         _object = ((IndigoDataValue) value).getIndigoObject();
      else if (value instanceof MolValue)
    	  _object = IndigoPlugin.getIndigo().loadQueryMolecule(((MolValue)value).getMolValue());
      else if (value instanceof SdfValue)
    	  _object = IndigoPlugin.getIndigo().loadQueryMolecule(((SdfValue)value).getSdfValue());
      else if (value instanceof SmilesValue) {
         String smiles = ((SmilesValue)value).getSmilesValue();
         _object = !smiles.matches("^[^>]*>[^>]*>[^>]*$")
               ? IndigoPlugin.getIndigo().loadMolecule(smiles)
               : IndigoPlugin.getIndigo().loadReaction(smiles);
      }
      else if (value instanceof SmartsValue)
         _object = IndigoPlugin.getIndigo().loadSmarts(((SmartsValue)value).getSmartsValue());
      else if (value instanceof RxnValue)
         _object = IndigoPlugin.getIndigo().loadQueryReaction((((RxnValue)value).getRxnValue()));
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
      
      if (!IndigoPlugin.getDefault().isRenderingEnabled())
      {
         String str = "";
         
         try
         {
            str = _object.smiles();
         }
         catch (Exception e)
         {
         }
         
         g.setFont(NO_2D_FONT);
         g.drawString(str, 2, 14);
         g.drawString("rendering disabled by user preference", 2, 34);
         return;
      }
      
      Dimension d = getSize();
      byte[] buf;

      try
      {
         IndigoPlugin.lock();
         
         Indigo indigo = IndigoPlugin.getIndigo();

         if (renderer == null)
            renderer = new IndigoRenderer(indigo);

         indigo.setOption("render-image-size", d.width, d.height);
         indigo.setOption("render-output-format", "png");
         indigo.setOption("render-bond-length", IndigoPlugin.getDefault().bondLength());
         indigo.setOption("render-implicit-hydrogens-visible", IndigoPlugin.getDefault().showImplicitHydrogens());
         indigo.setOption("render-coloring", IndigoPlugin.getDefault().coloring());
         indigo.setOption("render-highlight-thickness-enabled", true);
         indigo.setOption("render-highlight-color", 0.7f, 0, 0);
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
      return "Indigo Renderer";
   }

   @Override
   public Dimension getPreferredSize ()
   {
      return new Dimension(IndigoPlugin.getDefault().molImageWidth(),
            IndigoPlugin.getDefault().molImageHeight());
   }
}
