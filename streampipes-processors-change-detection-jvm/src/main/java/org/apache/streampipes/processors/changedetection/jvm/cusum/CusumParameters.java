package org.apache.streampipes.processors.changedetection.jvm.cusum;

import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public class CusumParameters  extends EventProcessorBindingParams {

    private String selectedNumberMapping;
    private Double paramK;
    private Double paramH;

    public CusumParameters(DataProcessorInvocation graph, String selectedNumberMapping, Double paramK, Double paramH) {
        super(graph);
        this.selectedNumberMapping = selectedNumberMapping;
        this.paramK = paramK;
        this.paramH = paramH;
    }

    public String getSelectedNumberMapping() { return selectedNumberMapping; }

    public Double getParamK() { return paramK; }

    public Double getParamH() {
        return paramH;
    }
}
