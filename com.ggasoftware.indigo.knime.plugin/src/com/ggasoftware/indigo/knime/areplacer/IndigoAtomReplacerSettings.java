package com.ggasoftware.indigo.knime.areplacer;

import org.knime.core.node.defaultnodesettings.SettingsModelBoolean;
import org.knime.core.node.defaultnodesettings.SettingsModelColumnName;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.ggasoftware.indigo.knime.IndigoNodeSettings;

public class IndigoAtomReplacerSettings extends IndigoNodeSettings
{
   public static final int INPUT_PORT = 0;
   
   public final SettingsModelColumnName colName = new SettingsModelColumnName("colName", null);
   public final SettingsModelBoolean appendColumn = new SettingsModelBoolean("appendColumn", false);
   public final SettingsModelString newColName = new SettingsModelString("newColName", null);
   public final SettingsModelString newAtomLabel = new SettingsModelString("newAtomLabel", "*");
   public final SettingsModelBoolean replaceHighlighted = new SettingsModelBoolean("replaceHighlighted", false);
   public final SettingsModelBoolean replaceSpecificAtom = new SettingsModelBoolean("replaceSpecificAtom", false);
   public final SettingsModelBoolean replaceAttachmentPoints = new SettingsModelBoolean("replaceAttachmentPoints", false);
   public final SettingsModelString specificAtom = new SettingsModelString("specificAtom", null);
   
   /*
    * Parameter is not saved
    */
   public STRUCTURE_TYPE structureType;
   
   public IndigoAtomReplacerSettings() {
      addSettingsParameter(colName);
      addSettingsParameter(appendColumn);
      addSettingsParameter(newColName);
      addSettingsParameter(newAtomLabel);
      addSettingsParameter(replaceHighlighted);
      addSettingsParameter(replaceSpecificAtom);
      addSettingsParameter(specificAtom);
      addSettingsParameter(replaceAttachmentPoints);
   }

}
