package de.fzi.cep.sepa.esper.main;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.desc.ModelSubmitter;
import de.fzi.cep.sepa.desc.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.esper.aggregate.avg.AggregationController;
import de.fzi.cep.sepa.esper.aggregate.count.CountController;
import de.fzi.cep.sepa.esper.aggregate.rate.EventRateController;
import de.fzi.cep.sepa.esper.config.EsperConfig;
import de.fzi.cep.sepa.esper.debs.c1.DebsChallenge1Controller;
import de.fzi.cep.sepa.esper.debs.c2.DebsChallenge2Controller;
import de.fzi.cep.sepa.esper.enrich.grid.GridEnrichmentController;
import de.fzi.cep.sepa.esper.enrich.math.MathController;
import de.fzi.cep.sepa.esper.enrich.timer.TimestampController;
import de.fzi.cep.sepa.esper.filter.numerical.NumericalFilterController;
import de.fzi.cep.sepa.esper.filter.text.TextFilterController;
import de.fzi.cep.sepa.esper.meets.MeetsController;
import de.fzi.cep.sepa.esper.movement.MovementController;
import de.fzi.cep.sepa.esper.output.topx.TopXController;
import de.fzi.cep.sepa.esper.pattern.PatternController;
import de.fzi.cep.sepa.esper.project.extract.ProjectController;

public class Init implements Runnable {

	public static void main(String[] args)
	{
		new Init().declare();
	}
	
	public void declare()
	{
		List<SemanticEventProcessingAgentDeclarer> declarers = new ArrayList<SemanticEventProcessingAgentDeclarer>();
		
		declarers.add(new MovementController());
		declarers.add(new TextFilterController());
		declarers.add(new PatternController());
		declarers.add(new NumericalFilterController());
		declarers.add(new MeetsController());
		declarers.add(new EventRateController());
		declarers.add(new AggregationController());
		declarers.add(new GridEnrichmentController());
		declarers.add(new ProjectController());
		declarers.add(new CountController());
		declarers.add(new TopXController());
		declarers.add(new TimestampController());
		declarers.add(new MathController());
		declarers.add(new DebsChallenge1Controller());
		declarers.add(new DebsChallenge2Controller());
		
		// Configure external timing for DEBS Challenge
		new Thread(new ExternalTimer()).start();
		
		try {
			ModelSubmitter.submitAgent(declarers, EsperConfig.serverUrl, 8090);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		declare();
	}
}
