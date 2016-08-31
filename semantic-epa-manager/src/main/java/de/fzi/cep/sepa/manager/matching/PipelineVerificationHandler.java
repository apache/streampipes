package de.fzi.cep.sepa.manager.matching;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.manager.matching.v2.ElementVerification;
import de.fzi.cep.sepa.manager.matching.v2.mapping.MappingPropertyCalculator;
import de.fzi.cep.sepa.manager.util.PipelineVerificationUtils;
import de.fzi.cep.sepa.manager.util.TreeUtils;
import de.fzi.cep.sepa.messages.PipelineModification;
import de.fzi.cep.sepa.messages.PipelineModificationMessage;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.connection.Connection;
import de.fzi.cep.sepa.model.client.exception.InvalidConnectionException;

import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.eventproperty.EventProperty;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyList;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyNested;
import de.fzi.cep.sepa.model.impl.eventproperty.EventPropertyPrimitive;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.MappingProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.MatchingStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.Option;
import de.fzi.cep.sepa.model.impl.staticproperty.StaticProperty;
import de.fzi.cep.sepa.storage.controller.StorageManager;


public class PipelineVerificationHandler {

    Pipeline pipeline;
    PipelineModificationMessage pipelineModificationMessage;

    List<InvocableSEPAElement> invocationGraphs;

    InvocableSEPAElement rdfRootElement;
    GenericTree<NamedSEPAElement> rdfTree;

