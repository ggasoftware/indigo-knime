package com.ggasoftware.indigo.knime;

import javax.swing.Icon;

import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataValue;
import org.knime.core.data.DataValueComparator;
import org.knime.core.data.renderer.DataValueRendererFamily;
import org.knime.core.data.renderer.DefaultDataValueRendererFamily;
import org.knime.core.data.renderer.MultiLineStringValueRenderer;

import com.ggasoftware.indigo.IndigoObject;

public interface IndigoValue extends DataValue
{

	IndigoObject getIndigoObject ();

	public static final UtilityFactory UTILITY = new IndigoUtilityFactory();

	/** Implementations of the meta information of this value class. */
	public static class IndigoUtilityFactory extends UtilityFactory
	{
		/** Singleton icon to be used to display this cell type. */
		private static final Icon ICON =
		 loadIcon(com.ggasoftware.indigo.knime.IndigoValue.class, "/indigo.png");

		private static final DataValueComparator COMPARATOR = new DataValueComparator() {
			@Override
			protected int compareDataValues (final DataValue v1, final DataValue v2)
			{
				int atomCount1 = ((IndigoValue) v1).getIndigoObject().countAtoms();
				int atomCount2 = ((IndigoValue) v2).getIndigoObject().countAtoms();
				return atomCount1 - atomCount2;
			}
		};

		/** Only subclasses are allowed to instantiate this class. */
		protected IndigoUtilityFactory()
		{
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Icon getIcon ()
		{
			return ICON;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected DataValueComparator getComparator ()
		{
			return COMPARATOR;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected DataValueRendererFamily getRendererFamily (
		      final DataColumnSpec spec)
		{
			return new DefaultDataValueRendererFamily(new IndigoValueRenderer(),
			      new MultiLineStringValueRenderer("String"));
		}
	}

}
