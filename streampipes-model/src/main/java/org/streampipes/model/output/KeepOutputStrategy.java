package org.streampipes.model.output;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.KEEP_OUTPUT_STRATEGY)
@Entity
public class KeepOutputStrategy extends OutputStrategy {

	private static final long serialVersionUID = 7643705399683055563L;

	@Deprecated
	@RdfProperty(StreamPipes.EVENT_NAME)
	String eventName;

	@RdfProperty(StreamPipes.KEEP_BOTH)
	private boolean keepBoth;

	public KeepOutputStrategy()
	{
		super();
	}
	
	public KeepOutputStrategy(KeepOutputStrategy other)
	{
		super(other);
		this.eventName = other.getEventName();
		this.keepBoth = other.isKeepBoth();
	}
	
	public KeepOutputStrategy(String name, String eventName) {
		super(name);
		this.eventName = eventName;
		this.keepBoth = true;
	}

	public KeepOutputStrategy(String name, boolean keepBoth) {
		super(name);
		this.keepBoth = keepBoth;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public boolean isKeepBoth() {
		return keepBoth;
	}

	public void setKeepBoth(boolean keepBoth) {
		this.keepBoth = keepBoth;
	}
}
