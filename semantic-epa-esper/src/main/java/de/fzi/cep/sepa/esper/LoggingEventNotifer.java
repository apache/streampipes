package de.fzi.cep.sepa.esper;

import java.util.EventObject;

import org.apache.camel.management.event.ExchangeSentEvent;
import org.apache.camel.support.EventNotifierSupport;

public class LoggingEventNotifer extends EventNotifierSupport {
	 
    public void notify(EventObject event) throws Exception {
 
        if (event instanceof ExchangeSentEvent) {
            ExchangeSentEvent sent = (ExchangeSentEvent) event;
            log.info("Took " + sent.getTimeTaken() + " millis to send to: " + sent.getEndpoint());
        }
 
    }
 
    public boolean isEnabled(EventObject event) {
        // we only want the sent events
        return event instanceof ExchangeSentEvent;
    }
 
    protected void doStart() throws Exception {
        // noop
    }
 
    protected void doStop() throws Exception {
        // noop
    }
 
}
