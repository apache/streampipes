package de.fzi.cep.sepa.manager.kpi.context;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by riemer on 03.10.2016.
 */
public class ContextModel {

    private ContextModelSensor contextModelSensor;
    private Map<String, ContextModelSensorDetails> contextModelSensorDetails;

    public ContextModel() {
        this.contextModelSensorDetails = new HashMap<>();
    }

    public ContextModelSensor getContextModelSensor() {
        return contextModelSensor;
    }

    public void setContextModelSensor(ContextModelSensor contextModelSensor) {
        this.contextModelSensor = contextModelSensor;
    }

    public Map<String, ContextModelSensorDetails> getContextModelSensorDetails() {
        return contextModelSensorDetails;
    }

    public void setContextModelSensorDetails(Map<String, ContextModelSensorDetails> contextModelSensorDetails) {
        this.contextModelSensorDetails = contextModelSensorDetails;
    }

    public void addSensorDetails(String sensorId, ContextModelSensorDetails sensorDetails) {
        contextModelSensorDetails.put(sensorId, sensorDetails);
    }
}
