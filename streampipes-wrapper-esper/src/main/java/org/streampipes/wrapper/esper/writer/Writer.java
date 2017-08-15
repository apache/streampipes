package org.streampipes.wrapper.esper.writer;

import com.espertech.esper.event.map.MapEventBean;

public interface Writer {

	void onEvent(MapEventBean bean);
}
