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

package edu.stanford.hivdb.drugresistance.database;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import edu.stanford.hivdb.drugresistance.TestMutationPatternFiles;
import edu.stanford.hivdb.drugresistance.TestMutationPatternFiles.TestMutationPatterns;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.drugs.DrugClass;

public class MutationPatternFileReaderTest {

	@Test
	public void testReadMutationListsForDrugClass() throws FileNotFoundException {
		final InputStream mutsStream =
				TestMutationPatternFiles.getTestMutationPatternsInputStream(TestMutationPatterns.INSTI_PATTERNS);
		final DrugClass drugClass = DrugClass.INSTI;
		final List<MutationSet> mutationLists = MutationPatternFileReader.readMutationListsForDrugClass(drugClass, mutsStream);
		final String eMutSet0 = "[E157Q]";
		final String eMutSet9 = "[E138A, G140S, Q148H]";
		assertEquals(eMutSet0, mutationLists.get(0).toString());
		assertEquals(eMutSet9, mutationLists.get(9).toString());
	}
}
