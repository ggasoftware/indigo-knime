package com.ggasoftware.indigo.knime;

import org.eclipse.jface.preference.*;
import org.eclipse.jface.util.*;
import org.eclipse.ui.plugin.*;
import org.osgi.framework.BundleContext;

import static com.ggasoftware.indigo.knime.IndigoPreferenceInitializer.*;

public class IndigoPlugin extends AbstractUIPlugin
{
	private static IndigoPlugin plugin;

	public static final String PLUGIN_ID = "com.ggasoftware.indigo.knime";

	public IndigoPlugin()
	{
		super();
		plugin = this;
	}

	private static int bondLength;
	private static boolean showImplicitHydrogens;
	private static boolean coloring;
	private static int molImageWidth;
	private static int molImageHeight;

	@Override
	public void start (final BundleContext context) throws Exception
	{
		super.start(context);
		final IPreferenceStore pStore = getDefault().getPreferenceStore();

		pStore.addPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange (final PropertyChangeEvent event)
			{
				if (event.getProperty().equals(PREF_BOND_LENGTH))
					bondLength = pStore.getInt(PREF_BOND_LENGTH);
				else if (event.getProperty().equals(PREF_SHOW_IMPLICIT_HYDROGENS))
					showImplicitHydrogens = pStore.getBoolean(PREF_SHOW_IMPLICIT_HYDROGENS);
				else if (event.getProperty().equals(PREF_COLORING))
					coloring =  pStore.getBoolean(PREF_COLORING);
				else if (event.getProperty().equals(PREF_MOL_IMAGE_WIDTH))
					molImageWidth = pStore.getInt(PREF_MOL_IMAGE_WIDTH);
				else if (event.getProperty().equals(PREF_MOL_IMAGE_HEIGHT))
					molImageHeight = pStore.getInt(PREF_MOL_IMAGE_HEIGHT);
			}
		});

		bondLength = pStore.getInt(PREF_BOND_LENGTH);
		showImplicitHydrogens = pStore.getBoolean(PREF_SHOW_IMPLICIT_HYDROGENS);
		coloring =  pStore.getBoolean(PREF_COLORING);
		molImageWidth = pStore.getInt(PREF_MOL_IMAGE_WIDTH);
		molImageHeight = pStore.getInt(PREF_MOL_IMAGE_HEIGHT);
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
}
