package org.streampipes.pe.sources.samples.main;

import org.streampipes.container.init.DeclarersSingleton;
import org.streampipes.container.standalone.init.StandaloneModelSubmitter;
import org.streampipes.commons.config.old.ClientConfiguration;
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

    public static void main(String[] args) {

        ClientConfiguration config = ClientConfiguration.INSTANCE;

        if (config.isTwitterActive()) DeclarersSingleton.getInstance().add(new TwitterStreamProducer());
        if (config.isMhwirthReplayActive()) {
            DeclarersSingleton.getInstance().add(new DDMProducer())
                    .add(new DrillBitProducer())
                    .add(new EnrichedEventProducer())
                    .add(new RamProducer())
                    .add(new FrictionCoefficientProducer());
        }
        if (config.isRandomNumberActive()) DeclarersSingleton.getInstance().add(new RandomDataProducer());
        if (config.isTaxiActive()) DeclarersSingleton.getInstance().add(new NYCTaxiProducer());
        if (config.isProveItActive()) DeclarersSingleton.getInstance().add(new ProveITEventProducer());
        if (config.isHellaReplayActive()) {
            DeclarersSingleton.getInstance().add(new VisualInspectionProducer())
                    .add(new MontracProducer())
                    .add(new MouldingMachineProducer())
                    .add(new EnvironmentalDataProducer());

        }
        DeclarersSingleton.getInstance().add(new WunderbarProducer())
                .add(new WunderbarProducer2());

        DeclarersSingleton.getInstance().setPort(8089);
        new SourcesSamplesInit().init();
    }

}
