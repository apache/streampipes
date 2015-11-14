package de.fzi.cep.sepa.runtime.flat.param;

import java.util.function.Supplier;

import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.runtime.param.EngineParameters;
import de.fzi.cep.sepa.runtime.param.RuntimeParameters;

public class FlatRuntimeParameters<B extends BindingParameters> extends RuntimeParameters<B>{

	
	public FlatRuntimeParameters(String uri, Supplier<EPEngine<B>> supplier,
			EngineParameters<B> engineParameters) {
		super(uri, supplier, engineParameters);
		
	}

}
