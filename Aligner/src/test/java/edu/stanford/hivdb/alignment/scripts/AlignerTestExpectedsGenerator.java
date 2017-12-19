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

package edu.stanford.hivdb.alignment.scripts;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import edu.stanford.hivdb.alignment.AlignedGeneSeq;
import edu.stanford.hivdb.alignment.AlignedSequence;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.testutils.TestSequencesFiles;
import edu.stanford.hivdb.testutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.utilities.FastaUtils;
import edu.stanford.hivdb.utilities.Sequence;
import edu.stanford.hivdb.alignment.Aligner;

public class AlignerTestExpectedsGenerator {

	/** Generate expected results for AlignerTest.
	 */
	public static void main (String [] args) throws IOException {
		for (TestSequencesProperties testSequenceProperty : TestSequencesProperties.values()) {
			if (!testSequenceProperty.forRoutineTesting) {
				continue;
			}
			//if (!testSequenceProperty.equals(TestSequencesProperties.RT_INSERTIONS_69)) {
			//	continue;
			//}

			final InputStream testSequenceInputStream = TestSequencesFiles.getTestSequenceInputStream(testSequenceProperty);
			System.out.println("In AlignedGeneSeqToJson:" + testSequenceProperty.toString());
			final List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);

			List<AlignedSequence> alignedSeqs = Aligner.parallelAlign(sequences);
			for (AlignedSequence alignedSeq : alignedSeqs) {
				Sequence seq = alignedSeq.getInputSequence();
				System.out.println("In AlignedGeneSeqToJson:" + seq.getHeader());
				Map<Gene, AlignedGeneSeq> alignmentResults = alignedSeq.getAlignedGeneSequenceMap();
				for (AlignedGeneSeq geneSeq : alignmentResults.values()) {
					geneSeq.getMatchPcnt();  // refresh cache
				}

				final String result = Json.dumps(alignmentResults);
				Path path = Paths.get(
					String.format("src/test/resources/%s_%s.json",
						testSequenceProperty.name(), seq.getHeader()));

				Files.write(path, result.getBytes());
			}
		}
	}
}
