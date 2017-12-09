package org.streampipes.pe.sinks.standalone.samples.demonstrator.pump;

import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.pe.sinks.standalone.config.ActionConfig;
import org.streampipes.wrapper.runtime.EventSink;

import java.io.IOException;
import java.util.Map;

public class DemonstratorPump implements EventSink<DemonstratorPumpParameters> {

	static Logger LOG = LoggerFactory.getLogger(DemonstratorPump.class);

	private DemonstratorPumpParameters params;
	
	private long sentLastTime;

	@Override
	public void bind(DemonstratorPumpParameters parameters) throws SpRuntimeException {
		this.params = parameters;
		this.sentLastTime = System.currentTimeMillis();
	}

	@Override
	public void onEvent(Map<String, Object> event, String sourceInfo) {
		long currentTime = System.currentTimeMillis();
		if ((currentTime - sentLastTime) >= 30000) {
			try {
			    LOG.info("Turned pump: " + params.getState() + " with demonstrator pump sink");
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
