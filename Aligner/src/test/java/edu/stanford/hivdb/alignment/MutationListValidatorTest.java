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

import java.util.List;
import java.io.InputStream;
import org.junit.Test;
import static org.junit.Assert.*;

import edu.stanford.hivdb.filetestutils.TestMutationsFiles;
import edu.stanford.hivdb.filetestutils.TestMutationsFiles.TestMutationsProperties;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.IUPACMutation;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.MutationFileReader;
import edu.stanford.hivdb.utilities.ValidationLevel;
import edu.stanford.hivdb.utilities.ValidationResult;

public class MutationListValidatorTest {

	private void assertValidationResult(
			Mutation[] mutations, ValidationLevel[] levels, String[] messages) {
		MutationSet mutSet = new MutationSet(mutations);
		MutationListValidator validator = new MutationListValidator(mutSet);
		if (levels.length == 0) {
			try {
				assertTrue(validator.validate());
			} catch (AssertionError e) {
				List<ValidationResult> results = validator.getValidationResults();
				System.out.println(results.get(0).getMessage());
				throw e;
			}
		}
		else {
			assertFalse(validator.validate());
			List<ValidationResult> results = validator.getValidationResults();
			assertEquals(levels.length, results.size());
			for (int i=0; i < levels.length; i ++) {
				assertEquals(levels[i], results.get(i).getLevel());
				assertEquals(messages[i], results.get(i).getMessage());
			}
		}
	}

	private void assertValidationResult(Mutation[] mutations) {
		assertValidationResult(
			mutations, new ValidationLevel[] {}, new String[] {});
	}


	@Test
	public void testMutationsFromFile() {
		final InputStream testMutationsInputStream =
				TestMutationsFiles.getTestMutationsInputStream(TestMutationsProperties.VALIDATION_TEST);
		final List<MutationSet> mutationSets = MutationFileReader.readMutationLists(testMutationsInputStream);
		for (MutationSet mutSet : mutationSets) {
			MutationListValidator validator = new MutationListValidator(mutSet);
			validator.validate();
			List<ValidationResult> results = validator.getValidationResults();
			for (ValidationResult result : results) {
				System.out.println(" Level:" + result.getLevel());
				System.out.println(" Message:" + result.getMessage());
			}
		}

	}


	@Test
	public void testMutationsWithTooManyStopCodons() {

		assertValidationResult(
			/* mutations as the input for MutationListValidator */
			new Mutation[] {
				new IUPACMutation(Gene.valueOf("HIV1RT"), 122, "*"),
				new IUPACMutation(Gene.valueOf("HIV1IN"), 23, "*"),
			},
			/* expected result level(s) */
			new ValidationLevel[] {
				ValidationLevel.SEVERE_WARNING,
				ValidationLevel.WARNING,
			},
			/* expected result message(s) */
			new String[] {
				"The submitted mutations contain 2 stop codons.",
				"There are 2 unusual mutations: RT_K122*, IN_A23*."
			});

	}

	@Test
	public void testMutationsWithStopCodon() {

		assertValidationResult(
			/* mutations as the input for MutationListValidator */
			new Mutation[] {
				new IUPACMutation(Gene.valueOf("HIV1RT"), 43, "*"),
			},
			/* expected result level(s) */
			new ValidationLevel[] {
				ValidationLevel.WARNING,
			},
			/* expected result message(s) */
			new String[] {
				"The submitted mutations contain 1 stop codon.",
			});

	}

	@Test
	public void testMutationsAllAtDRM() {
		assertValidationResult(
			/* mutations as the input for MutationListValidator */
			new Mutation[] {
				new IUPACMutation(Gene.valueOf("HIV1PR"), 54, "V"),
			}
			/* the expected result should be empty */
		);
	}

}
