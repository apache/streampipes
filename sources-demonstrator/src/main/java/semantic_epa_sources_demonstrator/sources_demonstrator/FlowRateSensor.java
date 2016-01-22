package semantic_epa_sources_demonstrator.sources_demonstrator;



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

						if (!data.equals("Error 404. Not Found")) {
							sendToBroker(data);
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