    public PipelineVerificationHandler(Pipeline pipeline, boolean isPartial)
            throws Exception {

        this.pipeline = pipeline;
        this.rdfRootElement = PipelineVerificationUtils.getRootNode(pipeline);
        this.invocationGraphs = new ArrayList<>();

        // prepare a list of all pipeline elements without the root element
        List<NamedSEPAElement> sepaElements = new ArrayList<NamedSEPAElement>();
        sepaElements.addAll(pipeline.getSepas());
        sepaElements.addAll(pipeline.getStreams());
        sepaElements.add(pipeline.getAction());
        sepaElements.remove(rdfRootElement);

        // we need a tree of invocation graphs if there is more than one SEPA
        rdfTree = new TreeBuilder(pipeline, rdfRootElement).generateTree(true);

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

    public PipelineVerificationHandler computeMappingProperties() {
        try {
            List<String> connectedTo = rdfRootElement.getConnectedTo();
            String domId = rdfRootElement.getDOM();

            List<StaticProperty> currentStaticProperties = rdfRootElement
                    .getStaticProperties();

            currentStaticProperties = clearOptions(currentStaticProperties);

            for (int i = 0; i < connectedTo.size(); i++) {
                NamedSEPAElement element = TreeUtils.findSEPAElement(rdfRootElement
                        .getConnectedTo().get(i), pipeline.getSepas(), pipeline
                        .getStreams());

                if (element instanceof SepaInvocation || element instanceof EventStream || element instanceof SecInvocation) {
                    InvocableSEPAElement currentSEPA = rdfRootElement;

                    if (element instanceof SepaInvocation) {

                        SepaInvocation ancestor = (SepaInvocation) TreeUtils.findByDomId(
                                connectedTo.get(i), invocationGraphs);

                        //currentStaticProperties = updateStaticProperties(currentStaticProperties, currentSEPA, ancestor.getOutputStream(), i);

                    } else if (element instanceof EventStream) {

                        EventStream stream = (EventStream) element;

                        //currentStaticProperties = updateStaticProperties(currentStaticProperties, currentSEPA, stream, i);
                    }
                    if (currentSEPA.getStreamRequirements().size() - 1 == i) {
                        PipelineModification modification = new PipelineModification(
                                domId, rdfRootElement.getElementId(), currentStaticProperties);
                        pipelineModificationMessage
                                .addPipelineModification(modification);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public PipelineVerificationHandler storeConnection() {
        String fromId = rdfRootElement.getConnectedTo().get(rdfRootElement.getConnectedTo().size() - 1);
        NamedSEPAElement sepaElement = TreeUtils.findSEPAElement(fromId, pipeline.getSepas(), pipeline.getStreams());

        Connection connection = new Connection(sepaElement.getElementId(), rdfRootElement.getElementId());
        StorageManager.INSTANCE.getConnectionStorageApi().addConnection(connection);
        return this;
    }

    public PipelineVerificationHandler computeMatchingProperties() {
        return this;
    }

    public PipelineModificationMessage getPipelineModificationMessage() {
        return pipelineModificationMessage;
    }


    private List<StaticProperty> updateStaticProperties(List<StaticProperty> currentStaticProperties, InvocableSEPAElement currentSEPA, EventStream ancestorOutputStream, int i) {
//        List<StaticProperty> newStaticProperties = new ArrayList<>();
//
//        for (de.fzi.cep.sepa.model.client.StaticProperty clientStaticProperty : currentStaticProperties) {
//            if (clientStaticProperty.getType() == StaticPropertyType.MATCHING_PROPERTY) {
//                List<Option> options;
//                if (i == 0) options = ((RadioGroupInput) clientStaticProperty.getInput()).getOptionLeft();
//                else options = ((RadioGroupInput) clientStaticProperty.getInput()).getOptionRight();
//
//                if (options.size() > 0)
//                    options.addAll(updateMatchingOptions(clientStaticProperty, currentSEPA, ancestorOutputStream, i));
//                else options = updateMatchingOptions(clientStaticProperty, currentSEPA, ancestorOutputStream, i);
//                newStaticProperties.add(updateStaticProperty(clientStaticProperty, options, i == 0));
//            } else if (clientStaticProperty.getType() == StaticPropertyType.MAPPING_PROPERTY) {
//
//                List<Option> options = ((SelectInput) clientStaticProperty.getInput()).getOptions();
//
//                if (options.size() > 0)
//                    options.addAll(updateMappingOptions(clientStaticProperty, currentSEPA, ancestorOutputStream, i));
//                else options = updateMappingOptions(clientStaticProperty, currentSEPA, ancestorOutputStream, i);
//                newStaticProperties.add(updateStaticProperty(clientStaticProperty, options, true));
//
//            } else if (clientStaticProperty.getType() == StaticPropertyType.CUSTOM_OUTPUT) {
//                SepaDescription convertedSepaElement = (SepaDescription) currentSEPA;
//                if (convertedSepaElement.getOutputStrategies().get(0) instanceof CustomOutputStrategy) {
//                    CustomOutputStrategy customOutput = (CustomOutputStrategy) convertedSepaElement.getOutputStrategies().get(0);
//                    List<Option> options = ((CheckboxInput) clientStaticProperty.getInput()).getOptions();
//                    if (options.size() == 0)
//                        options = convertCustomOutput(ancestorOutputStream.getEventSchema().getEventProperties(), new ArrayList<Option>());
//                    if (i > 0 && customOutput.isOutputRight())
//                        options.addAll(convertCustomOutput(ancestorOutputStream.getEventSchema().getEventProperties(), new ArrayList<Option>()));
//                    newStaticProperties.add(updateStaticProperty(clientStaticProperty, options, true));
//                }
//            } else if (clientStaticProperty.getType() == StaticPropertyType.REPLACE_OUTPUT) {
//                SepaDescription convertedSepaElement = (SepaDescription) currentSEPA;
//                if (convertedSepaElement.getOutputStrategies().get(0) instanceof ReplaceOutputStrategy) {
//                    ReplaceOutputStrategy replaceOutput = (ReplaceOutputStrategy) convertedSepaElement.getOutputStrategies().get(0);
//                    ReplaceOutputInput input = ((ReplaceOutputInput) clientStaticProperty.getInput());
//                    for (int j = 0; j < replaceOutput.getReplaceProperties().size(); j++) {
//                        UriPropertyMapping upm = replaceOutput.getReplaceProperties().get(j);
//                        List<Option> options = updateOptions(clientStaticProperty, currentSEPA, ancestorOutputStream, i, upm.getReplaceFrom());
//                        input.getPropertyMapping().get(j).setInput(new SelectFormInput(options));
//                    }
//                    de.fzi.cep.sepa.model.client.StaticProperty newProperty = new de.fzi.cep.sepa.model.client.StaticProperty();
//                    newProperty.setName(clientStaticProperty.getName());
//                    newProperty.setDescription(clientStaticProperty
//                            .getDescription());
//                    newProperty.setDOM(clientStaticProperty.getDOM());
//                    newProperty.setElementId(clientStaticProperty
//                            .getElementId());
//                    newProperty.setType(clientStaticProperty.getType());
//                    newProperty.setInput(input);
//                    newStaticProperties.add(newProperty);
//
//                }
//
//            } else
//                newStaticProperties.add(clientStaticProperty);
//        }

        return currentSEPA.getStaticProperties();
    }

    private List<StaticProperty> clearOptions(
            List<StaticProperty> currentStaticProperties) {
//        Iterator<StaticProperty> it = currentStaticProperties.iterator();
//        while (it.hasNext()) {
//            StaticProperty p = (StaticProperty) it.next();
//            if (p instanceof MappingProperty) {
//                ((MappingProperty) p).setOptions(new ArrayList<>());
//            } else if ((p.getType() == StaticPropertyType.CUSTOM_OUTPUT)) {
//                CheckboxInput input = (CheckboxInput) p.getInput();
//                input.setOptions(new ArrayList<Option>());
//            }
//        }
        return currentStaticProperties;
    }

    private de.fzi.cep.sepa.model.client.StaticProperty updateStaticProperty(de.fzi.cep.sepa.model.client.StaticProperty currentStaticProperty, List<Option> newOption, boolean firstStream) {
        de.fzi.cep.sepa.model.client.StaticProperty newProperty = new de.fzi.cep.sepa.model.client.StaticProperty();
        newProperty.setName(currentStaticProperty.getName());
        newProperty.setDescription(currentStaticProperty
                .getDescription());
        newProperty.setDOM(currentStaticProperty.getDOM());
        newProperty.setElementId(currentStaticProperty
                .getElementId());

//        if (currentStaticProperty.getInput() instanceof SelectFormInput)
//            newProperty.setInput(new SelectFormInput(newOption));
//        else if (currentStaticProperty.getInput() instanceof RadioGroupInput) {
//            RadioGroupInput input = (RadioGroupInput) currentStaticProperty.getInput();
//            if (firstStream) input.setOptionLeft(newOption);
//            else input.setOptionRight(newOption);
//            newProperty.setInput(input);
//        } else newProperty.setInput(new CheckboxInput(newOption));
        newProperty.setType(currentStaticProperty.getType());
        return newProperty;
    }

    private List<Option> updateMatchingOptions(de.fzi.cep.sepa.model.client.StaticProperty clientStaticProperty, de.fzi.cep.sepa.model.ConsumableSEPAElement sepa, EventStream stream, int i) {
        MatchingStaticProperty mp = TreeUtils.findMatchingProperty(clientStaticProperty.getElementId(), sepa);

        URI maps;
        if (i == 0) maps = mp.getMatchLeft();
        else maps = mp.getMatchRight();

        return updateOptions(clientStaticProperty, sepa, stream, i, maps);
    }

    private List<Option> updateMappingOptions(de.fzi.cep.sepa.model.client.StaticProperty clientStaticProperty, de.fzi.cep.sepa.model.ConsumableSEPAElement sepa, EventStream leftStream, int i) {
        MappingProperty mp = TreeUtils.findMappingProperty(
                clientStaticProperty.getElementId(), sepa);

        return updateOptions(clientStaticProperty, sepa, leftStream, i, mp.getMapsFrom());
    }

    private List<Option> updateOptions(de.fzi.cep.sepa.model.client.StaticProperty clientStaticProperty, de.fzi.cep.sepa.model.ConsumableSEPAElement sepa, EventStream leftStream, int i, URI maps) {

        List<Option> options = new ArrayList<>();

        if (maps != null) {
            EventProperty rightProperty = TreeUtils
                    .findEventProperty(maps
                            .toString(), sepa.getEventStreams());


            if (sepa.getEventStreams().get(i).getEventSchema().getEventProperties().contains(rightProperty)) {
                List<EventProperty> offeredMatchingProperties = new MappingPropertyCalculator()
                        .matchesProperties(leftStream.getEventSchema().getEventProperties(), rightProperty);

//                for (EventProperty matchedStreamProperty : offeredMatchingProperties) {
//                    options.add(new Option(matchedStreamProperty
//                            .getElementId().toString(),
//                            matchedStreamProperty.getRuntimeName()));
//                }
            } else {
                List<EventProperty> offeredMatchingProperties = new MappingPropertyCalculator().matchesPropertiesList(
                        leftStream.getEventSchema()
                                .getEventProperties(), rightProperty);

//                for (EventProperty matchedStreamProperty : offeredMatchingProperties) {
//                    options.add(new Option(matchedStreamProperty
//                            .getElementId().toString(),
//                            matchedStreamProperty.getRuntimeName()));
//                }
            }

        } else {
            for (EventProperty streamProperty : leftStream
                    .getEventSchema().getEventProperties()) {
                if ((streamProperty instanceof EventPropertyPrimitive) || streamProperty instanceof EventPropertyList) {
//                    options.add(new Option(streamProperty
//                            .getRdfId().toString(), streamProperty
//                            .getRuntimeName()));
                } else {
                    options.addAll(addNestedOptions((EventPropertyNested) streamProperty));
                }
            }
        }
        return options;
    }

    private List<Option> addNestedOptions(EventPropertyNested properties) {
        List<Option> options = new ArrayList<>();
        for (EventProperty p : properties.getEventProperties()) {
            if (p instanceof EventPropertyPrimitive) {
                //options.add(new Option(p.getRdfId().toString(), p.getRuntimeName()));
            }
            else options.addAll(addNestedOptions(properties));
        }
        return options;
    }

    private List<InvocableSEPAElement> makeInvocationGraphs(NamedSEPAElement rootElement) {
        GenericTree<NamedSEPAElement> tree = new TreeBuilder(
                pipeline, rootElement).generateTree(true);
        return new InvocationGraphBuilder(tree, true, null).buildGraph();
    }

    private SepaInvocation findInvocationGraph(List<InvocableSEPAElement> graphs, String domId) {
        return (SepaInvocation) TreeUtils.findByDomId(domId, invocationGraphs);
    }

    private List<Option> convertCustomOutput(List<EventProperty> eventProperties, List<Option> options) {

        for (EventProperty p : eventProperties) {
//            if (p instanceof EventPropertyPrimitive || p instanceof EventPropertyList) {
//                String runtimeName = p.getRuntimeName();
//                if (p instanceof EventPropertyPrimitive) options.add(new Option(p.getElementId(), runtimeName));
//                else if (p instanceof EventPropertyList) options.add(new Option(p.getElementId(), runtimeName));
//            } else if (p instanceof EventPropertyNested) {
//                options.add(new Option(p.getElementId(), p.getRuntimeName()));
//                options.addAll(convertCustomOutput(((EventPropertyNested) p).getEventProperties(), new ArrayList<Option>()));
//            }
        }
        return options;
    }


}
