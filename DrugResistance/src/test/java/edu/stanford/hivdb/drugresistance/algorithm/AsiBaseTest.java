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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Map;

import org.junit.Test;

import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;

public class AsiBaseTest {

	@Test
	public void testGetGene() {
		Asi asiObj = new AsiHivdb(Gene.RT, new MutationSet("RT184V"));
		assertEquals(Gene.RT, asiObj.getGene());

		asiObj = new AsiHivdb(Gene.PR, new MutationSet("PR24I,PR46L,PR54V"));
		assertEquals(Gene.PR, asiObj.getGene());

		asiObj = new AsiHivdb(Gene.IN, new MutationSet("IN140S,IN148H"));
		assertEquals(Gene.IN, asiObj.getGene());
	}

	@Test
	public void testGetDrugLevel() {
		Asi asiObj = new AsiHivdb(Gene.RT, new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals(3, asiObj.getDrugLevel(Drug.TDF));

		asiObj = new AsiHivdb(Gene.IN, new MutationSet("IN184A"));
		assertEquals(1, asiObj.getDrugLevel(Drug.DTG));
		assertEquals(1, asiObj.getDrugLevel(Drug.ABC));
	}

	@Test
	public void testGetDrugLevelText() {
		Asi asiObj = new AsiHivdb(Gene.RT, new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals("Low-Level Resistance", asiObj.getDrugLevelText(Drug.TDF));

		asiObj = new AsiHivdb(Gene.IN, new MutationSet("IN184A"));
		assertEquals("Susceptible", asiObj.getDrugLevelText(Drug.DTG));
		assertEquals("Susceptible", asiObj.getDrugLevelText(Drug.ABC));
	}

	@Test
	public void testGetDrugLevelSir() {
		Asi asiObj;

		asiObj = new AsiHivdb(Gene.PR, new MutationSet("PR73V,PR76V,PR84V,PR88S"));
		assertEquals("R", asiObj.getDrugLevelSir(Drug.FPV));

		asiObj = new AsiHivdb(Gene.RT, new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals("I", asiObj.getDrugLevelSir(Drug.TDF));

		asiObj = new AsiHivdb(Gene.IN, new MutationSet("IN184A"));
		assertEquals("S", asiObj.getDrugLevelSir(Drug.DTG));
		assertEquals("S", asiObj.getDrugLevelSir(Drug.ABC));

		asiObj = new AsiRega(Gene.PR, new MutationSet("PR73V,PR76V,PR84V,PR88S"));
		assertEquals("I", asiObj.getDrugLevelSir(Drug.FPV));

		asiObj = new AsiRega(Gene.RT, new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals("S", asiObj.getDrugLevelSir(Drug.TDF));

		asiObj = new AsiRega(Gene.IN, new MutationSet("IN184A"));
		assertEquals("S", asiObj.getDrugLevelSir(Drug.DTG));
		assertEquals("S", asiObj.getDrugLevelSir(Drug.ABC));

		asiObj = new AsiAnrs(Gene.PR, new MutationSet("PR73V,PR76V,PR84V,PR88S"));
		assertEquals("S", asiObj.getDrugLevelSir(Drug.FPV));

		asiObj = new AsiAnrs(Gene.RT, new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals("S", asiObj.getDrugLevelSir(Drug.TDF));

		asiObj = new AsiAnrs(Gene.IN, new MutationSet("IN184A"));
		assertEquals("S", asiObj.getDrugLevelSir(Drug.DTG));
		assertEquals("S", asiObj.getDrugLevelSir(Drug.ABC));
	}

	@Test
	public void testGetTotalScore() {
		Asi asiObj = new AsiHivdb(Gene.RT, new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals(15.0, asiObj.getTotalScore(Drug.TDF), 1e-6);

		asiObj = new AsiHivdb(Gene.IN, new MutationSet("IN184A"));
		assertEquals(0.0, asiObj.getTotalScore(Drug.DTG), 1e-6);
		assertEquals(0.0, asiObj.getTotalScore(Drug.ABC), 1e-6);
	}

	@Test
	public void testGetDrugClassTotalDrugScores() {
		Asi asiObj = new AsiHivdb(Gene.RT, new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		Map<DrugClass, Map<Drug, Double>> expected = new EnumMap<>(DrugClass.class);
		expected.put(DrugClass.NRTI, new EnumMap<>(Drug.class));
		expected.put(DrugClass.NNRTI, new EnumMap<>(Drug.class));
		expected.get(DrugClass.NRTI).put(Drug.D4T, 40.0);
		expected.get(DrugClass.NRTI).put(Drug.AZT, 55.0);
		expected.get(DrugClass.NRTI).put(Drug.FTC, 70.0);
		expected.get(DrugClass.NRTI).put(Drug.TDF, 15.0);
		expected.get(DrugClass.NRTI).put(Drug.DDI, 40.0);
		expected.get(DrugClass.NRTI).put(Drug.LMV, 70.0);
		expected.get(DrugClass.NRTI).put(Drug.ABC, 60.0);
		expected.get(DrugClass.NNRTI).put(Drug.NVP, 0.0);
		expected.get(DrugClass.NNRTI).put(Drug.RPV, 0.0);
		expected.get(DrugClass.NNRTI).put(Drug.ETR, 0.0);
		expected.get(DrugClass.NNRTI).put(Drug.EFV, 0.0);
		assertEquals(expected, asiObj.getDrugClassTotalDrugScores());
		assertEquals(
			expected.get(DrugClass.NRTI),
			asiObj.getDrugClassTotalDrugScores(DrugClass.NRTI));
		assertEquals(
			expected.get(DrugClass.NNRTI),
			asiObj.getDrugClassTotalDrugScores(DrugClass.NNRTI));
	}

	@Test
	public void testGetTriggeredMutations() {
		Asi asiObj = new AsiHivdb(
			Gene.RT, new MutationSet("RT67AN,RT71R,RT100I,RT101E,RT181C,RT184V,RT219Q"));
		MutationSet expected = new MutationSet("RT67AN,RT100I,RT101E,RT181C,RT184V,RT219Q");
		assertEquals(expected, asiObj.getTriggeredMutations());
		assertEquals(expected, asiObj.getTriggeredMutations());
		expected = new MutationSet("RT67AN,RT184V,RT219Q");
		assertEquals(expected, asiObj.getTriggeredMutations(DrugClass.NRTI));
		assertEquals(expected, asiObj.getTriggeredMutations(DrugClass.NRTI));
		expected = new MutationSet("RT100I,RT101E,RT181C");
		assertEquals(expected, asiObj.getTriggeredMutations(DrugClass.NNRTI));
		assertEquals(expected, asiObj.getTriggeredMutations(DrugClass.NNRTI));
	}

	@Test
	public void testGetDrugClassDrugMutScores() {
		Asi asiObj = new AsiHivdb(
			Gene.RT, new MutationSet("RT67AN,RT71R,RT100I,RT101E,RT181C,RT184V,RT219Q"));
		Map<DrugClass, Map<Drug, Map<Mutation, Double>>> expected = new EnumMap<>(DrugClass.class);
		expected.put(DrugClass.NRTI, new EnumMap<>(Drug.class));
		expected.put(DrugClass.NNRTI, new EnumMap<>(Drug.class));
		expected.get(DrugClass.NNRTI).put(Drug.NVP, new TreeMap<>());
		expected.get(DrugClass.NNRTI).get(Drug.NVP).put(Mutation.parseString("RT181C"), 60.0);
		expected.get(DrugClass.NNRTI).get(Drug.NVP).put(Mutation.parseString("RT101E"), 30.0);
		expected.get(DrugClass.NNRTI).get(Drug.NVP).put(Mutation.parseString("RT100I"), 60.0);
		expected.get(DrugClass.NNRTI).put(Drug.EFV, new TreeMap<>());
		expected.get(DrugClass.NNRTI).get(Drug.EFV).put(Mutation.parseString("RT181C"), 30.0);
		expected.get(DrugClass.NNRTI).get(Drug.EFV).put(Mutation.parseString("RT101E"), 15.0);
		expected.get(DrugClass.NNRTI).get(Drug.EFV).put(Mutation.parseString("RT100I"), 60.0);
		expected.get(DrugClass.NNRTI).put(Drug.RPV, new TreeMap<>());
		expected.get(DrugClass.NNRTI).get(Drug.RPV).put(Mutation.parseString("RT181C"), 45.0);
		expected.get(DrugClass.NNRTI).get(Drug.RPV).put(Mutation.parseString("RT101E"), 45.0);
		expected.get(DrugClass.NNRTI).get(Drug.RPV).put(Mutation.parseString("RT100I"), 60.0);
		expected.get(DrugClass.NNRTI).put(Drug.ETR, new TreeMap<>());
		expected.get(DrugClass.NNRTI).get(Drug.ETR).put(Mutation.parseString("RT181C"), 30.0);
		expected.get(DrugClass.NNRTI).get(Drug.ETR).put(Mutation.parseString("RT101E"), 15.0);
		expected.get(DrugClass.NNRTI).get(Drug.ETR).put(Mutation.parseString("RT100I"), 30.0);
		expected.get(DrugClass.NRTI).put(Drug.ABC, new TreeMap<>());
		expected.get(DrugClass.NRTI).get(Drug.ABC).put(Mutation.parseString("RT184V"), 15.0);
		expected.get(DrugClass.NRTI).get(Drug.ABC).put(Mutation.parseString("RT219Q"), 5.0);
		expected.get(DrugClass.NRTI).get(Drug.ABC).put(Mutation.parseString("RT67AN"), 5.0);
		expected.get(DrugClass.NRTI).put(Drug.D4T, new TreeMap<>());
		expected.get(DrugClass.NRTI).get(Drug.D4T).put(Mutation.parseString("RT184V"), -10.0);
		expected.get(DrugClass.NRTI).get(Drug.D4T).put(Mutation.parseString("RT219Q"), 10.0);
		expected.get(DrugClass.NRTI).get(Drug.D4T).put(Mutation.parseString("RT67AN"), 15.0);
		expected.get(DrugClass.NRTI).put(Drug.TDF, new TreeMap<>());
		expected.get(DrugClass.NRTI).get(Drug.TDF).put(Mutation.parseString("RT184V"), -10.0);
		expected.get(DrugClass.NRTI).get(Drug.TDF).put(Mutation.parseString("RT219Q"), 5.0);
		expected.get(DrugClass.NRTI).get(Drug.TDF).put(Mutation.parseString("RT67AN"), 5.0);
		expected.get(DrugClass.NRTI).put(Drug.DDI, new TreeMap<>());
		expected.get(DrugClass.NRTI).get(Drug.DDI).put(Mutation.parseString("RT184V"), 10.0);
		expected.get(DrugClass.NRTI).get(Drug.DDI).put(Mutation.parseString("RT219Q"), 5.0);
		expected.get(DrugClass.NRTI).get(Drug.DDI).put(Mutation.parseString("RT67AN"), 5.0);
		expected.get(DrugClass.NRTI).put(Drug.LMV, new TreeMap<>());
		expected.get(DrugClass.NRTI).get(Drug.LMV).put(Mutation.parseString("RT184V"), 60.0);
		expected.get(DrugClass.NRTI).put(Drug.AZT, new TreeMap<>());
		expected.get(DrugClass.NRTI).get(Drug.AZT).put(Mutation.parseString("RT184V"), -10.0);
		expected.get(DrugClass.NRTI).get(Drug.AZT).put(Mutation.parseString("RT219Q"), 10.0);
		expected.get(DrugClass.NRTI).get(Drug.AZT).put(Mutation.parseString("RT67AN"), 15.0);
		expected.get(DrugClass.NRTI).put(Drug.FTC, new TreeMap<>());
		expected.get(DrugClass.NRTI).get(Drug.FTC).put(Mutation.parseString("RT184V"), 60.0);
		assertEquals(expected, asiObj.getDrugClassDrugMutScores());
	}

	@Test
	public void testGetDrugClassDrugComboMutScores() {
		Asi asiObj = new AsiHivdb(
			Gene.RT, new MutationSet("RT67AN,RT71R,RT100I,RT101E,RT181C,RT184V,RT219Q"));
		Map<DrugClass, Map<Drug, Map<MutationSet, Double>>> expected = new EnumMap<>(DrugClass.class);
		expected.put(DrugClass.NNRTI, new EnumMap<>(Drug.class));
		expected.get(DrugClass.NNRTI).put(Drug.NVP, new HashMap<>());
		expected.get(DrugClass.NNRTI).get(Drug.NVP).put(new MutationSet("RT101E,RT181C"), 5.0);
		expected.get(DrugClass.NNRTI).put(Drug.EFV, new HashMap<>());
		expected.get(DrugClass.NNRTI).get(Drug.EFV).put(new MutationSet("RT101E,RT181C"), 5.0);
		expected.get(DrugClass.NNRTI).put(Drug.ETR, new HashMap<>());
		expected.get(DrugClass.NNRTI).get(Drug.ETR).put(new MutationSet("RT101E,RT181C"), 5.0);
		assertEquals(expected, asiObj.getDrugClassDrugComboMutScores());
	}

	@Test
	public void testGetTriggeredDrugRules() {
		Asi asiObj = new AsiAnrs(
			Gene.RT, new MutationSet("RT184V,RT219Q"));
		Map<Drug, Map<String, String>> expected = new EnumMap<>(Drug.class);
		expected.put(Drug.ABC, new TreeMap<>());
		expected.get(Drug.ABC).put("184VI", "Possible resistance");
		expected.put(Drug.FTC, new TreeMap<>());
		expected.get(Drug.FTC).put("184VI", "Resistance");
		expected.put(Drug.LMV, new TreeMap<>());
		expected.get(Drug.LMV).put("184VI", "Resistance");
		assertEquals(expected, asiObj.getTriggeredDrugRules());
	}

	@Test
	public void testGetDrugMutScores() {
		Asi asiObj = new AsiHivdb(
			Gene.RT, new MutationSet("RT184V,RT219Q"));
		Map<Drug, Map<Mutation, Double>> expected = new EnumMap<>(Drug.class);
		expected.put(Drug.ABC, new TreeMap<>());
		expected.get(Drug.ABC).put(Mutation.parseString("RT184V"), 15.0);
		expected.get(Drug.ABC).put(Mutation.parseString("RT219Q"), 5.0);
		expected.put(Drug.D4T, new TreeMap<>());
		expected.get(Drug.D4T).put(Mutation.parseString("RT184V"), -10.0);
		expected.get(Drug.D4T).put(Mutation.parseString("RT219Q"), 10.0);
		expected.put(Drug.TDF, new TreeMap<>());
		expected.get(Drug.TDF).put(Mutation.parseString("RT184V"), -10.0);
		expected.get(Drug.TDF).put(Mutation.parseString("RT219Q"), 5.0);
		expected.put(Drug.DDI, new TreeMap<>());
		expected.get(Drug.DDI).put(Mutation.parseString("RT184V"), 10.0);
		expected.get(Drug.DDI).put(Mutation.parseString("RT219Q"), 5.0);
		expected.put(Drug.LMV, new TreeMap<>());
		expected.get(Drug.LMV).put(Mutation.parseString("RT184V"), 60.0);
		expected.put(Drug.AZT, new TreeMap<>());
		expected.get(Drug.AZT).put(Mutation.parseString("RT184V"), -10.0);
		expected.get(Drug.AZT).put(Mutation.parseString("RT219Q"), 10.0);
		expected.put(Drug.FTC, new TreeMap<>());
		expected.get(Drug.FTC).put(Mutation.parseString("RT184V"), 60.0);
		assertEquals(expected, asiObj.getDrugMutScores());
	}

	@Test
	public void testGetDrugComboMutScores() {
		Asi asiObj = new AsiHivdb(
			Gene.RT, new MutationSet("RT67AN,RT71R,RT100I,RT101E,RT181C,RT184V,RT219Q"));
		Map<Drug, Map<MutationSet, Double>> expected = new EnumMap<>(Drug.class);
		expected.put(Drug.NVP, new HashMap<>());
		expected.get(Drug.NVP).put(new MutationSet("RT101E,RT181C"), 5.0);
		expected.put(Drug.EFV, new HashMap<>());
		expected.get(Drug.EFV).put(new MutationSet("RT101E,RT181C"), 5.0);
		expected.put(Drug.ETR, new HashMap<>());
		expected.get(Drug.ETR).put(new MutationSet("RT101E,RT181C"), 5.0);
		assertEquals(expected, asiObj.getDrugComboMutScores());
	}
}
