package de.fzi.cep.sepa.esper.writer;

import com.espertech.esper.client.EventBean;
import com.google.gson.Gson;

import de.fzi.cep.sepa.runtime.OutputCollector;

public class SEPAWriter implements Writer {

	private OutputCollector collector;
	
	public SEPAWriter(OutputCollector collector) {
		this.collector = collector;
	}
	
	@Override
	public void onEvent(EventBean bean) {
		//System.out.println(new Gson().toJson(bean.getUnderlying()));
		collector.send(bean.getUnderlying());
	}

}
