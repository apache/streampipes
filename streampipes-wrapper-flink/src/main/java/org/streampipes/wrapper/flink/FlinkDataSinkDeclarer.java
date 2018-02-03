package org.streampipes.wrapper.flink;

import org.streampipes.wrapper.declarer.EventSinkDeclarer;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public abstract class FlinkDataSinkDeclarer<B extends EventSinkBindingParams>
				extends EventSinkDeclarer<B, FlinkDataSinkRuntime<B>> {

}
