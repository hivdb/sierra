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

package edu.stanford.hivdb.sequences.scripts;

//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import edu.stanford.hivdb.genotypes.Subtype;
//import edu.stanford.hivdb.utilities.MyFileUtils;
//import edu.stanford.hivdb.utilities.Database;
//import edu.stanford.hivdb.utilities.ProgramProperties;

/**
 * Query all the subtype test sequences from HIVDB_Results. Write the sequences to a fasta file in
 * Utilities/src/main/resources. The header information will contain detailed information separated by
 * the pipe symbol.
 * TODO: Check output path
 */
//public class CreateSubtypeTestFile {
//	private static final String URL = ProgramProperties.getProperty("mysqlHIVDB_Results");
//	private static final String USER = ProgramProperties.getProperty("mysqlHIVDB_ResultsUser");
//	private static final String PWD = ProgramProperties.getProperty("mysqlHIVDB_ResultsPWD");
//	private static final String PATH = "/home/rshafer/sandbox/Utilities/src/main/resources/test_sequences/SubtypeTestsAll.txt";
//
//	private static final Database db;
//	static {
//		db = new Database(URL, USER, PWD);
//	}
//
//	public static void main(String[] args) throws SQLException {
//		System.out.println(PATH);
//		subtypeTestsAll(PATH);
//	}
//
//	private static void subtypeTestsAll(String filePath) throws SQLException {
//		final String sql = "SELECT PID, Genes, FirstNA, LastNA, GB, Subtype, Country, Year, AuthorYr, PMID, " +
//				"NumMix, NumDRMs, Sequence " +
//				"FROM tblSubtypeTestSeqs;";
//		System.out.println(sql);
//		StringBuffer queryOutput = new StringBuffer();
//		try (
//				Connection conn = db.getConnection();
//				PreparedStatement pstmt = db.prepareStatement(conn, sql);
//				ResultSet rs = pstmt.executeQuery();
//				)
//		{
//			while (rs.next()) {
//				int pid = rs.getInt(1);
//				String genes = rs.getString(2);
//				int firstNA = rs.getInt(3);
//				int lastNA = rs.getInt(4);
//				String gb = rs.getString(5);
//				Subtype subtype = Subtype.valueOf(rs.getString(6));
//				String country = rs.getString(7);
//				int year = rs.getInt(8);
//				String authorYr = rs.getString(9);
//				int pmid = rs.getInt(10);
//				int numMix = rs.getInt(11);
//				int numDRMs = rs.getInt(12);
//				String sequence = rs.getString(13);
//
//				String header = ">" + subtype + "|" + pid + "|" + genes + "|" + firstNA + "|" + lastNA + "|" +
//							gb + "|" + country + "|" + year + "|" + authorYr + "|" + pmid + "|" +
//							numMix + "|" + numDRMs;
//				queryOutput.append(header + "\n" + sequence + "\n");
//			}
//
//		}
//		MyFileUtils.writeFile(filePath, queryOutput.toString());
//	}
//
//
//
//}
