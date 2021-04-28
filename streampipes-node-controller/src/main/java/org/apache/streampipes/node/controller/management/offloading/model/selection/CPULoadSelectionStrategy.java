package org.apache.streampipes.node.controller.management.offloading.model.selection;

import org.apache.streampipes.model.base.InvocableStreamPipesEntity;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public class CPULoadSelectionStrategy implements SelectionStrategy{
    @Override
    public InvocableStreamPipesEntity selectEntity() {
        //TODO: Migrate the PE with the highest load on CPU (or possibly other resource)
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        return null;
    }
}
