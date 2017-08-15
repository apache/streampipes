package org.streampipes.wrapper.esper;

import com.espertech.esper.event.map.MapEventBean;
import org.streampipes.wrapper.esper.writer.Writer;

public class StatementAwareQueue extends AbstractQueueRunnable<MapEventBean[]>{

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
	protected void doNext(MapEventBean[] newEvents) throws Exception {
		currentTimestamp = System.currentTimeMillis();
		counter++;
		if (counter % 100000 == 0) System.out.println(counter + " Events received.");
		writer.onEvent(newEvents[0]);
	}

}
