package de.fzi.proasense.demonstrator.adapter.festo;

import java.util.Map;

public abstract class Value {

	/**
	 * Impulssignal Durchflusssensor B101 (Boolean)
	 */
	public static String DI_B101 = "DI_B101";

	public static boolean getDI_B101(Map<String, String> map) {
		return "1".equals(map.get(DI_B101)) ? true : false;
	}

	/**
	 * Schwimmerschalter (NC) Überlauf Behälter B101 (Boolean)
	 */
	public static String DI_S111 = "DI_S111";

	public static boolean getDI_S111(Map<String, String> map) {
		return "1".equals(map.get(DI_S111)) ? true : false;
	}

	/**
	 * Schwimmerschalter (NO) Behälter B102 (Boolean)
	 */
	public static String DI_S112 = "DI_S112";

	public static boolean getDI_S112(Map<String, String> map) {
		return "1".equals(map.get(DI_S112)) ? true : false;
	}

	/**
	 * Grenztaster min. Füllstand unterer Behälter B101 (Boolean)
	 */
	public static String DI_B113 = "DI_B113";

	public static boolean getDI_B113(Map<String, String> map) {
		return "1".equals(map.get(DI_B113)) ? true : false;
	}

	/**
	 * Grenztaster min. Füllstand oberer Behälter B102 (Boolean)
	 */
	public static String DI_B114 = "DI_B114";

	public static boolean getDI_B114(Map<String, String> map) {
		return "1".equals(map.get(DI_B114)) ? true : false;
	}

	/**
	 * Mikroschalter 2W-Kugelhahn V102 geschlossen (Boolean)
	 */
	public static String DI_S115 = "DI_S115";

	public static boolean getDI_S115(Map<String, String> map) {
		return "1".equals(map.get(DI_S115)) ? true : false;
	}

	/**
	 * Mikroschalter 2W-Kugelhahn V102 geöffnet (Boolean)
	 */
	public static String DI_S116 = "DI_S116";

	public static boolean getDI_S116(Map<String, String> map) {
		return "1".equals(map.get(DI_S116)) ? true : false;
	}

	/**
	 * Ultraschallsensor / ultrasonic sensor 50-345 mm
	 */
	public static String AI_B101 = "AI_B101";

	public static double getAI_B101(Map<String, String> map) {
		int value = Integer.parseInt(map.get(AI_B101));
		double multiplier = 0.072039072;
		
		return value * multiplier + 50;
	}

	/**
	 * Durchflusssensor / flow rate sensor 0,3-9,0 l/min
	 */
	public static String AI_B102 = "AI_B102";

	public static double getAI_B102(Map<String, String> map) {
		int value = Integer.parseInt(map.get(AI_B102));
		double multiplier = 0.0021245421;
		
		return value * multiplier + 0.3;
	}

	/**
	 * Drucksensor / pressure sensor 0-0,4 bar
	 */
	public static String AI_B103 = "AI_B103";

	public static double getAI_B103(Map<String, String> map) {
		int value = Integer.parseInt(map.get(AI_B103));
		double multiplier = 0.000097680097680;
		
		return value * multiplier;
	}

	/**
	 * Temperatursensor / temperature sensor 0-100°C
	 */
	public static String AI_B104 = "AI_B104";

	public static double getAI_B104(Map<String, String> map) {
		int value = Integer.parseInt(map.get(AI_B104));
		double multiplier = 0.0244200244;
		
		return value * multiplier;
	}
}
