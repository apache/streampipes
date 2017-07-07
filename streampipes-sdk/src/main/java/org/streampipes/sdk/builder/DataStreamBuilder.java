package org.streampipes.sdk.builder;

import org.streampipes.model.impl.EventGrounding;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.TransportFormat;
import org.streampipes.model.impl.TransportProtocol;
import org.streampipes.model.impl.eventproperty.EventProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by riemer on 06.12.2016.
 */
public class DataStreamBuilder extends AbstractPipelineElementBuilder<DataStreamBuilder, EventStream> {

    private List<EventProperty> eventProperties;
    private EventGrounding eventGrounding;

    protected DataStreamBuilder(String id, String label, String description) {
        super(id, label, description, new EventStream());
        this.eventProperties = new ArrayList<>();
        this.eventGrounding = new EventGrounding();
    }

    public static DataStreamBuilder create(String id, String label, String description) {
        return new DataStreamBuilder(id, label, description);
    }

    public DataStreamBuilder property(EventProperty property) {
        this.eventProperties.add(property);
        return me();
    }

    public DataStreamBuilder protocol(TransportProtocol protocol) {
        this.eventGrounding.setTransportProtocol(protocol);
        return this;
    }

    public DataStreamBuilder format(TransportFormat format) {
        this.eventGrounding.setTransportFormats(Arrays.asList(format));
        return this;
    }

    @Override
    protected DataStreamBuilder me() {
        return this;
    }

    @Override
    protected void prepareBuild() {
        this.elementDescription.setEventGrounding(eventGrounding);
        this.elementDescription.setEventSchema(new EventSchema(eventProperties));
    }
}
