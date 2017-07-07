package de.fzi.cep.sepa.esper.movement;

import java.util.ArrayList;
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
import de.fzi.cep.sepa.esper.EsperEventEngine;

public class MovementAnalysis extends EsperEventEngine<MovementParameter> {

	private GeodeticCalculator distanceCalc; // separated to prevent conflicts
	private GeodeticCalculator bearingCalc; // problem -> 2x computation of same value

	private static final Logger logger = LoggerFactory.getLogger(MovementAnalysis.class.getSimpleName());

	protected List<String> statements(final MovementParameter params) {
		
		List<String> statements = new ArrayList<String>();
		try {
			distanceCalc = new GeodeticCalculator(CRS.decode(params.getPositionCRS()));
			bearingCalc = new GeodeticCalculator(CRS.decode(params.getPositionCRS()));
		} catch (FactoryException e) {
			throw new IllegalArgumentException("CRS not recognized! "
				+ params.getPositionCRS(), e);
		}
		
		EPStatementObjectModel model = new EPStatementObjectModel();
		model.insertInto(new InsertIntoClause(params.getOutName())); // out name
		model.selectClause(SelectClause.createWildcard());
		model.fromClause(new FromClause().add(FilterStream.create(params.getInputStreamParams().get(0).getInName()))); // in name

		MatchRecognizeClause match = new MatchRecognizeClause();
		if (!params.getPartitionProperties().isEmpty()) { // partition by
			match.setPartitionExpressions(params.getPartitionProperties()
				.stream().map(propertyName -> property(propertyName)).collect(toList()));
		}

		List<SelectClauseExpression> measures = params.getInputStreamParams().get(0).getAllProperties().stream().map( // all props
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

		statements.add(model.toEPL());
		return statements;
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
			//logger.warn("InvalidTransformation: x={} y={} to x={} y={} calc={}", fromX, fromY, toX, toY, calc);
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
	public void onEvent(Map<String, Object> event, String sourceInfo) {
		logger.info("New event: {}", event);
	//	epService.getEPRuntime().sendEvent(event, (String) event.get(EVENT_NAME_PARAM));
		epService.getEPRuntime().sendEvent(event, sourceInfo);
	}

	@Override
	public void discard() {
		epService.destroy();
	}

	
}
