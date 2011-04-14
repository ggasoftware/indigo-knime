package com.ggasoftware.indigo.knime;

import org.eclipse.jface.preference.*;
import org.eclipse.jface.util.*;
import org.eclipse.ui.plugin.*;
import org.osgi.framework.BundleContext;
import java.util.concurrent.locks.ReentrantLock;

import com.ggasoftware.indigo.Indigo;

import static com.ggasoftware.indigo.knime.IndigoPreferenceInitializer.*;

public class IndigoPlugin extends AbstractUIPlugin
{
	private static IndigoPlugin plugin;

	public static final String PLUGIN_ID = "com.ggasoftware.indigo.knime";

   private static final ReentrantLock _lock = new ReentrantLock();
	
	private static Indigo indigo = new Indigo();
	
	public IndigoPlugin()
	{
		super();
		plugin = this;
	}

	public static Indigo getIndigo ()
	{
		return indigo;
	}
	
   public static void lock()
   {
   	_lock.lock();
   }
   
   public static void unlock()
   {
   	_lock.unlock();
   }
   
	private static int _bondLength;
	private static boolean _showImplicitHydrogens;
	private static boolean _coloring;
	private static int _molImageWidth;
	private static int _molImageHeight;

	@Override
	public void start (final BundleContext context) throws Exception
	{
		super.start(context);
		final IPreferenceStore pStore = getDefault().getPreferenceStore();

		pStore.addPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange (final PropertyChangeEvent event)
			{
				if (event.getProperty().equals(PREF_BOND_LENGTH))
					_bondLength = pStore.getInt(PREF_BOND_LENGTH);
				else if (event.getProperty().equals(PREF_SHOW_IMPLICIT_HYDROGENS))
					_showImplicitHydrogens = pStore.getBoolean(PREF_SHOW_IMPLICIT_HYDROGENS);
				else if (event.getProperty().equals(PREF_COLORING))
					_coloring =  pStore.getBoolean(PREF_COLORING);
				else if (event.getProperty().equals(PREF_MOL_IMAGE_WIDTH))
					_molImageWidth = pStore.getInt(PREF_MOL_IMAGE_WIDTH);
				else if (event.getProperty().equals(PREF_MOL_IMAGE_HEIGHT))
					_molImageHeight = pStore.getInt(PREF_MOL_IMAGE_HEIGHT);
			}
		});

		_bondLength = pStore.getInt(PREF_BOND_LENGTH);
		_showImplicitHydrogens = pStore.getBoolean(PREF_SHOW_IMPLICIT_HYDROGENS);
		_coloring =  pStore.getBoolean(PREF_COLORING);
		_molImageWidth = pStore.getInt(PREF_MOL_IMAGE_WIDTH);
		_molImageHeight = pStore.getInt(PREF_MOL_IMAGE_HEIGHT);
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
}
