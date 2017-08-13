package org.streampipes.wrapper.standalone;

import org.streampipes.wrapper.runtime.EventProcessorRuntime;
import org.streampipes.wrapper.standalone.manager.ProtocolManager;
import org.streampipes.wrapper.standalone.param.FlatEventProcessorRuntimeParams;

public class FlatEPRuntime extends EventProcessorRuntime {

	public FlatEPRuntime(FlatEventProcessorRuntimeParams<?> params) {
		super(params);
	}

	@Override
	public void initRuntime() {
		sources.forEach(source -> source.startRoute());
		destination.startRoute();
	}

	@Override
	public void preDiscard() {
		sources.forEach(source -> source.stopRoute());
		sources.forEach(source -> ProtocolManager.removeFromConsumerMap(source.getTopic(), source.getRouteId()));
		destination.stopRoute();
	}

	@Override
	public void postDiscard() {
		System.out.println("Discarding Topic" +destination.getTopic());
		sources.forEach(source -> ProtocolManager.removeConsumer(source.getTopic()));
		ProtocolManager.removeProducer(destination.getTopic());
	}

}
