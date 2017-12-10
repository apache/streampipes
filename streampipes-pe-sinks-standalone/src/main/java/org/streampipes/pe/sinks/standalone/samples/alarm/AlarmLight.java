package org.streampipes.pe.sinks.standalone.samples.alarm;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.messaging.jms.ActiveMQPublisher;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public class AlarmLight implements EventSink<AlarmLightParameters> {

	private ActiveMQPublisher publisher;
	private AlarmLightParameters params;
	
	private long sentLastTime;

	@Override
	public void bind(AlarmLightParameters parameters) throws SpRuntimeException {
		this.publisher = new ActiveMQPublisher(ActionConfig.INSTANCE.getJmsUrl(), ".openHAB");
		this.params = parameters;
		this.sentLastTime = System.currentTimeMillis();
	}

	@Override
	public void onEvent(Map<String, Object> event, String sourceInfo) {
		long currentTime = System.currentTimeMillis();
		if ((currentTime - sentLastTime) >= 30000) {
			publisher.publish(getCommand().getBytes());
			sentLastTime = currentTime;
		}
	}

	@Override
	public void discard() throws SpRuntimeException {
		this.publisher.disconnect();
	}

	private String getCommand() {
		if (params.getState().equals("On")) return "1";
		else return "0";
	}
}
