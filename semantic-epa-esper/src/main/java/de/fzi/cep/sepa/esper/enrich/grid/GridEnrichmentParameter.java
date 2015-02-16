package de.fzi.cep.sepa.esper.enrich.grid;

import java.util.List;

import de.fzi.cep.sepa.runtime.param.BindingParameters;

public class GridEnrichmentParameter extends BindingParameters {

	public GridEnrichmentParameter(String inName, String outName,
			List<String> allProperties, List<String> partitionProperties) {
		super(inName, outName, allProperties, partitionProperties);
		// TODO Auto-generated constructor stub
	}
	private double latitudeStart;
	private double longitudeStart;
}
