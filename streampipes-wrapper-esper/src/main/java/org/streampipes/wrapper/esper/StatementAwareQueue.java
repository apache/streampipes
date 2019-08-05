package org.streampipes.wrapper.esper;

import com.espertech.esper.client.EventBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.wrapper.esper.writer.Writer;

public class StatementAwareQueue extends AbstractQueueRunnable<EventBean[]> {

  private static final Logger LOG = LoggerFactory.getLogger(StatementAwareQueue.class);

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
    if (counter % 100000 == 0) {
      LOG.info("{} Events received.", counter);
    }
    writer.onEvent(newEvents[0]);
  }

}
