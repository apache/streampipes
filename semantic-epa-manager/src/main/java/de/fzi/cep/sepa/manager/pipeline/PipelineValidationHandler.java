package de.fzi.cep.sepa.manager.pipeline;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openrdf.model.Graph;
import org.openrdf.model.impl.GraphImpl;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.JSONLDMode;
import org.openrdf.rio.helpers.JSONLDSettings;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.exceptions.NoValidConnectionException;
import de.fzi.cep.sepa.manager.util.ClientModelUtils;
import de.fzi.cep.sepa.manager.util.TreeUtils;
import de.fzi.cep.sepa.manager.validator.ConnectionValidator;
import de.fzi.cep.sepa.messages.PipelineModification;
import de.fzi.cep.sepa.messages.PipelineModificationMessage;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.client.Pipeline;
import de.fzi.cep.sepa.model.client.SEPAClient;
import de.fzi.cep.sepa.model.client.SEPAElement;
import de.fzi.cep.sepa.model.client.StaticPropertyType;
import de.fzi.cep.sepa.model.client.StreamClient;
import de.fzi.cep.sepa.model.client.input.Option;
import de.fzi.cep.sepa.model.client.input.SelectFormInput;
import de.fzi.cep.sepa.model.impl.EventProperty;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.MappingProperty;
import de.fzi.cep.sepa.model.impl.graph.SEC;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.storage.util.Transformer;

public class PipelineValidationHandler {

	Pipeline pipeline;
	PipelineModificationMessage pipelineModificationMessage;

	List<SEPAInvocationGraph> invocationGraphs;
	List<SEPAElement> sepaClientElements;

	SEPAClient rootElement;
	NamedSEPAElement sepaRootElement;

	GenericTree<SEPAElement> clientTree;
	GenericTree<NamedSEPAElement> modelTree;

	public PipelineValidationHandler(Pipeline pipeline, boolean isPartial)
			throws Exception {
		this.pipeline = pipeline;
		this.rootElement = ClientModelUtils.getRootNode(pipeline);
		this.sepaRootElement = ClientModelUtils.transform(rootElement);

		// prepare a list of all pipeline elements without the root element
		List<SEPAElement> sepaElements = new ArrayList<SEPAElement>();
		sepaElements.addAll(pipeline.getSepas());
		sepaElements.addAll(pipeline.getStreams());
		sepaElements.remove(rootElement);

		// we need a tree of invocation graphs if there is more than one SEPA
		clientTree = new TreeBuilder(pipeline, rootElement)
				.generateClientTree();
		if (clientTree.maxDepth(clientTree.getRoot()) > 2)
			modelTree = new TreeBuilder(pipeline, rootElement)
					.generateTree(true);

		pipelineModificationMessage = new PipelineModificationMessage();
	}

	/**
	 * 
	 * @return
	 */
	public PipelineValidationHandler validateConnection()
			throws NoValidConnectionException {
		// determines if root element and current ancestor can be matched
		boolean match = false;

		// current root element can be either an action or a SEPA
		NamedSEPAElement rightElement = ClientModelUtils.transform(rootElement);

		// root element is an action
		if (rightElement instanceof SEC) {
			SEC sec = (SEC) rightElement;
			// match = ConnectionValidator.validateGrounding(left, sec.get);
		}

		// root element is SEPA
		if (rightElement instanceof SEPA) {
			SEPA sepa = (SEPA) rightElement;

			if (rootElement.getConnectedTo().size() == 1) {
				List<EventSchema> left;
				SEPAElement element = TreeUtils.findSEPAElement(rootElement
						.getConnectedTo().get(0), pipeline.getSepas(), pipeline
						.getStreams());
				if (element instanceof StreamClient) {
					left = Utils.createList(((EventStream) ClientModelUtils
							.transform(element)).getEventSchema());
				} else {
					GenericTree<NamedSEPAElement> tree = new TreeBuilder(
							pipeline, element).generateTree(true);
					invocationGraphs = new InvocationGraphBuilder(tree, true)
							.buildGraph();

					SEPAInvocationGraph ancestor = TreeUtils.findByDomId(
							element.getDOM(), invocationGraphs);
					left = Utils.createList(ancestor.getOutputStream()
							.getEventSchema());
					// System.out.println(ancestor.getOutputStream().getEventSchema().getEventProperties().size());
				}
				match = ConnectionValidator.validateSchema(
						left,
						Utils.createList(sepa.getEventStreams().get(0)
								.getEventSchema()));
				System.out.println(match);
			} else if (rootElement.getConnectedTo().size() == 2) {

				SEPAElement firstElement = TreeUtils.findSEPAElement(
						rootElement.getConnectedTo().get(0),
						pipeline.getSepas(), pipeline.getStreams());
				SEPAElement secondElement = TreeUtils.findSEPAElement(
						rootElement.getConnectedTo().get(1),
						pipeline.getSepas(), pipeline.getStreams());

				List<EventSchema> firstLeft = Utils
						.createList(((EventStream) ClientModelUtils
								.transform(firstElement)).getEventSchema());
				List<EventSchema> secondLeft = Utils
						.createList(((EventStream) ClientModelUtils
								.transform(secondElement)).getEventSchema());
				;

				match = ConnectionValidator.validateSchema(
						firstLeft,
						secondLeft,
						Utils.createList(sepa.getEventStreams().get(0)
								.getEventSchema()),
						Utils.createList(sepa.getEventStreams().get(1)
								.getEventSchema()));
			}
		}
		if (!match)
			throw new NoValidConnectionException();

		return this;
	}

	/**
	 * dummy method to compute mapping properties (based on EXACT input/output
	 * matching)
	 * 
	 * @return PipelineValidationHandler
	 */

