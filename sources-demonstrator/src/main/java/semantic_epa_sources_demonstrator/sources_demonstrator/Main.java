package semantic_epa_sources_demonstrator.sources_demonstrator;

/**
 * Hello world!
 *
 * bin/kafka-console-consumer.sh --zookeeper ipe-koi04.fzi.de:2181 --topic FlowSensor
 */
public class Main 
{
	public static String BROKER = "ipe-koi04.fzi.de:9092"; 
    public static void main( String[] args )
    {
        FlowRateSensor s = new FlowRateSensor("ipe-koi04.fzi.de", "FlowSensor");
        s.start();
        s.requestData();
    }
}
