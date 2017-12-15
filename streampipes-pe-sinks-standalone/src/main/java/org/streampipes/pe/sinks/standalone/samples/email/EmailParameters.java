package org.streampipes.pe.sinks.standalone.samples.email;

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.runtime.EventSink;

public class EmailParameters extends EventSinkBindingParams {

    private String fromEmailAddress;
    private String toEmailAddress;

    public EmailParameters(DataSinkInvocation graph, String fromEmailAddress, String toEmailAddress) {
        super(graph);
        this.fromEmailAddress = fromEmailAddress;
        this.toEmailAddress = toEmailAddress;
    }

    public String getFromEmailAddress() {
        return fromEmailAddress;
    }

    public String getToEmailAddress() {
        return toEmailAddress;
    }
}
