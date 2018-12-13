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

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationMapUtils;
import edu.stanford.hivdb.mutations.MutationMapUtils.SortOrder;

public class MutationMapUtilsTest {

	private Map<Mutation, Double> mutationScores = new HashMap<>();
	final private Mutation mut1 = new IUPACMutation(Gene.RT, 67, "N");
	final private Mutation mut2 = new IUPACMutation(Gene.RT, 69, "KS");
	final private Mutation mut3 = new IUPACMutation(Gene.RT, 65, "R");
	final private Mutation mut4 = new IUPACMutation(Gene.RT, 184, "V");
	final private Mutation mut5 = new IUPACMutation(Gene.RT, 41, "L");
	final private Mutation mut6 = new IUPACMutation(Gene.RT, 151, "M");

	@Before
	public void setUp() {
		mutationScores.clear();
		mutationScores.put(mut1, 5.0);
		mutationScores.put(mut2, 0.0);
		mutationScores.put(mut3, 45.0);
		mutationScores.put(mut4, 15.0);
		mutationScores.put(mut5, 10.0);
		mutationScores.put(mut6, 60.0);
	}

	@Test
	public void testDefaultConstructor() {
		final MutationMapUtils mutMapUtils = new MutationMapUtils();
		assertEquals(MutationMapUtils.class, mutMapUtils.getClass());
	}

	@Test
	public void testSortByComperatorAsc() {
		mutationScores = MutationMapUtils.sortByComparator(mutationScores, SortOrder.ASC);
		final String eScoresSortedByAsc = "T69KS (0), D67N (5), M41L (10), M184V (15), K65R (45), Q151M (60)";
		assertEquals(eScoresSortedByAsc, MutationMapUtils.printMutScoresAsInts(mutationScores));
	}

	@Test
	public void testSortByComperatorDesc() {
		mutationScores = MutationMapUtils.sortByComparator(mutationScores, SortOrder.DESC);
		final String eScoresSortedByDesc = "Q151M (60.0), K65R (45.0), M184V (15.0), M41L (10.0), D67N (5.0), T69KS (0.0)";
		assertEquals(eScoresSortedByDesc, MutationMapUtils.printMutScoresAsDouble(mutationScores));
	}

	@Test
	public void testConvertMutScoresToInts() {
		Map<Mutation, Integer> mutationScoresAsInts = new HashMap<>();
		mutationScoresAsInts.put(mut1, 5);
		mutationScoresAsInts.put(mut2, 0);
		mutationScoresAsInts.put(mut3, 45);
		mutationScoresAsInts.put(mut4, 15);
		mutationScoresAsInts.put(mut5, 10);
		mutationScoresAsInts.put(mut6, 60);
		assertEquals(mutationScoresAsInts,
					 MutationMapUtils.convertMutScoresToInts(mutationScores));
	}

	@Test
	public void testPrintMutSetScores() {
		Map<MutationSet, Double> mutSetScores = new HashMap<>();
		mutSetScores.put(new MutationSet(mut1, mut2), 5.0);
		mutSetScores.put(new MutationSet(""), 0.0);
		mutSetScores.put(new MutationSet(mut2), 0.0);
		mutSetScores.put(new MutationSet(mut3, mut4), 60.0);
		mutSetScores = MutationMapUtils.sortByComparator(mutSetScores, SortOrder.ASC);

		String eSortedMutSetScoresAsDoubles = "None (0.0), T69KS (0.0), D67N + T69KS (5.0), K65R + M184V (60.0)";
		String eSortedMutSetScoresAsInts = "None (0), T69KS (0), D67N + T69KS (5), K65R + M184V (60)";
		assertEquals(eSortedMutSetScoresAsDoubles, MutationMapUtils.printMutSetScoresAsDouble(mutSetScores));
		assertEquals(eSortedMutSetScoresAsInts, MutationMapUtils.printMutSetScoresAsInts(mutSetScores));
	}

	@Test
	public void testPrintZeroMutScoresAsInts() {
		Map<Mutation, Double> zeroMutScores = new HashMap<>();
		assertEquals("", MutationMapUtils.printMutScoresAsInts(zeroMutScores));
	}
}
