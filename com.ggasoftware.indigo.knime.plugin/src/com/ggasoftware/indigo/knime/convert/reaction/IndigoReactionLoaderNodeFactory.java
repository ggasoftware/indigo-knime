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

import org.knime.chem.types.RxnValue;
import org.knime.chem.types.SmartsValue;
import org.knime.chem.types.SmilesValue;
import org.knime.core.data.DataValue;
import org.knime.core.data.StringValue;

import com.ggasoftware.indigo.knime.convert.base.IndigoLoaderNodeFactory;


public class IndigoReactionLoaderNodeFactory extends
		IndigoLoaderNodeFactory<IndigoReactionLoaderNodeModel> {

	@Override
	public IndigoReactionLoaderNodeModel createNodeModelImpl() {
		return new IndigoReactionLoaderNodeModel();
	}

	@Override
	protected String getColumnLabel() {
		return "Reaction";
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Class<? extends DataValue>[] getFilterValueClasses() {
		return new Class[] { RxnValue.class, SmilesValue.class, 
				SmartsValue.class, StringValue.class };
	}
	
}
