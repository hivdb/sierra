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

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
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

import edu.stanford.hivdb.alignment.AlignedGeneSeq;
import edu.stanford.hivdb.mutations.FrameShift;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.utilities.FastaUtils;
import edu.stanford.hivdb.utilities.Sequence;

public class AlignerTest {
	private static final Logger LOGGER = LogManager.getLogger();

	@Test
	public void test() throws FileNotFoundException, IOException {

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


			Map<Sequence, AlignedSequence> allAligneds = (
				Aligner.parallelAlign(sequences)
				.stream()
				.collect(Collectors.toMap(as -> as.getInputSequence(), as -> as))
			);

			Type mapType = new TypeToken<Map<Gene, AlignedGeneSeq>>() {}.getType();

			for (Sequence seq : sequences) {
				LOGGER.debug("\nSequence:"  + seq.getHeader());
				AlignedSequence alignedSeq = allAligneds.get(seq);
				List<AlignedGeneSeq> alignmentResults = alignedSeq.getAlignedGeneSequences();
				final InputStream alignedGeneSeqJsonInputStream =
					AlignerTest.class.getClassLoader().getResourceAsStream(testSequenceProperty.name() +
							"_" + seq.getHeader() + ".json");

				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(alignedGeneSeqJsonInputStream));
				Map<Gene, AlignedGeneSeq> alignedGeneSeqsExpected = Json.loads(bufferedReader, mapType);

				// We verify that the number of genes in the alignment results matches the number
				// of genes in the expected results.
				//System.out.println("NumAlignedGenesExpected:" + alignedGeneSeqsExpected.size());
				//System.out.println(  "NumAlignedGenesActual:" + alignmentResults.size());
				Assert.assertEquals(alignedGeneSeqsExpected.size(), alignmentResults.size());

				for (AlignedGeneSeq alignedGeneSeq : alignmentResults) {
					final Gene gene = alignedGeneSeq.getGene();
					final MutationSet mutations = alignedGeneSeq.getMutations();
					final MutationSet expectedMutations = alignedGeneSeqsExpected.get(gene).getMutations();
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

					final List<FrameShift> frameShifts = alignedGeneSeq.getFrameShifts();
					final List<FrameShift> expectedFrameShifts = alignedGeneSeqsExpected.get(gene).getFrameShifts();
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


