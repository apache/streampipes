package org.apache.streampipes.node.controller.management.offloading.model.selection;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;

public interface SelectionStrategy {
    InvocableStreamPipesEntity selectEntity();
}
