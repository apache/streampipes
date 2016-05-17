package de.fzi.proasense.demonstrator.adapter.festo;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Async;
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.concurrent.FutureCallback;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.fzi.cep.sepa.commons.messaging.ProaSenseInternalProducer;
import de.fzi.proasense.demonstrator.adapter.Main;
import de.fzi.proasense.demonstrator.adapter.SensorValue;
import scala.Enumeration.Val;

public class Festo {
	private static String FESTO_URI = "http://192.168.0.198";

	private FestoProducer containerB101;
	private FestoProducer containerB102;
	private FestoProducer festoFlow;
	private FestoProducer pressureTank;

	public Festo() {
		containerB101 = new FestoProducer(Main.BROKER, "de.fzi.proasense.demonstrator.festo.container.b101",
				new ContainerB101Stream());
		containerB102 = new FestoProducer(Main.BROKER, "de.fzi.proasense.demonstrator.festo.container.b102",
				new ContainerB102Stream());
		festoFlow = new FestoProducer(Main.BROKER, "de.fzi.proasense.demonstrator.festo.flowrate",
				new FestoFlowStream());
		pressureTank = new FestoProducer(Main.BROKER, "de.fzi.proasense.demonstrator.festo.pressuretank",
				new PressuretankStream());

	}

	public void start() {
		Runnable r = new Runnable() {
			Map<String, String> map;
			public void run() {
				for (;;) {
					map = fetchData();
					containerB101.sendToBroker(map);
					containerB102.sendToBroker(map);
					festoFlow.sendToBroker(map);
					pressureTank.sendToBroker(map);
					
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		Thread thread = new Thread(r);
		thread.start();
	}


	private static Map<String, String> fetchData() {
		Map<String, String> map = new HashMap<String, String>();
		try {
			map = addModule1(map);
			map = addModule3(map);
			map = addModule4(map);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;
	}

	private static Map<String, String> addModule1(Map<String, String> map) throws ClientProtocolException, IOException {
		String res = Request.Get(FESTO_URI + "/includes/cpxmoduleinfo.php?i=1").connectTimeout(1000).execute()
				.returnContent().asString();

		Document doc = Jsoup.parse(res);
		Element table = doc.select("table").get(1); // select the first table.
		Elements rows = table.select("tr");

		map.put(Value.DI_B101, rows.get(2).select("td").get(1).text());
		map.put(Value.DI_S111, rows.get(3).select("td").get(1).text());
		map.put(Value.DI_S112, rows.get(4).select("td").get(1).text());
		map.put(Value.DI_B113, rows.get(5).select("td").get(1).text());
		map.put(Value.DI_B114, rows.get(6).select("td").get(1).text());
		map.put(Value.DI_S115, rows.get(7).select("td").get(1).text());
		map.put(Value.DI_S116, rows.get(8).select("td").get(1).text());

		return map;

	}

	private static Map<String, String> addModule3(Map<String, String> map) throws ClientProtocolException, IOException {
		String res = Request.Get(FESTO_URI + "/includes/cpxmoduleinfo.php?i=3").connectTimeout(1000).execute()
				.returnContent().asString();

		Document doc = Jsoup.parse(res);
		Element table = doc.select("table").get(1); // select the first table.
		Elements rows = table.select("tr");

		// TODO Find out in which row the values are
		map.put(Value.AI_B101, rows.get(2).select("td").get(1).text());
		map.put(Value.AI_B102, rows.get(3).select("td").get(1).text());

		return map;
	}

	private static Map<String, String> addModule4(Map<String, String> map) throws ClientProtocolException, IOException {
		String res = Request.Get(FESTO_URI + "/includes/cpxmoduleinfo.php?i=4").connectTimeout(1000).execute()
				.returnContent().asString();

		Document doc = Jsoup.parse(res);
		Element table = doc.select("table").get(1); // select the first table.
		Elements rows = table.select("tr");

		// TODO Find out in which row the values are
		map.put(Value.AI_B103, rows.get(2).select("td").get(1).text());
		map.put(Value.AI_B104, rows.get(3).select("td").get(1).text());

		return map;
	}
}