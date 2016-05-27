package de.fzi.cep.sepa.esper.filter.advancedtextfilter;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.io.Resources;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.staticproperty.CollectionStaticProperty;
import de.fzi.cep.sepa.model.impl.staticproperty.FreeTextStaticProperty;
import de.fzi.cep.sepa.model.util.SepaUtils;
import de.fzi.cep.sepa.runtime.flat.declarer.FlatEpDeclarer;
import de.fzi.cep.sepa.client.util.DeclarerUtils;

public class AdvancedTextFilterController extends FlatEpDeclarer<AdvancedTextFilterParameters> {

	@Override
	public SepaDescription declareModel() {
			
		try {
			return DeclarerUtils.descriptionFromResources(Resources.getResource("advancedtextfilter.jsonld"), SepaDescription.class);
		} catch (SepaParseException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
		
		String operation = SepaUtils.getOneOfProperty(sepa, "operatoin");
		CollectionStaticProperty collection = SepaUtils.getStaticPropertyByInternalName(sepa, "collection", CollectionStaticProperty.class);
		String propertyName = SepaUtils.getMappingPropertyName(sepa, "text-mapping");
		
		List<String> keywords = collection.getMembers()
				.stream()
				.map(m -> ((FreeTextStaticProperty)m).getValue())
				.collect(Collectors.toList());
					
		AdvancedTextFilterParameters staticParam = new AdvancedTextFilterParameters(sepa, operation, propertyName, keywords);
		
		try {
			invokeEPRuntime(staticParam, AdvancedTextFilter::new, sepa);
			return new Response(sepa.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(sepa.getElementId(), false, e.getMessage());
		}
	}
}
