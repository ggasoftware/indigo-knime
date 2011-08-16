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

package com.ggasoftware.indigo.knime.convert;

import org.knime.chem.types.RxnValue;
import org.knime.core.data.DataValue;


public class IndigoQueryReactionLoaderNodeFactory extends
		IndigoLoaderNodeFactory<IndigoQueryReactionLoaderNodeModel> {

	@Override
	public IndigoQueryReactionLoaderNodeModel createNodeModel() {
		return new IndigoQueryReactionLoaderNodeModel();
	}

	@Override
	protected String getColumnLabel() {
		return "Query Reaction";
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Class<? extends DataValue>[] getFilterValueClasses() {
		return new Class[] { RxnValue.class };
	}
	
}
