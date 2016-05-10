import java.io.BufferedReader;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import de.fzi.cep.sepa.sources.samples.util.Utils;


public class HellaParametersExtractor {

	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static final String FILENAME = "E:\\hella-data\\collected\\collected\\export\\environmental.csv";
	static final int MAX = 100;
	
	public static void main(String[] args) {
		
		Optional<BufferedReader> readerOpt = Utils.getReader(new File(FILENAME));
		try {
			if (readerOpt.isPresent())
			{
				BufferedReader reader = readerOpt.get();
				String line;
				String[] lineData;
				int counter = 0;
				Set<String> uniqueNames = new HashSet<>();
				
				while ((line = reader.readLine()) != null && counter <= MAX) {
					
					
					line = line.replaceAll("; ", ";"); 
					line = line.replaceAll("\"", "");
					lineData = line.split(",");
					if (lineData[2].startsWith("Bin")) uniqueNames.add(lineData[2] +lineData[3]);
					else uniqueNames.add(lineData[2]);
				}
				
				for(String uniqueName : uniqueNames) {
					System.out.println(uniqueName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
