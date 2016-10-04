package de.fzi.cep.sepa.manager.kpi.pipelineelements;

import de.fzi.cep.sepa.kpi.UnaryOperation;
import de.fzi.cep.sepa.manager.kpi.mapping.IdMapper;
import de.fzi.cep.sepa.model.impl.EventStream;

/**
 * Created by riemer on 03.10.2016.
 */
public class AggregationSettings extends Settings {

    private boolean partition;
    private String eventPropertyMapping;
    private String partitionMapping;
    private TimeWindow timeWindow;
    private String aggregationType;

    public AggregationSettings(boolean partition, String eventPropertyMapping, TimeWindow timeWindow) {
        this.partition = partition;
        this.eventPropertyMapping = eventPropertyMapping;
        this.timeWindow = timeWindow;
    }

    public AggregationSettings() {

    }

    public String getAggregationType() {
        return aggregationType;
    }

    public void setAggregationType(String aggregationType) {
        this.aggregationType = aggregationType;
    }

    public String getPartitionMapping() {
        return partitionMapping;
    }

    public void setPartitionMapping(String partitionMapping) {
        this.partitionMapping = partitionMapping;
    }

    public boolean isPartition() {
        return partition;
    }

    public void setPartition(boolean partition) {
        this.partition = partition;
    }

    public String getEventPropertyMapping() {
        return eventPropertyMapping;
    }

    public void setEventPropertyMapping(String eventPropertyMapping) {
        this.eventPropertyMapping = eventPropertyMapping;
    }

    public TimeWindow getTimeWindow() {
        return timeWindow;
    }

    public void setTimeWindow(TimeWindow timeWindow) {
        this.timeWindow = timeWindow;
    }

    public static AggregationSettings makeSettings(EventStream stream, UnaryOperation unaryPipelineOperation, IdMapper idMapper) {
        AggregationSettings settings = new AggregationSettings();
        settings.setAggregationType(unaryPipelineOperation.getUnaryOperationType().toString().toLowerCase());
        settings.setEventPropertyMapping(idMapper.getEventProperty(stream, unaryPipelineOperation.getEventPropertyName()).getElementId());
        if (unaryPipelineOperation.isPartition()) {
            settings.setPartitionMapping(idMapper.getEventProperty(stream, unaryPipelineOperation.getPartitionProperty()).getElementId());
        }

        TimeWindow timeWindow = new TimeWindow();
        timeWindow.setValue(unaryPipelineOperation.getWindow().getValue());
        settings.setTimeWindow(timeWindow);

        return settings;
    }
}
