package de.fzi.cep.sepa.esper.debs.c1;

import static com.espertech.esper.client.soda.Expressions.property;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.Expression;
import com.espertech.esper.client.soda.Expressions;
import com.espertech.esper.client.soda.Filter;
import com.espertech.esper.client.soda.FilterStream;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.InsertIntoClause;
import com.espertech.esper.client.soda.OrderByElement;
import com.espertech.esper.client.soda.SelectClause;
import com.espertech.esper.client.soda.View;

import de.fzi.cep.sepa.esper.EsperEventEngine;
import de.fzi.cep.sepa.esper.Writer;
import de.fzi.cep.sepa.esper.debs.c2.DebsChallenge2;
import de.fzi.cep.sepa.esper.debs.c2.DebsChallenge2Parameters;
import de.fzi.cep.sepa.esper.enrich.grid.CellOption;
import de.fzi.cep.sepa.esper.enrich.grid.GridCalculator;
import de.fzi.cep.sepa.esper.output.topx.OrderDirection;
import de.fzi.cep.sepa.runtime.OutputCollector;

public class DebsChallenge1 extends EsperEventEngine<DebsChallenge1Parameters>{

	private static final Logger logger = Logger.getAnonymousLogger();
	static String C1_FILENAME = "c:\\users\\riemer\\desktop\\debs-output-short-c1.txt";
	
	private static final double LAT = 41.474937;
	private static final double LON = -74.913585;
	private static final double SOUTHDIFF = 0.004491556;
	private static final double EASTDIFF = 0.005986;
	
	@Override
	protected List<String> statements(DebsChallenge1Parameters params) {
		List<String> statements = new ArrayList<String>();
		
		//statements.add(generateAppend1Statement(params));
		statements.add(generateAppend2Statement(params));
		statements.add(generateCountStatement(params));
		statements.add(generateTopXStatement(params));
		
		//remove
		statements.add(generateTopXDistinctStatement(params));
		
		statements.add("select * from StatusEvent");

		statements.addAll(addChallenge2Queries(params));
		
		return statements;
	}
	
	private String generateTopXDistinctStatement(
			DebsChallenge1Parameters params) {
		return "select lastWindow.list as list from TopXWindow.std:lastevent() as lastWindow, CountStatement.std:lastevent() as lastEvent where de.fzi.cep.sepa.esper.debs.c1.DebsChallenge1.isInArray(lastEvent, lastWindow.list)";
	}

	private Collection<? extends String> addChallenge2Queries(
			DebsChallenge1Parameters params) {
		DebsChallenge2Parameters c2Params = new DebsChallenge2Parameters(params.getInName(), 
				params.getOutName(), 
				params.getAllProperties(), 
				params.getPartitionProperties(), 
				params.getStartingLatitude(), 
				params.getStartingLongitude(), 
				250, params.getLatitudeName(), 
				params.getLongitudeName(), 
				params.getLatitude2Name(), 
				params.getLongitude2Name(), 
				params.getPropertyNames());
		return new DebsChallenge2().getStatementsInternal(c2Params);
	}


	private String generateTopXStatement(DebsChallenge1Parameters params) {
		EPStatementObjectModel model = new EPStatementObjectModel();
		
		//remove if not topxdistinct
		model.insertInto(InsertIntoClause.create("TopXWindow"));
		
		model.selectClause(makeTopXSelectClause(params));
		
		FilterStream stream = FilterStream.create("CountStatement", "allCounts");
		stream.setFilter(Filter.create("CountStatement", Expressions.gt("countValue", 1)));
		
		List<Expression> uniqueProperties = new ArrayList<>();
//		uniqueProperties.add(Expressions.property("cellOptions.cellX"));
//		uniqueProperties.add(Expressions.property("cellOptions.cellY"));
//		uniqueProperties.add(Expressions.property("cellOptions1.cellX"));
//		uniqueProperties.add(Expressions.property("cellOptions.cellY"));
		
		uniqueProperties.add(Expressions.property("cellXPickup"));
		uniqueProperties.add(Expressions.property("cellXDropoff"));
		uniqueProperties.add(Expressions.property("cellYPickup"));
		uniqueProperties.add(Expressions.property("cellYDropoff"));
	
		OrderDirection selectedDirection = OrderDirection.DESCENDING;
		
		OrderByElement element = new OrderByElement();
		element.setExpression(Expressions.property("countValue" +selectedDirection.toEpl()));
		
		List<Expression> viewExpressions = new ArrayList<Expression>();
		for(Expression u : uniqueProperties) viewExpressions.add(u);
		viewExpressions.add(Expressions.constant(10));
		viewExpressions.add(element.getExpression());
		viewExpressions.add(Expressions.property("read_datetime desc"));
		
		View timeView = View.create("win", "time", Expressions.timePeriod(0, 0, 30, 0, 0));
		View view = View.create("ext", "rank", viewExpressions);
		
		uniqueProperties.add(Expressions.property("countValue"));
		stream.addView(view);		
		stream.addView(timeView);
		
		FromClause fromClause = new FromClause();
		fromClause.add(stream);
		
		/*
		FilterStream lastEvent = FilterStream.create("CountStatement", "lastE");
		lastEvent.addView("std", "lastevent");
		fromClause.add(lastEvent);
		*/
		model.fromClause(fromClause); // in name
		
		
		//model.whereClause(Expressions.Expressions.eq(Expressions.property("istream()"), Expressions.property("true")));
		
		logger.info("Generated EPL: " +model.toEPL());
		
		return model.toEPL();
	}

