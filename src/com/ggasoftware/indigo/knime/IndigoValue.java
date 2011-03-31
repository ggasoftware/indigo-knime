package com.ggasoftware.indigo.knime;

import javax.swing.Icon;

import org.knime.core.data.*;
import org.knime.core.data.renderer.*;

import com.ggasoftware.indigo.IndigoObject;

public interface IndigoValue extends DataValue
{

	IndigoObject getIndigoObject ();

	public static final UtilityFactory UTILITY = new IndigoUtilityFactory();

	/** Implementations of the meta information of this value class. */
	public static class IndigoUtilityFactory extends UtilityFactory
	{
		/** Singleton icon to be used to display this cell type. */
		private static final Icon ICON = loadIcon(
		      com.ggasoftware.indigo.knime.IndigoValue.class, "/indigo.png");

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
		protected DataValueRendererFamily getRendererFamily (
		      final DataColumnSpec spec)
		{
			return new DefaultDataValueRendererFamily(new IndigoValueRenderer(),
			      new MultiLineStringValueRenderer("String"));
		}
	}
}
