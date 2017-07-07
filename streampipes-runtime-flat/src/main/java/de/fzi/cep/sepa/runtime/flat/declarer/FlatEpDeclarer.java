package de.fzi.cep.sepa.runtime.flat.declarer;

import java.util.List;
import java.util.function.Supplier;

import de.fzi.cep.sepa.declarer.EpDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.flat.FlatEPRuntime;
import de.fzi.cep.sepa.runtime.flat.param.FlatRuntimeParameters;
import de.fzi.cep.sepa.runtime.flat.routing.DestinationRoute;
import de.fzi.cep.sepa.runtime.flat.routing.SourceRoute;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.runtime.param.EngineParameters;

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