	private String generateCountStatement(DebsChallenge1Parameters params) {
		String statement = "insert into CountStatement select " 
				+getSelectClause(params) 
				//+"*, " 
				+" count(*) as countValue from AppendTwo" 
				+".win:time(30 min) " 
				//+"group by cellOptions.cellX, cellOptions.cellY, cellOptions1.cellX, cellOptions1.cellY";
				+"group by cellXPickup, cellXDropoff, cellYPickup, cellYDropoff";
		
		logger.info("Generated EPL: " +statement);
		
		return statement;
		
	}

	private String generateAppend1Statement(DebsChallenge1Parameters params)
	{
		EPStatementObjectModel model = new EPStatementObjectModel();
		model.insertInto(new InsertIntoClause("AppendOne")); // out name
		model.selectClause(makeSelectClause(params, true));
		model.fromClause(new FromClause().add(FilterStream.create(fixEventName(params.getInName())))); // in name
		
		logger.info("Generated EPL: " +model.toEPL());
		
		return model.toEPL();
	}
	
	private String generateAppend2Statement(DebsChallenge1Parameters params) {
		EPStatementObjectModel model = new EPStatementObjectModel();
		model.insertInto(new InsertIntoClause("AppendTwo")); // out name
		model.selectClause(makeSelectClause(params, false));//.add("cellOptions"));
		
		model.fromClause(new FromClause().add(FilterStream.create(fixEventName(params.getInName())))); // in name
		//model.fromClause(new FromClause().add(FilterStream.create("AppendOne"))); // in name
		
		logger.info("Generated EPL: " +model.toEPL());
		
		return model.toEPL();
	}
	
	private SelectClause makeSelectClause(DebsChallenge1Parameters params, boolean pickup)
	{
		String latPropertyName, lngPropertyName, cellOptionsName;
		if (pickup)
		{
			latPropertyName = params.getLatitudeName();
			lngPropertyName = params.getLongitudeName();
			cellOptionsName = "cellOptions";
		}
		else
		{
			latPropertyName = params.getLatitude2Name();
			lngPropertyName = params.getLongitude2Name();
			cellOptionsName = "cellOptions1";
		}
		
		SelectClause clause = SelectClause.create();
		/*
		for(String property : params.getPropertyNames())
		{
			clause.add(property);
		}
		*/
		clause.addWildcard();
		clause.add(Expressions.cast(Expressions.divide(Expressions.staticMethod(
				"Math",
				"abs", 
				Expressions.minus(Expressions.constant(LON), Expressions.property(lngPropertyName)) 
				), Expressions.constant(EASTDIFF)), "int"), "cellXPickup");
		
		clause.add(Expressions.cast(Expressions.divide(Expressions.staticMethod(
				"Math",
				"abs", 
				Expressions.minus(Expressions.constant(LAT), Expressions.property(latPropertyName))  
				), Expressions.constant(SOUTHDIFF)), "int"), "cellYPickup");
		
		clause.add(Expressions.cast(Expressions.divide(Expressions.staticMethod(
				"Math",
				"abs", 
				Expressions.minus(Expressions.constant(LON), Expressions.property(lngPropertyName)) 
				), Expressions.constant(EASTDIFF)), "int"), "cellXDropoff");
		
		clause.add(Expressions.cast(Expressions.divide(Expressions.staticMethod(
				"Math",
				"abs", 
				Expressions.minus(Expressions.constant(LAT), Expressions.property(latPropertyName))  
				), Expressions.constant(SOUTHDIFF)), "int"), "cellYDropoff");
		
		/*
		clause.add(Expressions.staticMethod(
				DebsChallenge1.class.getName(),
				"computeCells", 
				property(latPropertyName), 
				property(lngPropertyName), 
				Expressions.constant(params.getCellSize()),
				Expressions.constant(params.getStartingLatitude()),
				Expressions.constant(params.getStartingLongitude())), 
				cellOptionsName);
		*/
		return clause;
	}
	
	private String getSelectClause(DebsChallenge1Parameters params)
	{
		String result = "";
		for(String property : params.getPropertyNames())
		{
			result = result +property +", ";
		}
		//result = result +"cellOptions, ";
		//result = result +"cellOptions1, ";
		result = result +"cellXPickup, ";
		result = result +"cellXDropoff, ";
		result = result +"cellYPickup, ";
		result = result +"cellYDropoff, ";
		return result;
	}
	
	private SelectClause makeTopXSelectClause(DebsChallenge1Parameters params) {
		SelectClause clause = SelectClause.create();
		clause.add("distinct window(*) as list");
		/*
		clause.add(Expressions.staticMethod(
				DebsChallenge1.class.getName(),
				"addCounter",
				property("current_timestamp")), 
				"generalCounter");
		*/
		return clause;
	}
	
	public static CellOption computeCells(double latitude, double longitude, int cellSize, double latitudeStart, double longitudeStart)
	{
		return new GridCalculator().computeCellsNaive(latitude, longitude, cellSize, latitudeStart, longitudeStart);
	}
	
	static int generalCounter = 0;
	public static int addCounter(long timestamp)
	{
		generalCounter = generalCounter+1;
		return generalCounter;
	}
	
	@Override
	public Writer getWriter(OutputCollector collector, DebsChallenge1Parameters params)
	{
		DebsOutputParameters outputParams = new DebsOutputParameters(C1_FILENAME);	
		Challenge1FileWriter writer = new Challenge1FileWriter(outputParams, OutputType.PERSIST);
		
		return writer;
	}
	
	public static boolean isInArray(Map<String, Object> lastEvent, Map<String, Object>[] lastWindow)
	{
		for(Map<String, Object> windowItem : lastWindow)
		{
			if (windowItem.equals(lastEvent)) 
					return true;
		}
		return false;
	}

}
