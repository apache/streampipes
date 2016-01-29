package semantic_epa_sources_demonstrator.sources_demonstrator;

//0103203900000034094000446ea01a00000001858f507c41d14fdc0000000bff0000006ecc 00

public class FlowRateSensor extends Sensor {
	
	public FlowRateSensor(String url, String topic) {
		super(url, topic);
	}

	@Override
	public void start() {
		
		Runnable r = new Runnable() {

			public void run() {
				int i = 0;
				for (;;) {

					try {
						String data = requestData();
						FlowRateSensorValue sv = new FlowRateSensorValue(data);


						if (!data.equals("Error 404. Not Found")) {
							sendToBroker(sv.toJson());
						}
						Thread.sleep(1000);
						i++;
						if (i == 10) {
							System.out.println("I'm still alive!");
							i = 0;
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				
				}
			}
		};
		Thread thread = new Thread(r);
		thread.start();
	}
	
	
	

}
