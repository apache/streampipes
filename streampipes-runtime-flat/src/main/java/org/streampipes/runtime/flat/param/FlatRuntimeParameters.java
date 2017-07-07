package org.streampipes.runtime.flat.param;

import java.util.function.Supplier;

import org.streampipes.runtime.EPEngine;
import org.streampipes.runtime.BindingParameters;
import org.streampipes.runtime.EngineParameters;
import org.streampipes.runtime.RuntimeParameters;

public class FlatRuntimeParameters<B extends BindingParameters> extends RuntimeParameters<B>{

	
	public FlatRuntimeParameters(String uri, Supplier<EPEngine<B>> supplier,
			EngineParameters<B> engineParameters) {
		super(uri, supplier, engineParameters);
		
	}

}
