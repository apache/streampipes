package de.fzi.cep.sepa.storm.topology;

import java.util.HashMap;
import java.util.Map;

import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.storm.controller.ConfigurationMessage;
import de.fzi.cep.sepa.storm.controller.Operation;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public abstract class SepaBolt<B extends BindingParameters> extends BaseRichBolt {

	/**
     * Sink to the next bolt.
     * These objects cannot be initialized in the constructor since the connections are set up in runtime, when the
     * objects are deserialized on the Storm cluster. That is when the prepare() method is called and all transient
     * properties such as the collector are initialized
     */

    protected OutputCollector collector;

    /**
     * The id of the bolt, to build the topologies easier. It's more logical to have the ID binded to the bolt itself.
     * Strings are serializable so they could be distributed in the cluster
     */
    protected String id;


    public SepaBolt(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
    
    private Map<String, B> boltSettings;

    /**
     * This method is automatically called by the Storm framework.
     *
     * @param map
     * @param topologyContext
     * @param outputCollector
     */
    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
    	this.collector = outputCollector;
        this.boltSettings = new HashMap<>();
    }

    @Override
    public void execute(Tuple tuple) {
System.out.println("received " +this.getId());
        if (tuple.getSourceStreamId().equals(SepaSpout.SEPA_CONFIG_STREAM)) {
        	ConfigurationMessage<B> params = (ConfigurationMessage<B>) tuple.getValueByField("config");
        	performConfigAction(params);
        }
        if (tuple.getSourceStreamId().contentEquals(SepaSpout.SEPA_DATA_STREAM)) {
        	Map<String, Object> payload = (Map<String, Object>) tuple.getValueByField("payload");
			performEventAction(payload);
        }
	}

    protected void performConfigAction(ConfigurationMessage<B> params)
    {
    	if (params.getOperation() == Operation.BIND) boltSettings.put(params.getConfigurationId(), params.getBindingParameters());
    	else if (params.getOperation() == Operation.DETACH) boltSettings.remove(params.getConfigurationId());
    	else System.out.println("Not supported yet.");
    }
    
    protected abstract void performEventAction(Map<String, Object> event);
    
    protected B getEventSpecificParams(String key)
    {
    	return boltSettings.get(key);
    }
    
    protected void emit(Values values)
    {
    	collector.emit(SepaSpout.SEPA_DATA_STREAM, values);
    }
}
