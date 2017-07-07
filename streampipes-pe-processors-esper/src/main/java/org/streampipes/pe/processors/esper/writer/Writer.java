package org.streampipes.pe.processors.esper.writer;

import com.espertech.esper.client.EventBean;

public interface Writer {

	void onEvent(EventBean bean);
}