	public PipelineValidationHandler computeMappingProperties() {
		List<String> connectedTo = rootElement.getConnectedTo();
		String domId = rootElement.getDOM();
		List<de.fzi.cep.sepa.model.client.StaticProperty> currentStaticProperties = rootElement
				.getStaticProperties();
		List<de.fzi.cep.sepa.model.client.StaticProperty> newStaticProperties = new ArrayList<>();

		if (connectedTo.size() == 1) {
			SEPAElement element = TreeUtils.findSEPAElement(rootElement
					.getConnectedTo().get(0), pipeline.getSepas(), pipeline
					.getStreams());

			// TODO: eliminate duplicates
			if (element instanceof SEPAClient) {
				SEPAInvocationGraph ancestor = TreeUtils.findByDomId(
						connectedTo.get(0), invocationGraphs);
				SEPA currentSEPA = (SEPA) sepaRootElement;

				for (de.fzi.cep.sepa.model.client.StaticProperty clientStaticProperty : currentStaticProperties) {
					if (clientStaticProperty.getType() == StaticPropertyType.MAPPING_PROPERTY) {
						MappingProperty mp = TreeUtils.findMappingProperty(
								clientStaticProperty.getElementId(),
								currentSEPA);
						List<Option> options = new ArrayList<>();

						if (mp.getMapsFrom() != null) {
							EventProperty eventProperty = TreeUtils
									.findEventProperty(mp.getMapsFrom()
											.toString(), currentSEPA
											.getEventStreams());

							List<EventProperty> leftMatchingProperties = matches(
									eventProperty, ancestor.getOutputStream()
											.getEventSchema()
											.getEventProperties());

							for (EventProperty matchedStreamProperty : leftMatchingProperties) {
								options.add(new Option(matchedStreamProperty
										.getRdfId().toString(),
										matchedStreamProperty.getPropertyName()));
							}
						} else {
							for (EventProperty streamProperty : ancestor
									.getOutputStream().getEventSchema()
									.getEventProperties()) {
								options.add(new Option(streamProperty
										.getRdfId().toString(), streamProperty
										.getPropertyName()));
							}
						}

						newStaticProperties.add(updateStaticProperty(clientStaticProperty, options));

					} else
						newStaticProperties.add(clientStaticProperty);
				}
				PipelineModification modification = new PipelineModification(
						domId, rootElement.getElementId(), newStaticProperties);
				pipelineModificationMessage
						.addPipelineModification(modification);
			} else if (element instanceof StreamClient) {
				SEPA sepa = (SEPA) sepaRootElement;
				EventStream stream = (EventStream) ClientModelUtils
						.transform(element);

				for (de.fzi.cep.sepa.model.client.StaticProperty clientStaticProperty : currentStaticProperties) {
					if (clientStaticProperty.getType() == StaticPropertyType.MAPPING_PROPERTY) {
						MappingProperty mp = TreeUtils.findMappingProperty(
								clientStaticProperty.getElementId(), sepa);
						List<Option> options = new ArrayList<>();

						if (mp.getMapsFrom() != null) {
							EventProperty rightProperty = TreeUtils
									.findEventProperty(mp.getMapsFrom()
											.toString(), sepa.getEventStreams());

							List<EventProperty> leftMatchingProperties = matches(
									rightProperty, stream.getEventSchema()
											.getEventProperties());

							for (EventProperty matchedStreamProperty : leftMatchingProperties) {
								options.add(new Option(matchedStreamProperty
										.getRdfId().toString(),
										matchedStreamProperty.getPropertyName()));
							}
						} else {
							for (EventProperty streamProperty : stream
									.getEventSchema().getEventProperties()) {
								options.add(new Option(streamProperty
										.getRdfId().toString(), streamProperty
										.getPropertyName()));
							}
						}
						newStaticProperties.add(updateStaticProperty(clientStaticProperty, options));

					} else
						newStaticProperties.add(clientStaticProperty);
				}
				
				PipelineModification modification = new PipelineModification(
						domId, rootElement.getElementId(), newStaticProperties);
				pipelineModificationMessage
						.addPipelineModification(modification);
			}
		} else {

		}

		return this;
	}

	public PipelineValidationHandler computeMatchingProperties() {

		return this;
	}

	public PipelineModificationMessage getPipelineModificationMessage() {
		return pipelineModificationMessage;
	}

	private List<EventProperty> matches(EventProperty right,
			List<EventProperty> left) {
		List<EventProperty> matchingProperties = new ArrayList<>();
		for (EventProperty l : left) {
			if (matches(right, l))
				matchingProperties.add(l);
		}
		return matchingProperties;
	}

	private boolean matches(EventProperty right, EventProperty left) {
		boolean match = true;
		List<URI> leftUris = left.getSubClassOf();
		for (URI uri : right.getSubClassOf()) {
			if (!leftUris.contains(uri))
				match = false;
		}
		return match;
	}

	private de.fzi.cep.sepa.model.client.StaticProperty updateStaticProperty(de.fzi.cep.sepa.model.client.StaticProperty currentStaticProperty, List<Option> newOption)
	{
		de.fzi.cep.sepa.model.client.StaticProperty newProperty = new de.fzi.cep.sepa.model.client.StaticProperty();
		newProperty.setName(currentStaticProperty.getName());
		newProperty.setDescription(currentStaticProperty
				.getDescription());
		newProperty.setDOM(currentStaticProperty.getDOM());
		newProperty.setElementId(currentStaticProperty
				.getElementId());
		newProperty.setInput(new SelectFormInput(newOption));
		return newProperty;
	}

}
