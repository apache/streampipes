package de.fzi.cep.sepa.flink.samples.enrich.configurabletimestamp;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeHint;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.util.Collector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ConfigurableTimestampEnricher extends RichFlatMapFunction<Map<String, Object>, Map<String, Object>> {

	private String appendTimePropertyName;

	private transient ValueState<Long> state;

	public ConfigurableTimestampEnricher(String appendTimePropertyName) {
		this.appendTimePropertyName = appendTimePropertyName;
	}

	@Override
	public void flatMap(Map<String, Object> in,
						Collector<Map<String, Object>> out) throws Exception {
		Long currentState = state.value();

		if (currentState == Long.MIN_VALUE) {
			Calendar cal = Calendar.getInstance(); // locale-specific
			long time = cal.getTimeInMillis();
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);

			currentState = time - cal.getTimeInMillis();
			state.update(currentState);
		}

		in.put(appendTimePropertyName, System.currentTimeMillis() - currentState);
		out.collect(in);
	}

	public static void main(String[] agrs) {
		Calendar cal = Calendar.getInstance(); // locale-specific
		long time = cal.getTimeInMillis();
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		long diff = time - cal.getTimeInMillis();
		System.out.println();
		System.out.println(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(cal.getTimeInMillis()));
		System.out.println(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(time));
		System.out.println(new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(time - diff));


	}

	@Override
	public void open(Configuration config) {
		ValueStateDescriptor<Long> descriptor =
				new ValueStateDescriptor(
						"timediff",
						TypeInformation.of(new TypeHint<Long>() {}),
						Long.MIN_VALUE
				);
		state = this.getRuntimeContext().getState(descriptor);
	}



}
