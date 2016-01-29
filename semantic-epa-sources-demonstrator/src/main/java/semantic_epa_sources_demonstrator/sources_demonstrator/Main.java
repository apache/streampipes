package semantic_epa_sources_demonstrator.sources_demonstrator;

/**
 *
 * bin/kafka-console-consumer.sh --zookeeper ipe-koi04.fzi.de:2181 --topic FlowSensor
 */
public class Main 
{
	public static String BROKER = "ipe-koi04.fzi.de:9092"; 
    public static void main( String[] args )
    {
        FlowRateSensor s = new FlowRateSensor("http://192.168.0.106/cgi_tank_values.shtml", "FlowSensor");
        s.start();
//        s.requestData();
        
        System.out.println("Rasperry started");
    }
}
