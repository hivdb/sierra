/*

    Copyright (C) 2024 Stanford HIVDB team

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

package edu.stanford.hivdb.drugresistance.mutationpattern;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;


import edu.stanford.hivdb.drugresistance.algorithm.DrugResistanceAlgorithm;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.testutils.TestUtils;

public class MutationPatternsResistanceJsonComparisonTest {

	private static final String INPUT_DIR = "patterns-hiv1/";
	private static final HIV hiv = HIV.getInstance();
	
	@Test
	public void testDetermineResistanceForMutList() throws IOException {
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDefaultDrugResistAlgorithm();
		for (DrugClass<HIV> drugClass : hiv.getMainStrain().getDrugClasses()) {
			String filePath = INPUT_DIR + "patterns-" + drugClass + ".json";
			String expected = TestUtils.readTestResourceToString(filePath);
			
			MutationPatterns mutPatterns = new MutationPatterns(drugClass);
			String actual = mutPatterns.dumps(algorithm);
			
			assertEquals(expected, actual);
		}
	}
}
