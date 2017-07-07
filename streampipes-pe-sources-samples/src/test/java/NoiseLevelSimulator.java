import de.fzi.cep.sepa.messaging.jms.ActiveMQPublisher;

import javax.jms.JMSException;

public class NoiseLevelSimulator {

	public static void main(String[] args) {
		try {
			ActiveMQPublisher publisher = new ActiveMQPublisher("tcp://ipe-koi04.fzi.de:61616", "wunderbar.C0:9F:3C:5F:E1:21.noiseLevel");
		
			for(int i = 0; i <= 50; i++) {
				publisher.sendText(makeNoiseLevel());
				try {
					System.out.println("sending");
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	private static String makeNoiseLevel() {
		return "{\"timestamp\":" +System.currentTimeMillis() +",\"noiseLevel\":1.0}";
	}
}
