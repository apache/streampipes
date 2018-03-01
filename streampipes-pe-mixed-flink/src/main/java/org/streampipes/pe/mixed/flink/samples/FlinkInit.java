package org.streampipes.pe.mixed.flink.samples;


import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.streampipes.pe.mixed.flink.samples.axoom.MaintenancePredictionController;
import org.streampipes.pe.mixed.flink.samples.batchstream.FirstBatchThenStreamController;
import org.streampipes.pe.mixed.flink.samples.breakdown.Prediction2BreakdownController;
import org.streampipes.pe.mixed.flink.samples.classification.number.NumberClassificationController;
import org.streampipes.pe.mixed.flink.samples.converter.FieldConverterController;
import org.streampipes.pe.mixed.flink.samples.count.aggregate.CountAggregateController;
import org.streampipes.pe.mixed.flink.samples.delay.sensor.DelayController;
import org.streampipes.pe.mixed.flink.samples.delay.taxi.DelayTaxiController;
import org.streampipes.pe.mixed.flink.samples.elasticsearch.ElasticSearchController;
import org.streampipes.pe.mixed.flink.samples.enrich.timestamp.TimestampController;
import org.streampipes.pe.mixed.flink.samples.enrich.value.ValueController;
import org.streampipes.pe.mixed.flink.samples.file.FileSinkController;
import org.streampipes.pe.mixed.flink.samples.filter.numericallist.ListFilterController;
import org.streampipes.pe.mixed.flink.samples.hasher.FieldHasherController;
import org.streampipes.pe.mixed.flink.samples.healthindex.HealthIndexController;
import org.streampipes.pe.mixed.flink.samples.labelorder.LabelOrderController;
import org.streampipes.pe.mixed.flink.samples.measurementUnitConverter.MeasurementUnitConverterController;
import org.streampipes.pe.mixed.flink.samples.peak.PeakDetectionController;
import org.streampipes.pe.mixed.flink.samples.performance.PerformanceTestController;
import org.streampipes.pe.mixed.flink.samples.performancesink.PerformanceElasticController;
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
            .add(new ValueController())
            .add(new ListFilterController())
            .add(new PerformanceTestController())
            .add(new PerformanceElasticController())
            .add(new MeasurementUnitConverterController())
            .add(new FieldConverterController());

    new FlinkInit().init(FlinkConfig.INSTANCE);
  }

}
