package org.streampipes.rest.application;

import org.streampipes.rest.impl.ApplicationLink;
import org.streampipes.rest.impl.Authentication;
import org.streampipes.rest.impl.AutoComplete;
import org.streampipes.rest.impl.Deployment;
import org.streampipes.rest.impl.Icon;
import org.streampipes.rest.impl.Notification;
import org.streampipes.rest.impl.OntologyContext;
import org.streampipes.rest.impl.OntologyKnowledge;
import org.streampipes.rest.impl.OntologyMeasurementUnit;
import org.streampipes.rest.impl.OntologyPipelineElement;
import org.streampipes.rest.impl.Pipeline;
import org.streampipes.rest.impl.PipelineCategory;
import org.streampipes.rest.impl.PipelineElementCategory;
import org.streampipes.rest.impl.PipelineElementImport;
import org.streampipes.rest.impl.RdfEndpoint;
import org.streampipes.rest.impl.SemanticEventConsumer;
import org.streampipes.rest.impl.SemanticEventProcessingAgent;
import org.streampipes.rest.impl.SemanticEventProducer;
import org.streampipes.rest.impl.Setup;
import org.streampipes.rest.impl.User;
import org.streampipes.rest.impl.VirtualSensor;
import org.streampipes.rest.impl.Visualization;
import org.streampipes.rest.serializer.GsonClientModelProvider;
import org.streampipes.rest.serializer.GsonWithIdProvider;
import org.streampipes.rest.serializer.GsonWithoutIdProvider;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * Created by riemer on 29.08.2016.
 */
public class StreamPipesApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> apiClasses = new HashSet<>();

        // APIs
        apiClasses.add(Authentication.class);
        apiClasses.add(AutoComplete.class);
        apiClasses.add(PipelineElementCategory.class);
        apiClasses.add(Deployment.class);
        apiClasses.add(Icon.class);
        apiClasses.add(Notification.class);
        apiClasses.add(OntologyContext.class);
        apiClasses.add(OntologyKnowledge.class);
        apiClasses.add(OntologyMeasurementUnit.class);
        apiClasses.add(OntologyPipelineElement.class);
        apiClasses.add(Pipeline.class);
        apiClasses.add(PipelineCategory.class);
        apiClasses.add(PipelineElementImport.class);
        apiClasses.add(SemanticEventConsumer.class);
        apiClasses.add(SemanticEventProcessingAgent.class);
        apiClasses.add(SemanticEventProducer.class);
        apiClasses.add(Setup.class);
        apiClasses.add(VirtualSensor.class);
        apiClasses.add(Visualization.class);
        apiClasses.add(RdfEndpoint.class);
        apiClasses.add(ApplicationLink.class);
        apiClasses.add(User.class);

        // Serializers
        apiClasses.add(GsonWithIdProvider.class);
        apiClasses.add(GsonWithoutIdProvider.class);
        apiClasses.add(GsonClientModelProvider.class);

        return apiClasses;

    }
}
