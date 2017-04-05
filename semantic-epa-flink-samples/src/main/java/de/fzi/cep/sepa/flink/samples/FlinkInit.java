package de.fzi.cep.sepa.flink.samples;


import de.fzi.cep.sepa.client.init.DeclarersSingleton;
import de.fzi.cep.sepa.client.standalone.init.StandaloneModelSubmitter;
import de.fzi.cep.sepa.flink.samples.batchstream.FirstBatchThenStreamController;
import de.fzi.cep.sepa.flink.samples.breakdown.Prediction2BreakdownController;
import de.fzi.cep.sepa.flink.samples.classification.number.NumberClassificationController;
import de.fzi.cep.sepa.flink.samples.delay.sensor.DelayController;
import de.fzi.cep.sepa.flink.samples.delay.taxi.DelayTaxiController;
import de.fzi.cep.sepa.flink.samples.elasticsearch.ElasticSearchController;
import de.fzi.cep.sepa.flink.samples.enrich.timestamp.TimestampController;
import de.fzi.cep.sepa.flink.samples.file.FileSinkController;
import de.fzi.cep.sepa.flink.samples.hasher.FieldHasherController;
import de.fzi.cep.sepa.flink.samples.healthindex.HealthIndexController;
import de.fzi.cep.sepa.flink.samples.labelorder.LabelOrderController;
import de.fzi.cep.sepa.flink.samples.rename.FieldRenamerController;
import de.fzi.cep.sepa.flink.samples.statistics.StatisticsSummaryController;
import de.fzi.cep.sepa.flink.samples.timetofailure.TimeToFailureController;

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
            .add(new FileSinkController())
            .add(new StatisticsSummaryController())
            .add(new Prediction2BreakdownController());


    DeclarersSingleton.getInstance().setPort(8094);
    new FlinkInit().init();
  }

}
