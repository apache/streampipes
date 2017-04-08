package de.fzi.cep.sepa.flink.samples.count.aggregate;

import de.fzi.cep.sepa.flink.FlinkDeploymentConfig;
import de.fzi.cep.sepa.flink.FlinkSepaRuntime;
import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountAggregateProgram extends FlinkSepaRuntime<CountAggregateParameters>{

	public CountAggregateProgram(CountAggregateParameters params) {
		super(params);
	}

	public CountAggregateProgram(CountAggregateParameters params, FlinkDeploymentConfig config) {
		super(params, config);
	}

	@Override
	protected DataStream<Map<String, Object>> getApplicationLogic(
			DataStream<Map<String, Object>>... messageStream) {

		List<String> groupBy = params.getGroupBy();
		DataStream<Tuple2<String, Map<String, Object>>> bla = (DataStream<Tuple2<String, Map<String, Object>>>) messageStream[0]

				.map(new MapFunction<Map<String, Object>, Tuple2<String, Map<String, Object>>>() {
					@Override
					public Tuple2<String, Map<String, Object>> map(Map<String, Object> value) throws Exception {

						String newKey = "";

						for (String s : groupBy) {
							newKey = newKey + value.get(s).toString();
						}

						return new Tuple2<String, Map<String, Object>>(newKey, value);
					}
				})
				.keyBy(0)
				.timeWindow(Time.minutes(1), Time.seconds(30))
				.fold(new Tuple2<String, Map<String, Object>>("", new HashMap<String, Object>()), new MyFoldFunction(), new MyWindowFunction());

//						.fold(new Tuple2<String, Map<String, Object>>("", new HashMap<String, Object>()), new MyFoldFunction(),
//						new WindowFunction<Tuple2<String, Map<String, Object>>, Tuple2<String, Map<String, Object>>, String, TimeWindow>() {
//							@Override
//							public void apply(String s, TimeWindow window, Iterable<Tuple2<String, Map<String, Object>>> input, Collector<Tuple2<String, Map<String, Object>>> out) throws Exception {
//
//							}
//						}));


		return bla.map(new MapFunction< Tuple2<String, Map<String, Object>>, Map<String, Object>>() {
			@Override
			public Map<String, Object> map(Tuple2<String, Map<String, Object>> value) throws Exception {
				return value.f1;
			}
		});
	}


	private static class MyFoldFunction
			implements FoldFunction<Tuple2<String, Map<String, Object>>, Tuple2<String, Map<String, Object>> > {

		public Tuple2<String, Map<String, Object>> fold(Tuple2<String, Map<String, Object>> acc, Tuple2<String, Map<String, Object>> s) {
			Integer cur = acc.getField(2);
			acc.setField(2, cur + 1);
			return acc;
		}
	}

	private static class MyWindowFunction
			implements WindowFunction<Tuple2<String, Map<String, Object>>, Tuple2<String, Map<String, Object>>, String, TimeWindow> {

		public void apply(String key,
						  TimeWindow window,
						  Iterable<Tuple2<String, Map<String, Object>>> counts,
						  Collector<Tuple2<String, Map<String, Object>>> out) {
			Integer count = counts.iterator().next().getField(2);

//			out.collect(new Tuple2<String, Map<String, Object>>(key, window.getEnd(),count));
		}
	}
}
