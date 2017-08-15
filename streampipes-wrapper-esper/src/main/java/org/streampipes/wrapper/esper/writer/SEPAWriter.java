package org.streampipes.wrapper.esper.writer;

import com.espertech.esper.event.map.MapEventBean;
import org.streampipes.wrapper.routing.EventProcessorOutputCollector;

import java.util.Map;

public class SEPAWriter implements Writer {

	private EventProcessorOutputCollector collector;
	
	public SEPAWriter(EventProcessorOutputCollector collector) {
		this.collector = collector;
	}
	
	@Override
	public void onEvent(MapEventBean bean) {
		//System.out.println(new Gson().toJson(bean.getUnderlying()));
		collector.onEvent((Map<String, Object>) bean.getUnderlying());
	}

}
