package ${package}.esper;

import com.espertech.esper.client.EventBean;

import de.fzi.cep.sepa.runtime.OutputCollector;

public class SEPAWriter implements Writer {

	private OutputCollector collector;
	
	public SEPAWriter(OutputCollector collector) {
		this.collector = collector;
	}
	
	@Override
	public void onEvent(EventBean bean) {
		collector.send(bean.getUnderlying());
	}

}
