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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import edu.stanford.hivdb.alignment.AlignedSequence;
import edu.stanford.hivdb.alignment.Aligner;
import edu.stanford.hivdb.drugresistance.algorithm.Algorithm;
import edu.stanford.hivdb.drugresistance.database.HivdbVersion;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.utilities.FastaUtils;
import edu.stanford.hivdb.utilities.Sequence;

public class TabularAlgorithmComparisonTests {

	@Test
	public void test() throws IOException {
		final InputStream testSequenceInputStream =
				TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.SMALL);
		final List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);
		final List<Algorithm> algorithms = new ArrayList<>();
		algorithms.add(Algorithm.ANRS);
		algorithms.add(Algorithm.HIVDB);
		algorithms.add(Algorithm.REGA);
		final Map<String, String> customAlgs = new HashMap<>();
		final InputStream v7 = HivdbVersion.V7_0.getResource();
		customAlgs.put("HIVDB70", IOUtils.toString(v7));

		final List<AlignedSequence> alignedSeqs = Aligner.parallelAlign(sequences);
		TabularAlgorithmsComparison cmp = new TabularAlgorithmsComparison(alignedSeqs, algorithms, customAlgs);
		String result = cmp.toString();
		assertEquals(
			"sequenceName\tgene\tdrugName\tANRS.drugLevel\tHIVDB.drugLevel\tREGA.drugLevel\tHIVDB70.drugLevel",
			result.split("\n", 2)[0]
		);
		assertEquals(201, result.split("\n").length);
	}

}
