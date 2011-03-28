package com.ggasoftware.indigo.knime;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * <code>NodeDialog</code> for the "IndigoAromatizer" Node.
 * 
 * @author GGA Software Services LLC
 */
public class IndigoSimpleNodeDialog extends NodeDialogPane
{

	@SuppressWarnings("unchecked")
	private final ColumnSelectionComboxBox m_molColumn = new ColumnSelectionComboxBox(
	      (Border) null, IndigoValue.class);

	private final JCheckBox m_appendColumn = new JCheckBox("Append Column");

	private final JTextField m_newColName = new JTextField(20);

	private final IndigoSimpleSettings m_settings = new IndigoSimpleSettings();
	
	private final String _desc;

	protected IndigoSimpleNodeDialog (String desc)
	{
		super();
		
		_desc = desc; 

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

		m_appendColumn.addChangeListener(new ChangeListener() {
			public void stateChanged (final ChangeEvent e)
			{
				if (m_appendColumn.isSelected())
				{
					m_newColName.setEnabled(true);
					if ("".equals(m_newColName.getText()))
					{
						m_newColName.setText(m_molColumn.getSelectedColumn()
						      + " (" + _desc + ")");
					}
				} else
				{
					m_newColName.setEnabled(false);
				}
			}
		});
		m_newColName.setEnabled(m_appendColumn.isSelected());

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
		m_settings.saveSettings(settings);
	}
}
