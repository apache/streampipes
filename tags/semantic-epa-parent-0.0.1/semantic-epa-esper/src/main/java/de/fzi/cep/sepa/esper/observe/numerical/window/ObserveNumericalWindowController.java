package de.fzi.cep.sepa.esper.observe.numerical.window;

import com.google.common.io.Resources;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.esper.observe.numerical.value.ObserveNumerical;
import de.fzi.cep.sepa.esper.observe.numerical.value.ObserveNumericalParameters;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.util.DeclarerUtils;

public class ObserveNumericalWindowController extends FlatEpDeclarer<ObserveNumericalWindowParameters> {

	@Override
	public SepaDescription declareModel() {
		try {
			return DeclarerUtils.descriptionFromResources(Resources.getResource("observenumericalvaluewindow.jsonld"),
					SepaDescription.class);
		} catch (SepaParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Response invokeRuntime(SepaInvocation invocationGraph) {

		String valueLimit = SepaUtils.getOneOfProperty(invocationGraph, "value-limit");

		double threshold = Double.parseDouble(
				((FreeTextStaticProperty) (SepaUtils.getStaticPropertyByInternalName(invocationGraph, "threshold")))
						.getValue());

		String toObserve = SepaUtils.getMappingPropertyName(invocationGraph, "to-observe");

		int windowSize = Integer.parseInt(
				((FreeTextStaticProperty) (SepaUtils.getStaticPropertyByInternalName(invocationGraph, "window-size")))
						.getValue());

		String messageName = ((AppendOutputStrategy) invocationGraph.getOutputStrategies().get(0)).getEventProperties()
				.get(0).getRuntimeName();
		String averageName = ((AppendOutputStrategy) invocationGraph.getOutputStrategies().get(0)).getEventProperties()
				.get(1).getRuntimeName();

		String windowType = SepaUtils.getOneOfProperty(invocationGraph, "window-type");
		String groupBy = SepaUtils.getMappingPropertyName(invocationGraph, "group-by");

		ObserveNumericalWindowParameters params = new ObserveNumericalWindowParameters(invocationGraph, valueLimit,
				threshold, toObserve, windowType, windowSize, groupBy, messageName, averageName);

		try {
			invokeEPRuntime(params, ObserveNumericalWindow::new, invocationGraph);
			return new Response(invocationGraph.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(invocationGraph.getElementId(), false, e.getMessage());
		}
	}

}
