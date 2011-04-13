package com.ggasoftware.indigo.knime;

import org.eclipse.core.runtime.preferences.*;
import org.eclipse.jface.preference.*;

public class IndigoPreferenceInitializer extends AbstractPreferenceInitializer
{
   public static final String PREF_BOND_LENGTH = "knime.indigo.bondLength";
   public static final String PREF_SHOW_IMPLICIT_HYDROGENS = "knime.indigo.implicitHydrogensVisible";
   public static final String PREF_COLORING = "knime.indigo.coloring";
   public static final String PREF_MOL_IMAGE_WIDTH = "knime.indigo.moleculeImageWidth";
   public static final String PREF_MOL_IMAGE_HEIGHT = "knime.indigo.moleculeImageHeight";

	@Override
	public void initializeDefaultPreferences ()
	{
		IPreferenceStore store = IndigoPlugin.getDefault().getPreferenceStore();
		
		store.setDefault(PREF_BOND_LENGTH, 40);
		store.setDefault(PREF_SHOW_IMPLICIT_HYDROGENS, true);
		store.setDefault(PREF_COLORING, true);
		store.setDefault(PREF_MOL_IMAGE_WIDTH, 250);
		store.setDefault(PREF_MOL_IMAGE_HEIGHT, 150);
	}
}
