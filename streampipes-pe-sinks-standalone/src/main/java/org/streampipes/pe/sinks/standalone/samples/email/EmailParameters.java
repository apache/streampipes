package org.streampipes.pe.sinks.standalone.samples.email;

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.runtime.EventSink;

public class EmailParameters extends EventSinkBindingParams {

    private String toEmailAddress;
    private String subject;
    private String content;

    public EmailParameters(DataSinkInvocation graph, String toEmailAddress, String subject, String content) {
        super(graph);
        this.toEmailAddress = toEmailAddress;
        this.subject = subject;
        this.content = content;
    }

    public String getToEmailAddress() {
        return toEmailAddress;
    }

    public String getSubject() {
        return subject;
    }

    public String getContent() {
        return content;
    }
}
