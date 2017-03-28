package de.fzi.cep.sepa.manager.kpi.context;

import java.util.List;

/**
 * Created by riemer on 03.10.2016.
 */
public class ContextModelSensorDetails {

    private String topic;
    private String description;
    private String sensorId;
    private String name;

    private List<ContextEventProperty> contextEventProperties;

    public ContextModelSensorDetails() {

    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ContextEventProperty> getContextEventProperties() {
        return contextEventProperties;
    }

    public void setContextEventProperties(List<ContextEventProperty> contextEventProperties) {
        this.contextEventProperties = contextEventProperties;
    }
}
