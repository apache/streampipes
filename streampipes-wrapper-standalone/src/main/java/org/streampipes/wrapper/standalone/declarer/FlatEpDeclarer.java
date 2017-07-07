package org.streampipes.wrapper.standalone.declarer;

import java.util.List;
import java.util.function.Supplier;

import org.streampipes.wrapper.declarer.EpDeclarer;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.EPEngine;
import org.streampipes.wrapper.standalone.FlatEPRuntime;
import org.streampipes.wrapper.standalone.param.FlatRuntimeParameters;
import org.streampipes.wrapper.standalone.routing.DestinationRoute;
import org.streampipes.wrapper.standalone.routing.SourceRoute;
import org.streampipes.wrapper.BindingParameters;
import org.streampipes.wrapper.EngineParameters;

public abstract class FlatEpDeclarer <B extends BindingParameters> extends EpDeclarer<B, FlatEPRuntime>{


	List<SourceRoute> sourceRoutes;
	DestinationRoute destinationRoute;
	
	@Override
	public void preDetach() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public FlatEPRuntime prepareRuntime(B bindingParameters,
			Supplier<EPEngine<B>> supplier, EngineParameters<B> engineParams) {
		
		SepaInvocation sepa = bindingParameters.getGraph();

		
		FlatRuntimeParameters<B> runtimeParams = new FlatRuntimeParameters<>(sepa.getUri(), supplier, engineParams);
		FlatEPRuntime epRuntime = new FlatEPRuntime(runtimeParams);
		
		return epRuntime;
	}

	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
