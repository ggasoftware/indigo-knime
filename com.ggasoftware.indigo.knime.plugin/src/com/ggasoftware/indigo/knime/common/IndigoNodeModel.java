package com.ggasoftware.indigo.knime.common;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.DataValue;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeModel;
import org.knime.core.node.defaultnodesettings.SettingsModelString;

import com.ggasoftware.indigo.IndigoObject;
import com.ggasoftware.indigo.knime.IndigoNodeSettings.STRUCTURE_TYPE;
import com.ggasoftware.indigo.knime.cell.IndigoMolCell;
import com.ggasoftware.indigo.knime.cell.IndigoQueryMolCell;
import com.ggasoftware.indigo.knime.cell.IndigoQueryReactionCell;
import com.ggasoftware.indigo.knime.cell.IndigoReactionCell;

public abstract class IndigoNodeModel extends NodeModel
{
   protected IndigoNodeModel (int nrInDataPorts, int nrOutDataPorts)
   {
      super(nrInDataPorts, nrOutDataPorts);
   }

   protected void searchIndigoColumn (DataTableSpec spec, SettingsModelString colName, Class<? extends DataValue> cls)
   throws InvalidSettingsException {
      colName.setStringValue(searchIndigoColumn(spec, colName.getStringValue(), cls));
   }
   
   protected String searchIndigoColumn (DataTableSpec spec, String colName, Class<? extends DataValue> cls)
   throws InvalidSettingsException
   {
      if (colName == null)
      {
         for (DataColumnSpec cs : spec)
         {
            if (cs.getType().isCompatible(cls))
            {
               if (colName != null)
               {
                  setWarningMessage("Selected default column '" + colName + "' as " + cls.getName());
                  break;
               }
               else
                  colName = cs.getName();
            }
         }
         if (colName == null)
            throw new InvalidSettingsException("No " + cls.getName() + " found in the input table");
      }
      else
      {
         if (!spec.containsName(colName))
            throw new InvalidSettingsException("Column '" + colName + "' does not exist in input table");
         if (!spec.getColumnSpec(colName).getType().isCompatible(cls))
            throw new InvalidSettingsException("Column '" + colName + "' is not a " + cls.getName());
      }
      return colName;
   }

   protected void searchMixedIndigoColumn(DataTableSpec spec, SettingsModelString colNameModel, Class<? extends DataValue> class1,
         Class<? extends DataValue> class2) throws InvalidSettingsException {
      
      String colName = colNameModel.getStringValue();
      if(colName == null || colName.length() < 1)
         colName = null;
      
      if (colName == null) {
         for (DataColumnSpec cs : spec) {
            if (cs.getType().isCompatible(class1)) {
               colName = cs.getName();
               setWarningMessage("Indigo column autoselect: selected column '" + colName + "' as " + class1.getName());
               colNameModel.setStringValue(colName);
               break;
            } else if(cs.getType().isCompatible(class2)) {
               colName = cs.getName();
               setWarningMessage("Indigo column autoselect: selected column '" + colName + "' as " + class2.getName());
               colNameModel.setStringValue(colName);
               break;
            }
         }
         if (colName == null)
            throw new InvalidSettingsException("No " + class1.getName() + " or " + class2.getName() + " found in the input table");
      } else {
         if (!spec.containsName(colName))
            throw new InvalidSettingsException("Column '" + colName + "' does not exist in input table");
         
         if (! (spec.getColumnSpec(colName).getType().isCompatible(class1) || spec.getColumnSpec(colName).getType().isCompatible(class2)))
            throw new InvalidSettingsException("Column '" + colName + "' is not a " + class1.getName() + " or " + class2.getName());
      }
   }
   
   public enum MOLCELL_TYPE{
      Molecule, QuerySmile, QuerySmarts, QueryMolecule
   }

   public static MOLCELL_TYPE defineMolCellType(DataCell dataCell) {
      if (dataCell.getType().equals(IndigoMolCell.TYPE))
         return MOLCELL_TYPE.Molecule;

      IndigoQueryMolCell queryCell = (IndigoQueryMolCell) dataCell;

      if (queryCell.isSmarts())
         return MOLCELL_TYPE.QuerySmarts;

      if (countLines(queryCell.getSource()) > 1)
         return MOLCELL_TYPE.QueryMolecule;

      return MOLCELL_TYPE.QuerySmile;
   }
   
   public enum REACTIONCELL_TYPE{
      Reaction, QuerySmile, QuerySmarts, QueryReaction
   }
   
   public static REACTIONCELL_TYPE defineReactionCellType(DataCell dataCell) {
      if (dataCell.getType().equals(IndigoReactionCell.TYPE))
         return REACTIONCELL_TYPE.Reaction;
      
      IndigoQueryReactionCell queryCell = (IndigoQueryReactionCell) dataCell;
      
      if (queryCell.isSmarts())
         return REACTIONCELL_TYPE.QuerySmarts;
      
      if (countLines(queryCell.getSource()) > 1)
         return REACTIONCELL_TYPE.QueryReaction;
      
      return REACTIONCELL_TYPE.QuerySmile;
   }

   private final static String LINE_SEP = System.getProperty("line.separator");

   private static int countLines(String str) {
      return str.split(LINE_SEP).length;
   }
   
   protected DataColumnSpec _createNewColumnSpec(String colName, STRUCTURE_TYPE structureType) {
      DataColumnSpec result = null;
      switch(structureType){
         case Molecule:
            result = new DataColumnSpecCreator(colName, IndigoMolCell.TYPE).createSpec();
            break;
         case Reaction:
            result = new DataColumnSpecCreator(colName, IndigoReactionCell.TYPE).createSpec();
            break;
         case Unknown:
            throw new RuntimeException("Structure type is not defined");
      }
      return result;
   }
   
   protected DataCell _createNewDataCell(IndigoObject target, STRUCTURE_TYPE structureType) {
      DataCell result = null;
      if(target == null)
         return DataType.getMissingCell();
      
      switch (structureType) {
      case Molecule:
         result = new IndigoMolCell(target);
         break;
      case Reaction:
         result = new IndigoReactionCell(target);
         break;
      case Unknown:
         throw new RuntimeException("Structure type is not defined");
      }
      return result;
   }
}
