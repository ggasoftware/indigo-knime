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
import org.knime.core.data.def.StringCell;

import com.ggasoftware.indigo.Indigo;
import com.ggasoftware.indigo.IndigoInchi;
import com.ggasoftware.indigo.knime.cell.IndigoMolCell;
import com.ggasoftware.indigo.knime.convert.base.IndigoLoaderNodeModel;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

public class IndigoMoleculeLoaderNodeModel extends IndigoLoaderNodeModel {

	protected IndigoMoleculeLoaderNodeModel() {
      super(false);
   }

   @Override
	protected DataType getDataCellType() {
		return IndigoMolCell.TYPE;
	}

	@Override
	protected DataCell createDataCell(Indigo indigo, DataCell src) {
	   if(src.isMissing())
	      return DataType.getMissingCell();
      String value = src.toString();
	   if (src.getType().equals(StringCell.TYPE)) {
   	   if (value.startsWith("InChI="))
   	   {
   	      IndigoInchi inchi = IndigoPlugin.getIndigoInchi();
   	      return new IndigoMolCell(inchi.loadMolecule(value));
   	   }
	   }
      return new IndigoMolCell(indigo.loadMolecule(value));
	}
}
