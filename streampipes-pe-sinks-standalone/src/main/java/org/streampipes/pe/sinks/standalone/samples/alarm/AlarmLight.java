package org.streampipes.pe.sinks.standalone.samples.alarm;

import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.messaging.jms.ActiveMQPublisher;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;

public class AlarmLight implements InternalEventProcessor<byte[]> {

	private ActiveMQPublisher publisher;
	private AlarmLightParameters params;
	
	private long sentLastTime;
	
	public AlarmLight(AlarmLightParameters params) {
		this.publisher = new ActiveMQPublisher(ActionConfig.INSTANCE.getJmsUrl(), ".openHAB");
		this.params = params;
		this.sentLastTime = System.currentTimeMillis();
	}
	
	@Override
	public void onEvent(byte[] payload) {
		long currentTime = System.currentTimeMillis();
		if ((currentTime - sentLastTime) >= 30000) {
            publisher.publish(getCommand().getBytes());
            sentLastTime = currentTime;
        }
	}
	
	private String getCommand() {
		if (params.getState().equals("On")) return "1";
		else return "0";
	}

}
