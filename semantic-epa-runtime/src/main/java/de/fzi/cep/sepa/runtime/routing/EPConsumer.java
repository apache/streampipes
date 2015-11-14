package de.fzi.cep.sepa.runtime.routing;

import java.util.function.Consumer;

public interface EPConsumer extends Consumer<Object>{

	public void accept(Object event);
}
