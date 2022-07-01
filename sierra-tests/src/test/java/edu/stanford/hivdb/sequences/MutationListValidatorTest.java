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

import java.util.List;
import java.io.InputStream;
import org.junit.Test;
import static org.junit.Assert.*;

import edu.stanford.hivdb.mutations.AAMutation;
import edu.stanford.hivdb.mutations.CodonMutation;
import edu.stanford.hivdb.mutations.MutationFileReader;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.testutils.TestMutationsFiles;
import edu.stanford.hivdb.testutils.TestMutationsFiles.TestMutationsProperties;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.utilities.ValidationLevel;
import edu.stanford.hivdb.utilities.ValidationResult;

public class MutationListValidatorTest {

	final static HIV hiv = HIV.getInstance();
	final static List<String> includeGenes = List.of("PR", "RT", "IN");

	private void assertValidationResult(
			AAMutation<HIV>[] mutations, ValidationLevel[] levels, String[] messages) {
		MutationSet<HIV> mutSet = new MutationSet<HIV>(mutations);
		
		if (levels.length == 0) {
			@SuppressWarnings("unused")
			List<ValidationResult> results = hiv.validateMutations(mutSet, includeGenes);	
//			System.out.println(results.get(0).getMessage());
			}
		else {
			List<ValidationResult> results = hiv.validateMutations(mutSet, includeGenes);	
			assertEquals(levels.length, results.size());
			for (int i=0; i < levels.length; i ++) {
				assertEquals(levels[i], results.get(i).getLevel());
				assertEquals(messages[i], results.get(i).getMessage());
			}
		}
	}

	/* private void assertValidationResult(AAMutation<HIV>[] mutations) {
		assertValidationResult(
			mutations, new ValidationLevel[] {}, new String[] {});
	} */


	@Test
	public void testMutationsFromFile() {
		final InputStream testMutationsInputStream =
				TestMutationsFiles.getTestMutationsInputStream(TestMutationsProperties.VALIDATION_TEST);
		final List<MutationSet<HIV>> mutationSets = MutationFileReader.readMutationLists(testMutationsInputStream, hiv);
		for (MutationSet<HIV> mutSet : mutationSets) {
			@SuppressWarnings("unused")
			List<ValidationResult> results = hiv.validateMutations(mutSet, includeGenes);
//			for (ValidationResult result : results) {
//				System.out.println(" Level:" + result.getLevel());
//				System.out.println(" Message:" + result.getMessage());
//			}
		}

	}


	@SuppressWarnings("unchecked")
	@Test
	public void testMutationsWithTooManyStopCodons() {

		assertValidationResult(
			/* mutations as the input for MutationListValidator */
			new AAMutation[] {
				new CodonMutation<HIV>(hiv.getGene("HIV1RT"), 122, "*"),
				new CodonMutation<HIV>(hiv.getGene("HIV1IN"), 23, "*"),
			},
			/* expected result level(s) */
			new ValidationLevel[] {
				ValidationLevel.WARNING,
				ValidationLevel.WARNING
			},
			/* expected result message(s) */
			new String[] {
				"There is one stop codon in RT: RT:K122*.",
				"There is one stop codon in IN: IN:A23*."
			});

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMutationsWithStopCodon() {

		assertValidationResult(
			/* mutations as the input for MutationListValidator */
			new AAMutation[] {
				new CodonMutation<HIV>(hiv.getGene("HIV1RT"), 43, "*"),
			},
			/* expected result level(s) */
			new ValidationLevel[] {
				ValidationLevel.WARNING,
			},
			/* expected result message(s) */
			new String[] {
				"There is one stop codon in RT: RT:K43*.",
			});

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testMutationsAllAtDRM() {
		MutationSet<HIV> mutset =  new MutationSet<HIV>(new AAMutation[] {
				new CodonMutation<HIV>(hiv.getGene("HIV1PR"), 54, "V"),
			});
		/* mutations as the input for MutationListValidator */
		List<ValidationResult> results = hiv.validateMutations(mutset, includeGenes);
		assertEquals(0, results.size());
		/* the expected result should be empty */
	}

}
