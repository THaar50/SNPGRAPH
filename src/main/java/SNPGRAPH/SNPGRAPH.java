/*
 * Created as part of a bachelor thesis in winter semester 2017/18
 */

package SNPGRAPH;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 
 * @author Tobias Haar
 * 
 */

public class SNPGRAPH {
	
	private final static int DEFAULT = 400; 
	
	/*
	 * Main method testing for correct input and starting a new run
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println("No data file selected! Please select a file in narrowPeak format to base calculations on");
			throw new IllegalArgumentException();
		}
		
		//TODO: Read command line parameters from function call (read dbSNP file, read chipSeq file, window close config, window sizes)

		for(int i=0;i<args.length;i++){
			File file = new File(args[i]);
			newRun(200, file);
			System.out.println("Finished with dataset " + (i+1) + "!");
		}
	}

	/*
	 * Start new run 
	 */
	//closeAll
	public static void newRun(int windowSize, File file) {
		String file_path = file.getAbsolutePath();

		System.out.println("File: " + file_path);
		
		long starttime = System.nanoTime();
		Database database = new Database();

		// Prepare and read in data with scripts
		prepAndReadInData(file_path);
		
		long readInTime = System.nanoTime();
		System.out.println("Read in time: " + (readInTime-starttime)/1000000000.0 + " seconds");
		
		// Calculate overlap statistics (min, max values, distance counts, etc.)
		database.calcOverlapStats();

		// Create new panel and draw graph with given data
		//closeAll
		GraphPanel.showGUI(database.getUpSNP(), database.getDownSNP(),
				windowSize, database.getTotalFrags(), file.getName());
		
		database.dropTable("chipseq");
		System.out.println("Cleaned up database!");
		database.disconnect();
		
		// Execution time measurement
		long endtime = System.nanoTime();
		System.out.println("Time: " + (endtime - starttime)/1000000000.0 + " seconds");
		
	}

	/*
	 * Prepare data with the prepChipData script for given filename and import into
	 * database table with SQLite .import
	 */

	public static void prepAndReadInData(String file) {
		String[] cmd = { "sh", "../resources/main/prepChipData.sh", file };
		String[] cmd2 = { "sh", "../resources/main/importChipData.sh"};
		try {
			Process p = Runtime.getRuntime().exec(cmd);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
			}
			in.close();
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Importing into database..");
		
		// Import data into database with .import script
		try {
			Process p = Runtime.getRuntime().exec(cmd2);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					p.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
			}
			in.close();
			p.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Done!");

	}
	
	/*
	 * Rounds double value to a given decimal place 
	 */
	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

}
