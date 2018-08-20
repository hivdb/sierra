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

package edu.stanford.hivdb.mutations;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import edu.stanford.hivdb.testutils.TestMutationsFiles;
import edu.stanford.hivdb.testutils.TestMutationsFiles.TestMutationsProperties;
import edu.stanford.hivdb.utilities.MutationFileReader;

public class ApobecTest {
	@Test
	public void testCheckSequence() {
		final InputStream testMutationsInputStream =
				TestMutationsFiles.getTestMutationsInputStream(TestMutationsProperties.APOBEC_TEST);
		final List<MutationSet> mutationLists = MutationFileReader.readMutationLists(testMutationsInputStream);
		final Apobec a = new Apobec(mutationLists.get(0));
		// Let's confirm what we got back.
		// First, we expect three total Apobec muts.
		final MutationSet eMuts =
			new MutationSet("PR:48RE PR:52R RT:41I RT:190R");
		final MutationSet muts = a.getApobecMuts();
		assertEquals("Apobec mutations not as expected.", eMuts, muts);
		// Now let's check the DRMs.
		final MutationSet eDRMs =
			new MutationSet("PR:73S RT:67N RT:184I");
		final MutationSet drms = a.getApobecDRMs();
		assertEquals("DRMs not as expected.", eDRMs, drms);
		//Console dump if you want to see it.
	}

	@Test
	public void testRT239Stop() {
		MutationSet expected = new MutationSet("RT239*");
		final Apobec a = new Apobec(expected);
		assertEquals(expected, a.getApobecMuts());
		assertTrue(new Mutation(Gene.RT, 239, "*").isApobecMutation());
	}
}
