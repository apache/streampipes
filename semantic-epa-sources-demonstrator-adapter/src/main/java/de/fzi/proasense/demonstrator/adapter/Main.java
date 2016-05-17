package de.fzi.proasense.demonstrator.adapter;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.proasense.demonstrator.adapter.festo.Festo;
import de.fzi.proasense.demonstrator.adapter.siemens.FlowRateSensor;
import de.fzi.proasense.demonstrator.adapter.siemens.LevelSensor;

/**
 *
 * bin/kafka-console-consumer.sh --zookeeper ipe-koi04.fzi.de:2181 --topic FlowSensor
 */
public class Main 
{
	public static String BROKER = ClientConfiguration.INSTANCE.getKafkaUrl(); 
    public static void main( String[] args )
    {
        FlowRateSensor s1 = new FlowRateSensor("http://192.168.0.106/cgi_pv_frame.shtml", "de.fzi.proasense.demonstrator.siemens.flowrate.sensor1");
        s1.start();
        FlowRateSensor s2 = new FlowRateSensor("http://192.168.0.104/cgi_pv_frame.shtml", "de.fzi.proasense.demonstrator.siemens.flowrate.sensor2");
        s2.start();
//        LevelSensor s3 = new LevelSensor("http://192.168.0.105/cgi_pv_frame.shtml", "de.fzi.proasense.demonstrator.siemens.level.sensor1");
//        s3.start();
        
        Festo festo = new Festo();
        festo.start();
        
        System.out.println("Rasperry started");
    }
}
