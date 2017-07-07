package de.fzi.cep.sepa.manager.kpi.context;

import java.util.List;

/**
 * Created by riemer on 03.10.2016.
 */
public class ContextModelSensor {

    private List<String> sensor;

    public ContextModelSensor(List<String> sensor) {
        this.sensor = sensor;
    }

    public List<String> getSensor() {
        return sensor;
    }

    public void setSensor(List<String> sensor) {
        this.sensor = sensor;
    }
}
