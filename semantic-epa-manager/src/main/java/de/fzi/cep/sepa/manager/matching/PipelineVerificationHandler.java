package de.fzi.cep.sepa.manager.matching;

import de.fzi.cep.sepa.commons.exceptions.NoMatchingJsonSchemaException;
import de.fzi.cep.sepa.commons.exceptions.NoSepaInPipelineException;
import de.fzi.cep.sepa.commons.exceptions.RemoteServerNotAccessibleException;
import de.fzi.cep.sepa.manager.data.PipelineGraph;
import de.fzi.cep.sepa.manager.data.PipelineGraphBuilder;
import de.fzi.cep.sepa.manager.matching.v2.ElementVerification;
import de.fzi.cep.sepa.manager.matching.v2.mapping.MappingPropertyCalculator;
import de.fzi.cep.sepa.manager.util.PipelineVerificationUtils;
import de.fzi.cep.sepa.manager.util.TreeUtils;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.connection.Connection;
import de.fzi.cep.sepa.model.client.exception.InvalidConnectionException;
import de.fzi.cep.sepa.model.client.pipeline.Pipeline;
import de.fzi.cep.sepa.model.client.pipeline.PipelineModification;
import de.fzi.cep.sepa.model.client.pipeline.PipelineModificationMessage;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyList;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.CustomOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.ReplaceOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.UriPropertyMapping;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.Option;
import de.fzi.cep.sepa.model.impl.staticproperty.RemoteOneOfStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.storage.controller.StorageManager;
import org.apache.http.HttpVersion;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.fluent.Response;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class PipelineVerificationHandler {

    Pipeline pipeline;
    PipelineModificationMessage pipelineModificationMessage;

    List<InvocableSEPAElement> invocationGraphs;

    InvocableSEPAElement rdfRootElement;

    public PipelineVerificationHandler(Pipeline pipeline) throws NoSepaInPipelineException {

        this.pipeline = pipeline;
        this.rdfRootElement = PipelineVerificationUtils.getRootNode(pipeline);
        this.invocationGraphs = new ArrayList<>();

        // prepare a list of all pipeline elements without the root element
        List<NamedSEPAElement> sepaElements = new ArrayList<>();
        sepaElements.addAll(pipeline.getSepas());
        sepaElements.addAll(pipeline.getStreams());
        sepaElements.addAll(pipeline.getActions());
        sepaElements.remove(rdfRootElement);

        pipelineModificationMessage = new PipelineModificationMessage();
    }

    public PipelineVerificationHandler validateConnection() throws InvalidConnectionException {

        ElementVerification verifier = new ElementVerification();
        boolean verified = true;
        // current root element can be either an action or a SEPA
        InvocableSEPAElement rightElement = rdfRootElement;
        List<String> connectedTo = rdfRootElement.getConnectedTo();

        Iterator<String> it = connectedTo.iterator();

        while (it.hasNext()) {
            String domId = it.next();
            NamedSEPAElement element = TreeUtils.findSEPAElement(domId, pipeline.getSepas(), pipeline.getStreams());
            if (element instanceof EventStream) {
                EventStream leftEventStream = (EventStream) element;

                if (!(verifier.verify(leftEventStream, rightElement))) verified = false;
            } else {
                invocationGraphs.addAll(makeInvocationGraphs(element));
                SepaInvocation ancestor = findInvocationGraph(invocationGraphs, element.getDOM());
                if (!(verifier.verify(ancestor, rightElement))) verified = false;
            }
        }

        if (!verified) throw new InvalidConnectionException(verifier.getErrorLog());
        return this;
    }
    /**
     * dummy method to compute mapping properties (based on EXACT input/output
     * matching)
     *
     * @return PipelineValidationHandler
     */
   public PipelineVerificationHandler computeMappingProperties() throws RemoteServerNotAccessibleException, NoMatchingJsonSchemaException {
       return computeMappingProperties("");
   }

    /**
     * The username in the signature is used for the streamsets integration. Remove when it is no longer required
     * dummy method to compute mapping properties (based on EXACT input/output
     * matching)
     *
     * @return PipelineValidationHandler
     */

    public PipelineVerificationHandler computeMappingProperties(String username) throws RemoteServerNotAccessibleException, NoMatchingJsonSchemaException {
        List<String> connectedTo = rdfRootElement.getConnectedTo();
        String domId = rdfRootElement.getDOM();

        List<EventStream> tempStreams = new ArrayList<>();

        for (int i = 0; i < connectedTo.size(); i++) {
            NamedSEPAElement element = TreeUtils.findSEPAElement(rdfRootElement
                    .getConnectedTo().get(i), pipeline.getSepas(), pipeline
                    .getStreams());

            EventStream incomingStream;

            if (element instanceof SepaInvocation || element instanceof EventStream) {

                if (element instanceof SepaInvocation) {

                    SepaInvocation ancestor = (SepaInvocation) TreeUtils.findByDomId(
                            connectedTo.get(i), invocationGraphs);

                    incomingStream = ancestor.getOutputStream();
                    updateStaticProperties(ancestor.getOutputStream(), i, username);
                    updateOutputStrategy(ancestor.getOutputStream(), i);

                } else {

                    EventStream stream = (EventStream) element;
                    incomingStream = stream;
                    updateStaticProperties(stream, i, username);
                    updateOutputStrategy(stream, i);

                }

                tempStreams.add(incomingStream);
                if (rdfRootElement.getStreamRequirements().size() - 1 == i) {
                    PipelineModification modification = new PipelineModification(
                            domId,
                            rdfRootElement.getElementId(),
                            rdfRootElement.getStaticProperties());
                    modification.setInputStreams(tempStreams);
                    if (rdfRootElement instanceof SepaInvocation)
                        modification.setOutputStrategies(((SepaInvocation) rdfRootElement).getOutputStrategies());
                    pipelineModificationMessage
                            .addPipelineModification(modification);
                }
            }
        }
        return this;
    }

    public void updateStaticProperties(EventStream stream, Integer count, String username) throws RemoteServerNotAccessibleException, NoMatchingJsonSchemaException {

        rdfRootElement
                .getStaticProperties()
                .stream()
                .filter(property -> property instanceof MappingProperty)
                .forEach(property -> {
                    try {

                        MappingProperty mappingProperty = (MappingProperty) property;

                        if (mappingProperty.getMapsFrom() != null) {
                            if (inStream(rdfRootElement.getStreamRequirements().get(count), mappingProperty.getMapsFrom())) {
                                mappingProperty.setMapsFromOptions(new ArrayList<>());
                                ((MappingProperty) property)
                                        .setMapsFromOptions(findSupportedEventProperties(stream,
                                                rdfRootElement.getStreamRequirements(),
                                                mappingProperty.getMapsFrom()));
                            }
                        } else {
                            mappingProperty.setMapsFromOptions(new ArrayList<>());
                            for (EventProperty streamProperty : stream
                                    .getEventSchema().getEventProperties()) {

                                if ((streamProperty instanceof EventPropertyPrimitive) || streamProperty instanceof EventPropertyList) {
                                    mappingProperty.getMapsFromOptions().add(streamProperty);
                                } else {
                                    mappingProperty.getMapsFromOptions().addAll(addNestedProperties((EventPropertyNested) streamProperty));
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });


        List<StaticProperty> allProperties = rdfRootElement
                .getStaticProperties()
                .stream()
                .filter(property -> property instanceof RemoteOneOfStaticProperty)
                .collect(Collectors.toList());;

        for (StaticProperty property : allProperties) {
            updateRemoteOneOfStaticProperty((RemoteOneOfStaticProperty) property, username);
        }

    }

    /**
     * Calls the remote URL and uses the result to set the options of the OneOfStaticProperty
     * @param property
     */
    private void updateRemoteOneOfStaticProperty(RemoteOneOfStaticProperty property, String username) throws RemoteServerNotAccessibleException, NoMatchingJsonSchemaException {

        String label = property.getLabelFieldName();
        String description = property.getDescriptionFieldName();
        String value = property.getValueFieldName();

        try {
            //TODO make this part generic currently it just works with the streamstory component
            String operation = "";
            if (rdfRootElement.getBelongsTo().contains("activity")) {
                operation = "Activity";
            } else {
               operation = "Prediction";
            }

            String url = property.getRemoteUrl() + "streampipes/models?username=" + username + "&analyticsOperation=" + operation;
            Response res = Request.Get(url).useExpectContinue().setHeader("Content-Type", "application/json")
                    .version(HttpVersion.HTTP_1_1)
                    .execute();


            JSONArray data = new JSONArray(res.returnContent().toString());

            List<Option> options = new ArrayList<>();
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);
                options.add(new Option(object.getString(value)));
            }

            property.setOptions(options);
        } catch (JSONException e) {
            e.printStackTrace();
            throw new NoMatchingJsonSchemaException();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RemoteServerNotAccessibleException(e.toString(), property.getRemoteUrl());
        }

    }



    private boolean inStream(EventStream stream, URI mapsFrom) {
        return stream
                .getEventSchema()
                .getEventProperties()
                .stream().anyMatch(ep -> ep.getElementId().equals(mapsFrom.toString()));
    }

    private List<EventProperty> findSupportedEventProperties(EventStream streamOffer, List<EventStream> streamRequirements, URI mapsFrom) {
        EventProperty mapsFromProperty = TreeUtils
                .findEventProperty(mapsFrom.toString(), rdfRootElement.getStreamRequirements());

        return new MappingPropertyCalculator().matchesProperties(streamOffer.getEventSchema().getEventProperties(), mapsFromProperty);
    }

    private void updateOutputStrategy(EventStream stream, Integer count) {

        if (rdfRootElement instanceof SepaInvocation) {
            ((SepaInvocation) rdfRootElement)
                    .getOutputStrategies()
                    .stream()
                    .filter(strategy -> strategy instanceof CustomOutputStrategy)
                    .forEach(strategy -> {
                        CustomOutputStrategy outputStrategy = (CustomOutputStrategy) strategy;
                        if (count == 0) {
                            outputStrategy.setProvidesProperties(new ArrayList<>());
                        }
                        if (outputStrategy.isOutputRight() && count > 0)
                            outputStrategy.setProvidesProperties(stream.getEventSchema().getEventProperties());
                        else {
                            if (outputStrategy.getProvidesProperties() == null) {
                                outputStrategy.setProvidesProperties(new ArrayList<>());
                            }
                            outputStrategy.getProvidesProperties().addAll(stream.getEventSchema().getEventProperties());
                        }
                    });

            ((SepaInvocation) rdfRootElement)
                    .getOutputStrategies()
                    .stream()
                    .filter(strategy -> strategy instanceof ReplaceOutputStrategy)
                    .forEach(strategy -> {
                        ReplaceOutputStrategy outputStrategy = (ReplaceOutputStrategy) strategy;

                        for (UriPropertyMapping mapping : outputStrategy.getReplaceProperties()) {
                            if (mapping.getReplaceFrom() != null) {
                                mapping.setReplaceWithOptions(findSupportedEventProperties(stream, rdfRootElement.getStreamRequirements(), mapping.getReplaceFrom()));
                            } else {
                                mapping.setReplaceWithOptions(stream.getEventSchema().getEventProperties());
                            }
                        }
                    });
        }
    }

    private List<EventProperty> addNestedProperties(EventPropertyNested properties) {
        List<EventProperty> options = new ArrayList<>();
        for (EventProperty p : properties.getEventProperties()) {
            if (p instanceof EventPropertyPrimitive) options.add(p);
            else options.addAll(addNestedProperties(properties));
        }
        return options;
    }

    public PipelineVerificationHandler storeConnection() {
        String fromId = rdfRootElement.getConnectedTo().get(rdfRootElement.getConnectedTo().size() - 1);
        NamedSEPAElement sepaElement = TreeUtils.findSEPAElement(fromId, pipeline.getSepas(), pipeline.getStreams());
        String sourceId;
        if (sepaElement instanceof EventStream) {
            sourceId = sepaElement.getElementId();
        } else {
            sourceId = ((InvocableSEPAElement) sepaElement).getBelongsTo();
        }
        Connection connection = new Connection(sourceId, rdfRootElement.getBelongsTo());
        StorageManager.INSTANCE.getConnectionStorageApi().addConnection(connection);
        return this;
    }

    public PipelineModificationMessage getPipelineModificationMessage() {
        return pipelineModificationMessage;
    }


    private List<InvocableSEPAElement> makeInvocationGraphs(NamedSEPAElement rootElement) {
        PipelineGraph pipelineGraph = new PipelineGraphBuilder(pipeline).buildGraph();
        return new InvocationGraphBuilder(pipelineGraph, null).buildGraphs();
    }

    private SepaInvocation findInvocationGraph(List<InvocableSEPAElement> graphs, String domId) {
        return (SepaInvocation) TreeUtils.findByDomId(domId, graphs);
    }

}