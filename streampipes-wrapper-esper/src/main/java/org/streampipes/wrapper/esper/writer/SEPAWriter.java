package org.streampipes.wrapper.esper.writer;

import com.espertech.esper.client.EventBean;

import org.streampipes.wrapper.OutputCollector;

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
