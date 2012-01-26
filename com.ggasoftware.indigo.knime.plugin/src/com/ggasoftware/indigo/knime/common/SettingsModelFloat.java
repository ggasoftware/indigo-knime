package com.ggasoftware.indigo.knime.common;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;
import org.knime.core.node.NotConfigurableException;
import org.knime.core.node.defaultnodesettings.SettingsModel;
import org.knime.core.node.defaultnodesettings.SettingsModelFlowVariableCompatible;
import org.knime.core.node.port.PortObjectSpec;
import org.knime.core.node.workflow.FlowVariable;

public class SettingsModelFloat extends SettingsModel
implements SettingsModelFlowVariableCompatible {

    private float m_value;

    private final String m_configName;

    public SettingsModelFloat(final String configName,
            final float defaultValue) {
        if ((configName == null) || (configName == "")) {
            throw new IllegalArgumentException("The configName must be a "
                    + "non-empty string");
        }
        m_value = defaultValue;
        m_configName = configName;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected SettingsModelFloat createClone() {
        return new SettingsModelFloat(m_configName, m_value);
    }

    @Override
    protected String getModelTypeID() {
        return "SMID_float";
    }

    @Override
    protected String getConfigName() {
        return m_configName;
    }

    public void setFloatValue(final float newValue) {
        boolean notify = (newValue != m_value);

        m_value = newValue;

        if (notify) {
            notifyChangeListeners();
        }
    }

    public float getFloatValue() {
        return m_value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsForDialog(final NodeSettingsRO settings,
            final PortObjectSpec[] specs) throws NotConfigurableException {
        try {
            // use the current value, if no value is stored in the settings
            setFloatValue(settings.getFloat(m_configName, m_value));
        } catch (IllegalArgumentException e) {
            // if the value is not accepted, keep the old value.
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsForDialog(final NodeSettingsWO settings)
            throws InvalidSettingsException {
        saveSettingsForModel(settings);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateSettingsForModel(final NodeSettingsRO settings)
            throws InvalidSettingsException {

        validateValue(settings.getFloat(m_configName));
    }

    /**
     * Called during {@link #validateSettingsForModel}, can be overwritten by
     * derived classes.
     *
     * @param value the value to validate
     * @throws InvalidSettingsException if the value is not valid and should be
     *             rejected
     */
    protected void validateValue(final double value)
            throws InvalidSettingsException {
        // derived class needs to check value
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void loadSettingsForModel(final NodeSettingsRO settings)
            throws InvalidSettingsException {
        try {
            // no default value, throw an exception instead
            setFloatValue(settings.getFloat(m_configName));

        } catch (IllegalArgumentException iae) {
            // value not accepted
            throw new InvalidSettingsException(iae.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void saveSettingsForModel(final NodeSettingsWO settings) {
        settings.addFloat(m_configName, m_value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + " ('" + m_configName + "')";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return m_configName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FlowVariable.Type getFlowVariableType() {
        return FlowVariable.Type.DOUBLE;
    }
}