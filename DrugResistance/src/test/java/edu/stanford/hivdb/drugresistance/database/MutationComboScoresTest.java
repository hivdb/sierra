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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import edu.stanford.hivdb.drugresistance.database.MutationComboScores;

import static edu.stanford.hivdb.drugresistance.database.MutationComboScores.ComboScore;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;

public class MutationComboScoresTest {

	static final int TOTAL_COMBO_SCORES = 279;
	
	final Gene eGene = Gene.RT;
	final DrugClass eDrugClass = DrugClass.NNRTI;
	final String eRule = "100I+103N";
	final Drug eDrug = Drug.DOR;
	final double eScore = 15.0;
	final ComboScore comboScore
		= new ComboScore(eGene, eDrugClass, eRule, eDrug, eScore);
	final List<ComboScore> comboScores
		= MutationComboScores.getCombinationScores();
	
	@Test
	public void testDefaultConstructor() {
		final MutationComboScores mutComboScores = new MutationComboScores();
		assertEquals(MutationComboScores.class, mutComboScores.getClass());
	}
	
	@Test
	public void testComboScoreConstructor() {
		assertEquals(eGene, comboScore.gene);
		assertEquals(eDrugClass, comboScore.drugClass);
		assertEquals(eRule, comboScore.rule);
		assertEquals(eDrug, comboScore.drug);
		assertEquals(Double.valueOf(eScore), comboScore.score);
	}

	@Test
	public void testGetRuleMutations() {
		final MutationSet eRuleMuts = new MutationSet(Gene.RT, "100I, 103N");
		assertEquals(eRuleMuts, comboScore.getRuleMutations());
	}

	@Test
	public void testGetPositions() {
		final List<Integer> ePosList = Arrays.asList(100, 103);
		assertEquals(ePosList, comboScore.getPositions());
	}

	@Test
	public void testGetComboScores() {
		// This assertion is expected to fail after number of combo rules changed
		assertEquals(TOTAL_COMBO_SCORES, comboScores.size());
		for (DrugClass dc : DrugClass.values()) {
			final List<ComboScore> dcScores = MutationComboScores.getCombinationScores(dc);
			assertTrue(comboScores.containsAll(dcScores));
		}
	}

	@Test
	public void testGroupComboScoresByPosition() {
		for (Drug drug : Drug.values()) {
			Map<List<Integer>, List<ComboScore>> csByPos
				= MutationComboScores.groupComboScoresByPositions(drug);
			comboScores
				.stream()
				.filter(cs -> cs.drug == drug)
				.forEach(cs -> {
					List<ComboScore> csForPos = csByPos.get(cs.getPositions());
					assertTrue(csForPos.contains(cs));
				});
		}
	}
	
	@Test
	public void testGetComboMutDrugScores() {
		final double eComboScoreABC = 15.0;
		final double eComboScoreAZT = 10.0;
		final double eComboScoreD4T = 10.0;
		final double eComboScoreDDI = 10.0;
		final double eComboScoreFTC = 5.0;
		final double eComboScoreLMV = 5.0;
		final double eComboScoreTDF = 10.0;
		final MutationSet muts = new MutationSet(Gene.RT, "41L+215FY");
		final Map<DrugClass, Map<Drug, Map<MutationSet, Double>>> mcs =
				MutationComboScores.getComboMutDrugScoresForMutSet(Gene.RT, new MutationSet(muts));
		final double comboScoreABC
			= mcs.get(DrugClass.NRTI).get(Drug.ABC).get(muts);
		final double comboScoreAZT
			= mcs.get(DrugClass.NRTI).get(Drug.AZT).get(muts);
		final double comboScoreD4T
			= mcs.get(DrugClass.NRTI).get(Drug.D4T).get(muts);
		final double comboScoreDDI
			= mcs.get(DrugClass.NRTI).get(Drug.DDI).get(muts);
		final double comboScoreFTC
			= mcs.get(DrugClass.NRTI).get(Drug.FTC).get(muts);
		final double comboScoreLMV
			= mcs.get(DrugClass.NRTI).get(Drug.LMV).get(muts);
		final double comboScoreTDF
			= mcs.get(DrugClass.NRTI).get(Drug.TDF).get(muts);
		assertEquals(Double.valueOf(eComboScoreABC), Double.valueOf(comboScoreABC));
		assertEquals(Double.valueOf(eComboScoreAZT), Double.valueOf(comboScoreAZT));
		assertEquals(Double.valueOf(eComboScoreD4T), Double.valueOf(comboScoreD4T));
		assertEquals(Double.valueOf(eComboScoreDDI), Double.valueOf(comboScoreDDI));
		assertEquals(Double.valueOf(eComboScoreFTC), Double.valueOf(comboScoreFTC));
		assertEquals(Double.valueOf(eComboScoreLMV), Double.valueOf(comboScoreLMV));
		assertEquals(Double.valueOf(eComboScoreTDF), Double.valueOf(comboScoreTDF));
	}
	
