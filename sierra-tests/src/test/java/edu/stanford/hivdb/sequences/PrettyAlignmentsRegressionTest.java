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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.testutils.TestSequencesFiles;
import edu.stanford.hivdb.testutils.TestUtils;
import edu.stanford.hivdb.testutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.utilities.FastaUtils;

public class PrettyAlignmentsRegressionTest {

	private final static HIV hiv = HIV.getInstance();

	@Test
	public void test() {
		if (TestUtils.isTravisBuild()) {
			return;
		}

		final InputStream testSequenceInputStream = TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.MALDARELLI2);
		final List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);
		
		Aligner<HIV> aligner = Aligner.getInstance(hiv);
		Map<Sequence, AlignedSequence<HIV>> allAligneds = (
				aligner.parallelAlign(sequences)
			.stream().collect(Collectors.toMap(as -> as.getInputSequence(), as -> as))
		);
		
		for (Gene<HIV> gene : hiv.getGenes(hiv.getStrain("HIV1"))) {
			List<AlignedGeneSeq<HIV>> alignmentResults = new ArrayList<>();

			for (Sequence seq : sequences) {
				AlignedGeneSeq<HIV> alignedGeneSeq = allAligneds.get(seq).getAlignedGeneSequence(gene);
				if (alignedGeneSeq != null) {
					alignmentResults.add(alignedGeneSeq);
				}
			}
			if (alignmentResults.isEmpty()) {
				continue;
			}
			PrettyAlignments<HIV> prettyAlignment = new PrettyAlignments<HIV>(gene, alignmentResults);
			Map<String, Map<Integer, String>> sequenceAllPosAAs = prettyAlignment.getSequenceAllPosAAs();
			printOutAlignment(gene, prettyAlignment, sequenceAllPosAAs);
		}
	}

	private void printOutAlignment(Gene<HIV> gene, PrettyAlignments<HIV> prettyAlignment,
			Map<String, Map<Integer, String>> sequenceAllPosAAs) {
		StringBuilder output = new StringBuilder();
		
		output.append(String.format("%25s\t", "Sequence Names"));
		
		int firstAA = prettyAlignment.getFirstAA();
		int lastAA = prettyAlignment.getLastAA();
		for (int pos=firstAA; pos<=lastAA; pos++) {
			output.append(String.format("%5s\t", Integer.toString(pos)));
		}
		output.append("\n");
		

		for (String seqName : sequenceAllPosAAs.keySet()) {
			
			output.append(String.format("%25s\t", seqName));
			
			for (int pos=firstAA; pos<=lastAA; pos++) {
				output.append(String.format("%5s\t", sequenceAllPosAAs.get(seqName).get(pos)));
			}
			output.append("\n");
		}
		TestUtils.writeFile(gene.getName() + "PrettyAlignmentRegressionTest.txt", output.toString());
	}

}
