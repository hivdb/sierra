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

package edu.stanford.hivdb.drugresistance.scripts;

import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.stanford.hivdb.drugresistance.algorithm.Algorithm;
import edu.stanford.hivdb.drugresistance.algorithm.AlgorithmComparison;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.hivfacts.HIVGene;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.NucAminoAligner;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.utilities.FastaUtils;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.utilities.MyFileUtils;

public class AlgorithmComparisonTestExpectedsGenerator {

	public static void main(String[] args) {

		Map<String, Map<String, AlgorithmComparison>> r = new LinkedHashMap<>();
		for (TestSequencesProperties property : TestSequencesProperties.values()) {
			if (!property.forRoutineTesting) {
				continue;
			}
			System.out.println(String.format("Processing %s...", property));
			final InputStream testSequenceInputStream =
					TestSequencesFiles.getTestSequenceInputStream(property);
			final List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);

			List<AlignedSequence> allAligneds =
					NucAminoAligner.parallelAlign(sequences);

			for (AlignedSequence alignedSeq : allAligneds) {
				Sequence sequence = alignedSeq.getInputSequence();
				Map<HIVGene, MutationSet> mutationSets = alignedSeq.getMutations().groupByGene();
				AlgorithmComparison algorithmComparison =
					new AlgorithmComparison(mutationSets, Arrays.asList(Algorithm.values()));
				r.putIfAbsent(property.toString(), new LinkedHashMap<>());
				r.get(property.toString()).put(sequence.getHeader() + "-" + sequence.getSHA512(), algorithmComparison);
			}
		}
		final String result = Json.dumps(r);

		String fileName = "src/test/resources/AlgorithmComparisonTestExpecteds.json";
		System.out.println(String.format("Write to file %s. Done.", fileName));
		MyFileUtils.writeFile(fileName, result);

	}

}
