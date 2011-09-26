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

import org.knime.core.data.DataCell;
import org.knime.core.data.DataType;

import com.ggasoftware.indigo.Indigo;
import com.ggasoftware.indigo.knime.cell.IndigoMolCell;
import com.ggasoftware.indigo.knime.convert.base.IndigoLoaderNodeModel;

public class IndigoMoleculeLoaderNodeModel extends IndigoLoaderNodeModel {

	@Override
	protected DataType getDataCellType() {
		return IndigoMolCell.TYPE;
	}

	@Override
	protected DataCell createDataCell(Indigo indigo, DataCell src) {
		return new IndigoMolCell(indigo.loadMolecule(src.toString()));
	}
}
