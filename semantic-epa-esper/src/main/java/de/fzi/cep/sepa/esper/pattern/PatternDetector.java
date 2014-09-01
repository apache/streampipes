package de.fzi.cep.sepa.esper.pattern;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.Expression;
import com.espertech.esper.client.soda.Expressions;
import com.espertech.esper.client.soda.FilterStream;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.InsertIntoClause;
import com.espertech.esper.client.soda.PatternAndExpr;
import com.espertech.esper.client.soda.PatternExpr;
import com.espertech.esper.client.soda.PatternStream;
import com.espertech.esper.client.soda.Patterns;
import com.espertech.esper.client.soda.SelectClause;

import de.fzi.cep.sepa.esper.filter.text.TextFilter;
import de.fzi.cep.sepa.esper.filter.text.TextFilterParameter;
import de.fzi.cep.sepa.esper.util.StringOperator;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SEPAInvocationGraph;
import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.OutputCollector;
import de.fzi.cep.sepa.runtime.param.EngineParameters;

public class PatternDetector implements EPEngine<PatternParameters> {

	private EPServiceProvider epService;
	
	private static final Logger logger = LoggerFactory.getLogger(PatternDetector.class.getSimpleName());

	
	@Override
	public void bind(EngineParameters<PatternParameters> parameters,
			OutputCollector collector) {
		
		Configuration config = new Configuration();
		parameters.getInEventTypes().entrySet().forEach(e -> {
			Map inTypeMap = e.getValue();
			config.addEventType(e.getKey(), inTypeMap); // indirect cast from Class to Object
		});
		
		epService = EPServiceProviderManager.getDefaultProvider(config);

		EPStatementObjectModel model = statement(parameters.getStaticProperty());
		EPStatement statement = epService.getEPAdministrator().create(model);
		
	
		statement.addListener(listenerSendingTo(collector));
		statement.start();
		
	}
	
	private static UpdateListener listenerSendingTo(OutputCollector collector) {
		return new UpdateListener() {
			@Override
			public void update(EventBean[] newEvents, EventBean[] oldEvents) {
				if (newEvents != null && newEvents.length > 0) {
					logger.info("Sending event {} ", newEvents[0].getUnderlying());
					collector.send(newEvents[0].getUnderlying());
				} else {
					logger.info("Triggered listener but there is no new event");
				}
			}
		};
	}
	
	private EPStatementObjectModel statement(final PatternParameters params) {
		EPStatementObjectModel model = new EPStatementObjectModel();
		model.insertInto(new InsertIntoClause(params.getOutName())); // out name
		model.selectClause(SelectClause.createWildcard());
		
		SEPAInvocationGraph graph = params.getGraph();
		
		PatternAndExpr pattern = Patterns.and();
				 
		EventStream leftStream = graph.getInputStreams().get(0);
		EventStream rightStream = graph.getInputStreams().get(1);
		
		pattern.add(Patterns.everyFilter(leftStream.getName(), rightStream.getName()));
		pattern.add(Patterns.everyFilter(rightStream.getName(), rightStream.getName()));
		model.setFromClause(FromClause.create(PatternStream.create(pattern)));
		
		System.out.println(model.toEPL());
		return model;
		
	}

	@Override
	public void onEvent(Map<String, Object> event, String sourceInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void discard() {
		// TODO Auto-generated method stub
		
	}

}
