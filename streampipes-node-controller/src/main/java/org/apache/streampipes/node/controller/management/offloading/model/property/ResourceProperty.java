package org.apache.streampipes.node.controller.management.offloading.model.property;

import org.apache.streampipes.node.controller.management.resource.model.ResourceMetrics;

public interface ResourceProperty<T> {
    T getProperty(ResourceMetrics resourceMetrics);
}
