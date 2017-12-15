package org.streampipes.pe.sinks.standalone.samples.email;

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.runtime.EventSink;

public class EmailParameters extends EventSinkBindingParams {

    public EmailParameters(DataSinkInvocation graph) {
        super(graph);
    }
}
