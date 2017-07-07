import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.streampipes.pe.sources.samples.util.Utils;


public class EnvironmentalDataCleanser {

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static final String filenamePrefix = "e:\\hella-data\\collected\\collected\\export\\cleaned\\hella-";
	
	public static void main(String[] args) throws IOException
	{
		Optional<BufferedReader> readerOpt = Utils.getReader(new File("e:\\hella-data\\collected\\collected\\export\\environmental.csv"));
		{
			if (readerOpt.isPresent())
			{
				BufferedReader reader = readerOpt.get();
				String line;
				String[] lineData;
				File file = null;
				int counter = 0;
				Map<String, PrintWriter> sensors = new HashMap<>();
				
				while ((line = reader.readLine()) != null) {
					if (counter > 0)
					{
						line = line.replaceAll("\\(.*\\)", "");
						line = line.replaceAll("; ", ";"); 
						line = line.replaceAll("\"", "");
						lineData = line.split(",");
						//System.out.println(line);
						Calendar calendar =  Calendar.getInstance();
						try {
							calendar.setTime(getDate(lineData[4]));
							if (calendar.get(Calendar.YEAR) == 2015 && calendar.get(Calendar.MONTH) == 9)
							{
								if (!sensors.containsKey(lineData[2]))
								{
									file = new File(filenamePrefix +lineData[2] +".csv");
									sensors.put(lineData[2], new PrintWriter(new FileOutputStream(file), true));
								}
								sensors.get(lineData[2]).write(makeLine(lineData) +System.lineSeparator());
								if (counter % 100000 == 0) System.out.println(lineData[4]);
							}
						} catch(Exception e)
						{
							System.out.println(line);
							//e.printStackTrace();
						}
					}
					counter++;
					
				}
				for(String key : sensors.keySet())
					sensors.get(key).close();
			}
		}
	}
	
	private static String makeLine(String[] line)
	{
		String newLine = "";
		for(int i = 0; i<=4; i++)
		{
			newLine += line[i];
			if (i != 4) newLine +=",";
		}
		return newLine;
	}
	
	private static Date getDate(String date) throws ParseException {
		
		return sdf.parse(date);
		
	}
}
