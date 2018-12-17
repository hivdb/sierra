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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.stanford.hivdb.mutations.MutationPrevalences.MutationPrevalence;

public class MutationPrevalencesTest {

		final private List<String> eTypes = Arrays.asList("A", "B", "C", "D", "F", "G", "CRF01_AE", "CRF02_AG", "Other", "All");

		// MutationPrevalences calls populateMutationPrevalenceStore
		// in its static initializer. So calling it here is redundant.
		// However, doing so increases code coverage metrics.
		@BeforeClass
		public static void manualInit() throws IOException {
			MutationPrevalences.populateMutationPrevalenceStore();
		}

		@Test
		public void testTypeInit() {
			assertEquals(eTypes, MutationPrevalences.getAllTypes());
		}

		@Test
		public void testGeneInit() {
			List<Gene> eGenes = Arrays.asList(Gene.RT, Gene.IN, Gene.PR);
			List<Gene> genes = new ArrayList<>(MutationPrevalences.getNumPatients().keySet());
			assertTrue(CollectionUtils.isEqualCollection(eGenes, genes));
		}

		@Test
		public void testPatientsInit() {
			MutationPrevalences.getNumPatients().values().forEach(typeMap -> {
				List<String> types = new ArrayList<>(typeMap.keySet());
				assertTrue(CollectionUtils.isEqualCollection(eTypes, types));
			});
		}

		@Test
		public void testsMutationPrevalanceConstruction() {
			Mutation m = new Mutation(Gene.RT, 554, "S");
			MutationPrevalence mpFull = new MutationPrevalence(m, "Other", 736, 547, 74.3, 2, 0, 0.0);
			MutationPrevalence mpBrief = new MutationPrevalence(m, "Other", 736, 2);
			assertEquals("A554S Other 736 2 547 0 74.300000 0.000000", mpFull.toString());
			assertEquals("A554S Other 736 2 0 0 0.000000 0.000000", mpBrief.toString());
		}

		@Test
		public void testPrevalanceMatches() {
			// Since we update prevalence data periodically, we
			// expects the following assertions to ultimately fail.
			// Hence we must manually update these assertions every time
			// we upload new prevalence data.

			/* 1st mutation in the INI file with 2 different subtypes */
			Mutation m = new Mutation(Gene.IN, 1, "S");
			checkNullPrevalence(MutationPrevalences.getPrevalenceAtSamePosition(m), "S", "A");
			checkNullPrevalence(MutationPrevalences.getPrevalenceAtSamePosition(m), "S", "B");

			/* mutation towards the end of INI file */
			m = new Mutation(Gene.IN, 286, "N");
			checkPrevalence(MutationPrevalences.getPrevalenceAtSamePosition(m), "N", "CRF01_AE", 1818, 66, 3.6, 1, 0, 0);

			/* mutations in the middle of RTI file*/
			m = new Mutation(Gene.RT, 553, "I");
			checkNullPrevalence(MutationPrevalences.getPrevalenceAtSamePosition(m), "I", "G");
			m = new Mutation(Gene.RT, 554, "S");
			checkPrevalence(MutationPrevalences.getPrevalenceAtSamePosition(m), "S", "Other", 736, 547, 74.3, 2, 0, 0);

			/* mutation in the middle of PI file */
			m = new Mutation(Gene.PR, 72, "T");
			checkPrevalence(MutationPrevalences.getPrevalenceAtSamePosition(m), "T", "All", 98144, 3731, 3.8, 26381, 2105, 8.0);
		}

		@Test
		public void testGroupPrevalenceByPos() {
			MutationSet muts = new MutationSet("IN1S IN1S IN286N PR72T RT554S IN1S");
			Map<Mutation, List<MutationPrevalence>> mpByPos = MutationPrevalences.groupPrevalenceByPositions(muts);
			muts.forEach(mut -> {
				List<MutationPrevalence> prevsAtPos = MutationPrevalences.getPrevalenceAtSamePosition(mut);
				assertEquals(prevsAtPos, mpByPos.get(mut));
			});
		}

		private void checkNullPrevalence(
				List<MutationPrevalence> mps, String aa, String subtype) {
			MutationPrevalence mp = mps
				.stream()
				.filter(m -> m.subtype.equals(subtype) && m.getAA().equals(aa))
				.findFirst()
				.orElse(null);
			assertEquals(null, mp);
		}

		private void checkPrevalence(
				List<MutationPrevalence> mps, String aa, String subtype, int nNaive,
				int frequencyNaive, double percentageNaive, int nTreated,
				int frequencyTreated, double percentageTreated) {
			MutationPrevalence mp = mps
				.stream()
				.filter(m -> m.subtype.equals(subtype) && m.getAA().equals(aa))
				.findFirst()
				.orElse(null);
			assertEquals(new Integer(nNaive), mp.totalNaive);
			assertEquals(new Integer(frequencyNaive), mp.frequencyNaive);
			assertEquals(new Double(percentageNaive), mp.percentageNaive);
			assertEquals(new Integer(nTreated), mp.totalTreated);
			assertEquals(new Integer(frequencyTreated), mp.frequencyTreated);
			assertEquals(new Double(percentageTreated), mp.percentageTreated);
		}
}
