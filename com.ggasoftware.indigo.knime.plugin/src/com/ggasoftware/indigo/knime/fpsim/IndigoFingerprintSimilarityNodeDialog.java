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

package com.ggasoftware.indigo.knime.fpsim;

import java.awt.*;
import java.awt.event.*;
import java.text.*;

import javax.swing.*;
import javax.swing.border.Border;

import org.knime.core.data.*;
import org.knime.core.data.vector.bitvector.BitVectorValue;
import org.knime.core.node.*;
import org.knime.core.node.util.*;

import com.ggasoftware.indigo.knime.fpsim.IndigoFingerprintSimilaritySettings.Aggregation;
import com.ggasoftware.indigo.knime.fpsim.IndigoFingerprintSimilaritySettings.Metric;

public class IndigoFingerprintSimilarityNodeDialog extends NodeDialogPane
{
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _fpColumn = new ColumnSelectionComboxBox(
         (Border) null, BitVectorValue.class);
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _fpColumn2 = new ColumnSelectionComboxBox(
         (Border) null, BitVectorValue.class);
   private final JTextField _newColumn = new JTextField(20);
   private final JComboBox _metrics = new JComboBox(new Object[] {
         Metric.Tanimoto, Metric.EuclidSub, Metric.Tversky });
   private final JComboBox _aggregation = new JComboBox(new Object[] {
         Aggregation.Average, Aggregation.Minimum, Aggregation.Maximum});
   JLabel _alphaLabel = new JLabel("alpha:");
   JLabel _betaLabel = new JLabel("beta:");
   JFormattedTextField _alpha = new JFormattedTextField(
         NumberFormat.getNumberInstance());
   JFormattedTextField _beta = new JFormattedTextField(
         NumberFormat.getNumberInstance());
   JPanel _metricsPanel = new JPanel();

   private final IndigoFingerprintSimilaritySettings _settings = new IndigoFingerprintSimilaritySettings();

   protected IndigoFingerprintSimilarityNodeDialog()
   {
      JPanel p = new JPanel(new GridBagLayout());

      GridBagConstraints c = new GridBagConstraints();

      c.anchor = GridBagConstraints.WEST;
      c.insets = new Insets(2, 2, 2, 2);

      c.gridy = 0;
      c.gridx = 0;
      p.add(new JLabel("Column with fingerprints"), c);
      c.gridx = 1;
      p.add(_fpColumn, c);

      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Column with single template fingerprint"), c);
      c.gridx = 1;
      p.add(_fpColumn2, c);
     
      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("New column"), c);
      c.gridx = 1;
      p.add(_newColumn, c);

      _alpha.setColumns(3);
      _beta.setColumns(3);

      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Metric"), c);
      c.gridx = 1;
      p.add(_metrics, c);
      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel(), c);
      c.gridx = 1;
      _metricsPanel.add(_alphaLabel);
      _metricsPanel.add(_alpha);
      _metricsPanel.add(_betaLabel);
      _metricsPanel.add(_beta);
      p.add(_metricsPanel, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("Aggregation type"), c);
      c.gridx = 1;
      p.add(_aggregation, c);

      addTab("Standard settings", p);

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
      _metrics.setSelectedItem(_settings.metric);
      _aggregation.setSelectedItem(_settings.aggregation);
      _newColumn.setText(_settings.newColName);
      _fpColumn.update(specs[0], _settings.colName);
      _fpColumn2.update(specs[1], _settings.colName2);
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
      _settings.metric = (Metric) _metrics.getSelectedItem();
      _settings.aggregation = (Aggregation) _aggregation.getSelectedItem();
      _settings.newColName = _newColumn.getText();
      _settings.colName = _fpColumn.getSelectedColumn();
      _settings.colName2 = _fpColumn2.getSelectedColumn();
      _settings.tverskyAlpha = ((Number)_alpha.getValue()).floatValue();
      _settings.tverskyBeta = ((Number)_beta.getValue()).floatValue();

      _settings.saveSettings(settings);
   }
}
