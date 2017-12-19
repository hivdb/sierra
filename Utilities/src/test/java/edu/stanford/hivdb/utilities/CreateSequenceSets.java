/*
    
    Copyright (C) 2017 Stanford HIVDB team
    
    Sierra is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    Sierra is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package edu.stanford.hivdb.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import edu.stanford.hivdb.utilities.MyFileUtils;
import edu.stanford.hivdb.utilities.ProgramProperties;

/**
 * Create test datasets comprising multiple fasta sequences:
 * ">sequence_name" + additional optional information typically separated by pipe symbols
 * followed by a new line and then the nucleotide sequence
 * Currently the sequences are coming from the database HIVDB_Results. This is a large database.
 *
 * Because a single method is used to execute the queries and generate the fasta file. The queries
 * currently all select for fields of the same type
 */
public class CreateSequenceSets {
	private static final String HIVDB_RESULTS_URL = ProgramProperties.getProperty("mysqlHIVDB_Results");
	private static final String HIVDB_RESULTS_USER = ProgramProperties.getProperty("mysqlHIVDB_ResultsUser");
	private static final String HIVDB_RESULTS_PWD = ProgramProperties.getProperty("mysqlHIVDB_ResultsPWD");
	private static final String TEST_FILES_DIR = "src/main/resources/test_sequences";

	/**
	 * Each line calls a method which creates a fasta file with sequences meeting specific criteria denoted
	 * in part by the method name and output file name.
	 */
	public static void main(String[] args) {
		inSequences(TEST_FILES_DIR + "/" + "_JustIN.txt", 40);
		prRtIn(TEST_FILES_DIR + "/" + "_PRRTIN.txt", 40);
		prRt(TEST_FILES_DIR + "/" + "_PRRT.txt", 20);
		vgiSequences(TEST_FILES_DIR + "/" + "_vgi.txt",20);
		gagPr(TEST_FILES_DIR + "/" + "_gagPR.txt", 100);
		prSomeRT(TEST_FILES_DIR + "/" + "_PRSomeRT.txt", 100);
		extendedAAsRT(TEST_FILES_DIR + "/" + "_ExtendedAAsRT.txt", 100);
		rtInsertions69(TEST_FILES_DIR + "/" + "_RTInsertions69.txt", 100);
		rtInsertionsNot69(TEST_FILES_DIR + "/" + "_RTInsertionsNot69.txt", 100);
		rtDeletions69(TEST_FILES_DIR + "/" + "_RTDeletions69.txt", 100);
		rtDeletions67(TEST_FILES_DIR + "/" + "_RTDeletions67.txt", 100);
		rtDeletionsUnusual(TEST_FILES_DIR + "/" + "_RTDeletionsUnusual.txt", 10);
		prInsertions(TEST_FILES_DIR + "/" + "_PRInsertions.txt", 100);
	}


	private static void vgiSequences(String filePath, int numSequences) {
		String sql = "SELECT d.AccNum, s.Sequence " +
				"FROM GBSequenceData d, GBSequences s " +
				"WHERE d.AccNum = s.AccNum " +
				"AND d.Species = 'HIV1' " +
				"AND TestCategory = 'PossibleVGI' " +
				"ORDER BY AccNum LIMIT " + numSequences + ";";
		String queryOutput = "#" + sql + "\n";
		queryOutput += executeSqlStatement(sql);
		MyFileUtils.writeFile(filePath, queryOutput);

	}


	private static void inSequences(String filePath, int numSequences) {
		String sql = "SELECT d.AccNum, s.Sequence " +
				"FROM GBSequenceData d, GBSequences s " +
				"WHERE d.AccNum = s.AccNum " +
				"AND d.Species = 'HIV1' " +
				"AND TestCategory = 'JustIn' " +
				"AND (MutListIN LIKE '%92%' " +
				" OR MutListIN LIKE '%140%' " +
				" OR MutListIN LIKE '%143%' " +
				" OR MutListIN LIKE '%148%' " +
				" OR MutListIN LIKE '%155%') " +
				"AND Problems = 'None' " +
				"ORDER BY AccNum LIMIT " + numSequences + ";";
		String queryOutput = "#" + sql + "\n";
		queryOutput += executeSqlStatement(sql);
		MyFileUtils.writeFile(filePath, queryOutput);
	}




