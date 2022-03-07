package SNPGRAPH;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CmdParameters {

	/**
	 * options for the parser
	 */
	private final Options options;

	/**
	 * default values for options
	 */
	private final Map<String, String> defaults;

	/**
	 * formatter for printing help
	 */
	HelpFormatter formatter;

	/**
	 * internally used command line parser
	 */
	private final CommandLine cmd;

	/**
	 * parses the arguments and creates a new CmdParameter object
	 * 
	 * @param args
	 *            program arguments
	 * @throws ParseException
	 *             thrown in case of problems during parsing
	 */
	public CmdParameters(String[] args) throws ParseException {
		options = new Options();
		defaults = new HashMap<>();
		formatter = new HelpFormatter();
		prepareOptions();
		CommandLineParser parser = new DefaultParser();
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("snpgraph", options);
			throw (e);
		}
	}

	/**
	 * prepares the options for the command line parser
	 */
	private void prepareOptions() {
		Option snpFile = new Option("s", "snpFile", true, "dbSNP file (default: snpgraph/data/dbsnp/)");
		snpFile.setRequired(false);
		options.addOption(snpFile);
		defaults.put("snpFile", "/snpgraph/data/dbsnp/");

		Option chipSeqFile = new Option("c", "chipSeqFile", true, "chipSeq file in narrowPeak format (default: snpgraph/data/chipseq/)");
		chipSeqFile.setRequired(false);
		options.addOption(chipSeqFile);
		defaults.put("chipSeqFile", "/snpgraph/data/chipseq/");

		Option windowSize = new Option("w", "windowSize", true, "size of window to scan for SNPs around peak (default: 400)");
		windowSize.setRequired(false);
		options.addOption(windowSize);
		defaults.put("windowSize", "400");
		
		Option closeAll = new Option("closeall", false, "close all graph windows at once when closing one when this flag is set");
		closeAll.setRequired(false);
		options.addOption(closeAll);
	}

	/**
	 * Return value of an option as string
	 */
	public String getStringValue(String option) {
		return cmd.getOptionValue(option, defaults.get(option));
	}

	/**
	 * Return value of an option as integer
	 */
	public Integer getIntegerValue(String option) {
		return Integer.parseInt(cmd.getOptionValue(option, defaults.get(option)));
	}

	/**
	 * Checks if an option is available
	 */
	public boolean hasOption(String option) {
		return cmd.hasOption(option);
	}
}
