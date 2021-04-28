package org.apache.streampipes.node.controller.management.offloading.model.selection;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.node.controller.management.pe.storage.RunningInvocableInstances;

import java.util.List;
import java.util.Random;

public class RandomSelectionStrategy implements SelectionStrategy{

    @Override
    public InvocableStreamPipesEntity selectEntity() {
        List<InvocableStreamPipesEntity> instances = RunningInvocableInstances.INSTANCE.getAll();
        if(instances.size() == 0)
            return null;
        return instances.get(new Random().nextInt(instances.size()));
    }
}
