package de.fzi.cep.sepa.manager.matching;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.clarkparsia.empire.SupportsRdfId.URIKey;

import de.fzi.cep.sepa.commons.GenericTree;
import de.fzi.cep.sepa.commons.GenericTreeNode;
import de.fzi.cep.sepa.commons.GenericTreeTraversalOrderEnum;
import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.commons.config.BrokerConfig;
import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.manager.matching.output.OutputSchemaFactory;
import de.fzi.cep.sepa.manager.matching.output.OutputSchemaGenerator;
import de.fzi.cep.sepa.manager.matching.output.OutputStrategyRewriter;
import de.fzi.cep.sepa.manager.util.TopicGenerator;
import de.fzi.cep.sepa.model.InvocableSEPAElement;
import de.fzi.cep.sepa.model.NamedSEPAElement;
import de.fzi.cep.sepa.model.impl.EventGrounding;
import de.fzi.cep.sepa.model.impl.EventSchema;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.JmsTransportProtocol;
import de.fzi.cep.sepa.model.impl.KafkaTransportProtocol;
import de.fzi.cep.sepa.model.impl.TransportFormat;
import de.fzi.cep.sepa.model.impl.TransportProtocol;
import de.fzi.cep.sepa.model.impl.graph.SecDescription;
import de.fzi.cep.sepa.model.impl.graph.SecInvocation;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.transform.JsonLdTransformer;
import de.fzi.cep.sepa.model.vocabulary.MessageFormat;

public class InvocationGraphBuilder {

	private GenericTree<NamedSEPAElement> tree;
	private List<GenericTreeNode<NamedSEPAElement>> postOrder;
	private String pipelineId;
	
	List<InvocableSEPAElement> graphs;

	public InvocationGraphBuilder(GenericTree<NamedSEPAElement> tree,
			boolean isInvocationGraph, String pipelineId) {
		this.graphs = new ArrayList<>();
		this.tree = tree;
		this.pipelineId = pipelineId;
		this.postOrder = this.tree
				.build(GenericTreeTraversalOrderEnum.POST_ORDER);
		if (!isInvocationGraph)
			prepare();
	}