	@Test
	public void testGetComboMutDrugScoresForSingleMutSet_OfEmptySet() {
		final MutationSet muts = new MutationSet(Gene.RT, "");
		final Map<DrugClass, Map<Drug, Map<MutationSet, Double>>> mutComboScores =
			MutationComboScores.getComboMutDrugScoresForMutSet(Gene.RT, new MutationSet(muts));
		assertTrue(mutComboScores.isEmpty());
	}
	
	@Test
	public void testGetComboMutDrugScoresForSingleMutSet_OfSingleMut() {
		final MutationSet muts = new MutationSet(Gene.RT, "41L");
		final Map<DrugClass, Map<Drug, Map<MutationSet, Double>>> mutComboScores =
			MutationComboScores.getComboMutDrugScoresForMutSet(Gene.RT, new MutationSet(muts));
		assertTrue(mutComboScores.isEmpty());
	}
	
	@Test
	public void testGetComboMutDrugScoresOfMutSet_OfUnmatchedMuts() {
		final MutationSet muts = new MutationSet(Gene.RT, "41E+215L");
		final Map<DrugClass, Map<Drug, Map<MutationSet, Double>>> mutComboScores =
			MutationComboScores.getComboMutDrugScoresForMutSet(Gene.RT, new MutationSet(muts));
		assertTrue(mutComboScores.isEmpty());
	}
	
	@Test
	public void testGetComboMutDrugScoresOfMutSet_OfPartiallyMatchedMuts() {
		final DrugClass eDrugClass = DrugClass.NRTI;
		final MutationSet eMutSet = new MutationSet(Gene.RT, "41L+44D+210W+215Q");
		final Double eScore = 10.0;
		final Map<DrugClass, Map<Drug, Map<MutationSet, Double>>> mutComboScores =
			MutationComboScores.getComboMutDrugScoresForMutSet(Gene.RT, new MutationSet(eMutSet));
		for (Entry<Drug, Map<MutationSet, Double>> drugScorePair : mutComboScores.get(eDrugClass).entrySet()) {
			Double score = drugScorePair.getValue().get(eMutSet);
			assertEquals(eScore, score);
		}
	}
	
	@Test
	public void testGetComboMutDrugScoresOfMutSet_OfPartialScrambledMatch() {
		final DrugClass eDrugClass = DrugClass.NRTI;
		final MutationSet eMutSet = new MutationSet(Gene.RT, "210W+215Q+44D+41L");
		final Double eScore = 10.0;
		final Map<DrugClass, Map<Drug, Map<MutationSet, Double>>> mutComboScores =
			MutationComboScores.getComboMutDrugScoresForMutSet(Gene.RT, new MutationSet(eMutSet));
		printMatchingRules(mutComboScores);
		for (Entry<Drug, Map<MutationSet, Double>> drugScorePair : mutComboScores.get(eDrugClass).entrySet()) {
			Double score = drugScorePair.getValue().get(eMutSet);
			assertEquals(eScore, score);
		}
	}
	
	// print the results obtained when getRulesDrugsAndScoresForMutList is called
	@SuppressWarnings("unused")
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
