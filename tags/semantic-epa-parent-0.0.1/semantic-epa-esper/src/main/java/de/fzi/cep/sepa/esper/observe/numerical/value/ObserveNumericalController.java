package de.fzi.cep.sepa.esper.observe.numerical.value;

import com.google.common.io.Resources;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.util.DeclarerUtils;

public class ObserveNumericalController extends FlatEpDeclarer<ObserveNumericalParameters> {

	@Override
	public SepaDescription declareModel() {
		try {
			return DeclarerUtils.descriptionFromResources(Resources.getResource("observenumerical.jsonld"),
					SepaDescription.class);
		} catch (SepaParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Response invokeRuntime(SepaInvocation invocationGraph) {

		String valueLimit = SepaUtils.getOneOfProperty(invocationGraph, "value-limit");

		String outputProperty = ((AppendOutputStrategy) invocationGraph.getOutputStrategies().get(0)).getEventProperties().get(0).getRuntimeName();

		double threshold = Double.parseDouble(
				((FreeTextStaticProperty) (SepaUtils.getStaticPropertyByInternalName(invocationGraph, "threshold")))
						.getValue());

		String value = SepaUtils.getMappingPropertyName(invocationGraph, "number");
		
		ObserveNumericalParameters params = new ObserveNumericalParameters(invocationGraph, valueLimit, threshold, value, outputProperty);


		try {
			invokeEPRuntime(params, ObserveNumerical::new, invocationGraph);
			return new Response(invocationGraph.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(invocationGraph.getElementId(), false, e.getMessage());
		}
	}

}
