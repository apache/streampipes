package de.fzi.cep.sepa.flink.samples.axoom;

import org.apache.flink.streaming.api.operators.AbstractUdfStreamOperator;
import org.apache.flink.streaming.api.operators.OneInputStreamOperator;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by riemer on 12.04.2017.
 */
public class SlidingEventTimeWindow<IN> extends AbstractUdfStreamOperator<List<IN>,
        TimestampMappingFunction<IN>>
        implements
        OneInputStreamOperator<IN, List<IN>> {

  private Long timeWindowSizeInMillis;
  private List<IN> currentEvents;

  public SlidingEventTimeWindow(Long time, TimeUnit timeUnit, TimestampMappingFunction<IN>
          timestampMappingFunction) {
    super(timestampMappingFunction);
    this.timeWindowSizeInMillis = toMilliseconds(time, timeUnit);
    this.currentEvents = new ArrayList<>();

  }

  private Long toMilliseconds(Long time, TimeUnit timeUnit) {
    return timeUnit.toMillis(time);
  }

  @Override
  public void processElement(StreamRecord<IN> in) throws Exception {
    Long currentTimestamp = userFunction.getTimestamp(in.getValue());

    checkForRemoval(currentTimestamp);
    currentEvents.add(in.getValue());

    output.collect(new StreamRecord<>(currentEvents, System.currentTimeMillis()));

  }

  private void checkForRemoval(Long currentTimestamp) {
    Iterator<IN> it = currentEvents.iterator();

    while(it.hasNext()) {
      IN next = it.next();
      if (removalRequired(userFunction.getTimestamp(next), currentTimestamp)) {
        it.remove();
      } else {
        break;
      }
    }
  }

  private boolean removalRequired(Long oldTimestamp, Long currentTimestamp) {
    return (currentTimestamp - oldTimestamp > timeWindowSizeInMillis);
  }

}
