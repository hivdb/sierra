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

package edu.stanford.hivdb.alignment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

import edu.stanford.hivdb.filetestutils.TestSequencesFiles;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.utilities.MyFileUtils;
import edu.stanford.hivdb.utilities.FastaUtils;
import edu.stanford.hivdb.utilities.Sequence;

public class PrettyAlignmentsTest {

	@Test
	public void test() {
		final boolean isTravisBuild = System.getenv().getOrDefault("TRAVIS", "false").equals("true");
		if (isTravisBuild) {
			return;
		}
		final InputStream testSequenceInputStream = TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.MALDARELLI2);
		final List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);
		Map <Sequence, Map<Gene, AlignedGeneSeq>> allAligneds =
				Aligner.parallelAlign(sequences).stream()
				.collect(Collectors.toMap(
					as -> as.getInputSequence(),
					as -> as.getAlignedGeneSequenceMap()
				));
		for (Gene gene : Gene.values()) {
			List<AlignedGeneSeq> alignmentResults = new ArrayList<>();

			for (Sequence seq : sequences) {
				AlignedGeneSeq alignedGeneSeq = allAligneds.get(seq).get(gene);
				if (alignedGeneSeq != null) {
					alignmentResults.add(alignedGeneSeq);
				}
			}
			if (alignmentResults.isEmpty()) {
				continue;
			}
			PrettyAlignments prettyAlignment = new PrettyAlignments(gene, alignmentResults);
			Map<String, Map<Integer, String>> sequenceAllPosAAs = prettyAlignment.getSequenceAllPosAAs();
			printOutAlignment(gene, prettyAlignment, sequenceAllPosAAs);
		}
	}

	private void printOutAlignment(Gene gene, PrettyAlignments prettyAlignment,
			Map<String, Map<Integer, String>> sequenceAllPosAAs) {
		StringBuilder output = new StringBuilder();
		String header = String.format("%25s\t", "Sequence Names");
		int firstAA = prettyAlignment.getFirstAA();
		int lastAA = prettyAlignment.getLastAA();
		for (int pos=firstAA; pos<=lastAA; pos++) {
			header += String.format("%5s\t", Integer.toString(pos));
		}
		output.append(header + "\n");
		//List<String> rows = new ArrayList<>();
		for (String seqName : sequenceAllPosAAs.keySet()) {
			String row = String.format("%25s\t", seqName);
			for (int pos=firstAA; pos<=lastAA; pos++) {
				row += String.format("%5s\t", sequenceAllPosAAs.get(seqName).get(pos));
			}
			output.append(row + "\n");
		}
		MyFileUtils.writeFile(gene.toString() + "PrettyAlignmentTestOutput.txt", output.toString());
	}

}
