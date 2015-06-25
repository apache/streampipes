package de.fzi.cep.sepa.esper.debs.c2;

import static com.espertech.esper.client.soda.Expressions.property;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.Expressions;
import com.espertech.esper.client.soda.FilterStream;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.InsertIntoClause;
import com.espertech.esper.client.soda.SelectClause;

import de.fzi.cep.sepa.esper.EsperEventEngine;
import de.fzi.cep.sepa.esper.Writer;
import de.fzi.cep.sepa.esper.debs.c1.Challenge1FileWriter;
import de.fzi.cep.sepa.esper.debs.c1.DebsOutputParameters;
import de.fzi.cep.sepa.esper.debs.c1.OutputType;
import de.fzi.cep.sepa.esper.enrich.grid.CellOption;
import de.fzi.cep.sepa.esper.enrich.grid.GridCalculator2;
import de.fzi.cep.sepa.runtime.OutputCollector;

public class DebsChallenge2 extends EsperEventEngine<DebsChallenge2Parameters>{

	private static final Logger logger = Logger.getAnonymousLogger();
	public static String C2_FILENAME = "c:\\users\\riemer\\desktop\\debs-output-short-c2.txt";
	
	private static final double LAT = 41.474937;
    private static final double LON = -74.913585;

    // 250 meter south corresponds to a change of 0.002245778
    private static final double SOUTHDIFF = 0.002245778;

    // 500 meter east corresponds to a change of 0.002993 degrees
    private static final double EASTDIFF = 0.002993;
	
	public List<String> getStatementsInternal(DebsChallenge2Parameters params)
	{
		return statements(params);
	}
	
	@Override
	protected List<String> statements(DebsChallenge2Parameters params) {
		
		List<String> statements = new ArrayList<String>();
		
		//statements.add(generateAppend1Statement(params));
		statements.add(generateAppend2Statement(params));
		statements.add(generateAreaProfitStatement(params));
		
		//statements.add(generateEmptyTaxiStatement(params));
		//statements.add(generateProfitabilityStatement(params));
		
		statements.add(generateEmptyTaxiStatementFast(params));
		statements.add(generateProfitabilityStatementFast(params));
		
		statements.add(generateBestCellsStatement(params));
		//statements.add(generateBestCellsDistinctStatement(params));
		/*
		statements.add(generateCountStatement(params));
		statements.add(generateTopXStatement(params));
		*/
		
		//statements.add("select * from StatusEvent");

		return statements;
	}
	
	private String generateBestCellsDistinctStatement(
			DebsChallenge2Parameters params) {
		return "select profWindow.bestCells as bestCells from ProfitabilityWindow.std:lastevent() as profWindow, Profitability.std:lastevent() as lastEvent where de.fzi.cep.sepa.esper.debs.c2.DebsChallenge2.isInArray(lastEvent, profWindow.bestCells)";
	}

	// insert into ProfitabilityWindow 
	private String generateBestCellsStatement(DebsChallenge2Parameters params) {
		String statement = "select window(*) as bestCells from Profitability.win:time(15 min).ext:rank(cellX, cellY, 10, profitability desc, read_datetime desc)";
		return statement;
	}

	private String generateProfitabilityStatement(DebsChallenge2Parameters params) {
		String statement = "insert into Profitability "
				+ "select taxiPerArea.pickup_datetime as pickup_datetime, "
				+ "taxiPerArea.dropoff_datetime as dropoff_datetime, taxiPerArea.read_datetime as read_datetime, "
				+ "areaProfit.cellX as cellX, areaProfit.cellY as cellY, taxiPerArea.countValue as emptyTaxis, areaProfit.medianFare as medianFare, (areaProfit.medianFare / taxiPerArea.countValue) as profitability "
				+ "from TaxiPerArea(countValue>0).win:time(30 min).std:unique(cellX, cellY) as taxiPerArea, "
				+ "AreaProfitStatement(medianFare > 0).win:time(15 min).std:unique(cellX, cellY) as areaProfit"
				+ " where taxiPerArea.cellX = areaProfit.cellX "
				+ "and taxiPerArea.cellY = areaProfit.cellY";
		return statement;
	}
	
	private String generateProfitabilityStatementFast(DebsChallenge2Parameters params) {
		String statement = "insert into Profitability "
				+ "select pickup_datetime as pickup_datetime, "
				+ "dropoff_datetime as dropoff_datetime, read_datetime as read_datetime, "
				//+ "cellOptions1.cellX as cellX, cellOptions1.cellY as cellY, countValue as emptyTaxis, medianFare as medianFare, (medianFare / countValue) as profitability "
				+ "cellXDropoff as cellX, cellYDropoff as cellY, countValue as emptyTaxis, medianFare as medianFare, (medianFare / countValue) as profitability "
				+ "from TaxiPerArea(countValue>0, medianFare > 0)";//.win:time(30 min).std:unique(cellXDropoff, cellYDropoff)";
		return statement;
	}

