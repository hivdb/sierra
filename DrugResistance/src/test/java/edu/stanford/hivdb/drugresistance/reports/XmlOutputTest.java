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

package edu.stanford.hivdb.drugresistance.reports;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.GeneDRFast;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.hivfacts.HIVGene;
import edu.stanford.hivdb.hivfacts.extras.XmlOutput;
import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.NucAminoAligner;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.utilities.MyFileUtils;
import edu.stanford.hivdb.utilities.FastaUtils;

public class XmlOutputTest {
	private List<AlignedSequence> alignedSequences;
	private List<Map<HIVGene, GeneDR>> allResistanceResults;

	@Test
	public void test() {
		final InputStream testSequenceInputStream =
				TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.SMALL);
		final List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);
		runAnalysis(sequences);
		XmlOutput xml = new XmlOutput(alignedSequences, allResistanceResults);
		MyFileUtils.writeFile("Results.xml", xml.toString());
		System.out.println("Finished");
	}

	@Test
	public void testEmptySeq() {
		Sequence seq = new Sequence("empty", "EMPTY");
		runAnalysis(Arrays.asList(new Sequence[] {seq}));
		XmlOutput xml = new XmlOutput(alignedSequences, allResistanceResults);
		assertTrue(xml.toString().matches("^[\\s\\S]+refuse to process[\\s\\S]+$"));
	}

	private void runAnalysis(List<Sequence> sequences) {
		alignedSequences = new ArrayList<>();
		allResistanceResults = new ArrayList<>();
		alignedSequences = NucAminoAligner.parallelAlign(sequences);
		for (AlignedSequence alignedSeq : alignedSequences) {
			Map<HIVGene, GeneDR> resistanceResults =
				GeneDRFast.getResistanceByGeneFromAlignedGeneSeqs(
					alignedSeq.getAlignedGeneSequences()
				);
			allResistanceResults.add(resistanceResults);
		}

	}
}


