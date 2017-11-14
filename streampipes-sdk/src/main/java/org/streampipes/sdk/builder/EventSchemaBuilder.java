package org.streampipes.sdk.builder;

import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.schema.EventProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 06.12.2016.
 */
public class EventSchemaBuilder {

    private List<EventProperty> eventProperties;

    private EventSchemaBuilder() {
        this.eventProperties = new ArrayList<>();
    }

    public static EventSchemaBuilder create() {
        return new EventSchemaBuilder();
    }

    public EventSchemaBuilder property(EventProperty property) {
        eventProperties.add(property);
        return this;
    }


    public EventSchema buildSchema() {
        return new EventSchema(eventProperties);
    }

    public List<EventProperty> buildProperties() {
        return eventProperties;
    }
}
