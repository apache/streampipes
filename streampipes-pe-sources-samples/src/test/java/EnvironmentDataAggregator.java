import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.streampipes.pe.sources.samples.util.Utils;


public class EnvironmentDataAggregator {

	static final String inputfilenamePrefix = "e:\\hella-data\\collected\\collected\\export\\cleaned\\hella-Bin";
	static final String outputfilenamePrefix = "e:\\hella-data\\collected\\collected\\export\\cleaned\\aggregated\\hella-bin-all-ids";
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	public static void main(String[] args) throws IOException, ParseException
	{
		// output: timestamp, id, bin0...bin15
		
		Map<Integer, List<String[]>> dataset = new TreeMap<>();
		System.out.println("Start reading data...");
		// load data
		for(int i = 0; i <= 15; i++)
		{
			Optional<BufferedReader> readerOpt = Utils.getReader(new File(inputfilenamePrefix +i +".csv"));
			{
				if (readerOpt.isPresent())
				{
					String line;
					String key = null;
					BufferedReader reader = readerOpt.get();
					int counter = 0;
					
					List<String[]> lines = new ArrayList<>();
					
					while ((line = reader.readLine()) != null) {
						String[] lineData = line.split(",");
						/*if (lineData[1].startsWith("20.13"))*/ lines.add(lineData);
						if (counter == 0) key = lineData[2];
						counter++;
					}
					dataset.put(i, lines);
				}
			}
		}
		
		// write data
		System.out.println("Start writing data...");
		
		int rows = dataset.entrySet().stream().findFirst().get().getValue().size();
		int firstKey = dataset.keySet().stream().findFirst().get();
		
		int timestampIndex = 4; 
		int dataIndex = 3;
		int idIndex = 1;
		
		PrintWriter writer = new PrintWriter(new FileOutputStream(new File(outputfilenamePrefix +".csv")), true);
		
		for(int j = 0; j < rows; j++)
		{
			long firstTimestamp = sdf.parse(dataset.get(firstKey).get(j)[timestampIndex]).getTime();
			String[] firstId = dataset.get(firstKey).get(j)[idIndex].split("\\.");
			String[] dataRows = new String[16];
			int pointer = 0;
			boolean match = true;
			for(int key : dataset.keySet())
			{
				long currentTimestamp = sdf.parse(dataset.get(key).get(j)[timestampIndex]).getTime();
				String[] currentId = dataset.get(key).get(j)[idIndex].split("\\.");
				System.out.print(key +":");
				System.out.print(dataset.get(key).get(j)[dataIndex] +", ");
				
				if (currentTimestamp == firstTimestamp && currentId[0].equals(firstId[0]) && currentId[1].equals(firstId[1]))
				{
					dataRows[pointer] = dataset.get(key).get(j)[dataIndex];
					
				}
				else
				{
					//match = false;
					dataRows[pointer] = "-1";
					print(firstTimestamp, firstId[0] +"." +firstId[1], currentTimestamp, currentId[0] +"." +currentId[1]);
				}
				pointer++;
			}
			System.out.println();
			if (match) writer.append(makeRow(dataset.get(firstKey).get(j)[timestampIndex], firstId[0] +"." +firstId[1], dataRows) +System.lineSeparator());
		}
		writer.close();
	}
	
	private static void print(long firstTimestamp, String firstId, long currentTimestamp, String currentId) {
		System.out.println("mismatch..." +firstTimestamp +", " +firstId +", " +currentTimestamp +", " +currentId);	
	}

	private static CharSequence makeRow(String firstTimestamp, String firstId,
			String[] dataRows) {
		String row = firstTimestamp +"," +firstId +",";
		
		for(int i = 0; i < dataRows.length; i++)
		{
			row = row +dataRows[i];
			if (i != (dataRows.length -1)) row = row +",";
		}
		
		return row;
		
	}

	private static Date getDate(String date) throws ParseException {
		return sdf.parse(date);	
	}
}
