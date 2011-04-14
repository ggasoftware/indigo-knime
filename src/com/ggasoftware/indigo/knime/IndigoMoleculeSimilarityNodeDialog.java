package com.ggasoftware.indigo.knime;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.knime.core.data.*;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

import com.ggasoftware.indigo.knime.IndigoMoleculeSimilaritySettings.Metric;

/**
 * <code>NodeDialog</code> for the "IndigoMoleculeSimilarity" Node.
 * 
 * @author GGA Software Services LLC
 */
public class IndigoMoleculeSimilarityNodeDialog extends NodeDialogPane
{
	@SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
	      (Border) null, IndigoMolValue.class);
	private final JRadioButton _buttonSmiles = new JRadioButton("SMILES", true);
	private final JRadioButton _buttonFile = new JRadioButton("Load from file");
	private final ButtonGroup  _buttonGroup = new ButtonGroup();
	private final JTextField _smiles = new JTextField(20);
   private final FilesHistoryPanel _fileName = new FilesHistoryPanel(
         "class com.ggasoftware.indigo.knime.IndigoMoleculeSimilarityNodeDialog", "mol", "smi", "smiles");
	private final JTextField _newColumn = new JTextField(20);
	private final JComboBox _metrics = new JComboBox(new Object[] {Metric.Tanimoto, Metric.EuclidSub, Metric.Tversky});
	JLabel _alphaLabel = new JLabel("alpha:");
	JLabel _betaLabel = new JLabel("beta:");
	JFormattedTextField _alpha = new JFormattedTextField(NumberFormat.getNumberInstance());
	JFormattedTextField _beta = new JFormattedTextField(NumberFormat.getNumberInstance());
	JPanel _metricsPanel = new JPanel();

   private final IndigoMoleculeSimilaritySettings _settings = new IndigoMoleculeSimilaritySettings();
   
	protected IndigoMoleculeSimilarityNodeDialog()
	{
		_buttonGroup.add(_buttonSmiles);
		_buttonGroup.add(_buttonFile);
		
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
		p.add(_buttonSmiles, c);
		c.gridx = 1;
		p.add(_smiles, c);

		c.gridy++;

		c.gridx = 0;
		p.add(_buttonFile, c);
		c.gridx = 1;
		p.add(_fileName, c);
		_fileName.setEnabled(false);

		c.gridy++;
		c.gridx = 0;
		p.add(new JLabel("New column"), c);
		c.gridx = 1;
		p.add(_newColumn, c);
		
		_alpha.setColumns(3);
		_beta.setColumns(3);
		
		_metricsPanel.add(_metrics);
		_metricsPanel.add(_alphaLabel);
		_metricsPanel.add(_alpha);
		_metricsPanel.add(_betaLabel);
		_metricsPanel.add(_beta);
		
		c.gridy++;
		c.gridx = 0;
		p.add(new JLabel("Metric"), c);
		c.gridx = 1;
		p.add(_metricsPanel, c);
		
		addTab("Standard settings", p);
		
		_buttonSmiles.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				boolean b = _buttonSmiles.isSelected();
				
				_smiles.setEnabled(b);
				_fileName.setEnabled(!b);
			}
		});
		
		_metrics.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed (ActionEvent arg0)
			{
				if (_metrics.getSelectedItem() == Metric.Tversky)
				{
					_alpha.setVisible(true);
					_beta.setVisible(true);
					_alphaLabel.setVisible(true);
					_betaLabel.setVisible(true);
				}
				else
				{
					_alpha.setVisible(false);
					_beta.setVisible(false);
					_alphaLabel.setVisible(false);
					_betaLabel.setVisible(false);
				}
			}
		});
		
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void loadSettingsFrom (final NodeSettingsRO settings,
	      final DataTableSpec[] specs) throws NotConfigurableException
	{
		_settings.loadSettingsForDialog(settings);
		if (_settings.loadFromFile)
		{
			_fileName.setEnabled(true);
			_smiles.setText("");
			_smiles.setEnabled(false);
			_fileName.setSelectedFile(_settings.fileName);
			_buttonFile.setSelected(true);
		}
		else
		{
			_fileName.setEnabled(false);
			_fileName.setSelectedFile("");
			_smiles.setText(_settings.smiles);
			_smiles.setEnabled(true);
			_buttonSmiles.setSelected(true);
		}
		_metrics.setSelectedItem(_settings.metric);
		_newColumn.setText(_settings.newColName);
		_molColumn.update(specs[0], _settings.colName);
		_alpha.setValue(_settings.tverskyAlpha);
		_beta.setValue(_settings.tverskyBeta);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void saveSettingsTo (final NodeSettingsWO settings)
	      throws InvalidSettingsException
	{
		_settings.loadFromFile = _buttonFile.isSelected();
		_settings.smiles = _smiles.getText();
		_settings.fileName = _fileName.getSelectedFile();
		_settings.metric = (Metric)_metrics.getSelectedItem();
		_settings.newColName = _newColumn.getText();
		_settings.colName = _molColumn.getSelectedColumn();
		_settings.tverskyAlpha = ((Double)_alpha.getValue()).floatValue();
		_settings.tverskyBeta = ((Double)_beta.getValue()).floatValue();
		
		_settings.saveSettings(settings);
	}	
}