	private static void prRtIn(String filePath, int numSequences) {
		String sql = "SELECT d.AccNum, s.Sequence " +
				"FROM GBSequenceData d, GBSequences s " +
				"WHERE d.AccNum = s.AccNum " +
				"AND d.Species = 'HIV1' " +
				"AND MutListRT != 'NA' " +
				"AND MutListPR != 'NA' " +
				"AND MutListIN != 'NA' " +
				"AND Problems = 'None' " +
				"ORDER BY AccNum LIMIT " + numSequences + ";";
		String queryOutput = "#" + sql + "\n";
		queryOutput += executeSqlStatement(sql);
		MyFileUtils.writeFile(filePath, queryOutput);
	}

	private static void prRt (String filePath, int numSequences) {
		String sql = "SELECT d.AccNum, s.Sequence " +
				"FROM GBSequenceData d, GBSequences s " +
				"WHERE d.AccNum = s.AccNum " +
				"AND d.Species = 'HIV1' " +
				"AND FirstAAPR != 0 " +
				"AND LastAAPR != 0 " +
				"AND FirstAART != 0 " +
				"AND LastAART != 0 " +
				"AND FirstAAIN = 0 " +
				"AND LastAAIN = 0 " +
				"AND Problems = 'None' " +
				"ORDER BY AccNum LIMIT " + numSequences + ";";
		String queryOutput = "#" + sql + "\n";
		queryOutput += executeSqlStatement(sql);
		MyFileUtils.writeFile(filePath, queryOutput);
	}

	private static void gagPr(String filePath, int numSequences) {
		String sql = "SELECT d.AccNum, s.Sequence " +
				"FROM GBSequenceData d, GBSequences s " +
				"WHERE d.AccNum = s.AccNum " +
				"AND d.Species = 'HIV1' " +
				"AND FirstAAPR = 1 " +
				"AND LastAAPR = 99 " +
				"AND FirstAART = 0 " +
				"AND LastAART = 0 " +
				"AND FirstAAIN = 0 " +
				"AND LastAAIN = 0 " +
				"AND length(s.Sequence) > 400 " +
				"AND Problems = 'None' " +
				"ORDER BY AccNum LIMIT " + numSequences + ";";
		String queryOutput = "#" + sql + "\n";
		queryOutput += executeSqlStatement(sql);
		MyFileUtils.writeFile(filePath, queryOutput);
	}

	// Sequences that include PR and some of RT (possibly also some of gag)
	private static void prSomeRT (String filePath, int numSequences) {
		String sql = "SELECT d.AccNum, s.Sequence " +
				"FROM GBSequenceData d, GBSequences s " +
				"WHERE d.AccNum = s.AccNum " +
				"AND d.Species = 'HIV1' " +
				"AND FirstAAPR = 1 " +
				"AND LastAAPR = 99 " +
				"AND MutListRT = '' " +
				"AND FirstAAIN = 0 " +
				"AND LastAAIN = 0 " +
				"And length(s.Sequence) > 300 " +
				"AND Problems = 'None' " +
				"ORDER BY AccNum LIMIT " + numSequences + ";";
		String queryOutput = "#" + sql + "\n";
		queryOutput += executeSqlStatement(sql);
		MyFileUtils.writeFile(filePath, queryOutput);
	}

	// Select sequences that are annotated in GBSequenceData as having extendedAAs for RT
	private static void extendedAAsRT (String filePath, int numSequences) {
		String sql = "SELECT d.AccNum, s.Sequence " +
			"FROM GBSequenceData d, GBSequences s " +
			"WHERE d.AccNum = s.AccNum " +
			"AND d.Species = 'HIV1' " +
			"AND d.ExtendedAAs != '' " +
			"AND d.MutListRT != 'NA' " +
			"ORDER BY AccNum Limit " + numSequences + ";";
		String queryOutput = "#" + sql + "\n";
		queryOutput += executeSqlStatement(sql);
		MyFileUtils.writeFile(filePath, queryOutput);

	}

	// All sequences listed as having RT insertions at codon 69
	private static void rtInsertions69(String filePath, int numSequences) {
		String sql = "SELECT d.AccNum, s.Sequence " +
				"FROM GBSequenceData d, GBSequences s " +
				"WHERE d.AccNum = s.AccNum " +
				"AND d.Species = 'HIV1' " +
				"AND d.MutListRT like '%69S_S%' " +
				"ORDER BY AccNum LIMIT " + numSequences + ";";
		String queryOutput = "#" + sql + "\n";
		queryOutput += executeSqlStatement(sql);
		MyFileUtils.writeFile(filePath, queryOutput);
	}

