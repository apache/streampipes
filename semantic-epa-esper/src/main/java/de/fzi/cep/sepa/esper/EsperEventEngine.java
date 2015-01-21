package de.fzi.cep.sepa.esper;

import java.util.Map;

import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.OutputCollector;
import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.runtime.param.EngineParameters;

public abstract class EsperEventEngine<T extends BindingParameters> implements EPEngine<T>{

	@Override
	public void bind(EngineParameters<T> parameters, OutputCollector collector) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEvent(Map<String, Object> event, String sourceInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		
	}

}
