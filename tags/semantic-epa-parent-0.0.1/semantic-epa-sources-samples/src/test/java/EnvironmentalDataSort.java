import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import de.fzi.cep.sepa.sources.samples.util.Utils;


public class EnvironmentalDataSort {

	public static final String inputfilenamePrefix = "E:\\hella-data\\collected\\collected\\export\\cleaned\\aggregated\\hella-bin-all-ids";
	public static final String outputfilenamePrefix = "E:\\hella-data\\collected\\collected\\export\\cleaned\\aggregated\\hella-bin-all-ids-sorted";
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void main(String[] args) throws IOException {
		
			System.out.println("Start reading data...");
		// load data
			List<String[]> lines = new ArrayList<>();
			
			Optional<BufferedReader> readerOpt = Utils.getReader(new File(inputfilenamePrefix +".csv"));
			{
				if (readerOpt.isPresent())
				{
					String line;
					String key = null;
					BufferedReader reader = readerOpt.get();
					int counter = 0;
					
					while ((line = reader.readLine()) != null) {
						String[] lineData = line.split(",");
						/*if (lineData[1].startsWith("20.13"))*/ lines.add(lineData);
						if (counter == 0) key = lineData[2];
						counter++;
					}
				}
			}
		
		System.out.println("Sorting data");
		
		lines.sort(new Comparator<String[]>() {

			@Override
			public int compare(String[] o1, String[] o2) {
				try {
					long timestamp1 = sdf.parse(o1[0]).getTime();
					long timestamp2 = sdf.parse(o2[0]).getTime();
				
					if (timestamp1 > timestamp2) return 1;
					else if (timestamp1 == timestamp2) return 0;
					else return -1;
					
				} catch (ParseException e) {
					e.printStackTrace();
					return -1;
				}
			}
		});
		
		PrintWriter writer = new PrintWriter(new FileOutputStream(new File(outputfilenamePrefix +".csv")), true);
		
		
		for(String[] line : lines) {
			writer.append(makeRow(line) +System.lineSeparator());
		}
		
		writer.close();
	}

	private static CharSequence makeRow(String[] dataRows) {
		String row = "";
		for(int i = 0; i < dataRows.length; i++)
		{
			row = row +dataRows[i];
			if (i != (dataRows.length -1)) row = row +",";
		}
		
		return row;
		
	}
}
