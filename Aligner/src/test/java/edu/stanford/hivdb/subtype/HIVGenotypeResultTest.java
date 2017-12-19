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

package edu.stanford.hivdb.subtype;

import java.io.InputStream;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import edu.stanford.hivdb.alignment.AlignedSequence;
import edu.stanford.hivdb.alignment.Aligner;
import edu.stanford.hivdb.genotyper.BoundGenotype;
import edu.stanford.hivdb.genotyper.HIVGenotypeResult;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.mutations.Sdrms;
import edu.stanford.hivdb.subtype.Subtype;
import edu.stanford.hivdb.testutils.TestSequencesFiles;
import edu.stanford.hivdb.testutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.utilities.MyFileUtils;
// import edu.stanford.hivdb.utilities.NumberFormats;
import edu.stanford.hivdb.utilities.FastaUtils;
import edu.stanford.hivdb.utilities.Sequence;

public class HIVGenotypeResultTest {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final String filePath = "SubtypeResults.txt";

	@Test
	public void test() {
		final InputStream testSequenceInputStream =
				TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.SUBTYPE_TESTS_ALL);
		List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);
		StringBuffer output = new StringBuffer();
		sequences = sequences.subList(0, 1000);

		List<AlignedSequence> allAligneds = Aligner.parallelAlign(sequences);

		for (AlignedSequence alignedSeq : allAligneds) {

			// Example header: >B|107258|PR_RT|2253|3555|EU255372|Spain|2005|Holguin08|18691031|7|0
			// Subtype|PID|Genes|firstNA|lastNA|GB|Country|Year|AuthorYr|PMID|NumMix|NumDRM
			// System.out.println("SequenceNumber:" + i + "  " + sequence.getHeader());

			Sequence sequence = alignedSeq.getInputSequence();
			String[] headerFields = StringUtils.split(sequence.getHeader(), "|");
			Subtype knownSubtype = Subtype.valueOf(headerFields[0]);
			int firstNAFromHeader = Integer.parseInt(headerFields[3]);
			LOGGER.debug("Sequence header:" + sequence.getHeader());
			LOGGER.debug("From header: Subtype:" + knownSubtype + " FirstNA:" + firstNAFromHeader);

			String completeSequence = alignedSeq.getConcatenatedSeq();
			int firstNA = alignedSeq.getAbsoluteFirstNA();
			MutationSet seqMutations = alignedSeq.getMutations();
			MutationSet sdrms = Sdrms.getSdrms(seqMutations);
			LOGGER.debug("Genes:" + alignedSeq.getAvailableGenes() + " firstNA:" + firstNA);
			LOGGER.debug("completeSequence:" + completeSequence);
			LOGGER.debug("SDRMs:" + sdrms.join());

			HIVGenotypeResult genotypeResult = alignedSeq.getSubtypeResult();

			BoundGenotype bestMatch = genotypeResult.getBestMatch();
			//Subtype closestSubtype = closestSubtypes.get(0);
			//Double closestDistance = closestDistances.get(0);

			if (Subtype.valueOf(bestMatch) != knownSubtype) {
				output.append("Conflict" + "\t");
				output.append("Subtype:" + bestMatch.getDisplay() + "\t");
				output.append("SubtypeInDB:" + knownSubtype + "\t" +  sequence.getHeader());
				output.append("\n");
			} else {
				//output.append("OK" + "\t");
			}


		}

		MyFileUtils.writeFile(filePath, output.toString());

	}

	/*private static String printOutSubypeResults(Map<Integer, Map<Subtype, Map<String, Double>>> subtypeResults) {
		StringBuffer output = new StringBuffer();
		for (int i=1; i<= NUM_MATCHES_TO_SHOW; i++) {
			for (Subtype subtype : subtypeResults.get(i).keySet()) {
				for (String gb : subtypeResults.get(i).get(subtype).keySet()) {
					Double distance = subtypeResults.get(i).get(subtype).get(gb);
					output.append(i + "-" + subtype + "-" + gb + "-" + NumberFormats.prettyDecimal(distance) + "\t");
					LOGGER.debug("Rank:" + i + " Subtype:" + subtype + " GenBank:" + gb + " Distance:" + distance);
				}
			}
		}
		return output.toString();

	}*/

}
