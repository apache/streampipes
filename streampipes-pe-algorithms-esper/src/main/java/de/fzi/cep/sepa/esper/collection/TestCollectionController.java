package de.fzi.cep.sepa.esper.collection;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.io.Resources;

import de.fzi.cep.sepa.client.util.DeclarerUtils;
import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.CollectionStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.DomainStaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.model.vocabulary.SO;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
//import de.fzi.cep.sepa.client.util.DeclarerUtils;

public class TestCollectionController extends FlatEpDeclarer<TestCollectionParameters>{

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
