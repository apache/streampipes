package org.streampipes.pe.sources.samples.adapter;

public abstract class AbstractReplayTask implements Runnable {

    protected SimulationSettings simulationSettings;

    public AbstractReplayTask(SimulationSettings settings) {
        this.simulationSettings = settings;
    }
}
