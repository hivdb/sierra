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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationMapUtils;
import edu.stanford.hivdb.mutations.MutationMapUtils.SortOrder;

public class MutationMapUtilsTest {

	@Test
	public void test() {
		Mutation mut2 = new Mutation(Gene.RT, 69, "KS");
		Mutation mut1 = new Mutation(Gene.RT, 67, "N");
		Mutation mut3 = new Mutation(Gene.RT, 65, "R");
		Mutation mut4 = new Mutation(Gene.RT, 184, "V");
		Mutation mut5 = new Mutation(Gene.RT, 41, "L");
		Mutation mut6 = new Mutation(Gene.RT, 151, "M");

		Map <Mutation, Double>mutationScores = new HashMap<>();
		mutationScores.put(mut1, 5.0);
		mutationScores.put(mut2, 0.0);
		mutationScores.put(mut3, 45.0);
		mutationScores.put(mut4, 15.0);
		mutationScores.put(mut5, 10.0);
		mutationScores.put(mut6,  60.0);

		String mutScoresUnsorted = MutationMapUtils.printMutScoresAsInts(mutationScores);
		System.out.println("Unsorted:" + mutScoresUnsorted);

		Map<Mutation, Double> sortedMutationStringScores = MutationMapUtils.sortByComparator(mutationScores, SortOrder.DESC);
		String mutScoresSorted = MutationMapUtils.printMutScoresAsInts(sortedMutationStringScores);
		System.out.println("Sorted:" + mutScoresSorted);


	}

}
