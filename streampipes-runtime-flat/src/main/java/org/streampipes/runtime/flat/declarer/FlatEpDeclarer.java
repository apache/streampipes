package org.streampipes.runtime.flat.declarer;

import java.util.List;
import java.util.function.Supplier;

import org.streampipes.runtime.declarer.EpDeclarer;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.runtime.EPEngine;
import org.streampipes.runtime.flat.FlatEPRuntime;
import org.streampipes.runtime.flat.param.FlatRuntimeParameters;
import org.streampipes.runtime.flat.routing.DestinationRoute;
import org.streampipes.runtime.flat.routing.SourceRoute;
import org.streampipes.runtime.BindingParameters;
import org.streampipes.runtime.EngineParameters;

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
