package de.fzi.proasense.demonstrator.adapter.siemens;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;

import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.proasense.demonstrator.adapter.Main;
import de.fzi.proasense.demonstrator.adapter.SensorValue;


public abstract class SiemensSensor {

	private String url;
	private String topic;
	private ProaSenseInternalProducer producer;
	
	public SiemensSensor(String url, String topic) {
		producer = new ProaSenseInternalProducer(Main.BROKER, topic);		
		this.url = url;
		this.topic = topic;
	}
	public abstract SensorValue getSensorValues(String data);
	

	public void start() {
		
		Runnable r = new Runnable() {

			public void run() {
				for (;;) {

					try {
						String data = requestData();
						SensorValue sv = getSensorValues(data);


						if (!data.equals("Error 404. Not Found")) {
							sendToBroker(sv.toJson());
						}
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				
				}
			}
		};
		Thread thread = new Thread(r);
		thread.start();
	}
	
	public String requestData() {

		try {
			return Request.Get(url)
			.connectTimeout(1000)
			.execute().returnContent().asString();
		} catch (ClientProtocolException e) {
		//	e.printStackTrace();
		} catch (IOException e) {
		//	e.printStackTrace();
		}
		
		return "Error 404. Not Found";
	}
	
	public void sendToBroker(String s) {
		producer.send(s.getBytes());
	}
	
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	
}
