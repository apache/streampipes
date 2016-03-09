package de.fzi.cep.sepa.esper.pattern.streamstopped;

import java.util.List;

import de.fzi.cep.sepa.esper.EsperEventEngine;

public class StreamStopped extends EsperEventEngine<StreamStoppedParameter> {

	protected List<String> statements(final StreamStoppedParameter params) {
		String inName = params.getInputStreamParams().get(0).getInName();
		String topic = params.getTopic();
//		 String epl = "select rate(10) as rate from " +inName +" output snapshot every 5 sec";
		// String epl = "select count(*) from MyEvent.win:time(10 sec) having
		// count(*) >= 76";

//		String epl = "select * from pattern [every " + inName + ".win:time(10 sec) having count(*) >= 0 -> (timer:interval(10 sec) and not " + inName + "..win:time(10 sec) having count(*) == 0)]";
		 
		 String epl = "select '"+ topic +"' as topic, current_timestamp as timestamp from pattern[every a=" + fixEventName(inName) +
				 " -> timer:interval(6 sec) and not b="+ fixEventName(inName) + "]";

		return makeStatementList(epl);

	}
}
