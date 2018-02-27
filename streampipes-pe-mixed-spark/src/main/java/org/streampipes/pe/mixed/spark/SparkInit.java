package org.streampipes.pe.mixed.spark;

import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.streampipes.pe.mixed.spark.samples.enrich.timestamp.TimestampController;

/**
 * Created by Jochen Lutz on 2018-01-22.
 */
public class SparkInit extends StandaloneModelSubmitter {
    public static void main(String[] args) {
        DeclarersSingleton declarer = DeclarersSingleton.getInstance();

        declarer.add(new TimestampController());

        new SparkInit().init(SparkConfig.INSTANCE);
    }
}
