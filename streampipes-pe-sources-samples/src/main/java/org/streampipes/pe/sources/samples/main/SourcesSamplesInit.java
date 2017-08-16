package org.streampipes.pe.sources.samples.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.streampipes.pe.sources.samples.config.SourcesConfig;
import org.streampipes.pe.sources.samples.ddm.DDMProducer;
import org.streampipes.pe.sources.samples.drillbit.DrillBitProducer;
import org.streampipes.pe.sources.samples.enriched.EnrichedEventProducer;
import org.streampipes.pe.sources.samples.friction.FrictionCoefficientProducer;
import org.streampipes.pe.sources.samples.hella.EnvironmentalDataProducer;
import org.streampipes.pe.sources.samples.hella.MontracProducer;
import org.streampipes.pe.sources.samples.hella.MouldingMachineProducer;
import org.streampipes.pe.sources.samples.hella.VisualInspectionProducer;
import org.streampipes.pe.sources.samples.proveit.ProveITEventProducer;
import org.streampipes.pe.sources.samples.ram.RamProducer;
import org.streampipes.pe.sources.samples.random.RandomDataProducer;
import org.streampipes.pe.sources.samples.taxi.NYCTaxiProducer;
import org.streampipes.pe.sources.samples.twitter.TwitterStreamProducer;
import org.streampipes.pe.sources.samples.wunderbar.WunderbarProducer;
import org.streampipes.pe.sources.samples.wunderbar.WunderbarProducer2;

public class SourcesSamplesInit extends StandaloneModelSubmitter {

    static Logger LOG = LoggerFactory.getLogger(SourcesSamplesInit.class);

    public static void main(String[] args) {

        LOG.info("Starting SourcesSamplesInit");
        if (SourcesConfig.INSTANCE.isTwitterActive()) DeclarersSingleton.getInstance().add(new TwitterStreamProducer());
        if (SourcesConfig.INSTANCE.isMhwirthActive()) {
            DeclarersSingleton.getInstance().add(new DDMProducer())
                    .add(new DrillBitProducer())
                    .add(new EnrichedEventProducer())
                    .add(new RamProducer())
                    .add(new FrictionCoefficientProducer());
        }
        if (SourcesConfig.INSTANCE.isRandomNumberActive()) DeclarersSingleton.getInstance().add(new RandomDataProducer());
        if (SourcesConfig.INSTANCE.isTaxiActive()) DeclarersSingleton.getInstance().add(new NYCTaxiProducer());
        if (SourcesConfig.INSTANCE.isProveItActive()) DeclarersSingleton.getInstance().add(new ProveITEventProducer());
        if (SourcesConfig.INSTANCE.isHellaActive()) {
            DeclarersSingleton.getInstance().add(new VisualInspectionProducer())
                    .add(new MontracProducer())
                    .add(new MouldingMachineProducer())
                    .add(new EnvironmentalDataProducer());

        }
        DeclarersSingleton.getInstance().add(new WunderbarProducer())
                .add(new WunderbarProducer2());

        DeclarersSingleton.getInstance().setPort(SourcesConfig.INSTANCE.getPort());
        DeclarersSingleton.getInstance().setHostName(SourcesConfig.INSTANCE.getHost());
        new SourcesSamplesInit().init();
    }

}
