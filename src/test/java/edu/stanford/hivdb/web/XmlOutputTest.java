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

package edu.stanford.hivdb.web;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.Aligner;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.testutils.TestSequencesFiles;
import edu.stanford.hivdb.testutils.TestUtils;
import edu.stanford.hivdb.testutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.utilities.FastaUtils;

public class XmlOutputTest {
	private List<AlignedSequence<HIV>> alignedSequences;
	private List<Map<Gene<HIV>, GeneDR<HIV>>> allResistanceResults;

	private static final HIV hiv = HIV.getInstance();

	@Test
	public void test() {
		final InputStream testSequenceInputStream =
				TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.SMALL);
		final List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);
		runAnalysis(sequences);
		XmlOutput<HIV> xml = new XmlOutput<HIV>(hiv, alignedSequences, allResistanceResults);
		TestUtils.writeFile("XmlOutputTestResults.xml", xml.toString());
		System.out.println("Finished");
	}

	@Test
	public void testEmptySeq() {
		Sequence seq = new Sequence("empty", "EMPTY");
		runAnalysis(Arrays.asList(new Sequence[] {seq}));
		XmlOutput<HIV> xml = new XmlOutput<HIV>(hiv, alignedSequences, allResistanceResults);
		assertTrue(xml.toString().matches("^[\\s\\S]+refuse to process[\\s\\S]+$"));
	}

	private void runAnalysis(List<Sequence> sequences) {
		alignedSequences = new ArrayList<>();
		allResistanceResults = new ArrayList<>();
		Aligner<HIV> aligner = Aligner.getInstance(hiv);
		alignedSequences = aligner.parallelAlign(sequences);
		for (AlignedSequence<HIV> alignedSeq : alignedSequences) {
			Map<Gene<HIV>, GeneDR<HIV>> resistanceResults =
				GeneDR.newFromAlignedGeneSeqs(
					alignedSeq.getAlignedGeneSequences(), hiv.getLatestDrugResistAlgorithm("HIVDB")
				);
			allResistanceResults.add(resistanceResults);
		}

	}
}


