package ${package}.controller;

import de.fzi.cep.sepa.json.SchemaGenerator;
import de.fzi.cep.sepa.model.impl.Response;
import de.fzi.cep.sepa.model.impl.graph.SepaDescription;
import de.fzi.cep.sepa.model.impl.graph.SepaInvocation;
import de.fzi.cep.sepa.storm.controller.AbstractStormController;
import de.fzi.cep.sepa.storm.controller.ConfigurationMessage;
import de.fzi.cep.sepa.storm.controller.Operation;
import ${package}.controller.${classNamePrefix}Parameters;

public class ${classNamePrefix}Controller extends AbstractStormController<${classNamePrefix}Parameters>{

	@Override
	public SepaDescription declareModel() {
		
		SepaDescription desc = SchemaGenerator.fromJson("", SepaDescription.class); 
		return desc;
	}

	@Override
	public Response invokeRuntime(SepaInvocation invocationGraph) {
		
		${classNamePrefix}Parameters params = new ${classNamePrefix}Parameters(invocationGraph);
		ConfigurationMessage<${classNamePrefix}Parameters> msg = new ConfigurationMessage<>(Operation.BIND, params);
		
		return prepareTopology(msg);
	}

	@Override
	protected String getKafkaUrl() {
		return "kalmar39.fzi.de:9092";
	}

}
