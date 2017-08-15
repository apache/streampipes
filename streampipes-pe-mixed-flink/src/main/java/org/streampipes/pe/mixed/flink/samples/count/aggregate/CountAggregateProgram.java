package org.streampipes.pe.mixed.flink.samples.count.aggregate;

import org.streampipes.wrapper.flink.FlinkDeploymentConfig;
import org.streampipes.wrapper.flink.FlinkSepaRuntime;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.WindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CountAggregateProgram extends FlinkSepaRuntime<CountAggregateParameters> {

	private static String AGGREGATE_COUNT = "aggregate_taxi_count";
	public CountAggregateProgram(CountAggregateParameters params) {
		super(params);
		this.streamTimeCharacteristic = TimeCharacteristic.EventTime;
	}

	public CountAggregateProgram(CountAggregateParameters params, FlinkDeploymentConfig config) {
		super(params, config);
		this.streamTimeCharacteristic = TimeCharacteristic.EventTime;
	}

	@Override
	protected DataStream<Map<String, Object>> getApplicationLogic(
			DataStream<Map<String, Object>>... messageStream) {

		List<String> groupBy = params.getGroupBy();

		DataStream<Map<String, Object>> result = messageStream[0]
				.map(new MapFunction<Map<String, Object>, Tuple2<String, Map<String, Object>>>() {
					@Override
					public Tuple2<String, Map<String, Object>> map(Map<String, Object> value) throws Exception {

						String newKey = "";

						for (String s : groupBy) {
							newKey = newKey + value.get(s).toString() + "x";
						}

						newKey = newKey.substring(0, newKey.length()-1);

						return new Tuple2<String, Map<String, Object>>(newKey, value);
					}
				})
				.setParallelism(1)
				.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<Tuple2<String, Map<String, Object>>>() {

					@Override
					public long extractAscendingTimestamp(Tuple2<String, Map<String, Object>> element) {
						return (long) element.f1.get("tpep_pickup_datetime");
					}
				})
				.setParallelism(1)
				.keyBy(0)
				.timeWindow(params.getTimeWindowSize(), params.getSlideWindowSize())
				.apply(new MyWindow2Function())
				.setParallelism(1)
				.map(new MapFunction< Tuple2<String, Map<String, Object>>, Map<String, Object>>() {
					@Override
					public Map<String, Object> map(Tuple2<String, Map<String, Object>> value) throws Exception {
						return value.f1;
					}
				});

		return result;
	}

	private static class MyWindow2Function implements WindowFunction<Tuple2<String,Map<String,
			Object>>, Tuple2<String, Map<String, Object>>, Tuple, TimeWindow> {

		@Override
		public void apply(Tuple tuple, TimeWindow timeWindow, Iterable<Tuple2<String, Map<String, Object>>> iterable,
						  Collector<Tuple2<String, Map<String, Object>>> collector) throws Exception {

			Map<String, Object> result = new HashMap<>();
			String key = "";
			Iterator<Tuple2<String, Map<String, Object>>> iterator = iterable.iterator();
			int count = 0;
			long passengerCount = 0;
			double tripDistance = 0;
			double fareAmount = 0;
			double extra = 0;
			double tip_amount = 0;
			double tolls_amount = 0;
			double total_amount = 0;
			int[] rateCodeIdValues = {0, 0, 0, 0, 0, 0};
			int[] paymentTypeValues = {0, 0, 0, 0, 0, 0};
			int mtaTaxCount = 0;
			int improvementSurchargeCount = 0;

			double grid_lat_nw_key = 0;
			double grid_lon_nw_key = 0;
			double grid_lat_SE_key = 0;
			double grid_lon_SE_key = 0;

			while(iterator.hasNext()) {
				Tuple2<String, Map<String, Object>> tmp = iterator.next();
				count++;

				passengerCount += (int) tmp.f1.get("passenger_count");
				tripDistance += toDouble(tmp.f1.get("trip_distance"));
				fareAmount += toDouble(tmp.f1.get("fare_amount"));
				extra += toDouble(tmp.f1.get("extra"));
				tip_amount += toDouble(tmp.f1.get("tip_amount"));
				tolls_amount += toDouble(tmp.f1.get("tolls_amount"));
				total_amount += toDouble(tmp.f1.get("total_amount"));

				int rateCodeId = (int) tmp.f1.get("ratecode_id");
				if (rateCodeId > -1 && rateCodeId < 6) {
					rateCodeIdValues[rateCodeId - 1] += 1;
				}

				int paymentType = (int) tmp.f1.get("payment_type");
				if (paymentType > - 1 && paymentType < 6) {
					paymentTypeValues[paymentType - 1] += 1;
				}

				double mtaTax = toDouble(tmp.f1.get("mta_tax"));
				if (mtaTax == 0.5) {
					mtaTaxCount++;
				}

				double improvementSurcharge = toDouble(tmp.f1.get("improvement_surcharge"));
				if (improvementSurcharge == 0.3) {
					improvementSurchargeCount++;
				}

				result.put("vendor_id", tmp.f1.get("vendor_id"));
				grid_lat_nw_key = toDouble(tmp.f1.get(CountAggregateConstants.GRID_LAT_NW_KEY));
				grid_lon_nw_key = toDouble(tmp.f1.get(CountAggregateConstants.GRID_LON_NW_KEY));
				grid_lat_SE_key = toDouble(tmp.f1.get(CountAggregateConstants.GRID_LAT_SE_KEY));
				grid_lon_SE_key = toDouble(tmp.f1.get(CountAggregateConstants.GRID_LON_SE_KEY));


				key = tmp.f0;
			}

			double passengerCountAvg = ((double) passengerCount) / count;
			double tripDistanceAvg = ((double) tripDistance) / count;
			double fareAmountAvg = ((double) fareAmount) / count;
			double extraAvg = ((double) extra) / count;
			double tip_amountAvg = ((double) tip_amount) / count;
			double tolls_amountAvg = ((double) tolls_amount) / count;
			double total_amountAvg = ((double) total_amount) / count;

			result.put(CountAggregateConstants.WINDOW_TIME_START, timeWindow.getStart());
			result.put(CountAggregateConstants.WINDOW_TIME_END, timeWindow.getEnd());

			result.put(CountAggregateConstants.PASSENGER_COUNT_AVG, passengerCountAvg);

			result.put(CountAggregateConstants.TRIP_DISTANCE_AVG, tripDistanceAvg);
			result.put(CountAggregateConstants.FARE_AMOUNT_AVG, fareAmountAvg);
			result.put(CountAggregateConstants.EXTRA_AVG, extraAvg);
			result.put(CountAggregateConstants.TIP_AMOUNT_AVG, tip_amountAvg);
			result.put(CountAggregateConstants.TOLLS_AMOUNT_AVG, tolls_amountAvg);
			result.put(CountAggregateConstants.TOTAL_AMOUNT_AVG, total_amountAvg);

			// Rate code count
			result.put(CountAggregateConstants.RATE_CODE_ID_1, rateCodeIdValues[0]);
			result.put(CountAggregateConstants.RATE_CODE_ID_2, rateCodeIdValues[1]);
			result.put(CountAggregateConstants.RATE_CODE_ID_3, rateCodeIdValues[2]);
			result.put(CountAggregateConstants.RATE_CODE_ID_4, rateCodeIdValues[3]);
			result.put(CountAggregateConstants.RATE_CODE_ID_5, rateCodeIdValues[4]);
			result.put(CountAggregateConstants.RATE_CODE_ID_6, rateCodeIdValues[5]);

			//Payment type count
			result.put(CountAggregateConstants.PAYMENT_TYPE_1, paymentTypeValues[0]);
			result.put(CountAggregateConstants.PAYMENT_TYPE_2, paymentTypeValues[1]);
			result.put(CountAggregateConstants.PAYMENT_TYPE_3, paymentTypeValues[2]);
			result.put(CountAggregateConstants.PAYMENT_TYPE_4, paymentTypeValues[3]);
			result.put(CountAggregateConstants.PAYMENT_TYPE_5, paymentTypeValues[4]);
			result.put(CountAggregateConstants.PAYMENT_TYPE_6, paymentTypeValues[5]);

			result.put(CountAggregateConstants.MTA_TAX, mtaTaxCount);
			result.put(CountAggregateConstants.IMPROVEMENT_SURCHARGE, improvementSurchargeCount);

			result.put(CountAggregateConstants.GRID_LAT_NW_KEY, grid_lat_nw_key);
			result.put(CountAggregateConstants.GRID_LON_NW_KEY, grid_lon_nw_key);
			result.put(CountAggregateConstants.GRID_LAT_SE_KEY, grid_lat_SE_key);
			result.put(CountAggregateConstants.GRID_LON_SE_KEY, grid_lon_SE_key);

			result.put(CountAggregateConstants.GRID_CELL_ID, key);

			result.put(AGGREGATE_COUNT, count);


//			System.out.println("===================== Taxi Aggregate =======================");
//			System.out.println("Window start: " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(timeWindow.getStart()));
//			System.out.println(result);
//			System.out.println("Window end: " + new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(timeWindow.getEnd()));
//			System.out.println("============================================================");



			collector.collect(new Tuple2<String, Map<String, Object>>(key, result));
		}
	}

	private static double toDouble(Object o) {
		double result;
		if (o instanceof Integer) {
			result = (Integer) o;
		} else {
			result = (double) o;

		}

		return result;
	}

}
