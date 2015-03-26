package de.fzi.cep.sepa.esper.enrich.math;

import de.fzi.cep.sepa.esper.EsperDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SEPA;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;

public class MathController extends EsperDeclarer<MathParameter>{

	@Override
	public SEPA declareModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean invokeRuntime(SEPAInvocationGraph sepa) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean detachRuntime(SEPAInvocationGraph sepa) {
		// TODO Auto-generated method stub
		return false;
	}

}
