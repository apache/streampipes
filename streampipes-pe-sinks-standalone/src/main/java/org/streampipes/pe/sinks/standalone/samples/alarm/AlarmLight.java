package org.streampipes.pe.sinks.standalone.samples.alarm;

import org.streampipes.commons.config.ClientConfiguration;
import org.streampipes.messaging.EventListener;
import org.streampipes.messaging.jms.ActiveMQPublisher;

import javax.jms.JMSException;

public class AlarmLight implements EventListener<byte[]> {

	private ActiveMQPublisher publisher;
	private AlarmLightParameters params;
	
	private long sentLastTime;
	
	public AlarmLight(AlarmLightParameters params) {
		try {
			this.publisher = new ActiveMQPublisher(ClientConfiguration.INSTANCE.getJmsUrl(), ".openHAB");
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.params = params;
		this.sentLastTime = System.currentTimeMillis();
	}
	
	@Override
	public void onEvent(byte[] payload) {
		long currentTime = System.currentTimeMillis();
		if ((currentTime - sentLastTime) >= 30000) {
            publisher.publish(getCommand());
            sentLastTime = currentTime;
        }
	}
	
	private String getCommand() {
		if (params.getState().equals("On")) return "1";
		else return "0";
	}

}
