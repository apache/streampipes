package org.streampipes.pe.algorithms.esper.writer;

import com.espertech.esper.client.EventBean;

public interface Writer {

	void onEvent(EventBean bean);
}
