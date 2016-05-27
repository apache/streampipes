package de.fzi.cep.sepa.esper.classification.number;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.io.Resources;

import de.fzi.cep.sepa.client.util.DeclarerUtils;
import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.CollectionStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.DomainStaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;

public class NumberClassificationController extends FlatEpDeclarer<NumberClassificationParameters> {

	@Override
	public SepaDescription declareModel() {
		try {
			return DeclarerUtils.descriptionFromResources(Resources.getResource("numberclassification.jsonld"),
					SepaDescription.class);
		} catch (SepaParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {

		CollectionStaticProperty collection = SepaUtils.getStaticPropertyByInternalName(sepa, "classification_options",
				CollectionStaticProperty.class);
		String propertyName = SepaUtils.getMappingPropertyName(sepa, "to_classify");

		String outputProperty = ((AppendOutputStrategy) sepa.getOutputStrategies().get(0)).getEventProperties().get(0).getRuntimeName();

		List<DomainStaticProperty> domainConcepts = collection.getMembers().stream().map(m -> (DomainStaticProperty) m)
				.collect(Collectors.toList());
		
		List<DataClassification> domainConceptData = domainConcepts.stream()
				.map(m -> new DataClassification(Double.parseDouble(SepaUtils.getSupportedPropertyValue(m, SO.MinValue)),
						Double.parseDouble(SepaUtils.getSupportedPropertyValue(m, SO.MaxValue)),
						SepaUtils.getSupportedPropertyValue(m, SO.Text)))
				.collect(Collectors.toList());

		NumberClassificationParameters staticParam = new NumberClassificationParameters(sepa, propertyName, outputProperty,
				domainConceptData);

		try {
			invokeEPRuntime(staticParam, NumberClassification::new, sepa);
			return new Response(sepa.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(sepa.getElementId(), false, e.getMessage());
		}
	}

}
