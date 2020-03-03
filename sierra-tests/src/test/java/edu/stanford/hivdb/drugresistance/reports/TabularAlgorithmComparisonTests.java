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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.stanford.hivdb.drugresistance.algorithm.DrugResistanceAlgorithm;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.NucAminoAligner;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.testutils.TestSequencesFiles;
import edu.stanford.hivdb.testutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.utilities.FastaUtils;

public class TabularAlgorithmComparisonTests {

	
	private final static HIV hiv = HIV.getInstance();
	
	@Test
	public void test() throws IOException {
		final InputStream testSequenceInputStream =
				TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.SMALL);
		final List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);
		
		final List<DrugResistanceAlgorithm<HIV>> algorithms = new ArrayList<>();
		
		
		algorithms.add(hiv.getLatestDrugResistAlgorithm("ANRS"));
		algorithms.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));
		algorithms.add(hiv.getLatestDrugResistAlgorithm("Rega"));
		algorithms.add(hiv.getDrugResistAlgorithm("HIVDB_7.0"));
		

		NucAminoAligner<HIV> aligner = NucAminoAligner.getInstance(hiv);
		final List<AlignedSequence<HIV>> alignedSeqs = aligner.parallelAlign(sequences);
		TabularAlgorithmsComparison<HIV> cmp = new TabularAlgorithmsComparison<HIV>(alignedSeqs, algorithms);
		
		
		String result = cmp.toString();
		assertEquals(
			"sequenceName\tgene\tdrugName\tANRS_30.drugLevel\tHIVDB_8.9-1.drugLevel\tRega_9.1.drugLevel\tHIVDB_7.0.drugLevel",
			result.split("\n", 2)[0]
		);
		assertEquals(21, result.split("\n").length);
	}

}
