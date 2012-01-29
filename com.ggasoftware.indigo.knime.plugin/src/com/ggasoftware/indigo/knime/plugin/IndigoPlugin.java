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

package com.ggasoftware.indigo.knime.plugin;

import static com.ggasoftware.indigo.knime.plugin.IndigoPreferenceInitializer.PREF_BOND_LENGTH;
import static com.ggasoftware.indigo.knime.plugin.IndigoPreferenceInitializer.PREF_COLORING;
import static com.ggasoftware.indigo.knime.plugin.IndigoPreferenceInitializer.PREF_ENABLE_RENDERER;
import static com.ggasoftware.indigo.knime.plugin.IndigoPreferenceInitializer.PREF_MOL_IMAGE_HEIGHT;
import static com.ggasoftware.indigo.knime.plugin.IndigoPreferenceInitializer.PREF_MOL_IMAGE_WIDTH;
import static com.ggasoftware.indigo.knime.plugin.IndigoPreferenceInitializer.PREF_SHOW_IMPLICIT_HYDROGENS;

import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.knime.chem.types.CMLValue;
import org.knime.chem.types.MolValue;
import org.knime.chem.types.RxnValue;
import org.knime.chem.types.SdfValue;
import org.knime.chem.types.SmartsValue;
import org.knime.chem.types.SmilesValue;
import org.osgi.framework.BundleContext;

import com.ggasoftware.indigo.Indigo;
import com.ggasoftware.indigo.IndigoInchi;
import com.ggasoftware.indigo.knime.cell.IndigoDataValueRenderer;

public class IndigoPlugin extends AbstractUIPlugin
{
   private static IndigoPlugin plugin;

   public static final String PLUGIN_ID = "com.ggasoftware.indigo.knime";

   private static final ReentrantLock _lock = new ReentrantLock();

   private static Indigo indigo = new Indigo();
   private static IndigoInchi indigo_inchi;

   public IndigoPlugin()
   {
      super();
      plugin = this;
   }

   public static Indigo getIndigo ()
   {
      return indigo;
   }
   
   public static IndigoInchi getIndigoInchi ()
   {
      if (indigo_inchi == null)
         indigo_inchi = new IndigoInchi(indigo);
      return indigo_inchi; 
   }

   public static void lock ()
   {
      _lock.lock();
   }

   public static void unlock ()
   {
      _lock.unlock();
   }

   private static int _bondLength;
   private static boolean _showImplicitHydrogens;
   private static boolean _coloring;
   private static int _molImageWidth;
   private static int _molImageHeight;
   private static boolean _enableRendering;

   @Override
   public void start (final BundleContext context) throws Exception
   {
      super.start(context);
      final IPreferenceStore pStore = getDefault().getPreferenceStore();

      pStore.addPropertyChangeListener(new IPropertyChangeListener()
      {
         public void propertyChange (final PropertyChangeEvent event)
         {
            if (event.getProperty().equals(PREF_BOND_LENGTH))
               _bondLength = pStore.getInt(PREF_BOND_LENGTH);
            else if (event.getProperty().equals(PREF_SHOW_IMPLICIT_HYDROGENS))
               _showImplicitHydrogens = pStore
                     .getBoolean(PREF_SHOW_IMPLICIT_HYDROGENS);
            else if (event.getProperty().equals(PREF_COLORING))
               _coloring = pStore.getBoolean(PREF_COLORING);
            else if (event.getProperty().equals(PREF_MOL_IMAGE_WIDTH))
               _molImageWidth = pStore.getInt(PREF_MOL_IMAGE_WIDTH);
            else if (event.getProperty().equals(PREF_MOL_IMAGE_HEIGHT))
               _molImageHeight = pStore.getInt(PREF_MOL_IMAGE_HEIGHT);
            else if (event.getProperty().equals(PREF_ENABLE_RENDERER))
               _enableRendering = pStore.getBoolean(PREF_ENABLE_RENDERER);
         }
      });

      _enableRendering = pStore.getBoolean(PREF_ENABLE_RENDERER);
      _bondLength = pStore.getInt(PREF_BOND_LENGTH);
      _showImplicitHydrogens = pStore.getBoolean(PREF_SHOW_IMPLICIT_HYDROGENS);
      _coloring = pStore.getBoolean(PREF_COLORING);
      _molImageWidth = pStore.getInt(PREF_MOL_IMAGE_WIDTH);
      _molImageHeight = pStore.getInt(PREF_MOL_IMAGE_HEIGHT);
      
      MolValue.UTILITY.addRenderer(new IndigoDataValueRenderer(), false);
      SdfValue.UTILITY.addRenderer(new IndigoDataValueRenderer(), false);
      SmilesValue.UTILITY.addRenderer(new IndigoDataValueRenderer(), false);
      SmartsValue.UTILITY.addRenderer(new IndigoDataValueRenderer(), false);
      RxnValue.UTILITY.addRenderer(new IndigoDataValueRenderer(), false);
      CMLValue.UTILITY.addRenderer(new IndigoDataValueRenderer(), false);
   }

   @Override
   public void stop (final BundleContext context) throws Exception
   {
      super.stop(context);
      plugin = null;
   }

   public static IndigoPlugin getDefault ()
   {
      return plugin;
   }

   public float bondLength ()
   {
      return _bondLength;
   }

   public boolean showImplicitHydrogens ()
   {
      return _showImplicitHydrogens;
   }

   public boolean coloring ()
   {
      return _coloring;
   }

   public int molImageWidth ()
   {
      return _molImageWidth;
   }

   public int molImageHeight ()
   {
      return _molImageHeight;
   }
   
   public boolean isRenderingEnabled ()
   {
      return _enableRendering;
   }
}
