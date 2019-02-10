package org.streampipes.wrapper.flink.logger;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.streampipes.logging.impl.EventStatisticLogger;
import org.streampipes.model.base.InvocableStreamPipesEntity;

import java.util.Map;

public class StatisticLogger implements FlatMapFunction<Map<String, Object>, Map<String, Object>> {

    private InvocableStreamPipesEntity graph;

    public StatisticLogger(InvocableStreamPipesEntity graph) {
        this.graph = graph;
    }

    @Override
    public void flatMap(Map<String, Object> in, Collector<Map<String, Object>> out) throws Exception {
        EventStatisticLogger.log(graph.getName(), graph.getCorrespondingPipeline(), graph.getUri());
        out.collect(in);
    }
}


