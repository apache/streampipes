package de.fzi.cep.sepa.esper.writer;

import com.espertech.esper.client.EventBean;

public interface Writer {

	void onEvent(EventBean bean);
}
