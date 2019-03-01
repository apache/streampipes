package org.streampipes.wrapper.flink.logger;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.streampipes.logging.impl.EventStatisticLogger;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.model.runtime.Event;

public class StatisticLogger implements FlatMapFunction<Event, Event> {

    private InvocableStreamPipesEntity graph;

    public StatisticLogger(InvocableStreamPipesEntity graph) {
        this.graph = graph;
    }

    @Override
    public void flatMap(Event in, Collector<Event> out) throws Exception {
        EventStatisticLogger.log(graph.getName(), graph.getCorrespondingPipeline(), graph.getUri());
        out.collect(in);
    }
}


