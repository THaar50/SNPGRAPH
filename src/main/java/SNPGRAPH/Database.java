package SNPGRAPH;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

	private static final String DB_URL = "jdbc:sqlite:../resources/main/SNPGRAPH.db";
	private static final String DRIVER_URL = "org.sqlite.JDBC";

	private Connection conn;
	public double[] down_snp, up_snp;
	public int totalFrags;

	public Database() {
		this.conn = connect();
		this.up_snp = null;
		this.down_snp = null;
		this.totalFrags = 0;
	}

	/*
	 * Connect to the database using its path
	 */
	public Connection connect() {
		try {

			// Verifying the driver
			Class.forName(DRIVER_URL);
			this.conn = DriverManager.getConnection(DB_URL);
			System.out.println("Connection to SQLite database SNPGRAPH.db has been established.");

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		return this.conn;
	}

	/*
	 * Calculates statistics such as number of fragments in total, number of
	 * fragments with SNPs, number of SNPs in all fragments, average and maximum
	 * distance of SNPs to the peak, mean number and median of SNPs in all
	 * fragments
	 */
	public void calcOverlapStats() {
		String sql = "SELECT snp.start AS snp_start, chipseq.id "
				+ "AS chip_id, chipseq.start+chipseq.peak as peak_pos "
				+ "FROM snp INNER JOIN chipseq ON chipseq.chr=snp.chr WHERE "
				+ "snp.start>=chipseq.start AND snp.start<chipseq.end;";

		int peakPos, relSNPDist, chipID = 0, fragCount = 0, overlapCount = 0, distances = 0, maxDist = 0;

		System.out.println("Calculating statistics for SNP overlaps.. ");
		try (Statement stmt = this.conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				// Calculate overlap positions
				peakPos = rs.getInt("peak_pos");
				relSNPDist = peakPos - rs.getInt("snp_start");

				// Calculate number of fragments with SNPs
				if (rs.getInt("chip_id") > chipID) {
					chipID = rs.getInt("chip_id");
					fragCount++;
				}

				// Calculate average distance from peak
				distances += Math.abs(relSNPDist);
				overlapCount++;

				// Find maximum distance from peak
				if (maxDist < Math.abs(relSNPDist)) {
					maxDist = Math.abs(relSNPDist);
				}

			}

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		totalFrags = countFrags();
		//Testing for appropriate input
		if(totalFrags==0){
			dropTable("chipseq");
			System.out.println("A problem occured while reading in the data. Please try again!");
			throw new RuntimeException();
		}
		//Counting SNPs on the fragments
		countDistances(maxDist);

		System.out.println("ChIP-Seq fragments in total: " + totalFrags);

		System.out.println(overlapCount + " SNP overlaps found at " + fragCount
				+ " fragments!");

		System.out.println("ChiP-Seq fragments without any SNPs: "
				+ (totalFrags - fragCount)
				+ " ("
				+ SNPGRAPH.round(((double) (totalFrags - fragCount)
						/ (double) totalFrags * 100.0), 4) + "%)");

		System.out.println("Average distance from peak: "
				+ (distances / overlapCount));

		System.out.println("Maximum distance from peak: " + maxDist);

		System.out.println("Mean of SNPs in all fragments: "
				+ (double) overlapCount / (double) totalFrags);
		
		System.out.println("Mean of SNPs in all fragments containing SNPs: "
				+ (double) overlapCount / (double) fragCount);
		calcSNPMedian(fragCount, totalFrags);

	}

	/*
	 * Counts absolute occurrence of SNPs up in all fragments
	 */
	public void countDistances(int maxDist) {
		String sql = 
				"SELECT snp.start AS snp_start, "
				+ "chipseq.start+chipseq.peak as peak_pos FROM snp INNER JOIN chipseq ON chipseq.chr=snp.chr WHERE "
				+ "snp.start>=chipseq.start AND snp.start<chipseq.end;";

		down_snp = new double[maxDist + 1];
		up_snp = new double[maxDist + 1];

		int peakPos, relSNPDist;

		try (Statement stmt = this.conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				// Calculate overlap positions
				peakPos = rs.getInt("peak_pos");
				relSNPDist = peakPos - rs.getInt("snp_start");

				// Count distances from peak
				if (relSNPDist < 0) {
					down_snp[Math.abs(relSNPDist)]++;
				} else {
					up_snp[relSNPDist]++;
				}
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

	}

	/*
	 * Calculates median of SNPs in all fragments
	 */
	public void calcSNPMedian(int fragCount, int totalFrags) {
		String sql = "SELECT chip_id, COUNT(chip_id) AS chip_count FROM ("
				+ "SELECT chipseq.id AS chip_id FROM snp INNER JOIN chipseq ON chipseq.chr=snp.chr "
				+ "WHERE snp.chr=chipseq.chr "
				+ "AND snp.start>=chipseq.start "
				+ "AND snp.start<chipseq.end) GROUP BY chip_id ORDER BY chip_count;";

		int[] snp_frag_count = new int[totalFrags];
		int i = totalFrags-fragCount;

		try (Statement stmt = this.conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				snp_frag_count[i] = rs.getInt("chip_count");
				i++;
			}
			double median;
			if (snp_frag_count.length % 2 == 0)
				median = ((double) snp_frag_count[snp_frag_count.length / 2] + (double) snp_frag_count[snp_frag_count.length / 2 - 1]) / 2;
			else
				median = snp_frag_count[snp_frag_count.length / 2];
			System.out.println("Median of SNPs in all fragments: " + median);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Counts fragments in the dataset
	 */
	public int countFrags() {

		String sql = "SELECT COUNT(start) FROM chipseq;";

		int count = 0;

		try (Statement stmt = this.conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			count = rs.getInt(1);

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return count;
	}

	/*
	 * Getter for total number of of fragments
	 */
	public int getTotalFrags() {
		return this.totalFrags;
	}

	/*
	 * Getter for upstream SNP array
	 */
	public double[] getUpSNP() {
		return this.up_snp;

	}

	/*
	 * Getter for downstream SNP array
	 */
	public double[] getDownSNP() {
		return this.down_snp;

	}

	/*
	 * Drops given table in the database
	 */
	public void dropTable(String table) {

		String sql = "DROP TABLE IF EXISTS `" + table + "`";

		try (Statement stmt = this.conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Close connection to database if there is one
	 */
	public void disconnect() {
		try {
			if (this.conn != null) {
				this.conn.close();
				System.out.println("Connection to database closed!");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
