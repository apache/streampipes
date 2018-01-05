package org.streampipes.pe.sinks.standalone.samples.email;

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.runtime.EventSink;

public class EmailParameters extends EventSinkBindingParams {

    private String toEmailAddress;
    private String subject;

    public EmailParameters(DataSinkInvocation graph, String toEmailAddress, String subject) {
        super(graph);
        this.toEmailAddress = toEmailAddress;
    }

    public String getToEmailAddress() {
        return toEmailAddress;
    }

    public String getSubject() {
        return subject;
    }
}
