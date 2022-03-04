/*
 * Created as part of a bachelor thesis in winter semester 2017/18
 */

package SNPGRAPH;

import java.util.*;
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
		
		//TODO: File name extraction to do, args elements then run with seperate window sizes, graph output names according to
		//input filenames.
		Scanner in = new Scanner(System.in);
		System.out.print("Select window sizes for all " + args.length + " datasets (default is 400) greater than 50 and hit enter: ");
		int[] windowSize = new int[args.length];
		for(int i=0; i<args.length; i++){
			windowSize[i]= in.nextInt();
		}
		in.close();

		//TODO: DEBUG specifying how graph panels should be closed
//		Scanner in2 = new Scanner(System.in);
//		System.out.println("Close all graph panels at once? (y/n)");
//		String closeOnExit = in2.next();
//		in2.close();
		
		for(int i=0;i<args.length;i++){
			if(windowSize[i] <= 50){
				windowSize[i] = DEFAULT;
				System.out.println("Invalid window size input. Using default size..");
			}
			
			File f = new File(args[i]);
			String file_name = f.getName();
			
			//closeOnExit
			newRun(windowSize[i], file_name);
			System.out.println("Finished with dataset " + (i+1) + "!");
		}
	}

	/*
	 * Start new run 
	 */
	//closeAll
	public static void newRun(int windowSize, String file_name) {

		System.out.println("File: " + file_name);
		
		long starttime = System.nanoTime();
		Database database = new Database();

		// Prepare and read in data with scripts
//		prepAndReadInData(file_name);
		
		long readInTime = System.nanoTime();
		System.out.println("Read in time: " + (readInTime-starttime)/1000000000.0 + " seconds");
		
		// Calculate overlap statistics (min, max values, distance counts, etc.)
		database.calcOverlapStats();

		// Create new panel and draw graph with given data
		//closeAll
		GraphPanel.showGUI(database.getUpSNP(), database.getDownSNP(),
				windowSize, database.getTotalFrags(), file_name);
		
//		database.dropTable("chipseq");
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
		String[] cmd = { "sh", "prepChipData.sh", file };
		String[] cmd2 = { "sh", "importFile.sh"};
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
