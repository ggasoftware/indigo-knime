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

import org.knime.chem.types.MolValue;
import org.knime.chem.types.SdfValue;
import org.knime.chem.types.SmartsValue;
import org.knime.chem.types.SmilesValue;
import org.knime.core.data.DataValue;
import org.knime.core.data.StringValue;

import com.ggasoftware.indigo.knime.convert.base.IndigoLoaderNodeFactory;


public class IndigoQueryMoleculeLoaderNodeFactory extends
		IndigoLoaderNodeFactory<IndigoQueryMoleculeLoaderNodeModel> {

	@Override
	public IndigoQueryMoleculeLoaderNodeModel createNodeModelImpl() {
		return new IndigoQueryMoleculeLoaderNodeModel();
	}

	@Override
	protected String getColumnLabel() {
		return "Query Molecule";
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Class<? extends DataValue>[] getFilterValueClasses() {
		return new Class[] { SdfValue.class, MolValue.class, 
				SmilesValue.class, SmartsValue.class, StringValue.class };
	}

   @Override
   protected boolean isQuery() {
      return true;
   }
	
}
