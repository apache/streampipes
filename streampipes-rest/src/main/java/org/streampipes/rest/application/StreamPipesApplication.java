package org.streampipes.rest.application;

import org.streampipes.rest.impl.*;
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
        apiClasses.add(ConsulConfig.class);

        // Serializers
        apiClasses.add(GsonWithIdProvider.class);
        apiClasses.add(GsonWithoutIdProvider.class);
        apiClasses.add(GsonClientModelProvider.class);

        return apiClasses;

    }
}
