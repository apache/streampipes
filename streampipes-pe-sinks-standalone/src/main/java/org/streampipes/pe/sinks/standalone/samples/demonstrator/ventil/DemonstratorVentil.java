package org.streampipes.pe.sinks.standalone.samples.demonstrator.ventil;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.messaging.jms.ActiveMQPublisher;
import org.apache.http.client.fluent.Request;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.wrapper.runtime.EventSink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class DemonstratorVentil implements EventSink<DemonstratorVentilParameters> {

	static Logger LOG = LoggerFactory.getLogger(DemonstratorVentil.class);
	private ActiveMQPublisher publisher;
	private DemonstratorVentilParameters params;
	
	private long sentLastTime;

	@Override
	public void bind(DemonstratorVentilParameters parameters) throws SpRuntimeException {
		this.params = parameters;
		this.sentLastTime = System.currentTimeMillis();
	}

	@Override
	public void onEvent(Map<String, Object> event, String sourceInfo) {
		long currentTime = System.currentTimeMillis();
		if ((currentTime - sentLastTime) >= 30000) {
			try {

				LOG.info("Turned ventil: " + params.getState() + " with demonstrator ventil sink");
 				Request.Get(ActionConfig.INSTANCE.getDemonstratorVentilUrl()  + getCommand() + "?").connectTimeout(1000).execute()
                        .returnContent().asString();
			} catch (IOException e) {
				e.printStackTrace();
			}

			sentLastTime = currentTime;
		}
	}

	@Override
	public void discard() throws SpRuntimeException {
	}

	private String getCommand() {
		if (params.getState().equals("On")) return "1";
		else return "0";
	}
}
