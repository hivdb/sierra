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
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;

import org.junit.Test;

import edu.stanford.hivdb.mutations.AAMutation;
import edu.stanford.hivdb.mutations.ConsensusMutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.drugs.DrugResistanceAlgorithm;
import edu.stanford.hivdb.hivfacts.HIV;


public class AsiResultTest {

	private static final HIV hiv = HIV.getInstance();
	
	@Test
	public void testConstructor() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv,
				"PR46I,PR54V,PR73T,RT103N,RT41L,RT215E,RT181C,RT190A,IN66I");
		final DrugResistanceAlgorithm<HIV> hivdbAlgo = hiv.getLatestDrugResistAlgorithm("HIVDB");
		AsiResult<HIV> result = new AsiResult<HIV>(hiv.getGene("HIV1PR"), mutations, hivdbAlgo);
		
		assertNotNull(result);
	}

	@Test
	public void testGetGene() {

		List<DrugResistanceAlgorithm<HIV>> hivdbAlgo = new ArrayList<>();
		hivdbAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));


		MutationSet<HIV> mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		AlgorithmComparison<HIV> algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivdbAlgo);
		AsiResult<HIV> asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1RT")).get(0);
		assertEquals(hiv.getGene("HIV1RT"), asiObj.getGene());


		MutationSet<HIV> mutationSets2 = MutationSet.parseString(hiv.getGene("HIV1PR"), "PR24I,PR46L,PR54V");
		AlgorithmComparison<HIV> algorithmComparison2 = new AlgorithmComparison<HIV>(mutationSets2, hivdbAlgo);
		AsiResult<HIV> asiObj2 = algorithmComparison2.getAsiList(hiv.getGene("HIV1PR")).get(0);
		assertEquals(hiv.getGene("HIV1PR"), asiObj2.getGene());


		MutationSet<HIV> mutationSets3 = MutationSet.parseString(hiv.getGene("HIV1IN"), "IN140S,IN148H");
		AlgorithmComparison<HIV> algorithmComparison3 = new AlgorithmComparison<HIV>(mutationSets3, hivdbAlgo);
		AsiResult<HIV> asiObj3 = algorithmComparison3.getAsiList(hiv.getGene("HIV1IN")).get(0);
		assertEquals(hiv.getGene("HIV1IN"), asiObj3.getGene());

	}

	@Test
	public void testGetDrugLevel() {

		List<DrugResistanceAlgorithm<HIV>> hivdbAlgo = new ArrayList<>();
		hivdbAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));


		MutationSet<HIV> mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67N,RT70R,RT184V,RT219Q");
		AlgorithmComparison<HIV> algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivdbAlgo);
		AsiResult<HIV> asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1RT")).get(0);

		assertEquals(3, asiObj.getDrugLevel(hiv.getDrug("TDF")));

		MutationSet<HIV> mutationSets2 = MutationSet.parseString(hiv.getGene("HIV1IN"), "IN184A");
		AlgorithmComparison<HIV> algorithmComparison2 = new AlgorithmComparison<HIV>(mutationSets2, hivdbAlgo);
		AsiResult<HIV> asiObj2 = algorithmComparison2.getAsiList(hiv.getGene("HIV1IN")).get(0);


		assertEquals(1, asiObj2.getDrugLevel(hiv.getDrug("DTG")));
		assertEquals(1, asiObj2.getDrugLevel(hiv.getDrug("ABC")));
	}

	@Test
	public void testGetDrugLevelText() {
		List<DrugResistanceAlgorithm<HIV>> hivdbAlgo = new ArrayList<>();
		hivdbAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));

		MutationSet<HIV> mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67N,RT70R,RT184V,RT219Q");
		AlgorithmComparison<HIV> algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivdbAlgo);
		AsiResult<HIV> asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1RT")).get(0);

		assertEquals("Low-Level Resistance", asiObj.getDrugLevelText(hiv.getDrug("TDF")));


		MutationSet<HIV> mutationSets2 = MutationSet.parseString(hiv.getGene("HIV1IN"), "IN184A");
		AlgorithmComparison<HIV> algorithmComparison2 = new AlgorithmComparison<HIV>(mutationSets2, hivdbAlgo);
		AsiResult<HIV> asiObj2 = algorithmComparison2.getAsiList(hiv.getGene("HIV1IN")).get(0);

		assertEquals("Susceptible", asiObj2.getDrugLevelText(hiv.getDrug("DTG")));
		assertEquals("Susceptible", asiObj2.getDrugLevelText(hiv.getDrug("ABC")));
	}

	@Test
	public void testGetDrugLevelSir() {
		List<DrugResistanceAlgorithm<HIV>> hivdbAlgo = new ArrayList<>();
		hivdbAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));

		AsiResult<HIV> asiObj;
		MutationSet<HIV> mutationSets;
		AlgorithmComparison<HIV> algorithmComparison;

		// All the SIR results depend on Algorithm version.
		mutationSets = MutationSet.parseString(hiv.getGene("HIV1PR"), "PR73V,PR76V,PR84V,PR88S");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivdbAlgo);
		asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1PR")).get(0);

		assertEquals("R", asiObj.getDrugLevelSir(hiv.getDrug("FPV")));

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67N,RT70R,RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivdbAlgo);
		asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1RT")).get(0);
		assertEquals("I", asiObj.getDrugLevelSir(hiv.getDrug("TDF")));

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1IN"), "IN184A");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivdbAlgo);
		asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1IN")).get(0);
		assertEquals("S", asiObj.getDrugLevelSir(hiv.getDrug("DTG")));
		assertEquals("S", asiObj.getDrugLevelSir(hiv.getDrug("ABC")));


		List<DrugResistanceAlgorithm<HIV>> regaAlgo = new ArrayList<>();
		regaAlgo.add(hiv.getLatestDrugResistAlgorithm("Rega"));

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1PR"), "PR73V,PR76V,PR84V,PR88S");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, regaAlgo);
		asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1PR")).get(0);
		assertEquals("I", asiObj.getDrugLevelSir(hiv.getDrug("FPV")));

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67N,RT70R,RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, regaAlgo);
		asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1RT")).get(0);
		assertEquals("S", asiObj.getDrugLevelSir(hiv.getDrug("TDF")));

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1IN"), "IN184A");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, regaAlgo);
		asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1IN")).get(0);
		assertEquals("S", asiObj.getDrugLevelSir(hiv.getDrug("DTG")));
		assertEquals("S", asiObj.getDrugLevelSir(hiv.getDrug("ABC")));


		List<DrugResistanceAlgorithm<HIV>> ansrAlgo = new ArrayList<>();
		ansrAlgo.add(hiv.getLatestDrugResistAlgorithm("ANRS"));

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1PR"), "PR73V,PR76V,PR84V,PR88S");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, ansrAlgo);
		asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1PR")).get(0);
		assertEquals("S", asiObj.getDrugLevelSir(hiv.getDrug("FPV")));

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67N,RT70R,RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, ansrAlgo);
		asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1RT")).get(0);
		assertEquals("S", asiObj.getDrugLevelSir(hiv.getDrug("TDF")));

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1IN"), "IN184A");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, ansrAlgo);
		asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1IN")).get(0);
		assertEquals("S", asiObj.getDrugLevelSir(hiv.getDrug("DTG")));
		assertEquals("S", asiObj.getDrugLevelSir(hiv.getDrug("ABC")));
	}

	@Test
	public void testGetTotalScore() {
		List<DrugResistanceAlgorithm<HIV>> hivAlgo = new ArrayList<>();
		hivAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));

		AsiResult<HIV> asiObj;
		MutationSet<HIV> mutationSets;
		AlgorithmComparison<HIV> algorithmComparison;

		// All the results depend on Algorithm version.
		mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67N,RT70R,RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivAlgo);
		asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1RT")).get(0);


		assertEquals(15.0, asiObj.getTotalScore(hiv.getDrug("TDF")), 1e-6);

		assertEquals(0.0, asiObj.getTotalScore(hiv.getDrug("DTG")), 1e-6);
		assertEquals(60.0, asiObj.getTotalScore(hiv.getDrug("ABC")), 1e-6);
	}

	@Test
	public void testGetDrugClassTotalDrugScores() {
		List<DrugResistanceAlgorithm<HIV>> hivAlgo = new ArrayList<>();
		hivAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));

		AsiResult<HIV> asiObj;
		MutationSet<HIV> mutationSets;
		AlgorithmComparison<HIV> algorithmComparison;

		// All the results depend on Algorithm version.
		mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67N,RT70R,RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivAlgo);
		asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1RT")).get(0);
		Map<DrugClass<HIV>, Map<Drug<HIV>, Double>> expected = new TreeMap<>();
		expected.put(hiv.getDrugClass("NRTI"), new TreeMap<>());
		expected.put(hiv.getDrugClass("NNRTI"), new TreeMap<>());
		expected.get(hiv.getDrugClass("NRTI")).put(hiv.getDrug("D4T"), 40.0);
		expected.get(hiv.getDrugClass("NRTI")).put(hiv.getDrug("AZT"), 55.0);
		expected.get(hiv.getDrugClass("NRTI")).put(hiv.getDrug("FTC"), 70.0);
		expected.get(hiv.getDrugClass("NRTI")).put(hiv.getDrug("TDF"), 15.0);
		expected.get(hiv.getDrugClass("NRTI")).put(hiv.getDrug("DDI"), 40.0);
		expected.get(hiv.getDrugClass("NRTI")).put(hiv.getDrug("LMV"), 70.0);
		expected.get(hiv.getDrugClass("NRTI")).put(hiv.getDrug("ABC"), 60.0);
		expected.get(hiv.getDrugClass("NNRTI")).put(hiv.getDrug("DOR"), 0.0);
		expected.get(hiv.getDrugClass("NNRTI")).put(hiv.getDrug("NVP"), 0.0);
		expected.get(hiv.getDrugClass("NNRTI")).put(hiv.getDrug("RPV"), 0.0);
		expected.get(hiv.getDrugClass("NNRTI")).put(hiv.getDrug("ETR"), 0.0);
		expected.get(hiv.getDrugClass("NNRTI")).put(hiv.getDrug("EFV"), 0.0);
		assertEquals(expected, asiObj.getDrugClassTotalDrugScores());
		 assertEquals(
		 	expected.get(hiv.getDrugClass("NRTI")),
		 	asiObj.getDrugClassTotalDrugScores(hiv.getDrugClass("NRTI")));
		 assertEquals(
		 	expected.get(hiv.getDrugClass("NNRTI")),
		 	asiObj.getDrugClassTotalDrugScores(hiv.getDrugClass("NNRTI")));
	}

	@Test
	public void testGetTriggeredMutations() {
		List<DrugResistanceAlgorithm<HIV>> hivAlgo = new ArrayList<>();
		hivAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));

		AsiResult<HIV> asiObj;
		MutationSet<HIV> mutationSets;
		MutationSet<HIV> expected;
		AlgorithmComparison<HIV> algorithmComparison;

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67AN,RT71R,RT100I,RT101E,RT181C,RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivAlgo);
		asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1RT")).get(0);



		expected = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67AN,RT100I,RT101E,RT181C,RT184V,RT219Q");
		assertEquals(expected, asiObj.getTriggeredMutations());
		assertEquals(expected, asiObj.getTriggeredMutations());


		expected = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67AN,RT184V,RT219Q");
		assertEquals(expected, asiObj.getTriggeredMutations(hiv.getDrugClass("NRTI")));
		assertEquals(expected, asiObj.getTriggeredMutations(hiv.getDrugClass("NRTI")));


		expected =  MutationSet.parseString(hiv.getGene("HIV1RT"), "RT100I,RT101E,RT181C");
		assertEquals(expected, asiObj.getTriggeredMutations(hiv.getDrugClass("NNRTI")));
		assertEquals(expected, asiObj.getTriggeredMutations(hiv.getDrugClass("NNRTI")));
	}

	@Test
	public void testGetDrugClassDrugMutScores() {
		List<DrugResistanceAlgorithm<HIV>> hivAlgo = new ArrayList<>();
		hivAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));

		AsiResult<HIV> asiObj;
		MutationSet<HIV> mutationSets;
		AlgorithmComparison<HIV> algorithmComparison;

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67AN,RT71R,RT100I,RT101E,RT181C,RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivAlgo);
		asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1RT")).get(0);



		Map<DrugClass<HIV>, Map<Drug<HIV>, Map<AAMutation<HIV>, Double>>> expected = new TreeMap<>();
		expected.put(hiv.getDrugClass("NRTI"), new TreeMap<>());
		expected.put(hiv.getDrugClass("NNRTI"), new TreeMap<>());
		expected.get(hiv.getDrugClass("NNRTI")).put(hiv.getDrug("NVP"), new TreeMap<>());


		expected.get(hiv.getDrugClass("NNRTI")).get(hiv.getDrug("NVP")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 181, "C"), 60.0);
		expected.get(hiv.getDrugClass("NNRTI")).get(hiv.getDrug("NVP")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 101, "E"), 30.0);
		expected.get(hiv.getDrugClass("NNRTI")).get(hiv.getDrug("NVP")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 100, "I"), 60.0);
		expected.get(hiv.getDrugClass("NNRTI")).put(hiv.getDrug("EFV"), new TreeMap<>());
		expected.get(hiv.getDrugClass("NNRTI")).get(hiv.getDrug("EFV")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 181, "C"), 30.0);
		expected.get(hiv.getDrugClass("NNRTI")).get(hiv.getDrug("EFV")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 101, "E"), 15.0);
		expected.get(hiv.getDrugClass("NNRTI")).get(hiv.getDrug("EFV")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 100, "I"), 60.0);
		expected.get(hiv.getDrugClass("NNRTI")).put(hiv.getDrug("RPV"), new TreeMap<>());
		expected.get(hiv.getDrugClass("NNRTI")).get(hiv.getDrug("RPV")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 181, "C"), 45.0);
		expected.get(hiv.getDrugClass("NNRTI")).get(hiv.getDrug("RPV")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 101, "E"), 45.0);
		expected.get(hiv.getDrugClass("NNRTI")).get(hiv.getDrug("RPV")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 100, "I"), 60.0);
		expected.get(hiv.getDrugClass("NNRTI")).put(hiv.getDrug("ETR"), new TreeMap<>());
		expected.get(hiv.getDrugClass("NNRTI")).get(hiv.getDrug("ETR")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 181, "C"), 30.0);
		expected.get(hiv.getDrugClass("NNRTI")).get(hiv.getDrug("ETR")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 101, "E"), 15.0);
		expected.get(hiv.getDrugClass("NNRTI")).get(hiv.getDrug("ETR")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 100, "I"), 30.0);
		expected.get(hiv.getDrugClass("NNRTI")).put(hiv.getDrug("DOR"), new TreeMap<>());
		expected.get(hiv.getDrugClass("NNRTI")).get(hiv.getDrug("DOR")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 100, "I"), 15.0);
		expected.get(hiv.getDrugClass("NNRTI")).get(hiv.getDrug("DOR")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 101, "E"), 15.0);
		expected.get(hiv.getDrugClass("NNRTI")).get(hiv.getDrug("DOR")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 181, "C"), 10.0);
		expected.get(hiv.getDrugClass("NRTI")).put(hiv.getDrug("ABC"), new TreeMap<>());
		expected.get(hiv.getDrugClass("NRTI")).get(hiv.getDrug("ABC")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 184, "V"), 15.0);
		expected.get(hiv.getDrugClass("NRTI")).get(hiv.getDrug("ABC")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 219, "Q"), 5.0);
		expected.get(hiv.getDrugClass("NRTI")).get(hiv.getDrug("ABC")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 67, "AN"), 5.0);
		expected.get(hiv.getDrugClass("NRTI")).put(hiv.getDrug("D4T"), new TreeMap<>());
		expected.get(hiv.getDrugClass("NRTI")).get(hiv.getDrug("D4T")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 184, "V"), -10.0);
		expected.get(hiv.getDrugClass("NRTI")).get(hiv.getDrug("D4T")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 219, "Q"), 10.0);
		expected.get(hiv.getDrugClass("NRTI")).get(hiv.getDrug("D4T")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 67, "AN"), 15.0);
		expected.get(hiv.getDrugClass("NRTI")).put(hiv.getDrug("TDF"), new TreeMap<>());
		expected.get(hiv.getDrugClass("NRTI")).get(hiv.getDrug("TDF")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 184, "V"), -10.0);
		expected.get(hiv.getDrugClass("NRTI")).get(hiv.getDrug("TDF")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 219, "Q"), 5.0);
		expected.get(hiv.getDrugClass("NRTI")).get(hiv.getDrug("TDF")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 67, "AN"), 5.0);
		expected.get(hiv.getDrugClass("NRTI")).put(hiv.getDrug("DDI"), new TreeMap<>());
		expected.get(hiv.getDrugClass("NRTI")).get(hiv.getDrug("DDI")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 184, "V"), 10.0);
		expected.get(hiv.getDrugClass("NRTI")).get(hiv.getDrug("DDI")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 219, "Q"), 5.0);
		expected.get(hiv.getDrugClass("NRTI")).get(hiv.getDrug("DDI")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 67, "AN"), 5.0);
		expected.get(hiv.getDrugClass("NRTI")).put(hiv.getDrug("LMV"), new TreeMap<>());
		expected.get(hiv.getDrugClass("NRTI")).get(hiv.getDrug("LMV")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 184, "V"), 60.0);
		expected.get(hiv.getDrugClass("NRTI")).put(hiv.getDrug("AZT"), new TreeMap<>());
		expected.get(hiv.getDrugClass("NRTI")).get(hiv.getDrug("AZT")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 184, "V"), -10.0);
		expected.get(hiv.getDrugClass("NRTI")).get(hiv.getDrug("AZT")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 219, "Q"), 10.0);
		expected.get(hiv.getDrugClass("NRTI")).get(hiv.getDrug("AZT")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 67, "AN"), 15.0);
		expected.get(hiv.getDrugClass("NRTI")).put(hiv.getDrug("FTC"), new TreeMap<>());
		expected.get(hiv.getDrugClass("NRTI")).get(hiv.getDrug("FTC")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 184, "V"), 60.0);
		assertEquals(expected, asiObj.getDrugClassDrugMutScores());
	}

	@Test
	public void testGetDrugClassDrugComboMutScores() {
		List<DrugResistanceAlgorithm<HIV>> hivAlgo = new ArrayList<>();
		hivAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));

		AsiResult<HIV> asiObj;
		MutationSet<HIV> mutationSets;
		AlgorithmComparison<HIV> algorithmComparison;

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67AN,RT71R,RT100I,RT101E,RT181C,RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivAlgo);
		asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1RT")).get(0);


		Map<DrugClass<HIV>, Map<Drug<HIV>, Map<MutationSet<HIV>, Double>>> expected = new TreeMap<>();
		expected.put(hiv.getDrugClass("NNRTI"), new TreeMap<>());
		expected.get(hiv.getDrugClass("NNRTI")).put(hiv.getDrug("NVP"), new HashMap<>());
		expected.get(hiv.getDrugClass("NNRTI")).get(hiv.getDrug("NVP")).put(MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E,RT181C"), 5.0);
		expected.get(hiv.getDrugClass("NNRTI")).put(hiv.getDrug("EFV"), new HashMap<>());
		expected.get(hiv.getDrugClass("NNRTI")).get(hiv.getDrug("EFV")).put(MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E,RT181C"), 5.0);
		expected.get(hiv.getDrugClass("NNRTI")).put(hiv.getDrug("ETR"), new HashMap<>());
		expected.get(hiv.getDrugClass("NNRTI")).get(hiv.getDrug("ETR")).put(MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E,RT181C"), 5.0);
		assertEquals(expected, asiObj.getDrugClassDrugComboMutScores());
	}

	@Test
	public void testGetTriggeredDrugRules() {
		List<DrugResistanceAlgorithm<HIV>> hivAlgo = new ArrayList<>();
		hivAlgo.add(hiv.getLatestDrugResistAlgorithm("ANRS"));

		AsiResult<HIV> asiObj;
		MutationSet<HIV> mutationSets;
		AlgorithmComparison<HIV> algorithmComparison;

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivAlgo);
		asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1RT")).get(0);


		Map<Drug<HIV>, Map<String, String>> expected = new TreeMap<>();
		expected.put(hiv.getDrug("ABC"), new TreeMap<>());
		expected.get(hiv.getDrug("ABC")).put("184VI", "Possible resistance");
		expected.put(hiv.getDrug("FTC"), new TreeMap<>());
		expected.get(hiv.getDrug("FTC")).put("184VI", "Resistance");
		expected.put(hiv.getDrug("LMV"), new TreeMap<>());
		expected.get(hiv.getDrug("LMV")).put("184VI", "Resistance");
		assertEquals(expected, asiObj.getTriggeredDrugRules());
	}

	@Test
	public void testGetDrugMutScores() {
		List<DrugResistanceAlgorithm<HIV>> hivAlgo = new ArrayList<>();
		hivAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));

		AsiResult<HIV> asiObj;
		MutationSet<HIV> mutationSets;
		AlgorithmComparison<HIV> algorithmComparison;

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivAlgo);
		asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1RT")).get(0);

		Map<Drug<HIV>, Map<AAMutation<HIV>, Double>> expected = new TreeMap<>();
		expected.put(hiv.getDrug("ABC"), new TreeMap<>());
		expected.get(hiv.getDrug("ABC")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 184, "V"), 15.0);
		expected.get(hiv.getDrug("ABC")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 219, "Q"), 5.0);
		expected.put(hiv.getDrug("D4T"), new TreeMap<>());
		expected.get(hiv.getDrug("D4T")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 184, "V"), -10.0);
		expected.get(hiv.getDrug("D4T")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 219, "Q"), 10.0);
		expected.put(hiv.getDrug("TDF"), new TreeMap<>());
		expected.get(hiv.getDrug("TDF")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 184, "V"), -10.0);
		expected.get(hiv.getDrug("TDF")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 219, "Q"), 5.0);
		expected.put(hiv.getDrug("DDI"), new TreeMap<>());
		expected.get(hiv.getDrug("DDI")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 184, "V"), 10.0);
		expected.get(hiv.getDrug("DDI")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 219, "Q"), 5.0);
		expected.put(hiv.getDrug("LMV"), new TreeMap<>());
		expected.get(hiv.getDrug("LMV")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 184, "V"), 60.0);
		expected.put(hiv.getDrug("AZT"), new TreeMap<>());
		expected.get(hiv.getDrug("AZT")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 184, "V"), -10.0);
		expected.get(hiv.getDrug("AZT")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 219, "Q"), 10.0);
		expected.put(hiv.getDrug("FTC"), new TreeMap<>());
		expected.get(hiv.getDrug("FTC")).put(new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 184, "V"), 60.0);
		assertEquals(expected, asiObj.getDrugMutScores());
	}

	@Test
	public void testGetDrugComboMutScores() {
		List<DrugResistanceAlgorithm<HIV>> hivAlgo = new ArrayList<>();
		hivAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));

		AsiResult<HIV> asiObj;
		MutationSet<HIV> mutationSets;
		AlgorithmComparison<HIV> algorithmComparison;

		mutationSets = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT67AN,RT71R,RT100I,RT101E,RT181C,RT184V,RT219Q");
		algorithmComparison = new AlgorithmComparison<HIV>(mutationSets, hivAlgo);
		asiObj = algorithmComparison.getAsiList(hiv.getGene("HIV1RT")).get(0);

		Map<Drug<HIV>, Map<MutationSet<HIV>, Double>> expected = new TreeMap<>();
		expected.put(hiv.getDrug("NVP"), new HashMap<>());
		expected.get(hiv.getDrug("NVP")).put(MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E,RT181C"), 5.0);
		expected.put(hiv.getDrug("EFV"), new HashMap<>());
		expected.get(hiv.getDrug("EFV")).put(MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E,RT181C"), 5.0);
		expected.put(hiv.getDrug("ETR"), new HashMap<>());
		expected.get(hiv.getDrug("ETR")).put(MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E,RT181C"), 5.0);
		assertEquals(expected, asiObj.getDrugComboMutScores());
	}
}
