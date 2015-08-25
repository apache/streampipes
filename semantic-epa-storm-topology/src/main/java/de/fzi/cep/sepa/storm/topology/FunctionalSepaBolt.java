package de.fzi.cep.sepa.storm.topology;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzi.cep.sepa.runtime.param.BindingParameters;
import de.fzi.cep.sepa.storm.controller.ConfigurationMessage;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public abstract class FunctionalSepaBolt<B extends BindingParameters> extends SepaBolt<B> {

	private static final long serialVersionUID = -3694170770048756860L;
    
    private static Logger log = LoggerFactory.getLogger(FunctionalSepaBolt.class);
    
   
    public FunctionalSepaBolt(String id) {
        super(id);
    }

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        super.prepare(map, topologyContext, outputCollector);

    }
	
    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream(SepaSpout.SEPA_DATA_STREAM, new Fields("payload"));
        outputFieldsDeclarer.declareStream(SepaSpout.SEPA_CONFIG_STREAM, new Fields("config"));
    }
    
    @Override
    protected void performConfigAction(ConfigurationMessage<B> params)
    {
    	super.performConfigAction(params);
    	collector.emit(SepaSpout.SEPA_CONFIG_STREAM, new Values(params));
    }
    
}
