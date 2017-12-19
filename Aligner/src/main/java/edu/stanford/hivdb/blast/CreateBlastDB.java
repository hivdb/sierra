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

/**
 * Creates a blast database from a file containing fasta sequences.
 * Calls createDB with 3 parameters: (i) the directory containing the fasta file; (ii) the file name;
 *    (iii) and the name used for the 3 blast database files which will end with .db.nsq, .db.nhr, .db.nin
 */
public class CreateBlastDB {
	private static final String BLAST_FILES_DIR = CreateBlastDB.class.getClassLoader().getResource("SubtypeRefs").getPath();
	private static final String REF_SEQS = ProgramProperties.getProperty("SubtypeRefsTextFile");
	private static final String REF_SEQS_DB = ProgramProperties.getProperty("SubtypeRefsDB");
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Calls the method createDB which creates a blast database from a file containing fasta sequences.
	 * Calls createDB with 3 parameters: (i) the directory containing the fasta file; (ii) the file name;
	 *    (iii) and the name used for the 3 blast database files which will end with .db.nsq, .db.nhr, .db.nin
	 */
	public static void main(String[] args) {
		String blastDB = createDB(BLAST_FILES_DIR, REF_SEQS, REF_SEQS_DB);
		System.out.println("blastDB:" + blastDB);
	}


	private static String createDB(String blastDir, String fastaSeqsFileName, String blastDBName) {
		LOGGER.debug("Parameters:\nFile:" + fastaSeqsFileName + "  Dir:" + blastDir + "BlastDBName: " + blastDBName);
		String command = "makeblastdb -in " + blastDir + "/" + fastaSeqsFileName +
						 " -dbtype nucl -out " + blastDir + "/" + blastDBName;
		System.out.println(command);
		LOGGER.debug("Command to create the blast database:\n" + command + "\n");

		int exitValue = ExecCommand.run(command);
		if (exitValue == 0) {
			LOGGER.debug("Command was successful: The following is returned: " + blastDBName);
			return blastDBName;
		} else {
			LOGGER.debug("Command was unsuccessful: exitValue:" + exitValue);
			return null;
		}
	}
}
