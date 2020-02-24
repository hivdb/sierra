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

package edu.stanford.hivdb.drugresistance.algorithm;

import static org.junit.Assert.*;

import java.util.ArrayList;

import java.util.List;

import org.junit.Test;


import edu.stanford.hivdb.drugresistance.algorithm.AlgorithmComparison.ComparableDrugScore;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugResistanceAlgorithm;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.viruses.Gene;


public class AlgorithmComparisonTest {
	
	private static final HIV hiv1 = HIV.getInstance();
	private static final List<DrugResistanceAlgorithm<HIV>> algos = new ArrayList<>();
	private static final DrugResistanceAlgorithm<HIV> hivdbAlgo8 = hiv1.getDrugResistAlgorithm("HIVDB_8.0");
	private static final DrugResistanceAlgorithm<HIV> anrsAlgo30 = hiv1.getDrugResistAlgorithm("ANRS_30");
	private static final DrugResistanceAlgorithm<HIV> regaAlgo10 = hiv1.getDrugResistAlgorithm("Rega_10.0");
	
	static {
		algos.add(hivdbAlgo8);
		algos.add(anrsAlgo30);
		algos.add(regaAlgo10);
	}
	
	@Test
	public void testConstructor() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv1,
				"PR46I,PR54V,PR73T,RT103N,RT41L,RT215E,RT181C,RT190A,IN66I");
		
		AlgorithmComparison<HIV> algoCompare = new AlgorithmComparison<HIV>(mutations, algos);
		
		assertNotNull(algoCompare);
	}
	
	@Test
	public void testComparableDrugScore() {
		ComparableDrugScore<HIV> compDrugScore = new ComparableDrugScore<HIV>(
				hiv1.getDrug("ABC"), "HIVDB_8.9", SIREnum.R, "Interpretation", "Explanation"
				);
		assertNotNull(compDrugScore);
		
		assertEquals(compDrugScore.getDrug(), hiv1.getDrug("ABC"));
		assertEquals(compDrugScore.getAlgorithm(), "HIVDB_8.9");
		assertEquals(compDrugScore.getSIR(), SIREnum.R);
		assertEquals(compDrugScore.getInterpretation(), "Interpretation");
		assertEquals(compDrugScore.getExplanation(), "Explanation");
		
		assertEquals(compDrugScore.toString(), "ABC (HIVDB_8.9): R");
		
		
		ComparableDrugScore<HIV> compDrugScoreOther = new ComparableDrugScore<HIV>(
				hiv1.getDrug("ABC"), "HIVDB_8.9", SIREnum.R, "Interpretation", "Explanation"
				);
		assertTrue(compDrugScore.equals(compDrugScoreOther));
		
		assertTrue(compDrugScore.equals(compDrugScore));
		assertFalse(compDrugScore.equals(null));
		assertFalse(compDrugScore.equals(Integer.valueOf(1)));
		
		compDrugScoreOther = new ComparableDrugScore<HIV>(
				hiv1.getDrug("EFV"), "HIVDB_8.9", SIREnum.R, "Interpretation", "Explanation"
				);
		assertFalse(compDrugScore.equals(compDrugScoreOther));
		
		compDrugScoreOther = new ComparableDrugScore<HIV>(
				hiv1.getDrug("ABC"), "HIVDB_9.0", SIREnum.R, "Interpretation", "Explanation"
				);
		assertFalse(compDrugScore.equals(compDrugScoreOther));
		
		compDrugScoreOther = new ComparableDrugScore<HIV>(
				hiv1.getDrug("ABC"), "HIVDB_8.9", SIREnum.I, "Interpretation", "Explanation"
				);
		assertFalse(compDrugScore.equals(compDrugScoreOther));
		
		compDrugScoreOther = new ComparableDrugScore<HIV>(
				hiv1.getDrug("ABC"), "HIVDB_8.9", SIREnum.R, "InterpretationOther", "Explanation"
				);
		assertFalse(compDrugScore.equals(compDrugScoreOther));
		
		compDrugScoreOther = new ComparableDrugScore<HIV>(
				hiv1.getDrug("ABC"), "HIVDB_8.9", SIREnum.R, "Interpretation", "ExplanationOther"
				);
		assertFalse(compDrugScore.equals(compDrugScoreOther));
		
		assertTrue(compDrugScore.hashCode() != compDrugScoreOther.hashCode());

	}
	

	@Test
	public void testGetComparisonResults() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv1, "PR46I,PR54V,PR73T,RT103N,RT41L,RT215E,RT181C,RT190A,IN66I");

		AlgorithmComparison<HIV> algoCompare = new AlgorithmComparison<HIV>(mutations, algos);
		
		List<ComparableDrugScore<HIV>> result = algoCompare.getComparisonResults();
		
		assertFalse(result.isEmpty());
		
		assertEquals(SIREnum.I, getComparableDrugScore(result, hiv1.getDrug("ABC"), anrsAlgo30).getSIR());
		assertEquals("Possible resistance", getComparableDrugScore(result, hiv1.getDrug("ABC"), anrsAlgo30).getInterpretation());

		
		assertEquals(SIREnum.R, getComparableDrugScore(result, hiv1.getDrug("EFV"), anrsAlgo30).getSIR());
		assertEquals("Resistance", getComparableDrugScore(result, hiv1.getDrug("EFV"), anrsAlgo30).getInterpretation());
		
		assertEquals(SIREnum.R, getComparableDrugScore(result, hiv1.getDrug("EFV"), hivdbAlgo8).getSIR());
		assertEquals("High-Level Resistance", getComparableDrugScore(result, hiv1.getDrug("EFV"), hivdbAlgo8).getInterpretation());
		
		assertEquals(SIREnum.R, getComparableDrugScore(result, hiv1.getDrug("EFV"), regaAlgo10).getSIR());
		assertEquals("Resistant GSS 0", getComparableDrugScore(result, hiv1.getDrug("EFV"), regaAlgo10).getInterpretation());
	}

	private ComparableDrugScore<HIV> getComparableDrugScore(
			List<ComparableDrugScore<HIV>> list, Drug<HIV> drug, DrugResistanceAlgorithm<HIV> alg) {
		return list
			.stream()
			.filter(ds -> ds.getDrug() == drug && ds.getAlgorithm() == alg.getName()).findFirst().get();
	}


	@Test
	public void testGetAsiList() {
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv1, "PR46I,PR54V,PR73T,RT103N,RT41L,RT215E,RT181C,RT190A,IN66I");

		AlgorithmComparison<HIV> algoCompare = new AlgorithmComparison<HIV>(mutations, algos);
		
		List<AsiResult<HIV>> asiResults = algoCompare.getAsiList(hiv1.getGene("HIV1PR"));
		assertEquals(asiResults.size(), 3);
	}
	
	@Test
	public void tsetGetAlgorithms() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv1, "PR46I,PR54V,PR73T,RT103N,RT41L,RT215E,RT181C,RT190A,IN66I");

		AlgorithmComparison<HIV> algoCompare = new AlgorithmComparison<HIV>(mutations, algos);
		
		assertEquals(algoCompare.getAlgorithms().size(), 3);
		
	}
}
