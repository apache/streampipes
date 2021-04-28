package org.apache.streampipes.node.controller.management.offloading.model.property;

import org.apache.streampipes.node.controller.management.resource.model.ResourceMetrics;

public class FreeMemoryResourceProperty implements ResourceProperty<Long> {
    @Override
    public Long getProperty(ResourceMetrics resourceMetrics) {
        return resourceMetrics.getFreeMemoryInBytes();
    }
}
