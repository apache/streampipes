package de.fzi.cep.sepa.rest.application;

import de.fzi.cep.sepa.rest.impl.ApplicationLink;
import de.fzi.cep.sepa.rest.impl.Authentication;
import de.fzi.cep.sepa.rest.impl.AutoComplete;
import de.fzi.cep.sepa.rest.impl.Deployment;
import de.fzi.cep.sepa.rest.impl.Icon;
import de.fzi.cep.sepa.rest.impl.Notification;
import de.fzi.cep.sepa.rest.impl.OntologyContext;
import de.fzi.cep.sepa.rest.impl.OntologyKnowledge;
import de.fzi.cep.sepa.rest.impl.OntologyMeasurementUnit;
import de.fzi.cep.sepa.rest.impl.OntologyPipelineElement;
import de.fzi.cep.sepa.rest.impl.Pipeline;
import de.fzi.cep.sepa.rest.impl.PipelineCategory;
import de.fzi.cep.sepa.rest.impl.PipelineElementCategory;
import de.fzi.cep.sepa.rest.impl.PipelineElementImport;
import de.fzi.cep.sepa.rest.impl.RdfEndpoint;
import de.fzi.cep.sepa.rest.impl.SemanticEventConsumer;
import de.fzi.cep.sepa.rest.impl.SemanticEventProcessingAgent;
import de.fzi.cep.sepa.rest.impl.SemanticEventProducer;
import de.fzi.cep.sepa.rest.impl.Setup;
import de.fzi.cep.sepa.rest.impl.User;
import de.fzi.cep.sepa.rest.impl.VirtualSensor;
import de.fzi.cep.sepa.rest.impl.Visualization;
import de.fzi.cep.sepa.rest.serializer.GsonClientModelProvider;
import de.fzi.cep.sepa.rest.serializer.GsonWithIdProvider;
import de.fzi.cep.sepa.rest.serializer.GsonWithoutIdProvider;

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
