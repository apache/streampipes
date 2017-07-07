package org.streampipes.wrapper.flink.extensions;

import org.apache.flink.streaming.api.operators.AbstractStreamOperator;
import org.apache.flink.streaming.api.operators.OneInputStreamOperator;
import org.apache.flink.streaming.runtime.streamrecord.StreamRecord;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by riemer on 21.04.2017.
 */
public class SlidingBatchWindow<IN> extends AbstractStreamOperator<List<IN>> implements
        OneInputStreamOperator<IN, List<IN>> {

  private Integer windowSize;
  private List<IN> currentEvents;

  public SlidingBatchWindow(Integer windowSize) {
    super();
    this.windowSize = windowSize;
    this.currentEvents = new ArrayList<>();
  }

  @Override
  public void processElement(StreamRecord<IN> in) throws Exception {
    currentEvents.add(in.getValue());
    if (currentEvents.size() > windowSize) {
      currentEvents.remove(0);
    }

    output.collect(new StreamRecord<>(currentEvents, System.currentTimeMillis()));

  }
}
