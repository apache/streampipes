package de.fzi.cep.sepa.manager.kpi.mapping;

/**
 * Created by riemer on 03.10.2016.
 */
public class Mapping {

    private String contextModelName;
    private String streamPipesName;

    public Mapping(String contextModelName, String streamPipesName) {
        this.contextModelName = contextModelName;
        this.streamPipesName = streamPipesName;
    }

    public String getContextModelName() {
        return contextModelName;
    }

    public void setContextModelName(String contextModelName) {
        this.contextModelName = contextModelName;
    }

    public String getStreamPipesName() {
        return streamPipesName;
    }

    public void setStreamPipesName(String streamPipesName) {
        this.streamPipesName = streamPipesName;
    }
}
