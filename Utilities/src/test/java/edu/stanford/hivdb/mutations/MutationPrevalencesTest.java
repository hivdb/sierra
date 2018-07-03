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
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.stanford.hivdb.mutations.MutationPrevalences.MutationPrevalence;

public class MutationPrevalencesTest {
		@Test
		public void test() throws IOException {

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
			checkPrevalence(MutationPrevalences.getPrevalenceAtSamePosition(m), "T", "All", 98118, 3731, 3.8, 26364, 2105, 8.0);

		}

		private void checkNullPrevalence(
				List<MutationPrevalence> mps, String aa, String subtype) {
			MutationPrevalence mp = mps
				.stream()
				.filter(m -> m.subtype.equals(subtype) && m.mutation.getAAs().equals(aa))
				.findFirst()
				.orElse(null);
			Assert.assertEquals(null, mp);
		}

		private void checkPrevalence(
				List<MutationPrevalence> mps, String aa, String subtype, int nNaive,
				int frequencyNaive, double percentageNaive, int nTreated,
				int frequencyTreated, double percentageTreated) {
			MutationPrevalence mp = mps
				.stream()
				.filter(m -> m.subtype.equals(subtype) && m.mutation.getAAs().equals(aa))
				.findFirst()
				.orElse(null);
			System.out.println(mp);
			Assert.assertEquals(new Integer(nNaive), mp.totalNaive);
			Assert.assertEquals(new Integer(frequencyNaive), mp.frequencyNaive);
			Assert.assertEquals(new Double(percentageNaive), mp.percentageNaive);
			Assert.assertEquals(new Integer(nTreated), mp.totalTreated);
			Assert.assertEquals(new Integer(frequencyTreated), mp.frequencyTreated);
			Assert.assertEquals(new Double(percentageTreated), mp.percentageTreated);
		}
}
