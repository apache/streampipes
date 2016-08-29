package de.fzi.cep.sepa.rest.application;


import de.fzi.cep.sepa.rest.impl.*;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by riemer on 29.08.2016.
 */
public class StreamPipesApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> apiClasses = new HashSet<>();

        // APIs
        apiClasses.add(AppStore.class);
        apiClasses.add(Authentication.class);
        apiClasses.add(Category.class);
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

        return apiClasses;

    }
}
