package de.fzi.cep.sepa.manager.kpi.context;

/**
 * Created by riemer on 03.10.2016.
 */
public class ContextEventProperty {

    private boolean partition;
    private String name;
    private String type;

    public ContextEventProperty() {

    }

    public boolean isPartition() {
        return partition;
    }

    public void setPartition(boolean partition) {
        this.partition = partition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