	// All sequences listed as having RT insertions other than those beginning with '69S'
	private static void rtInsertionsNot69(String filePath, int numSequences) {
		String sql = "SELECT d.AccNum, s.Sequence " +
				"FROM GBSequenceData d, GBSequences s " +
				"WHERE d.AccNum = s.AccNum " +
				"AND d.Species = 'HIV1' " +
				"AND d.MutListRT like '%\\_%' " +
				"AND d.MutListRT not like '%69S_%' " +
				"ORDER BY AccNum LIMIT " + numSequences + ";";
		String queryOutput = "#" + sql + "\n";
		queryOutput += executeSqlStatement(sql);
		MyFileUtils.writeFile(filePath, queryOutput);
	}

	// All sequences listed as having RT Deletions at codon 69
	private static void rtDeletions69(String filePath, int numSequences) {
		String sql = "SELECT d.AccNum, s.Sequence " +
				"FROM GBSequenceData d, GBSequences s " +
				"WHERE d.AccNum = s.AccNum " +
				"AND d.Species = 'HIV1' " +
				"AND d.MutListRT like '%69~%' " +
				"ORDER BY AccNum LIMIT " + numSequences + ";";
		String queryOutput = "#" + sql + "\n";
		queryOutput += executeSqlStatement(sql);
		MyFileUtils.writeFile(filePath, queryOutput);
	}

	// All sequences listed as having RT Deletions at codon 69
	private static void rtDeletions67(String filePath, int numSequences) {
		String sql = "SELECT d.AccNum, s.Sequence " +
				"FROM GBSequenceData d, GBSequences s " +
				"WHERE d.AccNum = s.AccNum " +
				"AND d.Species = 'HIV1' " +
				"AND d.MutListRT like '%67~%' " +
				"ORDER BY AccNum LIMIT " + numSequences + ";";
		String queryOutput = "#" + sql + "\n";
		queryOutput += executeSqlStatement(sql);
		MyFileUtils.writeFile(filePath, queryOutput);
	}

	// All sequences listed as having RT Deletions at codon 67
	private static void rtDeletionsUnusual(String filePath, int numSequences) {
		String sql = "SELECT d.AccNum, s.Sequence " +
				"FROM GBSequenceData d, GBSequences s " +
				"WHERE d.AccNum = s.AccNum " +
				"AND d.Species = 'HIV1' " +
				"AND d.MutListRT like '%~%' " +
				"AND d.MutListRT NOT LIKE '%69~%' " +
				"AND d.MutListRT NOT LIKE '%67~%' " +
				"ORDER BY AccNum LIMIT " + numSequences + ";";
		String queryOutput = "#" + sql + "\n";
		queryOutput += executeSqlStatement(sql);
		MyFileUtils.writeFile(filePath, queryOutput);
	}

	// All sequences listed as having RT Deletions at codon 67
	private static void prInsertions(String filePath, int numSequences) {
		String sql = "SELECT d.AccNum, s.Sequence " +
				"FROM GBSequenceData d, GBSequences s " +
				"WHERE d.AccNum = s.AccNum " +
				"AND d.Species = 'HIV1' " +
				"AND d.MutListPR like '%\\_%' " +
				"ORDER BY AccNum LIMIT " + numSequences + ";";
		String queryOutput = "#" + sql + "\n";
		queryOutput += executeSqlStatement(sql);
		MyFileUtils.writeFile(filePath, queryOutput);
	}

	private static String executeSqlStatement(String statement) {
		final String sqlStatement = statement;
		System.out.println(sqlStatement);
		StringBuffer queryOutput = new StringBuffer();
		try {
			Class.forName("com.mysql.jdbc.Driver");
			final Connection conn = DriverManager.getConnection(HIVDB_RESULTS_URL, HIVDB_RESULTS_USER, HIVDB_RESULTS_PWD);
			SQLWarning warn = conn.getWarnings();
			while (warn != null) {
				System.out.println("SQLState: " + warn.getSQLState());
				System.out.println("Message: " + warn.getMessage());
				System.out.println("Vendor: " + warn.getErrorCode());
				System.out.println("");
				warn = warn.getNextWarning();
			}
			final Statement stmt = conn.createStatement();
			final ResultSet rs = stmt.executeQuery(sqlStatement);
			while (rs.next()) {
				String accNum = rs.getString(1);
				String sequence = rs.getString(2);
				queryOutput.append(">" + accNum + "\n" + sequence + "\n");
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//System.out.println(queryOutput.toString());
		return queryOutput.toString();
	}



}
