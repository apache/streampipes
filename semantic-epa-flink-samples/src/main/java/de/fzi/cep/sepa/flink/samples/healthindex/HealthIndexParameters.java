package de.fzi.cep.sepa.flink.samples.healthindex;

import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.param.BindingParameters;

/**
 * Created by riemer on 17.10.2016.
 */
public class HealthIndexParameters extends BindingParameters {

    private String zScoreMapping;
    private String frictionMapping;
    private String stddevMapping;

    public HealthIndexParameters(SepaInvocation graph, String zScoreMapping, String frictionMapping, String stddevMapping) {
        super(graph);
        this.zScoreMapping = zScoreMapping;
        this.frictionMapping = frictionMapping;
        this.stddevMapping = stddevMapping;
    }

    public String getzScoreMapping() {
        return zScoreMapping;
    }

    public void setzScoreMapping(String zScoreMapping) {
        this.zScoreMapping = zScoreMapping;
    }

    public String getFrictionMapping() {
        return frictionMapping;
    }

    public void setFrictionMapping(String frictionMapping) {
        this.frictionMapping = frictionMapping;
    }

    public String getStddevMapping() {
        return stddevMapping;
    }

    public void setStddevMapping(String stddevMapping) {
        this.stddevMapping = stddevMapping;
    }
}