	private String generateEmptyTaxiStatement(DebsChallenge2Parameters params) {
		String statement = "insert into TaxiPerArea "
				+ "select pickup_datetime, dropoff_datetime, read_datetime, cellOptions1.cellX as cellX, "
				+ "cellOptions1.cellY as cellY, "
				+ "count(*) as countValue "
				+ "from C2AppendTwo.win:time(30 min).std:unique(medallion) "
				+ "group by cellOptions1.cellX, cellOptions1.cellY";
		return statement;
	}
	
	private String generateEmptyTaxiStatementFast(DebsChallenge2Parameters params) {
		String statement = "insert into TaxiPerArea "
				+ "select *, "
				+ "count(*) as countValue "
				+ "from AreaProfitStatement.std:unique(medallion).win:time(30 min) "
				+ "group by cellXDropoff, cellYDropoff";
		return statement;
	}

	private String generateAreaProfitStatement(DebsChallenge2Parameters params) {
		String statement = "insert into AreaProfitStatement "
				+ "select *, " 
				+" median((fare_amount+tip_amount)) as medianFare "
				+ "from C2AppendTwo(fare_amount >= 0, tip_amount >= 0)" 
				+".win:time(15 min) "
				+"group by cellXPickup, cellYPickup";
		
		logger.info("Generated EPL: " +statement);
		
		return statement;
	}

	private String generateAppend1Statement(DebsChallenge2Parameters params)
	{
		EPStatementObjectModel model = new EPStatementObjectModel();
		model.insertInto(new InsertIntoClause("C2AppendOne")); // out name
		model.selectClause(makeSelectClause(params, true));
		model.fromClause(new FromClause().add(FilterStream.create(fixEventName(params.getInputStreamParams().get(0).getInName())))); // in name
		
		logger.info("Generated EPL: " +model.toEPL());
		
		return model.toEPL();
	}
	
	private String generateAppend2Statement(DebsChallenge2Parameters params) {
		EPStatementObjectModel model = new EPStatementObjectModel();
		model.insertInto(new InsertIntoClause("C2AppendTwo")); // out name
		model.selectClause(makeSelectClause(params, false));//.add("cellOptions"));
		
		//model.fromClause(new FromClause().add(FilterStream.create("C2AppendOne"))); // in name
		model.fromClause(new FromClause().add(FilterStream.create(fixEventName(params.getInputStreamParams().get(0).getInName())))); // in name
		
		
		logger.info("Generated EPL: " +model.toEPL());
		
		return model.toEPL();
	}
	
	private SelectClause makeSelectClause(DebsChallenge2Parameters params, boolean pickup)
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
		clause.add(Expressions.plus(Expressions.cast(Expressions.divide(Expressions.staticMethod(
				"Math",
				"abs", 
				Expressions.minus(Expressions.constant(LON), Expressions.property("pickup_longitude")) 
				), Expressions.constant(EASTDIFF)), "int"), Expressions.constant(1)), "cellXPickup");
		
		clause.add(Expressions.plus(Expressions.cast(Expressions.divide(Expressions.staticMethod(
				"Math",
				"abs", 
				Expressions.minus(Expressions.constant(LAT), Expressions.property("pickup_latitude"))  
				), Expressions.constant(SOUTHDIFF)), "int"), Expressions.constant(1)), "cellYPickup");
		
		clause.add(Expressions.plus(Expressions.cast(Expressions.divide(Expressions.staticMethod(
				"Math",
				"abs", 
				Expressions.minus(Expressions.constant(LON), Expressions.property("dropoff_longitude")) 
				), Expressions.constant(EASTDIFF)), "int"), Expressions.constant(1)), "cellXDropoff");
		
		clause.add(Expressions.plus(Expressions.cast(Expressions.divide(Expressions.staticMethod(
				"Math",
				"abs", 
				Expressions.minus(Expressions.constant(LAT), Expressions.property("dropoff_latitude"))  
				), Expressions.constant(SOUTHDIFF)), "int"), Expressions.constant(1)), "cellYDropoff");
		/*
		clause.add(Expressions.staticMethod(
				DebsChallenge2.class.getName(),
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
	
	public static CellOption computeCells(double latitude, double longitude, int cellSize, double latitudeStart, double longitudeStart)
	{
		return new GridCalculator2().computeCellsNaive(latitude, longitude, cellSize, latitudeStart, longitudeStart);
	}
	
	@Override
	public Writer getWriter(OutputCollector collector, DebsChallenge2Parameters params)
	{
		DebsOutputParameters outputParams = new DebsOutputParameters(C2_FILENAME);	
		Challenge1FileWriter writer = new Challenge1FileWriter(outputParams, OutputType.HIDE);
		
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
	
	public static void main(String[] args)
	{
		CellOption data = computeCells(41.474937, -74.913585, 250, LAT, LON);
		System.out.println(data.getCellX() +", " +data.getCellY());
	}
}
