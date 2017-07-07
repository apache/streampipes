/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.streampipes.pe.sinks.standalone.samples.number;

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
public class NumberController extends ActionController {

        @Override
        public boolean isVisualizable() {
                return false;
        }

        @Override
        public String getHtml(SecInvocation graph) {
                String newUrl = createWebsocketUri(graph);
                String inputTopic = extractTopic(graph);
                String propertyName = SepaUtils.getMappingPropertyName(graph, "mapping");
                String color = SepaUtils.getFreeTextStaticPropertyValue(graph, "color");
                NumberParameters numberParameters = new NumberParameters(inputTopic, newUrl, propertyName, color);
                return new NumberGenerator(numberParameters).generateHtml();
        }

        @Override
        public SecDescription declareModel() {
                SecDescription sec = new SecDescription("number", "Number", "Displays a colored circle with value", "");
                //sec.setIconUrl(ActionConfig.iconBaseUrl + "/---.png");
                sec.setCategory(Arrays.asList(EcType.VISUALIZATION_CHART.name()));
                
                List<EventProperty> eventProperties = new ArrayList<EventProperty>();
                EventProperty e1 = EpRequirements.numberReq();
                eventProperties.add(e1);

                EventSchema schema1 = new EventSchema();
                schema1.setEventProperties(eventProperties);

                EventStream stream1 = new EventStream();
                stream1.setEventSchema(schema1);
                stream1.setUri(ActionConfig.serverUrl + "/" + Utils.getRandomString());
                sec.addEventStream(stream1);

                List<StaticProperty> staticProperties = new ArrayList();
                staticProperties.add(new MappingPropertyUnary(URI.create(e1.getElementId()), "mapping", "Property Mapping", ""));
                staticProperties.add(new FreeTextStaticProperty("color", "Color of the cirlce", ""));
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
