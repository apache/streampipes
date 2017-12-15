package org.streampipes.pe.sinks.standalone.samples.email;

import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.ConfiguredEventSink;
import org.streampipes.wrapper.runtime.EventSink;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class EmailController extends StandaloneEventSinkDeclarer<EmailParameters> {

    @Override
    public ConfiguredEventSink<EmailParameters, EventSink<EmailParameters>> onInvocation(DataSinkInvocation graph) {
        return null;
    }

    @Override
    public DataSinkDescription declareModel() {
        return null;
    }
}