	private void prepare() {
		for (GenericTreeNode<NamedSEPAElement> node : postOrder) {
			if (node.getData() instanceof SepaDescription) {
				node.setData(new SepaInvocation((SepaDescription) node.getData()));
			}
			if (node.getData() instanceof SecDescription) {
				node.setData(new SecInvocation((SecDescription) node.getData()));
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List<InvocableSEPAElement> buildGraph() {
		Iterator<GenericTreeNode<NamedSEPAElement>> it = postOrder.iterator();
		while (it.hasNext()) {
			GenericTreeNode<NamedSEPAElement> node = it.next();
			NamedSEPAElement element = node.getData();
			if (element instanceof SepDescription) {
				
			} else if (element instanceof InvocableSEPAElement) {
				String outputTopic = TopicGenerator.generateRandomTopic();
				if (element instanceof SepaInvocation) {
					SepaInvocation thisGraph = (SepaInvocation) element;
					thisGraph.setCorrespondingPipeline(pipelineId);
					
					thisGraph = (SepaInvocation) buildSEPAElement(
							thisGraph, node, outputTopic);
					
					EventSchema outputSchema;
					EventStream outputStream = new EventStream();
					System.out.println("STREAM" +outputStream.getUri());
					List<OutputStrategy> supportedStrategies = thisGraph.getOutputStrategies();
//					outputStream.setRdfId(makeRandomUriKey(thisGraph.getUri()
//							.toString()));
					EventGrounding grounding = new EventGrounding();
					OutputSchemaGenerator schemaGenerator = new OutputSchemaFactory(supportedStrategies).getOuputSchemaGenerator();
					
					if (thisGraph.getInputStreams().size() == 1) 
						outputSchema = schemaGenerator.buildFromOneStream(thisGraph.getInputStreams().get(0));
					else
						outputSchema = schemaGenerator.buildFromTwoStreams(thisGraph.getInputStreams().get(0), thisGraph.getInputStreams().get(1));
					
					thisGraph.setOutputStrategies(Arrays.asList(schemaGenerator.getModifiedOutputStrategy(supportedStrategies.get(0))));

					if (node.getParent() != null)
					grounding.setTransportProtocol(getPreferredTransportProtocol(thisGraph, node, outputTopic));
					
					grounding
							.setTransportFormats(Utils
									.createList(getPreferredTransportFormat(thisGraph)));
					outputStream.setEventGrounding(grounding);
					outputStream.setEventSchema(outputSchema);

					thisGraph.setOutputStream(outputStream);
					graphs.add(thisGraph);
				} else {
					SecInvocation thisGraph = (SecInvocation) element;
					thisGraph.setCorrespondingPipeline(pipelineId);
					thisGraph = (SecInvocation) buildSEPAElement(
							thisGraph, node, outputTopic);
					graphs.add(thisGraph);
				}
			}
		}
		return graphs;
	}

	private TransportFormat getPreferredTransportFormat(
			SepaInvocation thisGraph) {
		try {
			if (thisGraph.getInputStreams().get(0).getEventGrounding()
					.getTransportFormats() == null)
				return new TransportFormat(MessageFormat.Json);
			if (thisGraph.getInputStreams().get(0).getEventGrounding().getTransportFormats().contains(MessageFormat.Json)) return new TransportFormat(MessageFormat.Json);
			for (TransportFormat format : thisGraph.getInputStreams().get(0)
					.getEventGrounding().getTransportFormats()) {
				if (thisGraph.getSupportedGrounding().getTransportFormats()
						.get(0).getRdfType().containsAll(format.getRdfType()))
					return format;
			}
		} catch (Exception e) {
			return new TransportFormat(MessageFormat.Json);
		}
		// TODO
		return new TransportFormat(MessageFormat.Json);
	}
	
	private TransportProtocol getPreferredTransportProtocol(SepaInvocation thisGraph, GenericTreeNode<NamedSEPAElement> node, String outputTopic)
	{
		BrokerConfig config = Configuration.getInstance().getBrokerConfig();
		if (Configuration.getInstance().isDemoMode()) return new JmsTransportProtocol(config.getJmsHost(), config.getJmsPort(), outputTopic);
		try {
		if (node.getParent().getData() instanceof InvocableSEPAElement)
		{
			InvocableSEPAElement invocable = (InvocableSEPAElement) node.getParent().getData();
			System.out.println(invocable.getName());
			if (invocable.getSupportedGrounding().getTransportProtocols().stream().anyMatch(g -> g instanceof KafkaTransportProtocol))
			{
				System.out.println("selected kafka");
				return new KafkaTransportProtocol(config.getKafkaHost(), config.getKafkaPort(), outputTopic, config.getZookeeperHost(), config.getZookeeperPort());
			}
			else
			{
				System.out.println("selected jms");
				return new JmsTransportProtocol(config.getJmsHost(), config.getJmsPort(), outputTopic);
			}
		}
		} catch (Exception e) { return new JmsTransportProtocol(config.getJmsHost(), config.getJmsPort(), outputTopic); }
		return new KafkaTransportProtocol(config.getKafkaHost(), config.getKafkaPort(), outputTopic, config.getZookeeperHost(), config.getZookeeperPort());
	}

	private InvocableSEPAElement buildSEPAElement(
			InvocableSEPAElement thisGraph,
			GenericTreeNode<NamedSEPAElement> node, String outputTopic) {
			try {
				thisGraph.setRdfId(new URIKey(new URI(thisGraph.getBelongsTo() +"/" +pipelineId +"-" +outputTopic)));
				thisGraph.setUri(thisGraph.getBelongsTo() +"/" +pipelineId +"-" +outputTopic);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			thisGraph.setUri(thisGraph.getBelongsTo() +"/" +pipelineId +"-" +outputTopic);
			

		for (int i = 0; i < node.getNumberOfChildren(); i++) {
			NamedSEPAElement child = node.getChildAt(i).getData();
			if (child instanceof EventStream) {
				EventStream thisStream = (EventStream) child;

				thisGraph.getInputStreams().get(i)
						.setEventSchema(thisStream.getEventSchema());
				thisGraph.getInputStreams().get(i)
						.setEventGrounding(thisStream.getEventGrounding());

			} else {
				SepaInvocation childSEPA = (SepaInvocation) child;
				thisGraph
						.getInputStreams()
						.get(i)
						.setEventSchema(
								childSEPA.getOutputStream().getEventSchema());
				thisGraph
						.getInputStreams()
						.get(i)
						.setEventGrounding(
								childSEPA.getOutputStream().getEventGrounding());
			}
		}
		return thisGraph;
	}
}
