package de.fzi.cep.sepa.esper.compose;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.rio.RDFHandlerException;

import com.clarkparsia.empire.annotation.InvalidRdfException;

import de.fzi.cep.sepa.commons.Utils;
import de.fzi.cep.sepa.esper.EsperDeclarer;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.esper.util.StandardTransportFormat;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.StaticProperty;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.model.impl.output.OutputStrategy;
import de.fzi.cep.sepa.model.impl.output.RenameOutputStrategy;
import de.fzi.cep.sepa.model.transform.JsonLdTransformer;

public class ComposeController extends EsperDeclarer<ComposeParameters>{

	@Override
	public SepaDescription declareModel() {
		List<String> domains = new ArrayList<String>();
		domains.add(Domain.DOMAIN_PERSONAL_ASSISTANT.toString());
		domains.add(Domain.DOMAIN_PROASENSE.toString());
		
		EventStream stream1 = new EventStream();
		EventStream stream2 = new EventStream();
		
		SepaDescription desc = new SepaDescription("/sepa/compose", "Compose EPA", "", "", "/sepa/compose", domains);
		
		stream1.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		stream2.setUri(EsperConfig.serverUrl +"/" +Utils.getRandomString());
		desc.addEventStream(stream1);
		desc.addEventStream(stream2);
		
		List<OutputStrategy> strategies = new ArrayList<OutputStrategy>();
		strategies.add(new RenameOutputStrategy());
		desc.setOutputStrategies(strategies);
		
		List<StaticProperty> staticProperties = new ArrayList<StaticProperty>();
		
		//staticProperties.add(new MatchingStaticProperty("select matching", ""));
		desc.setStaticProperties(staticProperties);
		desc.setSupportedGrounding(StandardTransportFormat.getSupportedGrounding());
		return desc;
	}

	@Override
	public boolean invokeRuntime(SepaInvocation sepa) {
	
		try {
			System.out.println(Utils.asString(new JsonLdTransformer().toJsonLd(sepa)));
		} catch (RDFHandlerException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| SecurityException | ClassNotFoundException
				| InvalidRdfException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ComposeParameters staticParam = new ComposeParameters(sepa);
		
	
		try {
			return invokeEPRuntime(staticParam, Compose::new, sepa);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
