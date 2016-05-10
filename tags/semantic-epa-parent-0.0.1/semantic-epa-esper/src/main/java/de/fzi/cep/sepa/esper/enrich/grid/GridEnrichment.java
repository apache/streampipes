package de.fzi.cep.sepa.esper.enrich.grid;

import static com.espertech.esper.client.soda.Expressions.property;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.espertech.esper.client.soda.EPStatementObjectModel;
import com.espertech.esper.client.soda.Expressions;
import com.espertech.esper.client.soda.FilterStream;
import com.espertech.esper.client.soda.FromClause;
import com.espertech.esper.client.soda.SelectClause;

import de.fzi.cep.sepa.esper.EsperEventEngine;

public class GridEnrichment extends EsperEventEngine<GridEnrichmentParameter>{

	private static final Logger logger = LoggerFactory.getLogger(GridEnrichment.class.getSimpleName());
	
	@Override
	protected List<String> statements(GridEnrichmentParameter params) {
		List<String> statements = new ArrayList<String>();
		
		EPStatementObjectModel model = new EPStatementObjectModel();
		//model.insertInto(new InsertIntoClause(fixEventName(params.getOutName()))); // out name
		model.selectClause(makeSelectClause(params));
		model.fromClause(new FromClause().add(FilterStream.create(fixEventName(params.getInputStreamParams().get(0).getInName())))); // in name
		
		logger.info("Generated EPL: " +model.toEPL());
		
		statements.add(model.toEPL());
		return statements;
	}
	
	private SelectClause makeSelectClause(GridEnrichmentParameter params)
	{
		SelectClause clause = SelectClause.create();
		for(String property : params.getSelectProperties())
		{
			clause.add(property);
		}
		clause.add(Expressions.staticMethod(
				GridEnrichment.class.getName(),
				"computeCells", 
				property(params.getLatPropertyName()), 
				property(params.getLngPropertyName()), 
				Expressions.constant(params.getCellSize()),
				Expressions.constant(params.getLatitudeStart()),
				Expressions.constant(params.getLongitudeStart())), 
				params.getCellOptionsPropertyName());

		return clause;
	}
	
	public static synchronized CellOption computeCells(double latitude, double longitude, int cellSize, double latitudeStart, double longitudeStart)
	{
		return new GridCalculator().computeCellsNaive(latitude, longitude, cellSize, latitudeStart, longitudeStart);
	}

}
