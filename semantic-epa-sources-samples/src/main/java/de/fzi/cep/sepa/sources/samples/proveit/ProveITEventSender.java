package de.fzi.cep.sepa.sources.samples.proveit;

import javax.jms.JMSException;

import com.google.gson.Gson;

import de.fzi.cep.sepa.sources.samples.activemq.ActiveMQPublisher;
import de.fzi.cep.sepa.sources.samples.activemq.IMessageListener;
import de.fzi.proveit.senslet.model.Senslet;

public class ProveITEventSender implements IMessageListener {

	private ActiveMQPublisher publisher;
	Gson gson;
	
	public ProveITEventSender(ActiveMQPublisher publisher) {
		this.publisher = publisher;
		gson = de.fzi.proveit.senslet.util.Utils.getGson();
	}
	
	@Override
	public void onEvent(String json) {
		System.out.println(json);
		Senslet senslet = gson.fromJson(json, Senslet.class);
		try {
			publisher.sendText(gson.toJson(ProveITEventConverter.makeFlat(senslet)));
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
