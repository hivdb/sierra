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

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.google.gson.reflect.TypeToken;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.hivfacts.hiv2.HIV2;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.mutations.FrameShift;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.testutils.TestSequencesFiles;
import edu.stanford.hivdb.testutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.utilities.FastaUtils;

public class AlignerTest {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final HIV hiv = HIV.getInstance();
	private static final HIV2 hiv2 = HIV2.getInstance();
	
	@Test
	public void testHIV2Alignment1() {
		Sequence seq = Sequence.fromGenbank("FJ442006");
		PostAlignAligner<HIV2> aligner = Aligner.getInstance(hiv2);
		AlignedSequence<HIV2> alignedSeq = aligner.align(seq);
		assertEquals(List.of(hiv2.getGene("HIV2BIN")), alignedSeq.getAvailableGenes());
		AlignedGeneSeq<HIV2> geneSeq = alignedSeq.getAlignedGeneSequence("IN");
		assertEquals(1, geneSeq.getFirstAA());
		assertEquals(296, geneSeq.getLastAA());
	}

	@Test
	public void testHIV2Alignment2() {
		Sequence seq = Sequence.fromGenbank("KJ131139");
		PostAlignAligner<HIV2> aligner = Aligner.getInstance(hiv2);
		AlignedSequence<HIV2> alignedSeq = aligner.align(seq);
		assertEquals(List.of(hiv2.getGene("HIV2APR")), alignedSeq.getAvailableGenes());
		AlignedGeneSeq<HIV2> geneSeq = alignedSeq.getAlignedGeneSequence("PR");
		assertEquals(1, geneSeq.getFirstAA());
		// The last codon TTT is too far from ref CTA
		assertEquals(99, geneSeq.getLastAA());
	}

//	@Test
	public void test() {

		for (TestSequencesProperties testSequenceProperty : TestSequencesProperties.values()) {
			if (!testSequenceProperty.forRoutineTesting) {
				continue;
			}
			// System.out.println(testSequenceProperty.propertyName);
			// if (!testSequenceProperty.equals(TestSequencesProperties.PROBLEM_SEQUENCES)) {
			// 	continue;
			// }
			LOGGER.debug("testSequenceProperty:" + testSequenceProperty);
			final InputStream testSequenceInputStream =
					TestSequencesFiles.getTestSequenceInputStream(testSequenceProperty);
			final List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);

			PostAlignAligner<HIV> aligner = Aligner.getInstance(hiv);
			Map<Sequence, AlignedSequence<HIV>> allAligneds = 
				aligner.parallelAlign(sequences)
				.stream()
				.collect(Collectors.toMap(as -> as.getInputSequence(), as -> as));

			Type mapType = new TypeToken<Map<Gene<HIV>, AlignedGeneSeq<HIV>>>() {}.getType();

			for (Sequence seq : sequences) {
				LOGGER.debug("\nSequence:"  + seq.getHeader());
				AlignedSequence<HIV> alignedSeq = allAligneds.get(seq);
				List<AlignedGeneSeq<HIV>> alignmentResults = alignedSeq.getAlignedGeneSequences();
				final InputStream alignedGeneSeqJsonInputStream =
					AlignerTest.class.getClassLoader().getResourceAsStream(testSequenceProperty.name() +
							"_" + seq.getHeader() + ".json");

				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(alignedGeneSeqJsonInputStream));
				Map<Gene<HIV>, AlignedGeneSeq<HIV>> alignedGeneSeqsExpected = Json.loads(bufferedReader, mapType);

				// We verify that the number of genes in the alignment results matches the number
				// of genes in the expected results.
				//System.out.println("NumAlignedGenesExpected:" + alignedGeneSeqsExpected.size());
				//System.out.println(  "NumAlignedGenesActual:" + alignmentResults.size());
				Assert.assertEquals(alignedGeneSeqsExpected.size(), alignmentResults.size());

				for (AlignedGeneSeq<HIV> alignedGeneSeq : alignmentResults) {
					final Gene<HIV> gene = alignedGeneSeq.getGene();
					final MutationSet<HIV> mutations = alignedGeneSeq.getMutations();
					final MutationSet<HIV> expectedMutations = alignedGeneSeqsExpected.get(gene).getMutations();
					final String name = alignedGeneSeq.getSequence().getHeader();
					final String msg = String.format("%s mismatched:", name);

					/*System.out.println(alignedGeneSeq.getOriginalAlignedNAs());
					System.out.println(alignedGeneSeq.getOriginalControlLine());
					System.out.println(alignedGeneSeq.getOriginalAATripletLine());
					for (Mutation mut: expectedMutations) {
						System.out.print(mut.toString() + " ");
					}
					System.out.println();
					for (Mutation mut: mutations) {
						System.out.print(mut.toString() + " ");
					}
					System.out.println();*/
					assertEquals(msg, expectedMutations, mutations);

					assertEquals(msg, mutations, expectedMutations);

					final int firstAA = alignedGeneSeq.getFirstAA();
					final int lastAA = alignedGeneSeq.getLastAA();
					final int expectedFirstAA = alignedGeneSeqsExpected.get(gene).getFirstAA();
					final int expectedLastAA = alignedGeneSeqsExpected.get(gene).getLastAA();
					assertEquals(msg, expectedFirstAA, firstAA);
					assertEquals(msg, expectedLastAA, lastAA);

					final String alignedNAs = alignedGeneSeq.getAlignedNAs();
					final String expectedAlignedNAs = alignedGeneSeqsExpected.get(gene).getAlignedNAs();
					//System.out.println("Expected:" + expectedAlignedNAs);
					//System.out.println("  Acutal:" + alignedNAs);
					assertEquals(msg, expectedAlignedNAs, alignedNAs);

					final List<FrameShift<HIV>> frameShifts = alignedGeneSeq.getFrameShifts();
					final List<FrameShift<HIV>> expectedFrameShifts = alignedGeneSeqsExpected.get(gene).getFrameShifts();
					/*for (FrameShift fs: expectedFrameShifts) {
						System.out.print(fs.toString());
					}
					System.out.println();
					for (FrameShift fs: frameShifts) {
						System.out.print(fs.toString());
					}
					System.out.println();*/
					assertEquals(msg, expectedFrameShifts, frameShifts);
				}
			}
		}


	}
}


