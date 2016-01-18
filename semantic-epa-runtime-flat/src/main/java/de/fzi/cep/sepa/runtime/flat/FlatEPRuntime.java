package de.fzi.cep.sepa.runtime.flat;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.RandomStringUtils;

import de.fzi.cep.sepa.runtime.EPRuntime;
import de.fzi.cep.sepa.runtime.flat.manager.ProtocolManager;
import de.fzi.cep.sepa.runtime.flat.param.FlatRuntimeParameters;
import de.fzi.cep.sepa.runtime.flat.routing.DestinationRoute;
import de.fzi.cep.sepa.runtime.flat.routing.SourceRoute;

public class FlatEPRuntime extends EPRuntime {

	List<SourceRoute> sources;
	DestinationRoute destination;
		
	public FlatEPRuntime(FlatRuntimeParameters<?> params) {
		super(params);
	
		sources = params.getEngineParameters().getGraph().getInputStreams()
				.stream()
				.map(is -> 
					new SourceRoute(
							"topic://" +is.getEventGrounding().getTransportProtocol().getTopicName(),
							RandomStringUtils.randomAlphabetic(6),
							ProtocolManager.findConsumer(
									is.getEventGrounding().getTransportProtocol(), 
									is.getEventGrounding().getTransportFormats().get(0)),
							engine))
				.collect(Collectors.toList());
		
		destination = new DestinationRoute(
				"topic://" +params.getEngineParameters().getGraph().getOutputStream().getEventGrounding().getTransportProtocol().getTopicName(),
				RandomStringUtils.randomAlphabetic(6),
				ProtocolManager.findProducer(
						params.getEngineParameters().getGraph().getOutputStream().getEventGrounding().getTransportProtocol(),
						params.getEngineParameters().getGraph().getOutputStream().getEventGrounding().getTransportFormats().get(0)), 
				collector);
		
	}

	@Override
	public void initRuntime() {
		sources.forEach(source -> source.startRoute());
		destination.startRoute();
	}

	@Override
	public void preDiscard() {
		sources.forEach(source -> source.stopRoute());
		destination.stopRoute();
	}

	@Override
	public void postDiscard() {
		System.out.println("Discarding Topic" +destination.getTopic());
		sources.forEach(source -> ProtocolManager.removeConsumer(source.getTopic()));
		ProtocolManager.removeProducer(destination.getTopic());
	}

}
