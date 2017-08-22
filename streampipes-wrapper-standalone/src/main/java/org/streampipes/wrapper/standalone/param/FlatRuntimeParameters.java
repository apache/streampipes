package org.streampipes.wrapper.standalone.param;

import java.util.function.Supplier;

import org.streampipes.wrapper.EPEngine;
import org.streampipes.wrapper.BindingParameters;
import org.streampipes.wrapper.EngineParameters;
import org.streampipes.wrapper.RuntimeParameters;

public class FlatRuntimeParameters<B extends BindingParameters> extends RuntimeParameters<B>{

	
	public FlatRuntimeParameters(String uri, Supplier<EPEngine<B>> supplier,
			EngineParameters<B> engineParameters) {
		super(uri, supplier, engineParameters);
		
	}

}
