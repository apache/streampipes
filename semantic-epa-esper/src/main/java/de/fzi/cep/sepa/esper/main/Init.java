package de.fzi.cep.sepa.esper.main;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.client.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.esper.absence.AbsenceController;
import de.fzi.cep.sepa.esper.aggregate.avg.AggregationController;
import de.fzi.cep.sepa.esper.aggregate.count.CountController;
import de.fzi.cep.sepa.esper.aggregate.rate.EventRateController;
import de.fzi.cep.sepa.esper.classification.number.NumberClassificationController;
import de.fzi.cep.sepa.esper.collection.TestCollectionController;
import de.fzi.cep.sepa.esper.compose.ComposeController;
import de.fzi.cep.sepa.esper.distribution.DistributionController;
import de.fzi.cep.sepa.esper.drillingstart.single.DrillingStartEnrichedController;
import de.fzi.cep.sepa.esper.enrich.fixed.StaticValueEnricherController;
import de.fzi.cep.sepa.esper.enrich.grid.GridEnrichmentController;
import de.fzi.cep.sepa.esper.enrich.math.MathController;
import de.fzi.cep.sepa.esper.enrich.timer.TimestampController;
import de.fzi.cep.sepa.esper.filter.numerical.NumericalFilterController;
import de.fzi.cep.sepa.esper.filter.text.TextFilterController;
import de.fzi.cep.sepa.esper.geo.durationofstay.DurationOfStayController;
import de.fzi.cep.sepa.esper.geo.geofencing.GeofencingController;
import de.fzi.cep.sepa.esper.meets.MeetsController;
import de.fzi.cep.sepa.esper.movement.MovementController;
import de.fzi.cep.sepa.esper.observe.numerical.value.ObserveNumericalController;
import de.fzi.cep.sepa.esper.observe.numerical.window.ObserveNumericalWindowController;
import de.fzi.cep.sepa.esper.output.topx.TopXController;
import de.fzi.cep.sepa.esper.pattern.and.AndController;
import de.fzi.cep.sepa.esper.pattern.increase.IncreaseController;
import de.fzi.cep.sepa.esper.pattern.sequence.SequenceController;
import de.fzi.cep.sepa.esper.pattern.streamstopped.StreamStoppedController;
import de.fzi.cep.sepa.esper.proasense.drillingstart.DrillingStartController;
import de.fzi.cep.sepa.esper.proasense.drillingstop.DrillingStopController;
import de.fzi.cep.sepa.esper.project.extract.ProjectController;
import de.fzi.cep.sepa.hella.minshuttletime.MinShuttleTimeController;
import de.fzi.cep.sepa.hella.shuttletime.ShuttleTimeController;

public class Init implements Runnable {

	public static void main(String[] args)
	{
		new Init().declare();
	}
	
	public void declare()
	{
		List<SemanticEventProcessingAgentDeclarer> declarers = new ArrayList<SemanticEventProcessingAgentDeclarer>();

//		declarers.add(new MovementController());
//		declarers.add(new TextFilterController());
//		declarers.add(new AndController());
//		declarers.add(new NumericalFilterController());
//		declarers.add(new MeetsController());
//		declarers.add(new EventRateController());
//		declarers.add(new AggregationController());
//		declarers.add(new GridEnrichmentController());
//		declarers.add(new ProjectController());
//		declarers.add(new CountController());
//		declarers.add(new TopXController());
//		declarers.add(new TimestampController());
//		declarers.add(new MathController());
//		//declarers.add(new DebsChallenge1Controller());
//		//declarers.add(new DebsChallenge2Controller());
//		declarers.add(new DrillingStartController());
//		declarers.add(new DrillingStopController());
//		declarers.add(new ComposeController());
//		declarers.add(new DrillingStartEnrichedController());
//		//declarers.add(new DrillingStopEnrichedController());
//		declarers.add(new DistributionController());
//		declarers.add(new StaticValueEnricherController());
//		declarers.add(new AbsenceController());
//		declarers.add(new ObserveNumericalController());
//		declarers.add(new ObserveNumericalWindowController());
//		declarers.add(new ShuttleTimeController());
//		declarers.add(new MinShuttleTimeController());
//		declarers.add(new TestCollectionController());
//		declarers.add(new NumberClassificationController());
//		//declarers.add(new AdvancedTextFilterController());
//		declarers.add(new SequenceController());
//		declarers.add(new IncreaseController());
//		declarers.add(new GeofencingController());
//		declarers.add(new DurationOfStayController());
//		declarers.add(new StreamStoppedController());


		// Configure external timing for DEBS Challenge
		//new Thread(new EsperEngineSettings()).start();

		try {
//			ModelSubmitter.submitAgent(declarers);
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
