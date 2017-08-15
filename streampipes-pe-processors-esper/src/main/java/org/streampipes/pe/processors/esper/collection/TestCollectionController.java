package org.streampipes.pe.processors.esper.collection;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.io.Resources;

import org.streampipes.container.util.DeclarerUtils;
import org.streampipes.commons.exceptions.SepaParseException;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.staticproperty.CollectionStaticProperty;
import org.streampipes.model.impl.staticproperty.DomainStaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.model.vocabulary.SO;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarer;
//import de.fzi.cep.sepa.client.util.DeclarerUtils;

public class TestCollectionController extends StandaloneEventProcessorDeclarer<TestCollectionParameters> {

	@Override
	public SepaDescription declareModel() {
		
		try {
			return DeclarerUtils.descriptionFromResources(Resources.getResource("testcollection.jsonld"), SepaDescription.class);
		} catch (SepaParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
		
		CollectionStaticProperty collection = SepaUtils.getStaticPropertyByInternalName(sepa, "collection", CollectionStaticProperty.class);
		String propertyName = SepaUtils.getMappingPropertyName(sepa, "number-mapping");
		
		List<DomainStaticProperty> domainConcepts = collection.getMembers().stream().map(m -> (DomainStaticProperty) m).collect(Collectors.toList());
		
		
		List<DataRange> domainConceptData = domainConcepts
				.stream()
				.map(m -> new DataRange(
						Integer.parseInt(SepaUtils.getSupportedPropertyValue(m, SO.MinValue)), 
						Integer.parseInt(SepaUtils.getSupportedPropertyValue(m, SO.MaxValue)), 
						Integer.parseInt(SepaUtils.getSupportedPropertyValue(m,  SO.Step))))
				.collect(Collectors.toList());
		
		TestCollectionParameters staticParam = new TestCollectionParameters(sepa, propertyName, domainConceptData);

		return submit(staticParam, TestCollection::new, sepa);

	}

}
