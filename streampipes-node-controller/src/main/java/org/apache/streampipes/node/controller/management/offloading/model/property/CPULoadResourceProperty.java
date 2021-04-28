package org.apache.streampipes.node.controller.management.offloading.model.property;

import org.apache.streampipes.node.controller.management.resource.model.ResourceMetrics;

public class CPULoadResourceProperty implements ResourceProperty<Float>{

    @Override
    public Float getProperty(ResourceMetrics resourceMetrics) {
        return resourceMetrics.getCpuLoadInPercent();
    }
}