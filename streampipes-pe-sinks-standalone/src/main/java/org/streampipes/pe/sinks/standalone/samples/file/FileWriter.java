package org.streampipes.pe.sinks.standalone.samples.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import org.streampipes.pe.sinks.standalone.messaging.jms.ActiveMQConsumer;
import org.streampipes.commons.messaging.IMessageListener;
import org.json.CDL;
import org.json.JSONArray;

public class FileWriter implements Runnable, IMessageListener<String> {

	FileParameters params;
	FileOutputStream stream;
	static long counter = 0;
	private boolean firstEvent;
	
	public FileWriter(FileParameters params)
	{
		this.params = params;
		prepare();

	}
	
	private void prepare()
	{
		File file = new File(params.getPath());
		try {
			stream = FileUtils.openOutputStream(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		firstEvent = true;
	}

	public void close() {
		try {
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		ActiveMQConsumer consumer = new ActiveMQConsumer(params.getUrl(), params.getTopic());
		consumer.setListener(this);
	}

	@Override
	public void onEvent(String json) {
		counter++;
		try {
			String stringArray = "[" + json + "]";
			JSONArray j = new JSONArray(stringArray);
			String csvString = CDL.toString(j);

			String csvWithoutHeader = "";

			if (firstEvent) {
				csvWithoutHeader = csvString + "\n";
				firstEvent = false;
			} else {
				csvWithoutHeader = csvString.substring(0, csvString.length() - 2).replaceAll(".*\n", "") + "\n";
			}

			stream.write(csvWithoutHeader.getBytes());
			if (counter % 10000 == 0) System.out.println(counter + " Event processed.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
