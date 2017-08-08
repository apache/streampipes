package org.streampipes.wrapper.standalone.param;

import org.streampipes.wrapper.EPEngine;
import org.streampipes.wrapper.params.BindingParameters;
import org.streampipes.wrapper.params.EngineParameters;
import org.streampipes.wrapper.params.RuntimeParameters;

import java.util.function.Supplier;

public class FlatRuntimeParameters<B extends BindingParameters> extends RuntimeParameters<B>{

	
	public FlatRuntimeParameters(String uri, Supplier<EPEngine<B>> supplier,
			EngineParameters<B> engineParameters) {
		super(uri, supplier, engineParameters);
		
	}

}
