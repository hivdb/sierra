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
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import com.google.common.collect.Sets;

import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.hivfacts.HIV;


public class GeneDRASIResultHandlerDrugSuscTest {

	private static final HIV hiv = HIV.getInstance();
	
	@Test
	public void testConstructor() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv,
				"PR46I,PR54V,PR73T,RT103N,RT41L,RT215E,RT181C,RT190A,IN66I");
		final DrugResistanceAlgorithm<HIV> hivdbAlgo = hiv.getLatestDrugResistAlgorithm("HIVDB");
		GeneDR<HIV> result = new GeneDR<HIV>(hiv.getGene("HIV1PR"), mutations, hivdbAlgo);
		
		assertNotNull(result);
	}

	@Test
	public void testGetDrugLevel() {

		List<DrugResistanceAlgorithm<HIV>> hivdbAlgo = new ArrayList<>();
		hivdbAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));


		MutationSet<HIV> mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67N,RT70R,RT184V,RT219Q");
		AlgorithmComparison<HIV> algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivdbAlgo);
		GeneDR<HIV> geneDR = algorithmComparison.getGeneDR(hiv.getGene("HIV1RT")).get(0);

		assertEquals((Integer) 2, geneDR.getDrugSusc(hiv.getDrug("TDF")).getLevel());

		MutationSet<HIV> mutationSets2 = MutationSet.parseString(hiv.getGene("HIV1IN"), "IN184A");
		AlgorithmComparison<HIV> algorithmComparison2 = new AlgorithmComparison<HIV>(mutationSets2, hivdbAlgo);
		GeneDR<HIV> geneDR2 = algorithmComparison2.getGeneDR(hiv.getGene("HIV1IN")).get(0);


		assertEquals((Integer) 1, geneDR2.getDrugSusc(hiv.getDrug("DTG")).getLevel());
	}

	@Test
	public void testGetDrugLevelText() {
		List<DrugResistanceAlgorithm<HIV>> hivdbAlgo = new ArrayList<>();
		hivdbAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));

		MutationSet<HIV> mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67N,RT70R,RT184V,RT219Q");
		AlgorithmComparison<HIV> algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivdbAlgo);
		GeneDR<HIV> geneDR = algorithmComparison.getGeneDR(hiv.getGene("HIV1RT")).get(0);

		assertEquals("Potential Low-Level Resistance", geneDR.getDrugSusc(hiv.getDrug("TDF")).getLevelText());


		MutationSet<HIV> mutationSets2 = MutationSet.parseString(hiv.getGene("HIV1IN"), "IN184A");
		AlgorithmComparison<HIV> algorithmComparison2 = new AlgorithmComparison<HIV>(mutationSets2, hivdbAlgo);
		GeneDR<HIV> geneDR2 = algorithmComparison2.getGeneDR(hiv.getGene("HIV1IN")).get(0);

		assertEquals("Susceptible", geneDR2.getDrugSusc(hiv.getDrug("DTG")).getLevelText());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testGetDrugSuscForOtherDrugClass() {
		List<DrugResistanceAlgorithm<HIV>> hivdbAlgo = new ArrayList<>();
		hivdbAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));
		MutationSet<HIV> mutationSets = MutationSet.parseString(hiv.getGene("HIV1IN"), "IN184A");
		AlgorithmComparison<HIV> algorithmComparison2 = new AlgorithmComparison<HIV>(mutationSets, hivdbAlgo);
		GeneDR<HIV> geneDR = algorithmComparison2.getGeneDR(hiv.getGene("HIV1IN")).get(0);
		geneDR.getDrugSusc(hiv.getDrug("ABC"));
	}

	@Test
	public void testGetDrugLevelSir() {
		List<DrugResistanceAlgorithm<HIV>> hivdbAlgo = new ArrayList<>();
		hivdbAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));

		GeneDR<HIV> geneDR;
		MutationSet<HIV> mutationSets;
		AlgorithmComparison<HIV> algorithmComparison;

		// All the SIR results depend on Algorithm version.
		mutationSets = MutationSet.parseString(hiv.getGene("HIV1PR"), "PR73V,PR76V,PR84V,PR88S");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivdbAlgo);
		geneDR = algorithmComparison.getGeneDR(hiv.getGene("HIV1PR")).get(0);

		assertEquals(SIREnum.R, geneDR.getDrugSusc(hiv.getDrug("FPV")).getSIR());

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67N,RT70R,RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivdbAlgo);
		geneDR = algorithmComparison.getGeneDR(hiv.getGene("HIV1RT")).get(0);
		assertEquals(SIREnum.S, geneDR.getDrugSusc(hiv.getDrug("TDF")).getSIR());

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1IN"), "IN184A");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivdbAlgo);
		geneDR = algorithmComparison.getGeneDR(hiv.getGene("HIV1IN")).get(0);
		assertEquals(SIREnum.S, geneDR.getDrugSusc(hiv.getDrug("DTG")).getSIR());


		List<DrugResistanceAlgorithm<HIV>> regaAlgo = new ArrayList<>();
		regaAlgo.add(hiv.getLatestDrugResistAlgorithm("Rega"));

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1PR"), "PR73V,PR76V,PR84V,PR88S");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, regaAlgo);
		geneDR = algorithmComparison.getGeneDR(hiv.getGene("HIV1PR")).get(0);
		assertEquals(SIREnum.I, geneDR.getDrugSusc(hiv.getDrug("FPV")).getSIR());

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67N,RT70R,RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, regaAlgo);
		geneDR = algorithmComparison.getGeneDR(hiv.getGene("HIV1RT")).get(0);
		assertEquals(SIREnum.S, geneDR.getDrugSusc(hiv.getDrug("TDF")).getSIR());

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1IN"), "IN184A");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, regaAlgo);
		geneDR = algorithmComparison.getGeneDR(hiv.getGene("HIV1IN")).get(0);
		assertEquals(SIREnum.S, geneDR.getDrugSusc(hiv.getDrug("DTG")).getSIR());


		List<DrugResistanceAlgorithm<HIV>> ansrAlgo = new ArrayList<>();
		ansrAlgo.add(hiv.getLatestDrugResistAlgorithm("ANRS"));

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1PR"), "PR73V,PR76V,PR84V,PR88S");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, ansrAlgo);
		geneDR = algorithmComparison.getGeneDR(hiv.getGene("HIV1PR")).get(0);
		// FPV is no longer available for ANRS 33
		assertEquals(null, geneDR.getDrugSusc(hiv.getDrug("FPV")));

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67N,RT70R,RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, ansrAlgo);
		geneDR = algorithmComparison.getGeneDR(hiv.getGene("HIV1RT")).get(0);
		assertEquals(SIREnum.S, geneDR.getDrugSusc(hiv.getDrug("TDF")).getSIR());

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1IN"), "IN184A");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, ansrAlgo);
		geneDR = algorithmComparison.getGeneDR(hiv.getGene("HIV1IN")).get(0);
		assertEquals(SIREnum.S, geneDR.getDrugSusc(hiv.getDrug("DTG")).getSIR());
	}

	@Test
	public void testGetTotalScore() {
		List<DrugResistanceAlgorithm<HIV>> hivAlgo = new ArrayList<>();
		hivAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));

		GeneDR<HIV> geneDR;
		MutationSet<HIV> mutationSets;
		AlgorithmComparison<HIV> algorithmComparison;

		// All the results depend on Algorithm version.
		mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67N,RT70R,RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivAlgo);
		geneDR = algorithmComparison.getGeneDR(hiv.getGene("HIV1RT")).get(0);


		assertEquals(10.0, geneDR.getDrugSusc(hiv.getDrug("TDF")).getScore(), 1e-6);

		assertEquals(45.0, geneDR.getDrugSusc(hiv.getDrug("ABC")).getScore(), 1e-6);

		IllegalArgumentException expectedExc = null;
		try {
			geneDR.getDrugSusc(hiv.getDrug("DTG"));
		}
		catch (IllegalArgumentException e) {
			expectedExc = e;
		}
		assertNotNull(expectedExc);
		assertEquals("The input drug DTG is for gene IN, but this GeneDR object is for RT", expectedExc.getMessage());
	}

	@Test
	public void testGetTotalDrugScores() {
		List<DrugResistanceAlgorithm<HIV>> hivAlgo = new ArrayList<>();
		hivAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));

		GeneDR<HIV> geneDR;
		MutationSet<HIV> mutationSets;
		AlgorithmComparison<HIV> algorithmComparison;

		// All the results depend on Algorithm version.
		mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67N,RT70R,RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivAlgo);
		geneDR = algorithmComparison.getGeneDR(hiv.getGene("HIV1RT")).get(0);
		Map<Drug<HIV>, Double> expected = new TreeMap<>();
		expected.put(hiv.getDrug("D4T"), 40.0);
		expected.put(hiv.getDrug("AZT"), 60.0);
		expected.put(hiv.getDrug("FTC"), 70.0);
		expected.put(hiv.getDrug("TDF"), 10.0);
		expected.put(hiv.getDrug("DDI"), 40.0);
		expected.put(hiv.getDrug("LMV"), 70.0);
		expected.put(hiv.getDrug("ABC"), 45.0);
		expected.put(hiv.getDrug("DOR"), 0.0);
		expected.put(hiv.getDrug("NVP"), 0.0);
		expected.put(hiv.getDrug("RPV"), 0.0);
		expected.put(hiv.getDrug("DPV"), 0.0);
		expected.put(hiv.getDrug("ETR"), 0.0);
		expected.put(hiv.getDrug("EFV"), 0.0);
		assertEquals(expected, geneDR.getTotalDrugScores());

		Map<Drug<HIV>, Double> expectedNRTI = new TreeMap<>();
		expectedNRTI.put(hiv.getDrug("D4T"), 40.0);
		expectedNRTI.put(hiv.getDrug("AZT"), 60.0);
		expectedNRTI.put(hiv.getDrug("FTC"), 70.0);
		expectedNRTI.put(hiv.getDrug("TDF"), 10.0);
		expectedNRTI.put(hiv.getDrug("DDI"), 40.0);
		expectedNRTI.put(hiv.getDrug("LMV"), 70.0);
		expectedNRTI.put(hiv.getDrug("ABC"), 45.0);
		 assertEquals(
		 	expectedNRTI,
		 	geneDR.getTotalDrugScores(hiv.getDrugClass("NRTI")));

		Map<Drug<HIV>, Double> expectedNNRTI = new TreeMap<>();
		expectedNNRTI.put(hiv.getDrug("DOR"), 0.0);
		expectedNNRTI.put(hiv.getDrug("NVP"), 0.0);
		expectedNNRTI.put(hiv.getDrug("RPV"), 0.0);
		expectedNNRTI.put(hiv.getDrug("DPV"), 0.0);
		expectedNNRTI.put(hiv.getDrug("ETR"), 0.0);
		expectedNNRTI.put(hiv.getDrug("EFV"), 0.0);
		 assertEquals(
		 	expectedNNRTI,
		 	geneDR.getTotalDrugScores(hiv.getDrugClass("NNRTI")));
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testGetScoredMutations() {
		List<DrugResistanceAlgorithm<HIV>> hivAlgo = new ArrayList<>();
		hivAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));

		GeneDR<HIV> geneDR;
		MutationSet<HIV> mutationSets;
		Set<MutationSet<HIV>> expected;
		AlgorithmComparison<HIV> algorithmComparison;

		mutationSets = MutationSet.parseString(hiv, "RT67AN,RT71R,RT100I,RT101E,RT181C,RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivAlgo);
		geneDR = algorithmComparison.getGeneDR(hiv.getGene("HIV1RT")).get(0);

		expected = Sets.newHashSet(
			MutationSet.parseString(hiv, "RT67AN"),
			MutationSet.parseString(hiv, "RT100I"),
			MutationSet.parseString(hiv, "RT101E"),
			MutationSet.parseString(hiv, "RT101E,RT181C"),
			MutationSet.parseString(hiv, "RT181C"),
			MutationSet.parseString(hiv, "RT184V"),
			MutationSet.parseString(hiv, "RT219Q")
		);
		assertEquals(expected, geneDR.getScoredMutations());


		expected = Sets.newHashSet(
			MutationSet.parseString(hiv, "RT67AN"),
			MutationSet.parseString(hiv, "RT184V"),
			MutationSet.parseString(hiv, "RT219Q")
		);
		assertEquals(expected, geneDR.getScoredMutations(ds -> ds.drugClassIs(hiv.getDrugClass("NRTI"))));


		expected = Sets.newHashSet(
			MutationSet.parseString(hiv, "RT100I"),
			MutationSet.parseString(hiv, "RT101E"),
			MutationSet.parseString(hiv, "RT101E,RT181C"),
			MutationSet.parseString(hiv, "RT181C")
		);
		assertEquals(expected, geneDR.getScoredMutations(ds -> ds.drugClassIs(hiv.getDrugClass("NNRTI"))));
	}

	@Test
	public void testDrugSuscGetPartialScores() {
		List<DrugResistanceAlgorithm<HIV>> hivAlgo = new ArrayList<>();
		hivAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));

		GeneDR<HIV> geneDR;
		MutationSet<HIV> mutationSets;
		AlgorithmComparison<HIV> algorithmComparison;

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67AN,RT71R,RT100I,RT101E,RT181C,RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivAlgo);
		geneDR = algorithmComparison.getGeneDR(hiv.getGene("HIV1RT")).get(0);

		SortedMap<Drug<HIV>, Map<MutationSet<HIV>, Double>> expected = new TreeMap<>();

		SortedMap<MutationSet<HIV>, Double> mutScores = new TreeMap<>();
		mutScores.put(hiv.newMutationSet("RT181C"), 60.);
		mutScores.put(hiv.newMutationSet("RT101E"), 30.);
		mutScores.put(hiv.newMutationSet("RT100I"), 60.);
		expected.put(hiv.getDrug("NVP"), mutScores);

		mutScores = new TreeMap<>();
		mutScores.put(hiv.newMutationSet("RT181C"), 30.);
		mutScores.put(hiv.newMutationSet("RT101E"), 15.);
		mutScores.put(hiv.newMutationSet("RT100I"), 45.);
		mutScores.put(hiv.newMutationSet("RT101E,RT181C"), 5.);
		expected.put(hiv.getDrug("EFV"), mutScores);

		mutScores = new TreeMap<>();
		mutScores.put(hiv.newMutationSet("RT181C"), 45.);
		mutScores.put(hiv.newMutationSet("RT101E"), 45.);
		mutScores.put(hiv.newMutationSet("RT100I"), 45.);
		expected.put(hiv.getDrug("RPV"), mutScores);

		mutScores = new TreeMap<>();
		mutScores.put(hiv.newMutationSet("RT100I"), 60.);
		mutScores.put(hiv.newMutationSet("RT101E"), 45.);
		mutScores.put(hiv.newMutationSet("RT181C"), 60.);
		expected.put(hiv.getDrug("DPV"), mutScores);

		mutScores = new TreeMap<>();
		mutScores.put(hiv.newMutationSet("RT181C"), 30.);
		mutScores.put(hiv.newMutationSet("RT101E"), 10.);
		mutScores.put(hiv.newMutationSet("RT100I"), 30.);
		mutScores.put(hiv.newMutationSet("RT101E,RT181C"), 5.);
		expected.put(hiv.getDrug("ETR"), mutScores);
				
		mutScores = new TreeMap<>();
		mutScores.put(hiv.newMutationSet("RT100I"), 10.);
		mutScores.put(hiv.newMutationSet("RT101E"), 5.);
		mutScores.put(hiv.newMutationSet("RT101E,RT181C"), 5.);
		mutScores.put(hiv.newMutationSet("RT181C"), 5.);
		expected.put(hiv.getDrug("DOR"), mutScores);
				
		mutScores = new TreeMap<>();
		mutScores.put(hiv.newMutationSet("RT184V"), 15.);
		mutScores.put(hiv.newMutationSet("RT67AN"), 5.);
		expected.put(hiv.getDrug("ABC"), mutScores);
				
		mutScores = new TreeMap<>();
		mutScores.put(hiv.newMutationSet("RT184V"), -10.);
		mutScores.put(hiv.newMutationSet("RT219Q"), 10.);
		mutScores.put(hiv.newMutationSet("RT67AN"), 15.);
		expected.put(hiv.getDrug("D4T"), mutScores);
				
		mutScores = new TreeMap<>();
		mutScores.put(hiv.newMutationSet("RT184V"), -10.);
		mutScores.put(hiv.newMutationSet("RT67AN"), 5.);
		expected.put(hiv.getDrug("TDF"), mutScores);

				
		mutScores = new TreeMap<>();
		mutScores.put(hiv.newMutationSet("RT184V"), 10.);
		mutScores.put(hiv.newMutationSet("RT219Q"), 5.);
		mutScores.put(hiv.newMutationSet("RT67AN"), 5.);
		expected.put(hiv.getDrug("DDI"), mutScores);

		mutScores = new TreeMap<>();
		mutScores.put(hiv.newMutationSet("RT184V"), 60.);
		expected.put(hiv.getDrug("LMV"), mutScores);

		mutScores = new TreeMap<>();
		mutScores.put(hiv.newMutationSet("RT184V"), -10.);
		mutScores.put(hiv.newMutationSet("RT219Q"), 10.);
		mutScores.put(hiv.newMutationSet("RT67AN"), 15.);
		expected.put(hiv.getDrug("AZT"), mutScores);

		mutScores = new TreeMap<>();
		mutScores.put(hiv.newMutationSet("RT184V"), 60.);
		expected.put(hiv.getDrug("FTC"), mutScores);

		assertEquals(
			expected,
			geneDR.getDrugSuscs()
				.stream()
				.collect(Collectors.toMap(
					ds -> ds.getDrug(),
					ds -> ds.getPartialScores(),
					(a, b) -> a,
					TreeMap::new
				))
		);
	}

	@Test
	public void testDrugSuscGetStatement() {
		List<DrugResistanceAlgorithm<HIV>> hivAlgo = new ArrayList<>();
		hivAlgo.add(hiv.getLatestDrugResistAlgorithm("ANRS"));

		GeneDR<HIV> geneDR;
		MutationSet<HIV> mutationSets;
		AlgorithmComparison<HIV> algorithmComparison;

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivAlgo);
		geneDR = algorithmComparison.getGeneDR(hiv.getGene("HIV1RT")).get(0);


		SortedMap<Drug<HIV>, Pair<String, String>> expected = new TreeMap<>();
		expected.put(hiv.getDrug("ABC"), Pair.of("184VI", "Possible resistance"));
		expected.put(hiv.getDrug("FTC"), Pair.of("184VI", "Resistance"));
		expected.put(hiv.getDrug("LMV"), Pair.of("184VI", "Resistance"));
		assertEquals(
			expected,
			geneDR.getDrugSuscs(ds -> ds.isTriggered())
			.stream()
			.collect(Collectors.toMap(
				ds -> ds.getDrug(),
				ds -> Pair.of(ds.getStatement(), ds.getLevelText()),
				(a, b) -> a,
				TreeMap::new
			))
		);
	}
	
	@Test
	public void testNotTriggered() {
		List<DrugResistanceAlgorithm<HIV>> hivAlgo = new ArrayList<>();
		hivAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));
		hivAlgo.add(hiv.getLatestDrugResistAlgorithm("ANRS"));
		hivAlgo.add(hiv.getLatestDrugResistAlgorithm("Rega"));

		MutationSet<HIV> mutationSet;
		AlgorithmComparison<HIV> algorithmComparison;

		mutationSet = hiv.newMutationSet("RT99E");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSet, hivAlgo);
		for (GeneDR<HIV> geneDR : algorithmComparison.getGeneDR(hiv.getGene("HIV1RT"))) {
			ASIDrugSusc<HIV> drugSusc = geneDR.getDrugSusc(hiv.getDrug("ABC"));
			assertFalse(drugSusc.isTriggered());
		}

		mutationSet = hiv.newMutationSet("PR99E");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSet, hivAlgo);
		for (GeneDR<HIV> geneDR : algorithmComparison.getGeneDR(hiv.getGene("HIV1PR"))) {
			ASIDrugSusc<HIV> drugSusc = geneDR.getDrugSusc(hiv.getDrug("DRV"));
			if (drugSusc.getAlgorithmObj().getFamily().equals("Rega")) {
				// For rega, the "SELECT ATLEAST 0 FROM (1P)" triggered almost everything
				assertTrue(drugSusc.isTriggered());
			}
			else {
				assertFalse(drugSusc.isTriggered());
			}
		}
	}
}
