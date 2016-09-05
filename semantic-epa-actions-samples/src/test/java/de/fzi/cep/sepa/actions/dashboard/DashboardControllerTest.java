package de.fzi.cep.sepa.actions.dashboard;

import de.fzi.cep.sepa.model.builder.SchemaBuilder;
import de.fzi.cep.sepa.model.builder.StreamBuilder;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardControllerTest {
    @Test
    public void invokeRuntime() throws Exception {
        DashboardController dc = new DashboardController();
        SecInvocation invoke = new SecInvocation();
        List<EventProperty> properties = new ArrayList<>();
        properties.add(new EventPropertyPrimitive("runtimeType", "runtimeName",
                "measurementUnit", null));
        EventStream stream = StreamBuilder.createStream("", "", "").schema(SchemaBuilder.create().properties(properties).build()).build();
        invoke.setInputStreams(Arrays.asList(stream));
        invoke.setCorrespondingPipeline("lalaPipeline");

        dc.invokeRuntime(invoke);
    }

}