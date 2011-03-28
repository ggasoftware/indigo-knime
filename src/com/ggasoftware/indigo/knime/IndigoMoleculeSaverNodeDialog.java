package com.ggasoftware.indigo.knime;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.knime.chem.types.*;
import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.node.util.ColumnSelectionComboxBox;

import com.ggasoftware.indigo.knime.IndigoMoleculeSaverSettings.Format;

/**
 * <code>NodeDialog</code> for the "IndigoMoleculeSaver" Node.
 * 
 * @author GGA Software Services LLC
 */
public class IndigoMoleculeSaverNodeDialog extends NodeDialogPane
{

	@SuppressWarnings("unchecked")
	private final ColumnSelectionComboxBox m_molColumn = new ColumnSelectionComboxBox(
	      (Border) null, IndigoValue.class);

	private final JComboBox m_destFormat = new JComboBox(new Object[] {
	      Format.SDF, Format.Smiles, Format.CML });

	private final JCheckBox m_appendColumn = new JCheckBox("Append Column");

	private final JTextField m_newColName = new JTextField(20);

	private final IndigoMoleculeSaverSettings m_settings = new IndigoMoleculeSaverSettings();

	/**
	 * New pane for configuring the IndigoMoleculeSaver node.
	 */
	@SuppressWarnings("serial")
   protected IndigoMoleculeSaverNodeDialog()
	{

		JPanel p = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);
		c.gridx = 0;
		c.gridy = 0;
		p.add(new JLabel("Indigo column   "), c);
		c.gridx = 1;
		p.add(m_molColumn, c);

		c.gridy++;
		c.gridx = 0;
		p.add(m_appendColumn, c);
		c.gridx = 1;
		p.add(m_newColName, c);

		c.gridy++;
		c.gridx = 0;
		p.add(new JLabel("Destination format   "), c);
		c.gridx = 1;
		p.add(m_destFormat, c);

		m_appendColumn.addChangeListener(new ChangeListener() {
			public void stateChanged (final ChangeEvent e)
			{
				if (m_appendColumn.isSelected())
				{
					m_newColName.setEnabled(true);
					if ("".equals(m_newColName.getText()))
					{
						m_newColName.setText(m_molColumn.getSelectedColumn()
						      + " (saved)");
					}
				} else
				{
					m_newColName.setEnabled(false);
				}
			}
		});
		m_newColName.setEnabled(m_appendColumn.isSelected());

		m_destFormat.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent (final JList list,
			      final Object value, final int index, final boolean isSelected,
			      final boolean cellHasFocus)
			{
				super.getListCellRendererComponent(list, value, index, isSelected,
				      cellHasFocus);
				if (value == Format.SDF)
				{
					setIcon(SdfValue.UTILITY.getIcon());
					setText("SDF");
				} else if (value == Format.Smiles)
				{
					setIcon(SmilesValue.UTILITY.getIcon());
					setText("Smiles");
				} else if (value == Format.CML)
				{
					setIcon(CMLValue.UTILITY.getIcon());
					setText("CML");
				} else
				{
					setIcon(null);
					setText("");
				}
				return this;
			}
		});

		addTab("Standard settings", p);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadSettingsFrom (final NodeSettingsRO settings,
	      final DataTableSpec[] specs) throws NotConfigurableException
	{
		m_settings.loadSettingsForDialog(settings);

		m_molColumn.update(specs[0], m_settings.colName);
		m_appendColumn.setSelected(!m_settings.replaceColumn);
		m_newColName.setEnabled(!m_settings.replaceColumn);
		m_newColName.setText(m_settings.newColName);
		m_destFormat.setSelectedItem(m_settings.destFormat);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo (final NodeSettingsWO settings)
	      throws InvalidSettingsException
	{
		m_settings.colName = m_molColumn.getSelectedColumn();
		m_settings.replaceColumn = !m_appendColumn.isSelected();
		m_settings.newColName = m_newColName.getText();
		m_settings.destFormat = ((Format) m_destFormat.getSelectedItem());
		m_settings.saveSettings(settings);
	}
}
