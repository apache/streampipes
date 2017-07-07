package de.fzi.cep.sepa.manager.kpi.pipelineelements;

/**
 * Created by riemer on 04.10.2016.
 */
public class TimeWindow {

    private String windowType;
    private String timeUnit;
    private Integer value;

    public TimeWindow(String windowType, String timeUnit, Integer value) {
        this.windowType = windowType;
        this.timeUnit = timeUnit;
        this.value = value;
    }

    public TimeWindow() {

    }

    public String getWindowType() {
        return windowType;
    }

    public void setWindowType(String windowType) {
        this.windowType = windowType;
    }

    public String getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(String timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
