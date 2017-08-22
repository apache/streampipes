package org.streampipes.pe.processors.storm.topology;

import java.util.Random;

public class Name {
	private final static String TOPOLOGY_NAME = "sentiment-detection";
	private static int ID = 0;
	
	public static String getTopologyName() {
		if (ID == 0) {
			Random random = new Random();
			ID = random.nextInt(10000 - 1 + 1) + 1;
		}

		return TOPOLOGY_NAME + ID;
	}

}
