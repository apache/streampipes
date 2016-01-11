package de.fzi.cep.sepa.flink.samples.classification.number;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.io.Resources;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.flink.AbstractFlinkAgentDeclarer;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.AppendOutputStrategy;
import de.fzi.cep.sepa.model.impl.staticproperty.CollectionStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.DomainStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.SupportedProperty;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.util.DeclarerUtils;

public class NumberClassificationController extends AbstractFlinkAgentDeclarer<NumberClassificationParameters> {

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
	protected FlinkSepaRuntime<NumberClassificationParameters> getRuntime(SepaInvocation graph) {
		CollectionStaticProperty collection = SepaUtils.getStaticPropertyByInternalName(graph, "classification_options",
				CollectionStaticProperty.class);
		String propertyName = SepaUtils.getMappingPropertyName(graph, "to_classify");

		String outputProperty = ((AppendOutputStrategy) graph.getOutputStrategies().get(0)).getEventProperties().get(0)
				.getRuntimeName();

		List<DomainStaticProperty> domainConcepts = collection.getMembers().stream().map(m -> (DomainStaticProperty) m)
				.collect(Collectors.toList());

		// List<DataClassification> domainConceptData = domainConcepts.stream()
		// .map(m -> new
		// DataClassification(Double.parseDouble(SepaUtils.getSupportedPropertyValue(m,
		// SO.MinValue)),
		// Double.parseDouble(SepaUtils.getSupportedPropertyValue(m,
		// SO.MaxValue)),
		// SepaUtils.getSupportedPropertyValue(m, SO.Text)))
		// .collect(Collectors.toList());

		//TODO find a better solution
		List<DataClassification> domainConceptData = new ArrayList<>();
		List<SupportedProperty> supportedProperties = domainConcepts.get(0).getSupportedProperties();
		for (int i = 0; i < supportedProperties.size() - 2; i = i + 3) {
			domainConceptData.add(new DataClassification(Double.parseDouble(supportedProperties.get(i).getValue()),
					Double.parseDouble(supportedProperties.get(i + 1).getValue()),
					supportedProperties.get(i + 2).getValue()));
		}

		// return new NumberClassificationProgram(new
		// NumberClassificationParameters(graph, propertyName, outputProperty,
		// domainConceptData),
		// new FlinkDeploymentConfig(Config.JAR_FILE, Config.FLINK_HOST,
		// Config.FLINK_PORT));

		return new NumberClassificationProgram(
				new NumberClassificationParameters(graph, propertyName, outputProperty, domainConceptData));
	}

}
