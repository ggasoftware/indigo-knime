package com.ggasoftware.indigo.knime;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.*;

public class IndigoPreferencePage extends FieldEditorPreferencePage implements
      IWorkbenchPreferencePage
{

	public IndigoPreferencePage ()
	{
      super(GRID);

      setPreferenceStore(IndigoPlugin.getDefault().getPreferenceStore());
      setDescription("KNIME Indigo preferences");
	}
	
	@Override
	public void init (IWorkbench workbench)
	{
	}

	@Override
	protected void createFieldEditors ()
	{
      Composite parent = getFieldEditorParent();
      
      addField(new BooleanFieldEditor(
            IndigoPreferenceInitializer.PREF_COLORING,
            "Colored rendering", parent));

      addField(new BooleanFieldEditor(
            IndigoPreferenceInitializer.PREF_SHOW_IMPLICIT_HYDROGENS,
            "Show implicit hydrogens", parent));

      addField(new IntegerFieldEditor(
      		IndigoPreferenceInitializer.PREF_BOND_LENGTH,
      		"Desired bond length in pixels", parent));
      
      addField(new IntegerFieldEditor(
      		IndigoPreferenceInitializer.PREF_MOL_IMAGE_WIDTH,
      		"Molecule image width in pixels", parent));
      
      addField(new IntegerFieldEditor(
      		IndigoPreferenceInitializer.PREF_MOL_IMAGE_HEIGHT,
      		"Molecule image height in pixels", parent));
	}
}
