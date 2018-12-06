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

package edu.stanford.hivdb.utilities;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import edu.stanford.hivdb.filetestutils.TestMutationsFiles;
import edu.stanford.hivdb.filetestutils.TestMutationsFiles.TestMutationsProperties;
import edu.stanford.hivdb.mutations.MutationSet;

public class MutationFileReaderTest {

	@Test
	public void testReadMutationLists() {
		final InputStream mutsStream =
				TestMutationsFiles.getTestMutationsInputStream(TestMutationsProperties.SIMPLE_MUTATIONS_TEST);
		final List<MutationSet> mutationLists = MutationFileReader.readMutationLists(mutsStream);
		final String eMutSet0 = "[M36I, I54V, V82F, M41L, K103N, M184V, T215Y, Q148H]";
		final String eMutSet5 = "[M36I, V82F, M41L, K103N, M184V, T215SY, Q148H]";
		final String mutSet0 = mutationLists.get(0).toString();
		final String mutSet5 = mutationLists.get(5).toString();
		assertEquals(6, mutationLists.size());
		assertEquals(eMutSet0, mutSet0);
		assertEquals(eMutSet5, mutSet5);
	}
	
//	void printMutationListsFromFile() {
//		final InputStream testMutationsInputStream = TestMutationsFiles.getTestMutationsInputStream(TestMutationsProperties.SIMPLE_MUTATIONS_TEST);
//		final List<MutationSet> mutationLists = MutationFileReader.readMutationLists(testMutationsInputStream);
//		
//		int numMutLists = mutationLists.size();
//		System.out.println("The file " + TestMutationsProperties.SIMPLE_MUTATIONS_TEST +
//				" contained " + numMutLists + " mutation lists.");
//		for (MutationSet mutList : mutationLists) {
//			System.out.println();
//			for (Gene gene : Gene.values()) {
//				System.out.printf("%s:", gene);
//				MutationSet geneMutList = mutList.getGeneMutations(gene);
//				if (geneMutList.size() > 0) {
//					System.out.println(geneMutList.join());
//				}
//			}
//		}
//	}
}