package org.streampipes.wrapper.esper.writer;

import com.espertech.esper.client.EventBean;
import org.streampipes.wrapper.routing.SpOutputCollector;

import java.util.Map;

public class SEPAWriter implements Writer {

	private SpOutputCollector collector;
	
	public SEPAWriter(SpOutputCollector collector) {
		this.collector = collector;
	}
	
	@Override
	public void onEvent(EventBean bean) {
		//System.out.println(new Gson().toJson(bean.getUnderlying()));
		collector.collect((Map<String, Object>) bean.getUnderlying());
	}

}
