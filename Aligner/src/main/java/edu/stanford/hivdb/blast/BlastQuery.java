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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.hivdb.utilities.ExecCommand;
import edu.stanford.hivdb.utilities.ProgramProperties;

public class BlastQuery {
	private static final String REF_SEQS_DB = ProgramProperties.getProperty("SubtypeRefsDB");
	private static final int XML_OUTPUT_FMT = 5;
	private static final int WORD_SIZE = 20;
	private static final int REWARD = 2;
	private static final int PENALTY = -3;
	private static final int GAP_OPEN = 5;
	private static final int GAP_EXTEND = 2;

	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Query a blast database using a file containing a single sequence. Five items are required:
	 *  1. A directory containing the blast database and query sequence
	 *  2. The name of the blast database
	 *  3. The name of the query sequence
	 *  4. The name of the output file containing the blast results
	 *  5. The output file format
	 *  6. The number of hits to show indicated by -max_target_seqs if outfmt is >=5 or
	 *     by -num_alignments -num_descriptions if number of hits is lower
	 * Parameters 1 and 2 are constants
	 * This method receives 3 strings containing parameters 3 - 5.
	 * @param querySeqFile, outputFile, outputFileFmt
	 * @return the name of the output file (outputFile) if successful.
	 * TODO: Make the blast query parameters (word size, reward, penalty, gap open, gap extend) program properties
	 */
	public static int queryDB(String blastFilesDir, String querySeqFile, String outputFile, int numTargetSeqs) {
		LOGGER.debug("Parameters:\nDir:" + blastFilesDir);
		LOGGER.debug("QueryFile:" + querySeqFile);
		LOGGER.debug("QueryDB:" + REF_SEQS_DB);
		LOGGER.debug("OutputFileName:" + outputFile);
		LOGGER.debug("OutputFormat:" + XML_OUTPUT_FMT);

        String cmd = "blastn";
        cmd += " -outfmt " + XML_OUTPUT_FMT;
        cmd += " -word_size " + WORD_SIZE;
        cmd += " -reward " + REWARD;
        cmd += " -penalty " + PENALTY;
        cmd += " -gapopen " + GAP_OPEN;
        cmd += " -gapextend " + GAP_EXTEND;
        cmd += " -query " + querySeqFile;
        cmd += " -db " + blastFilesDir + "/" + REF_SEQS_DB;
        cmd += " -out " + blastFilesDir + "/" + outputFile;
        cmd += " -max_target_seqs " + numTargetSeqs;

        LOGGER.debug("Command:" + cmd);
        System.out.println("Command:" + cmd);
        ExecCommand.run(cmd);
        return 0;
	}




}
