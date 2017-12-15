package org.streampipes.pe.sinks.standalone.samples.email;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public class EmailPublisher implements EventSink<EmailParameters> {

    @Override
    public void bind(EmailParameters parameters) throws SpRuntimeException {

    }

    @Override
    public void onEvent(Map<String, Object> event, String sourceInfo) {

    }

    @Override
    public void discard() throws SpRuntimeException {

    }
}
