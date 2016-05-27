package ${package}.${elementName};

import com.google.common.io.Resources;

import de.fzi.cep.sepa.commons.exceptions.SepaParseException;

import de.fzi.cep.sepa.desc.EpDeclarer;

import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;

import de.fzi.cep.sepa.model.util.SepaUtils;

import de.fzi.cep.sepa.client.util.DeclarerUtils;

public class ${classNamePrefix}Controller extends EpDeclarer<${classNamePrefix}Parameters> {

	@Override
	public SepaDescription declareModel() {
			
		try {
			return DeclarerUtils.descriptionFromResources(Resources.getResource("${elementName}.jsonLd"), SepaDescription.class);
		} catch (SepaParseException e) {
			e.printStackTrace();
			return null;
		}
		
	}

	@Override
	public Response invokeRuntime(SepaInvocation sepa) {
		
		${classNamePrefix}Parameters staticParams = new ${classNamePrefix}Parameters(sepa);
		
		try {
			invokeEPRuntime(staticParams, ${classNamePrefix}::new, sepa);
			return new Response(sepa.getElementId(), true);
		} catch (Exception e) {
			e.printStackTrace();
			return new Response(sepa.getElementId(), false, e.getMessage());
		}
	}
}
