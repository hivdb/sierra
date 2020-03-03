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
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.MutationSet;


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
	public void testGetGene() {
		List<DrugResistanceAlgorithm<HIV>> hivdbAlgo = new ArrayList<>();
		hivdbAlgo.add(hiv1.getLatestDrugResistAlgorithm("HIVDB"));

		MutationSet<HIV> mutationSets = MutationSet.parseString(hiv1.getGene("HIV1RT"), "RT184V");
		AlgorithmComparison<HIV> algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivdbAlgo);
		GeneDR<HIV> geneDR = algorithmComparison.getGeneDR(hiv1.getGene("HIV1RT")).get(0);
		assertEquals(hiv1.getGene("HIV1RT"), geneDR.getGene());


		MutationSet<HIV> mutationSets2 = MutationSet.parseString(hiv1.getGene("HIV1PR"), "PR24I,PR46L,PR54V");
		AlgorithmComparison<HIV> algorithmComparison2 = new AlgorithmComparison<HIV>(mutationSets2, hivdbAlgo);
		GeneDR<HIV> geneDR2 = algorithmComparison2.getGeneDR(hiv1.getGene("HIV1PR")).get(0);
		assertEquals(hiv1.getGene("HIV1PR"), geneDR2.getGene());


		MutationSet<HIV> mutationSets3 = MutationSet.parseString(hiv1.getGene("HIV1IN"), "IN140S,IN148H");
		AlgorithmComparison<HIV> algorithmComparison3 = new AlgorithmComparison<HIV>(mutationSets3, hivdbAlgo);
		GeneDR<HIV> geneDR3 = algorithmComparison3.getGeneDR(hiv1.getGene("HIV1IN")).get(0);
		assertEquals(hiv1.getGene("HIV1IN"), geneDR3.getGene());
	}
	
	@Test
	public void testASIDrugSusc() {
		ASIDrugSusc<HIV> drugSusc = new ASIDrugSusc<HIV>(
			hiv1.getDrug("ABC"),
			hiv1.getDrugResistAlgorithm("HIVDB_8.9"),
			/* score */ 90.,
			/* level */ 5,
			/* levelText */ "LevelText",
			/* SIR */ SIREnum.R,
			/* partialScores */ Collections.emptyMap(),
			/* statement */ "Statement",
			/* triggered */ true
		);
		assertNotNull(drugSusc);
		
		assertEquals(drugSusc.getDrug(), hiv1.getDrug("ABC"));
		assertEquals(drugSusc.getAlgorithm(), "HIVDB_8.9");
		assertEquals(drugSusc.getSIR(), SIREnum.R);
		assertEquals(drugSusc.getInterpretation(), "LevelText");
		assertEquals(drugSusc.getExplanation(), "Statement (LevelText)");
		assertEquals(drugSusc.toString(), "ABC (HIVDB_8.9): R");
	
		ASIDrugSusc<HIV> compDrugScoreOther = new ASIDrugSusc<HIV>(
			hiv1.getDrug("ABC"),
			hiv1.getDrugResistAlgorithm("HIVDB_8.9"),
			/* score */ 90.,
			/* level */ 5,
			/* levelText */ "LevelText",
			/* SIR */ SIREnum.R,
			/* partialScores */ Collections.emptyMap(),
			/* statement */ "Statement",
			/* triggered */ true
		);
		assertEquals(drugSusc, compDrugScoreOther);
		assertEquals(drugSusc.hashCode(), compDrugScoreOther.hashCode());
		
		assertEquals(drugSusc, drugSusc);
		assertNotEquals(drugSusc, null);
		assertNotEquals(drugSusc, Integer.valueOf(1));
		
		compDrugScoreOther = new ASIDrugSusc<HIV>(
			hiv1.getDrug("EFV"),
			hiv1.getDrugResistAlgorithm("HIVDB_8.9"),
			/* score */ 90.,
			/* level */ 5,
			/* levelText */ "LevelText",
			/* SIR */ SIREnum.R,
			/* partialScores */ Collections.emptyMap(),
			/* statement */ "Statement",
			/* triggered */ true
		);
		assertNotEquals(drugSusc, compDrugScoreOther);
		
		compDrugScoreOther = new ASIDrugSusc<HIV>(
			hiv1.getDrug("ABC"),
			hiv1.getDrugResistAlgorithm("HIVDB_8.8"),
			/* score */ 90.,
			/* level */ 5,
			/* levelText */ "LevelText",
			/* SIR */ SIREnum.R,
			/* partialScores */ Collections.emptyMap(),
			/* statement */ "Statement",
			/* triggered */ true
		);
		assertNotEquals(drugSusc, compDrugScoreOther);
		
		compDrugScoreOther = new ASIDrugSusc<HIV>(
			hiv1.getDrug("ABC"),
			hiv1.getDrugResistAlgorithm("HIVDB_8.9"),
			/* score */ 90.,
			/* level */ 5,
			/* levelText */ "LevelText",
			/* SIR */ SIREnum.I,
			/* partialScores */ Collections.emptyMap(),
			/* statement */ "Statement",
			/* triggered */ true
		);
		assertNotEquals(drugSusc, compDrugScoreOther);
		
		compDrugScoreOther = new ASIDrugSusc<HIV>(
			hiv1.getDrug("ABC"),
			hiv1.getDrugResistAlgorithm("HIVDB_8.9"),
			/* score */ 90.,
			/* level */ 5,
			/* levelText */ "LevelTextOther",
			/* SIR */ SIREnum.R,
			/* partialScores */ Collections.emptyMap(),
			/* statement */ "Statement",
			/* triggered */ true
		);
		assertNotEquals(drugSusc, compDrugScoreOther);
		
		compDrugScoreOther = new ASIDrugSusc<HIV>(
			hiv1.getDrug("ABC"),
			hiv1.getDrugResistAlgorithm("HIVDB_8.9"),
			/* score */ 90.,
			/* level */ 5,
			/* levelText */ "LevelText",
			/* SIR */ SIREnum.R,
			/* partialScores */ Collections.emptyMap(),
			/* statement */ "StatementOther",
			/* triggered */ true
		);
		assertNotEquals(drugSusc, compDrugScoreOther);
	}
	

	@Test
	public void testGetComparisonResults() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv1, "PR46I,PR54V,PR73T,RT103N,RT41L,RT215E,RT181C,RT190A,IN66I");

		AlgorithmComparison<HIV> algoCompare = new AlgorithmComparison<HIV>(mutations, algos);
		
		List<ASIDrugSusc<HIV>> result = algoCompare.getComparisonResults();
		
		assertFalse(result.isEmpty());
		
		assertEquals(SIREnum.I, getDrugSusc(result, hiv1.getDrug("ABC"), anrsAlgo30).getSIR());
		assertEquals("Possible resistance", getDrugSusc(result, hiv1.getDrug("ABC"), anrsAlgo30).getInterpretation());

		
		assertEquals(SIREnum.R, getDrugSusc(result, hiv1.getDrug("EFV"), anrsAlgo30).getSIR());
		assertEquals("Resistance", getDrugSusc(result, hiv1.getDrug("EFV"), anrsAlgo30).getInterpretation());
		
		assertEquals(SIREnum.R, getDrugSusc(result, hiv1.getDrug("EFV"), hivdbAlgo8).getSIR());
		assertEquals("High-Level Resistance", getDrugSusc(result, hiv1.getDrug("EFV"), hivdbAlgo8).getInterpretation());
		
		assertEquals(SIREnum.R, getDrugSusc(result, hiv1.getDrug("EFV"), regaAlgo10).getSIR());
		assertEquals("Resistant GSS 0", getDrugSusc(result, hiv1.getDrug("EFV"), regaAlgo10).getInterpretation());
	}

	private ASIDrugSusc<HIV> getDrugSusc(
			List<ASIDrugSusc<HIV>> list, Drug<HIV> drug, DrugResistanceAlgorithm<HIV> alg) {
		return list
			.stream()
			.filter(ds -> ds.getDrug() == drug && ds.getAlgorithmObj() == alg).findFirst().get();
	}


	@Test
	public void testGetAsiList() {
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv1, "PR46I,PR54V,PR73T,RT103N,RT41L,RT215E,RT181C,RT190A,IN66I");

		AlgorithmComparison<HIV> algoCompare = new AlgorithmComparison<HIV>(mutations, algos);
		
		List<GeneDR<HIV>> asiResults = algoCompare.getGeneDR(hiv1.getGene("HIV1PR"));
		assertEquals(asiResults.size(), 3);
	}
	
	@Test
	public void tsetGetAlgorithms() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv1, "PR46I,PR54V,PR73T,RT103N,RT41L,RT215E,RT181C,RT190A,IN66I");

		AlgorithmComparison<HIV> algoCompare = new AlgorithmComparison<HIV>(mutations, algos);
		
		assertEquals(algoCompare.getAlgorithms().size(), 3);
		
	}
}
