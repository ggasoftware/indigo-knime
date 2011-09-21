package com.ggasoftware.indigo.knime.io.rxnreader;

import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JPanel;
import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.util.FilesHistoryPanel;

public class RXNReaderNodeDialog extends NodeDialogPane {

   private RXNReaderSettings m_settings = new RXNReaderSettings();

   private final FilesHistoryPanel m_filePane = new FilesHistoryPanel(
         RXNReaderNodeDialog.class.toString(),
         new String[] { ".rxn", ".rdf" });

   protected RXNReaderNodeDialog() {
      super();

      JPanel p = new JPanel();

      GridBagConstraints c = new GridBagConstraints();

      c.gridx = 0;
      c.gridy = 0;
      c.insets = new Insets(2, 0, 2, 0);
      c.anchor = 17;

      c.fill = 2;
      p.add(this.m_filePane, c);

      addTab("Standard settings", p);
   }

   @Override
   protected void loadSettingsFrom(NodeSettingsRO settings,
         DataTableSpec[] specs) throws NotConfigurableException {
      m_settings.loadSettingsForDialog(settings);

      m_filePane.setSelectedFile(m_settings.fileName);
   }

   @Override
   protected void saveSettingsTo(NodeSettingsWO settings)
   throws InvalidSettingsException {
      m_settings.fileName = m_filePane.getSelectedFile(); 

      m_settings.saveSettings(settings);
   }

}
