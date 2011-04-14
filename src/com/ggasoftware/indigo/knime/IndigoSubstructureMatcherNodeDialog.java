package com.ggasoftware.indigo.knime;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.node.util.*;
/**
 * <code>NodeDialog</code> for the "IndigoSmartsMatcher" Node.
 * 
 * @author GGA Software Services LLC
 */
public class IndigoSubstructureMatcherNodeDialog extends NodeDialogPane
{
	private final IndigoSubstructureMatcherSettings _settings = new IndigoSubstructureMatcherSettings();
	@SuppressWarnings("unchecked")
	private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
	      (Border) null, IndigoMolValue.class);
	
	private final JRadioButton _buttonSmarts= new JRadioButton("SMARTS", true);
	private final JRadioButton _buttonFile = new JRadioButton("Query file");
	private final ButtonGroup  _buttonGroup = new ButtonGroup();
   private final FilesHistoryPanel _fileName = new FilesHistoryPanel(
         "class com.ggasoftware.indigo.knime.IndigoSubstructureMatcherNodeDialog", "mol", "smi", "smiles");
	private final JTextField _smarts = new JTextField(20);

	/**
	 * New pane for configuring the IndigoSmartsMatcher node.
	 */
	protected IndigoSubstructureMatcherNodeDialog()
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

		_buttonGroup.add(_buttonFile);
		_buttonGroup.add(_buttonSmarts);
		
		c.gridy++;
		c.gridx = 0;
		p.add(_buttonSmarts, c);
		c.gridx = 1;
      p.add(_smarts, c);
      
		c.gridy++;
		c.gridx = 0;
		p.add(_buttonFile, c);
		c.gridx = 1;
      p.add(_fileName, c);

		_buttonSmarts.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				boolean b = _buttonSmarts.isSelected();
				
				_smarts.setEnabled(b);
				_fileName.setEnabled(!b);
			}
		});

		_buttonSmarts.setSelected(true);
		_fileName.setEnabled(false);
		
		addTab("Standard settings", p);
	}

	@Override
	protected void loadSettingsFrom (final NodeSettingsRO settings,
	      final DataTableSpec[] specs) throws NotConfigurableException
	{
		_settings.loadSettingsForDialog(settings);

		_fileName.setSelectedFile(_settings.queryFileName);
		_molColumn.update(specs[0], _settings.colName);
		_smarts.setText(_settings.smarts);
		_fileName.setSelectedFile(_settings.queryFileName);
		if (_settings.loadFromFile)
			_buttonFile.setSelected(true);
		else
			_buttonSmarts.setSelected(true);
	}

	@Override
	protected void saveSettingsTo (NodeSettingsWO settings)
	      throws InvalidSettingsException
	{
		_settings.queryFileName = _fileName.getSelectedFile();
		_settings.colName = _molColumn.getSelectedColumn();
		_settings.smarts = _smarts.getText();
		_settings.loadFromFile = _buttonFile.isSelected();
		_settings.smarts = _smarts.getText();

		_settings.saveSettings(settings);
	}
}
