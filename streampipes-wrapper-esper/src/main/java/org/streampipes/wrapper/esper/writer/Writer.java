package org.streampipes.wrapper.esper.writer;

import com.espertech.esper.client.EventBean;

public interface Writer {

	void onEvent(EventBean bean);
}
