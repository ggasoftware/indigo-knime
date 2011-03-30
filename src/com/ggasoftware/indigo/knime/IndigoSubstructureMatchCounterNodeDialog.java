package com.ggasoftware.indigo.knime;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.node.util.*;
import com.ggasoftware.indigo.knime.IndigoSubstructureMatchCounterSettings.Uniqueness;

/**
 * <code>NodeDialog</code> for the "IndigoSubstructureMatchCounter" Node.
 * 
 * @author GGA Software Services LLC
 */
public class IndigoSubstructureMatchCounterNodeDialog extends NodeDialogPane
{
	private final IndigoSubstructureMatchCounterSettings _settings = new IndigoSubstructureMatchCounterSettings();
	@SuppressWarnings("unchecked")
	private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
	      (Border) null, IndigoValue.class);
	private final JTextField _newColName = new JTextField(20);
	
	private final JComboBox _uniqueness = new JComboBox(new Object[] {Uniqueness.Atoms, Uniqueness.Bonds, Uniqueness.None});
   private final FilesHistoryPanel _fileName = new FilesHistoryPanel(
         "class com.ggasoftware.indigo.knime.IndigoSubstructureMatcherNodeDialog", "mol");

	/**
	 * New pane for configuring the IndigoSmartsMatcher node.
	 */
	protected IndigoSubstructureMatchCounterNodeDialog()
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
		p.add(new JLabel("New column"), c);
		c.gridx = 1;
		p.add(_newColName, c);
		
		c.gridy++;
		c.gridx = 0;
		p.add(new JLabel("Query file: "), c);
		c.gridx = 1;
      p.add(_fileName, c);

		c.gridy++;
		c.gridx = 0;
		p.add(new JLabel("Uniqueness"), c);
		c.gridx = 1;
		p.add(_uniqueness, c);
		
		addTab("Standard settings", p);
	}

	@Override
	protected void loadSettingsFrom (final NodeSettingsRO settings,
	      final DataTableSpec[] specs) throws NotConfigurableException
	{
		_settings.loadSettingsForDialog(settings);

		_molColumn.update(specs[0], _settings.colName);
		_newColName.setText(_settings.newColName);
		_uniqueness.setSelectedItem(_settings.uniqueness);
		_fileName.setSelectedFile(_settings.queryFileName);
	}

	@Override
	protected void saveSettingsTo (NodeSettingsWO settings)
	      throws InvalidSettingsException
	{
		_settings.colName = _molColumn.getSelectedColumn();
		_settings.newColName = _newColName.getText();
		_settings.uniqueness= ((Uniqueness)_uniqueness.getSelectedItem());
		_settings.queryFileName = _fileName.getSelectedFile();

		_settings.saveSettings(settings);
	}
}
