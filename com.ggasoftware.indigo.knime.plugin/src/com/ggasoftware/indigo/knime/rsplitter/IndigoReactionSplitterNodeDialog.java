package com.ggasoftware.indigo.knime.rsplitter;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;

/**
 * <code>NodeDialog</code> for the "IndigoReactionSplitter" Node.
 * 
 * 
 */
public class IndigoReactionSplitterNodeDialog extends NodeDialogPane {

   /**
    * New pane for configuring IndigoReactionSplitter node dialog. This is just
    * a suggestion to demonstrate possible default dialog components.
    */
   protected IndigoReactionSplitterNodeDialog() {
      super();

   }

   @Override
   protected void saveSettingsTo(NodeSettingsWO settings)
         throws InvalidSettingsException {
      
   }
   
   @Override
   protected void loadSettingsFrom(NodeSettingsRO settings,
         DataTableSpec[] specs) throws NotConfigurableException {
   }
}
