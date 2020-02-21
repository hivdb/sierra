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

package edu.stanford.hivdb.sequences;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import edu.stanford.hivdb.filetestutils.TestSequencesFiles;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.FrameShift;
import edu.stanford.hivdb.sequences.AlignedGeneSeq;
import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.NucAminoAligner;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.testutils.TestUtils;
import edu.stanford.hivdb.utilities.PrettyPairwise;
import edu.stanford.hivdb.utilities.FastaUtils;

public class PrettyPairwiseTest {
	final String OUTPUT_FILE = "PrettyPairwise.txt";

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();

	private static final HIV hiv = HIV.getInstance();

	@Test
	public void test() {
		final InputStream testSequenceInputStream =
				TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.RT_DELETIONS_67);
		final List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);

		StringBuilder output = new StringBuilder();
		for (Sequence seq : sequences) {
			AlignedSequence<HIV> alignedSeq = NucAminoAligner.getInstance(hiv).align(seq);
			for (AlignedGeneSeq<HIV> alignedGeneSeq : alignedSeq.getAlignedGeneSequences() ) {
				PrettyPairwise<HIV> prettyPairwise = alignedGeneSeq.getPrettyPairwise();
				List<String> positionHeader = prettyPairwise.getPositionLine();
				List<String> referenceAAs = prettyPairwise.getRefAALine();
				List<String> alignedNAs = prettyPairwise.getAlignedNAsLine();
				List<String> mutationLine = prettyPairwise.getMutationLine();
				output.append(seq.getHeader() + "\n");
				output.append(alignedGeneSeq.getMutationListString() + "\n");
				for (FrameShift<HIV> fs: alignedGeneSeq.getFrameShifts()) {
					output.append(fs.getHumanFormat() + "\n");
				}
				output.append(joinCodonsWithSpaces(positionHeader, 2) + "\n");
				output.append(joinCodonsWithSpaces(referenceAAs, 2) + "\n");
				output.append(joinCodonsWithSpaces(alignedNAs, 2) + "\n");
				output.append(joinCodonsWithSpaces(mutationLine, 2) + "\n");
				output.append("\n\n");
			}
		}
		TestUtils.writeFile(OUTPUT_FILE, output.toString());
	}

	public String joinCodonsWithSpaces (List<String> positions, int numSpaces) {
		StringBuilder outputLine = new StringBuilder();
		String spaces = StringUtils.repeat(" ", numSpaces);
		for (int i=0; i<positions.size(); i++) {
			outputLine.append(positions.get(i));
			outputLine.append(spaces);
		}
		return outputLine.toString();
	}


}
