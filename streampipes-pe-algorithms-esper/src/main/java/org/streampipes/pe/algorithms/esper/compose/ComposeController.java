package org.streampipes.pe.algorithms.esper.compose;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.openrdf.rio.RDFHandlerException;

import com.clarkparsia.empire.annotation.InvalidRdfException;

import org.streampipes.commons.Utils;
import org.streampipes.pe.algorithms.esper.config.EsperConfig;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.Response;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.output.OutputStrategy;
import org.streampipes.model.impl.output.RenameOutputStrategy;
import org.streampipes.model.impl.staticproperty.StaticProperty;
import org.streampipes.model.transform.JsonLdTransformer;
import org.streampipes.runtime.flat.declarer.FlatEpDeclarer;
import org.streampipes.container.util.StandardTransportFormat;

public class ComposeController extends FlatEpDeclarer<ComposeParameters>{

	@Override
	public SepaDescription declareModel() {
		
		EventStream stream1 = new EventStream();
		EventStream stream2 = new EventStream();
		
		SepaDescription desc = new SepaDescription("compose", "Compose EPA", "");
		
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
	public Response invokeRuntime(SepaInvocation sepa) {
	
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

		return submit(staticParam, Compose::new, sepa);

	}

}
