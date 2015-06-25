package de.fzi.cep.sepa.esper;

import com.espertech.esper.client.EventBean;

public class StatementAwareQueue extends AbstractQueueRunnable<EventBean[]>{

	private int counter = 0;
	private Writer writer;
	
	public StatementAwareQueue(Writer writer, int maxQueueSize, int closeAfter) {
		super(maxQueueSize, closeAfter);
		this.writer = writer;
	}
	
	public StatementAwareQueue(Writer writer, int maxQueueSize) {
		super(maxQueueSize);
		this.writer = writer;
	}

	@Override
	protected void doNext(EventBean[] newEvents) throws Exception {
		currentTimestamp = System.currentTimeMillis();
		counter++;
		if (counter % 100000 == 0) System.out.println(counter + " Events received.");
		writer.onEvent(newEvents[0]);
	}

}
