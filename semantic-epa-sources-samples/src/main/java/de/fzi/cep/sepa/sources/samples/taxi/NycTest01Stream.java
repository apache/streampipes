package de.fzi.cep.sepa.sources.samples.taxi;

import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;

public class NycTest01Stream implements EventStreamDeclarer {

	@Override
	public EventStream declareModel(SepDescription sep) {
		return new NYCTaxiStream().declareModel(sep);
	}

	@Override
	public void executeStream() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isExecutable() {
		return true;
	}

}
