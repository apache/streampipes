package org.streampipes.runtime.routing;

public class Timer {

	public static long start;
	public static long stop;
	
	public static void start()
	{
		start = System.currentTimeMillis();
	}
	
	public static void stop() {
		stop = System.currentTimeMillis();
		System.out.println("Took: " +(stop-start));
	}
}
