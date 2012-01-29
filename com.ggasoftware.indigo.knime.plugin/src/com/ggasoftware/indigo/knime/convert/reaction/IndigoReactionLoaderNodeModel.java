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

package com.ggasoftware.indigo.knime.convert.reaction;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataType;

import com.ggasoftware.indigo.Indigo;
import com.ggasoftware.indigo.knime.cell.IndigoReactionCell;
import com.ggasoftware.indigo.knime.convert.base.IndigoLoaderNodeModel;

public class IndigoReactionLoaderNodeModel extends IndigoLoaderNodeModel {

	protected IndigoReactionLoaderNodeModel() {
      super(false);
   }

   @Override
	protected DataType getDataCellType() {
		return IndigoReactionCell.TYPE;
	}

	@Override
	protected DataCell createDataCell(Indigo indigo, DataCell src) {
	   if(src.isMissing())
         return DataType.getMissingCell();
		return new IndigoReactionCell(indigo.loadReaction(src.toString()));
	}

}
