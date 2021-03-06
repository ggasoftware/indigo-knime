package com.ggasoftware.indigo.knime.areplacer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.knime.core.data.DataTableSpec;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.util.ColumnSelectionComboxBox;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ggasoftware.indigo.knime.cell.IndigoMolValue;
import com.ggasoftware.indigo.knime.cell.IndigoReactionValue;
import com.ggasoftware.indigo.knime.common.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.common.IndigoNodeSettings;
import com.ggasoftware.indigo.knime.common.IndigoNodeSettings.STRUCTURE_TYPE;

public class IndigoAtomReplacerNodeDialog extends NodeDialogPane
{
   private static final NodeLogger LOGGER = NodeLogger.getLogger(IndigoAtomReplacerNodeDialog.class);
   
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _indigoColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class, IndigoReactionValue.class);

   private final JCheckBox _appendColumn = new JCheckBox("Append Column");
   private final JTextField _newColName = new JTextField(20);
   private final IndigoAtomReplacerSettings _settings = new IndigoAtomReplacerSettings();
   private final JTextField _newAtomLabel = new JTextField(4);
   private final JCheckBox _replaceHighlighted = new JCheckBox("Replace only highlighted atoms");
   private final JCheckBox _replaceSpecificAtoms = new JCheckBox("Replace specific atoms");
   private final JComboBox _specificAtomGroup = new JComboBox();
   private final JTextField _specificAtoms = new JTextField(20);
   private final Map<String, String> _specificAtomGroupsMap = new HashMap<String, String>();
   private final String _customTitle = "Custom ...";
   private final JCheckBox _replaceAttachmentPoints = new JCheckBox("Replace attachment points");
   
   private final JLabel _structureType = new JLabel();
   private DataTableSpec _indigoSpec;

   private final ChangeListener _changeListener = new ChangeListener() {
      public void stateChanged (ChangeEvent e)
      {
         if (_appendColumn.isSelected())
         {
            _newColName.setEnabled(true);
            if ("".equals(_newColName.getText()))
               _newColName.setText(_indigoColumn.getSelectedColumn() + " (replaced atoms)");
         }
         else
            _newColName.setEnabled(false);
         _specificAtoms.setEnabled(_replaceSpecificAtoms.isSelected());
         _specificAtomGroup.setEnabled(_replaceSpecificAtoms.isSelected());
      }
   };
   
   private final ItemListener _columnChangeListener = new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
         STRUCTURE_TYPE stype = _getStructureType();
         switch(stype) {
            case Unknown:
               _structureType.setText("Unknown");
               break;
            case Reaction:
               _structureType.setText("Reaction");
               break;
            case Molecule:
               _structureType.setText("Molecule");
               break;
         }
      }
   };
   
   private void updateAtomsListByEvent (Object source)
   {
      // Compare values from drop-down listbox and the text field
      Object selected = _specificAtomGroup.getSelectedObjects()[0];

      String groupElements = _specificAtomGroupsMap.get(selected);
      String elementsFromField = _specificAtoms.getText();
      if (groupElements != null && groupElements.equals(elementsFromField))
         return;
      if (source == _specificAtoms) {
         // Try to find such value from the predefined set
         if (_specificAtomGroupsMap.containsValue(elementsFromField)) {
            for (String key: _specificAtomGroupsMap.keySet()) {
               if (_specificAtomGroupsMap.get(key).equals(elementsFromField)) {
                  _specificAtomGroup.setSelectedItem(key);
                  return;
               }
            }
         } else {
            _specificAtomGroup.setSelectedItem(_customTitle);
         }
      } else if (groupElements != null) {
         // Just put the value from the combobox
         if (_specificAtoms.getText() != groupElements)
            _specificAtoms.setText(groupElements);
      }
   }
   
   private final ActionListener _actionListener = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
         updateAtomsListByEvent(arg0.getSource());
      }
   };
   private final DocumentListener _documentListener = new DocumentListener() {

      @Override
      public void changedUpdate(DocumentEvent arg0) {
         updateAtomsListByEvent(_specificAtoms);
      }

      @Override
      public void insertUpdate(DocumentEvent arg0) {
         updateAtomsListByEvent(_specificAtoms);
      }

      @Override
      public void removeUpdate(DocumentEvent arg0) {
         updateAtomsListByEvent(_specificAtoms);
      }
   };
   
   
   /**
    * New pane for configuring the IndigoAtomReplacer node.
    */
   protected IndigoAtomReplacerNodeDialog()
   {
      super();
      
      _registerDialogComponents();
      
      IndigoDialogPanel dialogPanel = new IndigoDialogPanel();
      
      dialogPanel.addItemsPanel("Column Settings");
      dialogPanel.addItem("Structure type", _structureType);
      dialogPanel.addItem("Indigo column", _indigoColumn);
      dialogPanel.addItem(_appendColumn, _newColName);
      dialogPanel.addItemsPanel("Atom Settings");
      dialogPanel.addItem("New atom label:", _newAtomLabel);
      dialogPanel.addItem(_replaceHighlighted);
      dialogPanel.addItem(_replaceSpecificAtoms);
      dialogPanel.addItem(_specificAtomGroup, _specificAtoms);
      dialogPanel.addItem(_replaceAttachmentPoints);
      
      loadAtomGroups();
      
      _appendColumn.addChangeListener(_changeListener);
      _replaceSpecificAtoms.addChangeListener(_changeListener);
      _specificAtoms.getDocument().addDocumentListener(_documentListener);
      _specificAtomGroup.addActionListener(_actionListener);
      _indigoColumn.addItemListener(_columnChangeListener);
     
      addTab("Standard settings", dialogPanel.getPanel());
   }
   
   private void _registerDialogComponents() {
      
      _settings.registerDialogComponent(_indigoColumn, IndigoAtomReplacerSettings.INPUT_PORT, _settings.colName);
      _settings.registerDialogComponent(_appendColumn, _settings.appendColumn);
      _settings.registerDialogComponent(_newColName, _settings.newColName);
      _settings.registerDialogComponent(_newAtomLabel, _settings.newAtomLabel);

      _settings.registerDialogComponent(_replaceHighlighted, _settings.replaceHighlighted);
      _settings.registerDialogComponent(_replaceSpecificAtoms, _settings.replaceSpecificAtom);
      _settings.registerDialogComponent(_specificAtoms, _settings.specificAtom);
      
      _settings.registerDialogComponent(_replaceAttachmentPoints, _settings.replaceAttachmentPoints);
      
   }

   private void loadAtomGroups ()
   {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      try {
         ArrayList<String> atomGroups = new ArrayList<String>();
         
         DocumentBuilder db = dbf.newDocumentBuilder();
         Document doc = db.parse(getClass().getResourceAsStream("IndigoAtomReplacerConfig.xml"));
         Element docElement = doc.getDocumentElement();
         NodeList groups = docElement.getElementsByTagName("Group");
         int groupsCount = groups.getLength();
         for (int i = 0; i < groupsCount; i++) {
            Element group = (Element)groups.item(i);
            String name = group.getAttribute("name");
            String elementsValue = "";
            NodeList elements = group.getElementsByTagName("Elements");
            if (elements.getLength() > 0) {
               elementsValue = elements.item(0).getFirstChild().getNodeValue();
            }
            NodeList includes = group.getElementsByTagName("Include");
            int includesCount = includes.getLength(); 
            for (int j = 0; j < includesCount; j++) {
               String groupName = includes.item(j).getFirstChild().getNodeValue();
               if (!elementsValue.equals(""))
                  elementsValue += ", ";
               elementsValue += _specificAtomGroupsMap.get(groupName);  
            }
            _specificAtomGroupsMap.put(name, elementsValue);
            atomGroups.add(name);
         }
         Collections.sort(atomGroups);
         for (String group : atomGroups)
            _specificAtomGroup.addItem(group);
      } catch (ParserConfigurationException e) {
         LOGGER.error(e);
         e.printStackTrace();
      } catch (SAXException e) {
         LOGGER.error(e);
         e.printStackTrace();
      } catch (IOException e) {
         LOGGER.error(e);
         e.printStackTrace();
      }
      
      _specificAtomGroup.addItem(_customTitle);
   }
   
   /*
    * Returns current column selection state
    */
   private STRUCTURE_TYPE _getStructureType() {
      return IndigoNodeSettings.getStructureType(_indigoSpec, _indigoColumn.getSelectedColumn());
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      try {
         _settings.loadSettingsFrom(settings);
         _settings.loadDialogSettings(specs);
         
         _indigoSpec = specs[IndigoAtomReplacerSettings.INPUT_PORT];
         _changeListener.stateChanged(null);
         _columnChangeListener.itemStateChanged(null);
         updateAtomsListByEvent(_specificAtoms);
      } catch (InvalidSettingsException e) {
         throw new NotConfigurableException(e.getMessage());
      }

   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveSettingsTo (final NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      STRUCTURE_TYPE stype = _getStructureType();

      if (stype.equals(STRUCTURE_TYPE.Unknown))
         throw new InvalidSettingsException("can not define the indigo column type");
      
      _settings.saveDialogSettings();
      _settings.saveSettingsTo(settings);
   }
}
