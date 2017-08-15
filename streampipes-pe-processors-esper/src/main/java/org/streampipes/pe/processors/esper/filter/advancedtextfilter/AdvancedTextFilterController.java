package org.streampipes.pe.processors.esper.filter.advancedtextfilter;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.io.Resources;

import org.streampipes.commons.exceptions.SepaParseException;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.staticproperty.CollectionStaticProperty;
import org.streampipes.model.impl.staticproperty.FreeTextStaticProperty;
import org.streampipes.model.util.SepaUtils;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessorDeclarer;
import org.streampipes.container.util.DeclarerUtils;

public class AdvancedTextFilterController extends StandaloneEventProcessorDeclarer<AdvancedTextFilterParameters> {

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

		return submit(staticParam, AdvancedTextFilter::new, sepa);
	}
}
