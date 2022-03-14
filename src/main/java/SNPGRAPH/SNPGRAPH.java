/*
 * Created as part of a bachelor thesis in winter semester 2017/18
 */

package SNPGRAPH;

import org.apache.commons.cli.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 
 * @author Tobias Haar
 * 
 */

public class SNPGRAPH {
	/**
	 * Main method testing for correct input and starting a new run
	 */
	public static void main(String[] args) {

		CmdParameters cmdParameters;
		try {
			cmdParameters = new CmdParameters(args);
		} catch (ParseException e) {
			return;
		}

		final int windowSize = cmdParameters.getIntegerValue("windowSize");

		System.setProperty("snpgraph.chipseq.datapath", cmdParameters.getStringValue("chipSeqFile"));
		System.setProperty("snpgraph.dbsnp.datapath", cmdParameters.getStringValue("snpFile"));
		System.setProperty("snpgraph.resources", "/snpgraph/build/resources/main/");

        Database database = new Database();
		File dbsnpDir = new File(System.getProperty("snpgraph.dbsnp.datapath"));
		if (dbsnpDir.isFile()) {
			System.out.println("Importing new SNP data and overwriting existing SNP data...");
			database.dropTable("snp");
			prepAndReadInSnpData(dbsnpDir.getAbsolutePath());
		} else {
			File[] dbsnpFiles = dbsnpDir.listFiles();
			if (dbsnpFiles == null) {
				System.out.println("No dbSNP file found in " + dbsnpDir.getAbsolutePath());
				return;
			}
			if (dbsnpFiles.length > 1) {
                System.out.println("Too many dbSNP files found. Please select one.");
				return;
			}
            if (dbsnpFiles.length == 1) {
				if (database.tableExists("snp")) {
					System.out.println("Using existing SNP data from database.");
				} else {
					database.dropTable("snp");
					prepAndReadInSnpData(dbsnpFiles[0].getAbsolutePath());
				}
            }
		}

		File chipseqDir = new File(System.getProperty("snpgraph.chipseq.datapath"));
		if (chipseqDir.isFile()) {
			newRun(windowSize, chipseqDir, database);
			System.out.println("Done with " + chipseqDir.getName());
		} else {
			File[] chipseqFiles = chipseqDir.listFiles();
			if (chipseqFiles != null) {
				for (File chipseqFile : chipseqFiles) {
					newRun(windowSize, chipseqFile, database);
					System.out.println("Done with " + chipseqFile.getName());
				}
			} else {
				System.out.println("No chipSeq files found in " + System.getProperty("snpgraph.chipseq.datapath"));
			}
		}
	}

	/**
	 * Start new run 
	 */
	//closeAll
	public static void newRun(int windowSize, File file, Database database) {
		String file_path = file.getAbsolutePath();

		System.out.println("File: " + file_path);
		
		long starttime = System.nanoTime();

		// Prepare and read in data with scripts
		prepAndReadInChipData(file_path);
		
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

	/**
	 * Prepare data with the prepChipData script for given filename and import into
	 * database table with SQLite .import
	 */

	public static void prepAndReadInChipData(String file) {
		String[] cmd = { "sh", System.getProperty("snpgraph.resources") + "prepChipData.sh", file };
		String[] cmd2 = { "sh", System.getProperty("snpgraph.resources") + "importChipData.sh" };

        runScript(cmd);
		System.out.println("Importing into database..");
		// Import data into database with .import script
        runScript(cmd2);
		System.out.println("Done!");
	}

    /**
     * Prepare data with the prepSNPData script for given filename and import into
     * database table with SQLite .import
     */

    public static void prepAndReadInSnpData(String file) {
        String[] cmd = { "sh", System.getProperty("snpgraph.resources") + "prepSNPData.sh", file };
        String[] cmd2 = { "sh", System.getProperty("snpgraph.resources") + "importSNPData.sh" };

        runScript(cmd);
        System.out.println("Importing into database..");
        // Import data into database with .import script
        runScript(cmd2);
        System.out.println("Done!");
    }

    /**
     * Executes given shell command in a new process.
     * @param cmd shell command as a list.
     */

    public static void runScript(String[] cmd) {
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
    }
	
	/**
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
