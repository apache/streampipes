package org.apache.streampipes.manager.execution.pipeline.executor.operations;

import java.util.ArrayList;
import java.util.List;

public class LifecycleEntity<T> {

    private final List<T> entitiesToStart;

    private final List<T> entitiesToStop;

    private final List<T> entitiesToStore;

    private final List<T> entitiesToDelete;

    public LifecycleEntity(){
        entitiesToStart = new ArrayList<>();
        entitiesToStop = new ArrayList<>();
        entitiesToStore = new ArrayList<>();
        entitiesToDelete = new ArrayList<>();
    }

    public List<T> getEntitiesToStart() {
        return entitiesToStart;
    }

    public List<T> getEntitiesToStop() {
        return entitiesToStop;
    }

    public List<T> getEntitiesToStore() {
        return entitiesToStore;
    }

    public List<T> getEntitiesToDelete() {
        return entitiesToDelete;
    }
}
