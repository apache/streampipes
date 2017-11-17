package org.streampipes.pe.mixed.flink.samples;


import com.orbitz.consul.NotRegisteredException;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.streampipes.container.util.ConsulServiceDiscovery;
import org.streampipes.pe.mixed.flink.samples.axoom.MaintenancePredictionController;
import org.streampipes.pe.mixed.flink.samples.batchstream.FirstBatchThenStreamController;
import org.streampipes.pe.mixed.flink.samples.breakdown.Prediction2BreakdownController;
import org.streampipes.pe.mixed.flink.samples.classification.number.NumberClassificationController;
import org.streampipes.pe.mixed.flink.samples.count.aggregate.CountAggregateController;
import org.streampipes.pe.mixed.flink.samples.delay.sensor.DelayController;
import org.streampipes.pe.mixed.flink.samples.delay.taxi.DelayTaxiController;
import org.streampipes.pe.mixed.flink.samples.elasticsearch.ElasticSearchController;
import org.streampipes.pe.mixed.flink.samples.enrich.timestamp.TimestampController;
import org.streampipes.pe.mixed.flink.samples.enrich.value.ValueController;
import org.streampipes.pe.mixed.flink.samples.file.FileSinkController;
import org.streampipes.pe.mixed.flink.samples.hasher.FieldHasherController;
import org.streampipes.pe.mixed.flink.samples.healthindex.HealthIndexController;
import org.streampipes.pe.mixed.flink.samples.labelorder.LabelOrderController;
import org.streampipes.pe.mixed.flink.samples.peak.PeakDetectionController;
import org.streampipes.pe.mixed.flink.samples.rename.FieldRenamerController;
import org.streampipes.pe.mixed.flink.samples.spatial.gridenricher.SpatialGridEnrichmentController;
import org.streampipes.pe.mixed.flink.samples.statistics.StatisticsSummaryController;
import org.streampipes.pe.mixed.flink.samples.statistics.window.StatisticsSummaryControllerWindow;
import org.streampipes.pe.mixed.flink.samples.timetofailure.TimeToFailureController;

public class FlinkInit extends StandaloneModelSubmitter {

  public static void main(String[] args) {
    DeclarersSingleton.getInstance()
            //.add(new WordCountController())
            .add(new FirstBatchThenStreamController())
            .add(new DelayController())
            .add(new DelayTaxiController())
            .add(new LabelOrderController())
            .add(new ElasticSearchController())
            .add(new NumberClassificationController())
            .add(new TimestampController())
            .add(new FieldHasherController())
            .add(new FieldRenamerController())
            .add(new HealthIndexController())
            .add(new TimeToFailureController())
            .add(new CountAggregateController())
            .add(new FileSinkController())
            .add(new StatisticsSummaryController())
            .add(new Prediction2BreakdownController())
            .add(new SpatialGridEnrichmentController())
            .add(new MaintenancePredictionController())
            .add(new StatisticsSummaryControllerWindow())
            .add(new PeakDetectionController())
            .add(new ValueController());


    DeclarersSingleton.getInstance()
            .setHostName(FlinkConfig.INSTANCE.getHost());
    DeclarersSingleton.getInstance()
            .setPort(FlinkConfig.INSTANCE.getPort());
            ;

    ConsulServiceDiscovery.registerPeService(FlinkConfig.INSTANCE.getHost(),
                                          //TODO
                                          //  "http://" + FlinkConfig.INSTANCE.getHost(),
                                          "http://141.21.14.37",
                                            FlinkConfig.INSTANCE.getPort());


    new FlinkInit().init();
  }

}
