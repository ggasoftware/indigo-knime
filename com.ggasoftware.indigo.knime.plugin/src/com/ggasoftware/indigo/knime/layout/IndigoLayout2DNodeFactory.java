package com.ggasoftware.indigo.knime.layout;


import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;
import org.knime.core.node.defaultnodesettings.SettingsModelIntegerBounded;

import com.ggasoftware.indigo.Indigo;
import com.ggasoftware.indigo.IndigoObject;
import com.ggasoftware.indigo.knime.common.IndigoDialogPanel;
import com.ggasoftware.indigo.knime.common.transformer.IndigoTransformer;
import com.ggasoftware.indigo.knime.common.transformer.IndigoTransformerNodeDialog;
import com.ggasoftware.indigo.knime.common.transformer.IndigoTransformerNodeModel;
import com.ggasoftware.indigo.knime.common.transformer.IndigoTransformerSettings;
import com.ggasoftware.indigo.knime.common.transformer.IndigoTransformerNodeDialog.DialogComponents;
import com.ggasoftware.indigo.knime.plugin.IndigoPlugin;

/**
 * <code>NodeFactory</code> for the "IndigoLayout2D" Node.
 * 
 *
 * @author 
 */
public class IndigoLayout2DNodeFactory 
        extends NodeFactory<IndigoTransformerNodeModel> {

   public class IndigoLayout2DSettings extends IndigoTransformerSettings {
      public static final double DEFAULT_BOND_LENGTH = 1.6;
      public static final int DEFAULT_MAX_ITERATIONS = 0;
      
//      public final SettingsModelDoubleBounded _bondLength = new SettingsModelDoubleBounded("bondLength", DEFAULT_BOND_LENGTH, 0.1, Double.MAX_VALUE);
      public final SettingsModelIntegerBounded maxIterations = new SettingsModelIntegerBounded("maxIterations", DEFAULT_MAX_ITERATIONS, 0, Integer.MAX_VALUE);
      public IndigoLayout2DSettings() {
         super();
//         addSettingsParameter(_bondLength);
         addSettingsParameter(maxIterations);
      }
   }
    /**
     * {@inheritDoc}
     */
    @Override
    public IndigoTransformerNodeModel createNodeModel() {
       final IndigoLayout2DSettings settings = new IndigoLayout2DSettings();
       return new IndigoTransformerNodeModel("2d coordinates", settings,
             new IndigoTransformer() {
               @Override
               public void transform(IndigoObject io, boolean reaction) {
                  Indigo indigo = IndigoPlugin.getIndigo();
                  indigo.setOption("layout-max-iterations", settings.maxIterations.getIntValue());
                  
                  io.layout();
               }
            });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNrNodeViews() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeView<IndigoTransformerNodeModel> createNodeView(final int viewIndex,
            final IndigoTransformerNodeModel nodeModel) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasDialog() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NodeDialogPane createNodeDialogPane() {
       final IndigoLayout2DSettings rsettings = new IndigoLayout2DSettings();
       
       DialogComponents dialogComponents = new DialogComponents() {
          
//          JFormattedTextField _bondLength = new JFormattedTextField(NumberFormat.getNumberInstance());
          
          private final JSpinner _maxIterations = new JSpinner(new SpinnerNumberModel(IndigoLayout2DSettings.DEFAULT_MAX_ITERATIONS, 
                0, Integer.MAX_VALUE, 1));
          
          @Override
          public void loadDialogComponents(IndigoDialogPanel dialogPanel, IndigoTransformerSettings settings) {
//             settings.registerDialogComponent(_bondLength, rsettings._bondLength);
             settings.registerDialogComponent(_maxIterations, rsettings.maxIterations);

             // Initialize
             dialogPanel.addItemsPanel("2D coordinates settings");
//             dialogPanel.addItem("Bond length", _bondLength);
             dialogPanel.addItem("Max iterations (0 means no limit)", _maxIterations);
          }
          
       };
        return new IndigoTransformerNodeDialog("with coordinates", rsettings, dialogComponents);
    }

}

