package de.fzi.cep.sepa.actions.alarm;

import javax.jms.JMSException;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.commons.messaging.IMessageListener;
import de.fzi.cep.sepa.commons.messaging.activemq.ActiveMQPublisher;

public class AlarmLight implements IMessageListener<byte[]>{

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
		try {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - sentLastTime) >= 30000) {
				publisher.sendText(getCommand());	
				sentLastTime = currentTime;
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String getCommand() {
		if (params.getState().equals("On")) return "1";
		else return "0";
	}

}
