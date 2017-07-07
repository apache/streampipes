package org.streampipes.pe.sources.samples.adapter;

/**
 * Created by riemer on 06.01.2017.
 */
public abstract class AbstractReplayTask implements Runnable {

    protected SimulationSettings simulationSettings;

    public AbstractReplayTask(SimulationSettings settings) {
        this.simulationSettings = settings;
    }
}
