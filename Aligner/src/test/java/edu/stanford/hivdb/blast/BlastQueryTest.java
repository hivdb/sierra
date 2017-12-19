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
import java.io.IOException;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Ignore;
import edu.stanford.hivdb.blast.BlastQuery;
import edu.stanford.hivdb.blast.SubtypeBlast;
import edu.stanford.hivdb.utilities.LogFormatter;
import edu.stanford.hivdb.utilities.ProgramProperties;
import edu.stanford.hivdb.utilities.FastaUtils;
import edu.stanford.hivdb.utilities.Sequence;


/**
 *
 *
 */
public class BlastQueryTest {
	private static final String BLAST_FILES_DIR = SubtypeBlast.class.getClassLoader().getResource(ProgramProperties.getProperty("BlastFilesDir")).getPath();
	private static final String TEST_SEQS_FILE = SubtypeBlast.class.getClassLoader().getResource("SubtypeTestFiles/cape_verde_short.txt").getPath();
	private static final String BLAST_OUTPUT_FILE = ProgramProperties.getProperty("BlastOutput");
	private static final String QUERY_SEQ_FILE_NAME = "querySeq.txt";
	private static final int NUM_TARGET_SEQS = 20;

	private static final Logger LOGGER = Logger.getLogger(BlastQueryTest.class.getName());
	static {
		LOGGER.setLevel(Level.FINER);
		try {
			final String logFile = BlastQueryTest.class.getClassLoader().getResource("OutputFiles/" + BlastQueryTest.class.getSimpleName() + ".log").getPath();
			final FileHandler fileHandler = new FileHandler(logFile, false);
			final LogFormatter formatter = new LogFormatter();
			fileHandler.setFormatter(formatter);
			LOGGER.addHandler(fileHandler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * Tests the queryDB method. It reads a file containing one or more sequences. It iterates through
	 *  each sequence and does the following:
	 *  1. Writes the sequence to QUERY_SEQ_FILE_NAME in the BLAST_DIR
	 *  2. Calls the queryDB method of the BlastQuery class providing it with the paths to
	 *     the file with query sequence and to the file which will contain the blast output.
	 *     It also provides queryDB with the number of target sequences to be included in the output.
	 *  3. Records how long the query takes.
	 */
	@Ignore
	public void queryDBTest() {
		List<Sequence> testSequences = FastaUtils.readFile(TEST_SEQS_FILE);
		for (Sequence testSeq : testSequences) {
			LOGGER.finer(testSeq.getHeader() + "\n" + testSeq.getSequence());
			String testSeqFileName = BLAST_FILES_DIR + "/" + QUERY_SEQ_FILE_NAME;
			FastaUtils.writeFile(testSeq, testSeqFileName);
			long startTime = System.currentTimeMillis();
			BlastQuery.queryDB(BLAST_FILES_DIR, testSeqFileName, BLAST_OUTPUT_FILE, NUM_TARGET_SEQS);
			long endTime = System.currentTimeMillis();
			System.out.println("Sequence header:" + testSeq.getHeader());
			LOGGER.finer("Blast query executiion time:" + (endTime - startTime) + "ms" + "\n");
		}
	}

}
