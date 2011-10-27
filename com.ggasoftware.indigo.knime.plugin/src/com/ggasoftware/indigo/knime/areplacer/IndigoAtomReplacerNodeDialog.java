package com.ggasoftware.indigo.knime.areplacer;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
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

public class IndigoAtomReplacerNodeDialog extends NodeDialogPane
{
   private static final NodeLogger LOGGER = NodeLogger.getLogger(IndigoAtomReplacerNodeDialog.class);
   
   @SuppressWarnings("unchecked")
   private final ColumnSelectionComboxBox _molColumn = new ColumnSelectionComboxBox(
         (Border) null, IndigoMolValue.class);

   private final JCheckBox _appendColumn = new JCheckBox("Append Column");
   private final JTextField _newColName = new JTextField(20);
   private final IndigoAtomReplacerSettings _settings = new IndigoAtomReplacerSettings();
   private final JTextField _newAtomLabel = new JTextField(4);
   private final JCheckBox _replaceHighlighted = new JCheckBox("Replace only highlighted atoms");
   private final JCheckBox _replaceSpecificAtoms = new JCheckBox("Replace specific atoms");
   private final JComboBox _specificAtomGroup = new JComboBox();
   private final JTextField _specificAtoms = new JTextField(30);
   private final Map<String, String> _specificAtomGroupsMap = new HashMap<String, String>();
   private final String _customTitle = "Custom ...";
   private final JCheckBox _replaceAttachmentPoints = new JCheckBox("Replace attachment points");

   private final ChangeListener _changeListener = new ChangeListener() {
      public void stateChanged (ChangeEvent e)
      {
         if (_appendColumn.isSelected())
         {
            _newColName.setEnabled(true);
            if ("".equals(_newColName.getText()))
               _newColName.setText(_molColumn.getSelectedColumn() + " (replaced atoms)");
         }
         else
            _newColName.setEnabled(false);
         _specificAtoms.setEnabled(_replaceSpecificAtoms.isSelected());
         _specificAtomGroup.setEnabled(_replaceSpecificAtoms.isSelected());
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

      JPanel p = new JPanel(new GridBagLayout());

      GridBagConstraints c = new GridBagConstraints();

      c.anchor = GridBagConstraints.WEST;
      c.insets = new Insets(2, 2, 2, 2);
      c.gridx = 0;
      c.gridy = 0;
      p.add(new JLabel("Indigo column   "), c);
      c.gridx = 1;
      p.add(_molColumn, c);

      c.gridy++;
      c.gridx = 0;
      p.add(_appendColumn, c);
      c.gridx = 1;
      p.add(_newColName, c);

      c.gridy++;
      c.gridx = 0;
      p.add(new JLabel("New atom label:"), c);
      c.gridx = 1;
      p.add(_newAtomLabel, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(_replaceHighlighted, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(_replaceSpecificAtoms, c);
      c.gridx = 1;
      p.add(_specificAtomGroup, c);
      c.gridy++;
      p.add(_specificAtoms, c);
      
      c.gridy++;
      c.gridx = 0;
      p.add(_replaceAttachmentPoints, c);
      
      loadAtomGroups();
      
      _appendColumn.addChangeListener(_changeListener);
      _replaceSpecificAtoms.addChangeListener(_changeListener);
      _specificAtoms.getDocument().addDocumentListener(_documentListener);
      _specificAtomGroup.addActionListener(_actionListener);
     
      addTab("Standard settings", p);
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
   
   /**
    * {@inheritDoc}
    */
   @Override
   protected void loadSettingsFrom (final NodeSettingsRO settings,
         final DataTableSpec[] specs) throws NotConfigurableException
   {
      _settings.loadSettingsForDialog(settings);

      _molColumn.update(specs[0], _settings.colName);
      _appendColumn.setSelected(!_settings.replaceColumn);
      _newColName.setEnabled(!_settings.replaceColumn);
      _newColName.setText(_settings.newColName);
      _newAtomLabel.setText(_settings.newAtomLabel);

      _replaceHighlighted.setSelected(_settings.replaceHighlighted);
      _replaceSpecificAtoms.setSelected(_settings.replaceSpecificAtom);
      _specificAtoms.setText(_settings.specificAtom);
      
      _replaceAttachmentPoints.setSelected(_settings.replaceAttachmentPoints);
      
      _changeListener.stateChanged(null);
      updateAtomsListByEvent(_specificAtoms);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void saveSettingsTo (final NodeSettingsWO settings)
         throws InvalidSettingsException
   {
      _settings.colName = _molColumn.getSelectedColumn();
      _settings.replaceColumn = !_appendColumn.isSelected();
      _settings.newColName = _newColName.getText();
      _settings.newAtomLabel = _newAtomLabel.getText();
      
      _settings.replaceHighlighted = _replaceHighlighted.isSelected();
      _settings.replaceSpecificAtom = _replaceSpecificAtoms.isSelected();
      _settings.specificAtom = _specificAtoms.getText();

      _settings.replaceAttachmentPoints = _replaceAttachmentPoints.isSelected();
      
      _settings.saveSettings(settings);
   }
}
