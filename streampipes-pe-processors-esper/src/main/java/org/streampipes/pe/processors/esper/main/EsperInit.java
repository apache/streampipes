package org.streampipes.pe.processors.esper.main;

import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.streampipes.dataformat.json.JsonDataFormatFactory;
import org.streampipes.messaging.kafka.SpKafkaProtocolFactory;
import org.streampipes.pe.processors.esper.absence.AbsenceController;
import org.streampipes.pe.processors.esper.aggregate.avg.AggregationController;
import org.streampipes.pe.processors.esper.aggregate.count.CountController;
import org.streampipes.pe.processors.esper.aggregate.rate.EventRateController;
import org.streampipes.pe.processors.esper.compose.ComposeController;
import org.streampipes.pe.processors.esper.config.EsperConfig;
import org.streampipes.pe.processors.esper.distribution.DistributionController;
import org.streampipes.pe.processors.esper.enrich.binarymath.BinaryMathController;
import org.streampipes.pe.processors.esper.enrich.grid.GridEnrichmentController;
import org.streampipes.pe.processors.esper.enrich.math.MathController;
import org.streampipes.pe.processors.esper.enrich.timer.TimestampController;
import org.streampipes.pe.processors.esper.extract.ProjectController;
import org.streampipes.pe.processors.esper.filter.numerical.NumericalFilterController;
import org.streampipes.pe.processors.esper.filter.text.TextFilterController;
import org.streampipes.pe.processors.esper.geo.durationofstay.DurationOfStayController;
import org.streampipes.pe.processors.esper.geo.geofencing.GeofencingController;
import org.streampipes.pe.processors.esper.meets.MeetsController;
import org.streampipes.pe.processors.esper.movement.MovementController;
import org.streampipes.pe.processors.esper.number.NumberClassificationController;
import org.streampipes.pe.processors.esper.numerical.value.ObserveNumericalController;
import org.streampipes.pe.processors.esper.numerical.window.ObserveNumericalWindowController;
import org.streampipes.pe.processors.esper.pattern.and.AndController;
import org.streampipes.pe.processors.esper.pattern.increase.IncreaseController;
import org.streampipes.pe.processors.esper.pattern.sequence.SequenceController;
import org.streampipes.pe.processors.esper.pattern.streamstopped.StreamStoppedController;
import org.streampipes.pe.processors.esper.proasense.hella.minshuttletime.MinShuttleTimeController;
import org.streampipes.pe.processors.esper.proasense.hella.shuttletime.ShuttleTimeController;
import org.streampipes.pe.processors.esper.proasense.mhwirth.drillingstart.DrillingStartController;
import org.streampipes.pe.processors.esper.proasense.mhwirth.drillingstop.DrillingStopController;
import org.streampipes.pe.processors.esper.proasense.mhwirth.single.DrillingStartEnrichedController;
import org.streampipes.pe.processors.esper.topx.TopXController;
import org.streampipes.wrapper.esper.EsperEngineSettings;

public class EsperInit extends StandaloneModelSubmitter {

  public static void main(String[] args) {
    new EsperInit().declare();
  }

  public void declare() {
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
            .add(new AbsenceController())
            .add(new ObserveNumericalController())
            .add(new ObserveNumericalWindowController())
            .add(new ShuttleTimeController())
            .add(new MinShuttleTimeController())
            .add(new NumberClassificationController())
            //.add(new AdvancedTextFilterController())
            .add(new SequenceController())
            .add(new IncreaseController())
            .add(new GeofencingController())
            .add(new DurationOfStayController())
            .add(new StreamStoppedController())
            .add(new BinaryMathController());

    DeclarersSingleton.getInstance().setPort(EsperConfig.INSTANCE.getPort());
    DeclarersSingleton.getInstance().setHostName(EsperConfig.INSTANCE.getHost());
    DeclarersSingleton.getInstance().registerDataFormat(new JsonDataFormatFactory());
    DeclarersSingleton.getInstance().registerProtocol(new SpKafkaProtocolFactory());

    new Thread(new EsperEngineSettings()).start();
    new EsperInit().init();

  }
}
