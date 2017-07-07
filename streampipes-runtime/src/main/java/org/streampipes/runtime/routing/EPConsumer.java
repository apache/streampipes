package org.streampipes.runtime.routing;

import java.util.function.Consumer;

public interface EPConsumer extends Consumer<Object>{

	void accept(Object event);
}
