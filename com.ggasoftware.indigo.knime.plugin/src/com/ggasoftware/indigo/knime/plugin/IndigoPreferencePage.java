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

import org.eclipse.jface.preference.*;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.*;

public class IndigoPreferencePage extends FieldEditorPreferencePage implements
      IWorkbenchPreferencePage
{

   public IndigoPreferencePage()
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
            IndigoPreferenceInitializer.PREF_COLORING, "Colored rendering",
            parent));

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
