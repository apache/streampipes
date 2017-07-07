/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.streampipes.pe.sinks.standalone.samples.verticalbar;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.pe.sinks.standalone.samples.ActionController;
import org.streampipes.pe.sinks.standalone.samples.util.ActionUtils;
import org.streampipes.commons.Utils;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.model.impl.EcType;
import org.streampipes.model.impl.EventSchema;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.util.SepaUtils;

/**
 *
 * @author eberle
 */
public class VerticalBarController extends ActionController {

        @Override
        public boolean isVisualizable() {
                return false;
        }

        @Override
        public String getHtml(SecInvocation graph) {
                String newUrl = createWebsocketUri(graph);
                String inputTopic = extractTopic(graph);
                int min = Integer.parseInt(((FreeTextStaticProperty) (SepaUtils
                        .getStaticPropertyByInternalName(graph, "min"))).getValue());
                int max = Integer.parseInt(((FreeTextStaticProperty) (SepaUtils
                        .getStaticPropertyByInternalName(graph, "max"))).getValue());
                String propertyName = SepaUtils.getMappingPropertyName(graph, "mapping");
                String color = SepaUtils.getFreeTextStaticPropertyValue(graph, "color");
                VerticalBarParameters verticalBarParameters = new VerticalBarParameters(inputTopic, newUrl, min, max, propertyName, color);
                return new VerticalBarGenerator(verticalBarParameters).generateHtml();
        }

        @Override
        public SecDescription declareModel() {
                SecDescription sec = new SecDescription("verticalBar", "VerticalBar", "Displays the current value in dependence to a max and min value.");
                sec.setCategory(Arrays.asList(EcType.VISUALIZATION_CHART.name()));
                EventStream stream1 = new EventStream();
                EventSchema schema1 = new EventSchema();

                List<EventProperty> eventProperties = new ArrayList<EventProperty>();
                EventProperty e1 = EpRequirements.numberReq();
                eventProperties.add(e1);
                schema1.setEventProperties(eventProperties);
                stream1.setEventSchema(schema1);

                stream1.setUri(ActionConfig.serverUrl + "/" + Utils.getRandomString());
                sec.addEventStream(stream1);

                List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
                staticProperties.add(new FreeTextStaticProperty("min", "min value", ""));
                staticProperties.add(new FreeTextStaticProperty("max", "max value", ""));
                staticProperties.add(new FreeTextStaticProperty("color", "Color of the cirlce", ""));

                staticProperties.add(new MappingPropertyUnary(URI.create(e1.getElementId()), "mapping", "Select Mapping", ""));

                sec.setStaticProperties(staticProperties);
                sec.setSupportedGrounding(ActionUtils.getSupportedGrounding());

                return sec;
        }

    @Override
    public Response invokeRuntime(SecInvocation invocationGraph) {
        String pipelineId = invocationGraph.getCorrespondingPipeline();
        return new Response(pipelineId, true);
    }

    @Override
    public Response detachRuntime(String pipelineId) {
        return new Response(pipelineId, true);
    }

}
