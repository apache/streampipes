package de.fzi.cep.sepa.esper;

import com.espertech.esper.client.EventBean;

public interface Writer {

	public void onEvent(EventBean bean);
}
