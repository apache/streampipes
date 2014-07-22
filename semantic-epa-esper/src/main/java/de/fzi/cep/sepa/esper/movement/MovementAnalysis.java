package de.fzi.cep.sepa.esper.movement;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

import org.geotools.geometry.DirectPosition2D;
import org.geotools.referencing.CRS;
import org.geotools.referencing.GeodeticCalculator;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.operation.TransformException;
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
import com.espertech.esper.client.soda.FilterStream;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.InsertIntoClause;
import com.espertech.esper.client.soda.MatchRecogizePatternElementType;
import com.espertech.esper.client.soda.MatchRecognizeClause;
import com.espertech.esper.client.soda.MatchRecognizeDefine;
import com.espertech.esper.client.soda.MatchRecognizeRegExAtom;
import com.espertech.esper.client.soda.MatchRecognizeRegExConcatenation;
import com.espertech.esper.client.soda.MatchRecognizeSkipClause;
import com.espertech.esper.client.soda.SelectClause;
import com.espertech.esper.client.soda.SelectClauseExpression;

import static com.espertech.esper.client.soda.Expressions.*;
import de.fzi.cep.sepa.runtime.EPEngine;
import de.fzi.cep.sepa.runtime.OutputCollector;
import de.fzi.cep.sepa.runtime.param.EngineParameters;

public class MovementAnalysis implements EPEngine<MovementParameter> {

	private GeodeticCalculator distanceCalc; // separated to prevent conflicts
	private GeodeticCalculator bearingCalc; // problem -> 2x computation of same value

	private EPServiceProvider epService;

	private static final Logger logger = LoggerFactory.getLogger(MovementAnalysis.class.getSimpleName());

	private static final String EVENT_NAME_PARAM = "name";

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void bind(EngineParameters<MovementParameter> parameters, OutputCollector collector) {
		if (parameters.getInEventTypes().size() != 1)
			throw new IllegalArgumentException("Movement analysis only possible on one event type.");

		try {
			distanceCalc = new GeodeticCalculator(CRS.decode(parameters.getStaticProperty().getPositionCRS()));
			bearingCalc = new GeodeticCalculator(CRS.decode(parameters.getStaticProperty().getPositionCRS()));
		} catch (FactoryException e) {
			throw new IllegalArgumentException("CRS not recognized! "
				+ parameters.getStaticProperty().getPositionCRS(), e);
		}

		Configuration config = new Configuration();
		parameters.getInEventTypes().entrySet().forEach(e -> {
			Map inTypeMap = e.getValue();
			config.addEventType(e.getKey(), inTypeMap); // indirect cast from Class to Object
		});
		// Map outTypeMap = parameters.getOutEventType();
		// config.addEventType(parameters.getOutEventName(), outTypeMap); // indirect cast from Class to Object

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

	private EPStatementObjectModel statement(final MovementParameter params) {
		EPStatementObjectModel model = new EPStatementObjectModel();
		model.insertInto(new InsertIntoClause(params.getOutName())); // out name
		model.selectClause(SelectClause.createWildcard());
		model.fromClause(new FromClause().add(FilterStream.create(params.getInName()))); // in name

		MatchRecognizeClause match = new MatchRecognizeClause();
		if (!params.getPartitionProperties().isEmpty()) { // partition by
			match.setPartitionExpressions(params.getPartitionProperties()
				.stream().map(propertyName -> property(propertyName)).collect(toList()));
		}

		List<SelectClauseExpression> measures = params.getAllProperties().stream().map( // all props
			property -> new SelectClauseExpression(property("B." + property), property)
			).collect(toList());

		Expression interval = minus("B." + params.getTimestampProperty(),
			"A." + params.getTimestampProperty()); // timestamp property
		measures.add(new SelectClauseExpression(interval, "interval"));

		Expression distance = staticMethod(MovementAnalysis.class.getName(), "distance",
			property("A." + params.getXProperty()), // x and y property
			property("A." + params.getYProperty()),
			property("B." + params.getXProperty()),
			property("B." + params.getYProperty()),
			constant(distanceCalc));
		measures.add(new SelectClauseExpression(distance, "distance"));

		Expression speed = staticMethod(MovementAnalysis.class.getName(), "speed", distance, interval);
		measures.add(new SelectClauseExpression(speed, "speed"));

		Expression course = staticMethod(MovementAnalysis.class.getName(), "bearing",
			property("A." + params.getXProperty()), // x and y property
			property("A." + params.getYProperty()),
			property("B." + params.getXProperty()),
			property("B." + params.getYProperty()),
			constant(bearingCalc));
		measures.add(new SelectClauseExpression(course, "bearing"));

		match.setMeasures(measures);

		match.setSkipClause(MatchRecognizeSkipClause.TO_NEXT_ROW);

		MatchRecognizeRegExConcatenation concat = new MatchRecognizeRegExConcatenation();
		concat.setChildren(Arrays.asList(
			new MatchRecognizeRegExAtom("A", MatchRecogizePatternElementType.SINGLE),
			new MatchRecognizeRegExAtom("B", MatchRecogizePatternElementType.SINGLE)));
		match.setPattern(concat);

		match.setDefines(Arrays.asList(
			new MatchRecognizeDefine("B", lt(interval, constant(params.getMaxInterval()))))); // max interval

		model.setMatchRecognizeClause(match);

		return model;
		// return "INSERT INTO MovementEvent SELECT * FROM PositionEvent match_recognize ( "
		// + "partition by userId "
		// + "measures B.userId as userId, B.position as position, B.timestamp as timestamp, B.timestamp - A.timestamp "
		// + "as interval, distance(A, B) as distance, speed(A, B) as speed, courseAngle(A, B) as course "
		// + "after match skip to next row "
		// + "pattern (A B) define B as (B.timestamp - A.timestamp) < 8000 )";
	}

	// X Y determined by the CRS used X = first coordinate, Y = second coordinate
	public static synchronized double distance(double fromX, double fromY, double toX, double toY,
		GeodeticCalculator calc) { // in meters
		return prepare(fromX, fromY, toX, toY, calc).getOrthodromicDistance();
	}

	public static double speed(double distance, long msInterval) {
		return (distance / (msInterval * 0.001));
	}

	public static synchronized double bearing(double fromX, double fromY, double toX, double toY,
		GeodeticCalculator calc) { // initial bearing -> forward azimuth
		return prepare(fromX, fromY, toX, toY, calc).getAzimuth();
	}

	private static GeodeticCalculator prepare(double fromX, double fromY, double toX, double toY,
		GeodeticCalculator calc) {
		try {
			calc.setStartingPosition(new DirectPosition2D(fromX, fromY));
			calc.setDestinationPosition(new DirectPosition2D(toX, toY));
		} catch (TransformException e) {
			logger.warn("InvalidTransformation: x={} y={} to x={} y={} calc={}", fromX, fromY, toX, toY, calc);
			throw new RuntimeException("Coordinates do match CRS", e);
		}
		return calc;
	}

	public static void main(String[] args) throws Exception {
		GeodeticCalculator calc = new GeodeticCalculator(CRS.decode("EPSG:4326"));

		// in EPSG 4326 X = Latitude, Y = Longitude
		System.out.println(bearing(49.009304, 8.410172, 49.011071, 8.410168, calc));

		System.out.println(speed(30, 10));
	}

	@Override
	public void onEvent(Map<String, Object> event) {
		logger.info("New event: {}", event);
		epService.getEPRuntime().sendEvent(event, (String) event.get(EVENT_NAME_PARAM));
	}

	@Override
	public void discard() {
		epService.destroy();
	}
}
