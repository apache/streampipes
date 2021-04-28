package org.apache.streampipes.node.controller.management.offloading.model;

import org.apache.streampipes.node.controller.management.offloading.model.policies.OffloadingPolicy;
import org.apache.streampipes.node.controller.management.offloading.model.property.ResourceProperty;
import org.apache.streampipes.node.controller.management.offloading.model.selection.SelectionStrategy;

public class OffloadingStrategy<T> {
    private SelectionStrategy selectionStrategy;
    private OffloadingPolicy<T> offloadingPolicy;
    private ResourceProperty<T> resourceProperty;

    public OffloadingStrategy(OffloadingPolicy offloadingPolicy, ResourceProperty resourceProperty,
                              SelectionStrategy selectionStrategy){
        this.offloadingPolicy = offloadingPolicy;
        this.resourceProperty = resourceProperty;
        this.selectionStrategy = selectionStrategy;
    }

    public SelectionStrategy getSelectionStrategy() {
        return selectionStrategy;
    }

    public void setSelectionStrategy(SelectionStrategy selectionStrategy) {
        this.selectionStrategy = selectionStrategy;
    }

    public OffloadingPolicy<?> getOffloadingPolicy() {
        return offloadingPolicy;
    }

    public void setOffloadingPolicy(OffloadingPolicy<T> offloadingPolicy) {
        this.offloadingPolicy = offloadingPolicy;
    }

    public ResourceProperty<?> getResourceProperty() {
        return resourceProperty;
    }

    public void setResourceProperty(ResourceProperty<T> resourceProperty) {
        this.resourceProperty = resourceProperty;
    }

}
