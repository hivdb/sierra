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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.stanford.hivdb.drugresistance.database.MutationComboScores;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;

public class MutationComboScoresTest {

	@Test
	public void test() {
		Mutation mut1 = new Mutation(Gene.RT, 41, "L");
		Mutation mut2 = new Mutation(Gene.RT, 215, "SY");
		// Mutation mut3 = new Mutation(Gene.RT, 118, "VI");
		Mutation mut4 = new Mutation(Gene.RT, 190, "E");
		// Mutation mut5 = new Mutation(Gene.PR, 73, "SC");
		// Mutation mut6 = new Mutation(Gene.PR, 90, "LM");
		// Mutation mut7 = new Mutation(Gene.PR, 84, "V");

		List<Mutation> mutList = new ArrayList<>();
		mutList.add(mut1);
		mutList.add(mut2);
		//mutList.add(mut3);
		mutList.add(mut4);
		//mutList.add(mut5);
		//mutList.add(mut6);
		//mutList.add(mut7);

		Map<DrugClass, Map<Drug, Map<MutationSet, Double>>> matchingRTRulesDrugScores =
				MutationComboScores.getComboMutDrugScoresForMutSet(Gene.RT, new MutationSet(mutList));
		Map<DrugClass, Map<Drug, Map<MutationSet, Double>>> matchingPRRulesDrugScores =
				MutationComboScores.getComboMutDrugScoresForMutSet(Gene.PR, new MutationSet(mutList));
		printMatchingRules(matchingRTRulesDrugScores);
		printMatchingRules(matchingPRRulesDrugScores);
	}

	// print the results obtained when getRulesDrugsAndScoresForMutList is called
	private static void printMatchingRules(Map<DrugClass, Map<Drug, Map<MutationSet, Double>>> matchingRTRulesDrugScores) {
		for (DrugClass drugClass : matchingRTRulesDrugScores.keySet()) {
			for (Drug drug : matchingRTRulesDrugScores.get(drugClass).keySet()) {
				for (MutationSet comboMuts : matchingRTRulesDrugScores.get(drugClass).get(drug).keySet()) {
					double score = matchingRTRulesDrugScores.get(drugClass).get(drug).get(comboMuts);
					//Collections.sort(comboMuts);
					String comboMutString = comboMuts.join('+');
					System.out.println(drugClass + ":" + comboMutString + ": " + drug + ":" + score);
				}
			}
		}
	}

}
