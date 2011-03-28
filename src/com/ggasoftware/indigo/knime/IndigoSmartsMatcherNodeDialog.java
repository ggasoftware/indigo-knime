package com.ggasoftware.indigo.knime;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

/**
 * <code>NodeDialog</code> for the "IndigoSmartsMatcher" Node.
 * 
 * @author GGA Software Services LLC
 */
public class IndigoSmartsMatcherNodeDialog extends NodeDialogPane
{

	private final JTextField _smarts = new JTextField(20);
	private final IndigoSmartsMatcherSettings _settings = new IndigoSmartsMatcherSettings();
	@SuppressWarnings("unchecked")
	private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
	      (Border) null, IndigoValue.class);

	/**
	 * New pane for configuring the IndigoSmartsMatcher node.
	 */
	protected IndigoSmartsMatcherNodeDialog()
	{
		super();

		JPanel p = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();

		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(2, 2, 2, 2);
		c.gridy = 0;
		c.gridx = 0;
		p.add(new JLabel("Molecule column"), c);
		c.gridx = 1;
		p.add(_molColumn, c);

		c.gridy++;

		c.gridx = 0;
		p.add(new JLabel("SMARTS"), c);
		c.gridx = 1;
		p.add(_smarts, c);

		addTab("Standard settings", p);
	}

	@Override
	protected void loadSettingsFrom (final NodeSettingsRO settings,
	      final DataTableSpec[] specs) throws NotConfigurableException
	{
		_settings.loadSettingsForDialog(settings);

		_smarts.setText(_settings.smarts);
		_molColumn.update(specs[0], _settings.colName);
	}

	@Override
	protected void saveSettingsTo (NodeSettingsWO settings)
	      throws InvalidSettingsException
	{
		_settings.smarts = _smarts.getText();
		_settings.colName = _molColumn.getSelectedColumn();

		_settings.saveSettings(settings);
	}
}
