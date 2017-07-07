package de.fzi.cep.sepa.sdk.builder;

import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;

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
