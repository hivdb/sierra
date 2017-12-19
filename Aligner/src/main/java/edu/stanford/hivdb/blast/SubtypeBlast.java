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

package edu.stanford.hivdb.blast;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import edu.stanford.hivdb.utilities.ProgramProperties;
import edu.stanford.hivdb.utilities.FastaUtils;
import edu.stanford.hivdb.utilities.Sequence;

public class SubtypeBlast {
	private static final String BLAST_FILES_DIR = SubtypeBlast.class.getClassLoader().getResource(ProgramProperties.getProperty("BlastFilesDir")).getPath();
	private static final String TEST_SEQS_FILE = SubtypeBlast.class.getClassLoader().getResource("SubtypeTestFiles/cape_verde_short.txt").getPath();
	private static final String BLAST_OUTPUT_FILE = ProgramProperties.getProperty("BlastOutput");
	private static final String QUERY_SEQ_FILE_NAME = "querySeq.txt";
	private static final int NUM_TARGET_SEQS = 10;
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Reads a file containing one or more sequences.
	 * Iteratively reads each sequence in the file and does the following:
	 *   1. Writes the sequence to a file named QUERY_SEQ_FILE_NAME
	 *   2. Calls the queryDB method in the BlastQuery class
	 *   3. Calls the parseBlastOutput method in the SaxBlaster class
	 *   4. interprets the results and prints to the console and log
	 *
	 *  1. The directory with the BLAST DB and files (provided by the method)
	 *  2. The name of the DB (provided by the method)
	 *  3. The file containing a query sequence TEST_SEQ (created by this method)
	 *  4. The name of the output file BLAST_OUTPUT
	 *  5. The format of the output file (the xml format)
	 */

	public static void main (String[] args)  {
		List<Sequence> testSequences = FastaUtils.readFile(TEST_SEQS_FILE);
		for (Sequence testSeq : testSequences) {
			System.out.println("Sequence header:" + testSeq.getHeader());
			LOGGER.debug(testSeq.getHeader());
			LOGGER.debug(testSeq.getSequence());

			// Print query sequence to the query sequence file
			String testSeqFileName = BLAST_FILES_DIR + "/" + QUERY_SEQ_FILE_NAME;
			FastaUtils.writeFile(testSeq, testSeqFileName);

			// Run the Blast query and print execution time to log file
			long startTime = System.currentTimeMillis();
			int errorCode = BlastQuery.queryDB(BLAST_FILES_DIR, testSeqFileName,
											   BLAST_OUTPUT_FILE, NUM_TARGET_SEQS);
			long endTime = System.currentTimeMillis();
			LOGGER.debug("Blast query execution time:" + (endTime - startTime) + "ms");

			try {
				if (errorCode == 0) {
					String filePath = BLAST_FILES_DIR + "/" + BLAST_OUTPUT_FILE;
					BlastXmlOutputParser blastXmlOutputParser = new BlastXmlOutputParser(filePath);
					String tabularOutput = blastXmlOutputParser.getTabularOutput();
					LOGGER.debug("\n" + tabularOutput);
				} else {
					System.out.println("errorCode:" + errorCode + ". There was a problem with queryDB!");
				}
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}


		}
	}
}
