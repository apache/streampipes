package ${package}.topology;

import java.util.Map;
import java.util.Properties;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.tuple.Values;
import de.fzi.cep.sepa.model.util.SepaUtils;
import ${package}.controller.${classNamePrefix}Parameters;
import de.fzi.cep.sepa.storm.topology.FunctionalSepaBolt;


public class ${classNamePrefix}Bolt extends FunctionalSepaBolt<${classNamePrefix}Parameters> {

	private static final long serialVersionUID = -3911542682275246545L;

	public ${classNamePrefix}Bolt(String id) {
		super(id);
	}
	
	@Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        super.prepare(map, topologyContext, outputCollector);
       
    }
	
	@Override
	protected void performEventAction(Map<String, Object> event, ${classNamePrefix}Parameters params, String configurationId) {
		
        emit(new Values(event));	
	}

}
