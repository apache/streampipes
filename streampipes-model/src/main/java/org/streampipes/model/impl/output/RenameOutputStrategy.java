package org.streampipes.model.impl.output;

import com.clarkparsia.empire.annotation.Namespaces;
import com.clarkparsia.empire.annotation.RdfProperty;
import com.clarkparsia.empire.annotation.RdfsClass;

import javax.persistence.Entity;

@Namespaces({"sepa", "http://sepa.event-processing.org/sepa#",
	 "dc",   "http://purl.org/dc/terms/"})
@RdfsClass("sepa:RenameOutputStrategy")
@Entity
public class RenameOutputStrategy extends OutputStrategy {

	private static final long serialVersionUID = 7643705399683055563L;
	
	@RdfProperty("sepa:eventName")
	String eventName;

	@RdfProperty("sepa:keepBoth")
	private boolean keepBoth;

	public RenameOutputStrategy()
	{
		super();
	}
	
	public RenameOutputStrategy(RenameOutputStrategy other)
	{
		super(other);
		this.eventName = other.getEventName();
		this.keepBoth = other.isKeepBoth();
	}
	
	public RenameOutputStrategy(String name, String eventName) {
		super(name);
		this.eventName = eventName;
		this.keepBoth = true;
	}

	public RenameOutputStrategy(String name, boolean keepBoth) {
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
