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

package com.ggasoftware.indigo.knime.convert.molecule;

import com.ggasoftware.indigo.knime.convert.base.IndigoSaverNodeFactory;
import com.ggasoftware.indigo.knime.convert.base.IndigoSaverSettings.Format;

public class IndigoQueryMoleculeSaverNodeFactory extends
		IndigoSaverNodeFactory<IndigoQueryMoleculeSaverNodeModel> {

	@Override
	protected Object[] getFormats() {
		return new Object[] { Format.Mol, Format.SDF, Format.Smiles };
	}

	@Override
	public IndigoQueryMoleculeSaverNodeModel createNodeModel() {
		return new IndigoQueryMoleculeSaverNodeModel();
	}
}
