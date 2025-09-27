package org.apache.streampipes.model.loadbalancer;

import org.apache.streampipes.commons.random.UUIDGenerator;

import java.util.ArrayList;
import java.util.List;

public class LoadBalanceResourceUnit<T> {

    String pipelineId;

    List<T> elements;

    List<String> labels;

    String serviceId;

    public LoadBalanceResourceUnit() {
        this.elements = new ArrayList<>();
    }

    public void addElements(T t) {
        elements.add(t);
    }

    public List<T> getElements() {
        return elements;
    }


    public String getPipelineId() {
        return pipelineId;
    }

    public void setPipelineId(String pipelineId) {
        this.pipelineId = pipelineId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
}
