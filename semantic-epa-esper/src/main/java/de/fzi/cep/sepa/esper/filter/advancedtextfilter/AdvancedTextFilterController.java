package de.fzi.cep.sepa.esper.filter.advancedtextfilter;

import com.google.common.io.Resources;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;
import de.fzi.cep.sepa.desc.EpDeclarer;

import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.util.DeclarerUtils;

public class AdvancedTextFilterController extends EpDeclarer<AdvancedTextFilterParameters> {

	@Override
	public SepaDescription declareModel() {
			
		try {
			return DeclarerUtils.descriptionFromResources(Resources.getResource("advancedtextfilter.jsonLd"), SepaDescription.class);
		} catch (SepaParseException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
		
	
		AdvancedTextFilterParameters staticParam = new AdvancedTextFilterParameters(sepa);
		
		try {
			invokeEPRuntime(staticParam, AdvancedTextFilter::new, sepa);
			return new Response(sepa.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(sepa.getElementId(), false, e.getMessage());
		}
	}
}
