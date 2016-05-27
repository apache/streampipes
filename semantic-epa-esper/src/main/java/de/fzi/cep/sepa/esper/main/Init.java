package de.fzi.cep.sepa.esper.main;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.client.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.standalone.init.StandaloneModelSubmitter;
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

public class Init extends StandaloneModelSubmitter implements Runnable {

	public static void main(String[] args)
	{
		new Init().declare();
	}
	
	public void declare()
	{
        DeclarersSingleton.getInstance()

		.add(new MovementController())
		.add(new TextFilterController())
		.add(new AndController())
		.add(new NumericalFilterController())
		.add(new MeetsController())
		.add(new EventRateController())
		.add(new AggregationController())
		.add(new GridEnrichmentController())
		.add(new ProjectController())
		.add(new CountController())
		.add(new TopXController())
		.add(new TimestampController())
		.add(new MathController())
		//.add(new DebsChallenge1Controller())
		//.add(new DebsChallenge2Controller())
		.add(new DrillingStartController())
		.add(new DrillingStopController())
		.add(new ComposeController())
		.add(new DrillingStartEnrichedController())
		//.add(new DrillingStopEnrichedController())
		.add(new DistributionController())
		.add(new StaticValueEnricherController())
		.add(new AbsenceController())
		.add(new ObserveNumericalController())
		.add(new ObserveNumericalWindowController())
		.add(new ShuttleTimeController())
		.add(new MinShuttleTimeController())
		.add(new TestCollectionController())
		.add(new NumberClassificationController())
		//.add(new AdvancedTextFilterController())
		.add(new SequenceController())
		.add(new IncreaseController())
		.add(new GeofencingController())
		.add(new DurationOfStayController())
		.add(new StreamStoppedController());


		// Configure external timing for DEBS Challenge
		new Thread(new EsperEngineSettings()).start();
        new Init().init();

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
