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

import edu.stanford.hivdb.hivfacts.HIVDrug;
import edu.stanford.hivdb.hivfacts.HIVDrugClass;
import edu.stanford.hivdb.hivfacts.HIVGene;
import edu.stanford.hivdb.mutations.ConsensusMutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.hivfacts.HIVAAMutation;

public class AsiBaseTest {

	@Test
	public void testGetGene() {
		AsiResult asiObj = new AsiHivdb(HIVGene.valueOf("HIV1RT"), new MutationSet("RT184V"));
		assertEquals(HIVGene.valueOf("HIV1RT"), asiObj.getGene());

		asiObj = new AsiHivdb(HIVGene.valueOf("HIV1PR"), new MutationSet("PR24I,PR46L,PR54V"));
		assertEquals(HIVGene.valueOf("HIV1PR"), asiObj.getGene());

		asiObj = new AsiHivdb(HIVGene.valueOf("HIV1IN"), new MutationSet("IN140S,IN148H"));
		assertEquals(HIVGene.valueOf("HIV1IN"), asiObj.getGene());
	}

	@Test
	public void testGetDrugLevel() {
		AsiResult asiObj = new AsiHivdb(HIVGene.valueOf("HIV1RT"), new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals(3, asiObj.getDrugLevel(HIVDrug.TDF));

		asiObj = new AsiHivdb(HIVGene.valueOf("HIV1IN"), new MutationSet("IN184A"));
		assertEquals(1, asiObj.getDrugLevel(HIVDrug.DTG));
		assertEquals(1, asiObj.getDrugLevel(HIVDrug.ABC));
	}

	@Test
	public void testGetDrugLevelText() {
		AsiResult asiObj = new AsiHivdb(HIVGene.valueOf("HIV1RT"), new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals("Low-Level Resistance", asiObj.getDrugLevelText(HIVDrug.TDF));

		asiObj = new AsiHivdb(HIVGene.valueOf("HIV1IN"), new MutationSet("IN184A"));
		assertEquals("Susceptible", asiObj.getDrugLevelText(HIVDrug.DTG));
		assertEquals("Susceptible", asiObj.getDrugLevelText(HIVDrug.ABC));
	}

	@Test
	public void testGetDrugLevelSir() {
		AsiResult asiObj;

		asiObj = new AsiHivdb(HIVGene.valueOf("HIV1PR"), new MutationSet("PR73V,PR76V,PR84V,PR88S"));
		assertEquals("R", asiObj.getDrugLevelSir(HIVDrug.FPV));

		asiObj = new AsiHivdb(HIVGene.valueOf("HIV1RT"), new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals("I", asiObj.getDrugLevelSir(HIVDrug.TDF));

		asiObj = new AsiHivdb(HIVGene.valueOf("HIV1IN"), new MutationSet("IN184A"));
		assertEquals("S", asiObj.getDrugLevelSir(HIVDrug.DTG));
		assertEquals("S", asiObj.getDrugLevelSir(HIVDrug.ABC));

		asiObj = new AsiRega(HIVGene.valueOf("HIV1PR"), new MutationSet("PR73V,PR76V,PR84V,PR88S"));
		assertEquals("I", asiObj.getDrugLevelSir(HIVDrug.FPV));

		asiObj = new AsiRega(HIVGene.valueOf("HIV1RT"), new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals("S", asiObj.getDrugLevelSir(HIVDrug.TDF));

		asiObj = new AsiRega(HIVGene.valueOf("HIV1IN"), new MutationSet("IN184A"));
		assertEquals("S", asiObj.getDrugLevelSir(HIVDrug.DTG));
		assertEquals("S", asiObj.getDrugLevelSir(HIVDrug.ABC));

		asiObj = new AsiAnrs(HIVGene.valueOf("HIV1PR"), new MutationSet("PR73V,PR76V,PR84V,PR88S"));
		assertEquals("S", asiObj.getDrugLevelSir(HIVDrug.FPV));

		asiObj = new AsiAnrs(HIVGene.valueOf("HIV1RT"), new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals("S", asiObj.getDrugLevelSir(HIVDrug.TDF));

		asiObj = new AsiAnrs(HIVGene.valueOf("HIV1IN"), new MutationSet("IN184A"));
		assertEquals("S", asiObj.getDrugLevelSir(HIVDrug.DTG));
		assertEquals("S", asiObj.getDrugLevelSir(HIVDrug.ABC));
	}

	@Test
	public void testGetTotalScore() {
		AsiResult asiObj = new AsiHivdb(HIVGene.valueOf("HIV1RT"), new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals(15.0, asiObj.getTotalScore(HIVDrug.TDF), 1e-6);

		asiObj = new AsiHivdb(HIVGene.valueOf("HIV1IN"), new MutationSet("IN184A"));
		assertEquals(0.0, asiObj.getTotalScore(HIVDrug.DTG), 1e-6);
		assertEquals(0.0, asiObj.getTotalScore(HIVDrug.ABC), 1e-6);
	}

	@Test
	public void testGetDrugClassTotalDrugScores() {
		AsiResult asiObj = new AsiHivdb(HIVGene.valueOf("HIV1RT"), new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		Map<HIVDrugClass, Map<HIVDrug, Double>> expected = new EnumMap<>(HIVDrugClass.class);
		expected.put(HIVDrugClass.NRTI, new EnumMap<>(HIVDrug.class));
		expected.put(HIVDrugClass.NNRTI, new EnumMap<>(HIVDrug.class));
		expected.get(HIVDrugClass.NRTI).put(HIVDrug.D4T, 40.0);
		expected.get(HIVDrugClass.NRTI).put(HIVDrug.AZT, 55.0);
		expected.get(HIVDrugClass.NRTI).put(HIVDrug.FTC, 70.0);
		expected.get(HIVDrugClass.NRTI).put(HIVDrug.TDF, 15.0);
		expected.get(HIVDrugClass.NRTI).put(HIVDrug.DDI, 40.0);
		expected.get(HIVDrugClass.NRTI).put(HIVDrug.LMV, 70.0);
		expected.get(HIVDrugClass.NRTI).put(HIVDrug.ABC, 60.0);
		expected.get(HIVDrugClass.NNRTI).put(HIVDrug.DOR, 0.0);
		expected.get(HIVDrugClass.NNRTI).put(HIVDrug.NVP, 0.0);
		expected.get(HIVDrugClass.NNRTI).put(HIVDrug.RPV, 0.0);
		expected.get(HIVDrugClass.NNRTI).put(HIVDrug.ETR, 0.0);
		expected.get(HIVDrugClass.NNRTI).put(HIVDrug.EFV, 0.0);
		assertEquals(expected, asiObj.getDrugClassTotalDrugScores());
		assertEquals(
			expected.get(HIVDrugClass.NRTI),
			asiObj.getDrugClassTotalDrugScores(HIVDrugClass.NRTI));
		assertEquals(
			expected.get(HIVDrugClass.NNRTI),
			asiObj.getDrugClassTotalDrugScores(HIVDrugClass.NNRTI));
	}

	@Test
	public void testGetTriggeredMutations() {
		AsiResult asiObj = new AsiHivdb(
			HIVGene.valueOf("HIV1RT"), new MutationSet("RT67AN,RT71R,RT100I,RT101E,RT181C,RT184V,RT219Q"));
		MutationSet expected = new MutationSet("RT67AN,RT100I,RT101E,RT181C,RT184V,RT219Q");
		assertEquals(expected, asiObj.getTriggeredMutations());
		assertEquals(expected, asiObj.getTriggeredMutations());
		expected = new MutationSet("RT67AN,RT184V,RT219Q");
		assertEquals(expected, asiObj.getTriggeredMutations(HIVDrugClass.NRTI));
		assertEquals(expected, asiObj.getTriggeredMutations(HIVDrugClass.NRTI));
		expected = new MutationSet("RT100I,RT101E,RT181C");
		assertEquals(expected, asiObj.getTriggeredMutations(HIVDrugClass.NNRTI));
		assertEquals(expected, asiObj.getTriggeredMutations(HIVDrugClass.NNRTI));
	}

	@Test
	public void testGetDrugClassDrugMutScores() {
		AsiResult asiObj = new AsiHivdb(
			HIVGene.valueOf("HIV1RT"), new MutationSet("RT67AN,RT71R,RT100I,RT101E,RT181C,RT184V,RT219Q"));
		Map<HIVDrugClass, Map<HIVDrug, Map<HIVAAMutation, Double>>> expected = new EnumMap<>(HIVDrugClass.class);
		expected.put(HIVDrugClass.NRTI, new EnumMap<>(HIVDrug.class));
		expected.put(HIVDrugClass.NNRTI, new EnumMap<>(HIVDrug.class));
		expected.get(HIVDrugClass.NNRTI).put(HIVDrug.NVP, new TreeMap<>());
		expected.get(HIVDrugClass.NNRTI).get(HIVDrug.NVP).put(ConsensusMutation.parseString("RT181C"), 60.0);
		expected.get(HIVDrugClass.NNRTI).get(HIVDrug.NVP).put(ConsensusMutation.parseString("RT101E"), 30.0);
		expected.get(HIVDrugClass.NNRTI).get(HIVDrug.NVP).put(ConsensusMutation.parseString("RT100I"), 60.0);
		expected.get(HIVDrugClass.NNRTI).put(HIVDrug.EFV, new TreeMap<>());
		expected.get(HIVDrugClass.NNRTI).get(HIVDrug.EFV).put(ConsensusMutation.parseString("RT181C"), 30.0);
		expected.get(HIVDrugClass.NNRTI).get(HIVDrug.EFV).put(ConsensusMutation.parseString("RT101E"), 15.0);
		expected.get(HIVDrugClass.NNRTI).get(HIVDrug.EFV).put(ConsensusMutation.parseString("RT100I"), 60.0);
		expected.get(HIVDrugClass.NNRTI).put(HIVDrug.RPV, new TreeMap<>());
		expected.get(HIVDrugClass.NNRTI).get(HIVDrug.RPV).put(ConsensusMutation.parseString("RT181C"), 45.0);
		expected.get(HIVDrugClass.NNRTI).get(HIVDrug.RPV).put(ConsensusMutation.parseString("RT101E"), 45.0);
		expected.get(HIVDrugClass.NNRTI).get(HIVDrug.RPV).put(ConsensusMutation.parseString("RT100I"), 60.0);
		expected.get(HIVDrugClass.NNRTI).put(HIVDrug.ETR, new TreeMap<>());
		expected.get(HIVDrugClass.NNRTI).get(HIVDrug.ETR).put(ConsensusMutation.parseString("RT181C"), 30.0);
		expected.get(HIVDrugClass.NNRTI).get(HIVDrug.ETR).put(ConsensusMutation.parseString("RT101E"), 15.0);
		expected.get(HIVDrugClass.NNRTI).get(HIVDrug.ETR).put(ConsensusMutation.parseString("RT100I"), 30.0);
		expected.get(HIVDrugClass.NNRTI).put(HIVDrug.DOR, new TreeMap<>());
		expected.get(HIVDrugClass.NNRTI).get(HIVDrug.DOR).put(ConsensusMutation.parseString("RT100I"), 15.0);
		expected.get(HIVDrugClass.NNRTI).get(HIVDrug.DOR).put(ConsensusMutation.parseString("RT101E"), 15.0);
		expected.get(HIVDrugClass.NNRTI).get(HIVDrug.DOR).put(ConsensusMutation.parseString("RT181C"), 10.0);
		expected.get(HIVDrugClass.NRTI).put(HIVDrug.ABC, new TreeMap<>());
		expected.get(HIVDrugClass.NRTI).get(HIVDrug.ABC).put(ConsensusMutation.parseString("RT184V"), 15.0);
		expected.get(HIVDrugClass.NRTI).get(HIVDrug.ABC).put(ConsensusMutation.parseString("RT219Q"), 5.0);
		expected.get(HIVDrugClass.NRTI).get(HIVDrug.ABC).put(ConsensusMutation.parseString("RT67AN"), 5.0);
		expected.get(HIVDrugClass.NRTI).put(HIVDrug.D4T, new TreeMap<>());
		expected.get(HIVDrugClass.NRTI).get(HIVDrug.D4T).put(ConsensusMutation.parseString("RT184V"), -10.0);
		expected.get(HIVDrugClass.NRTI).get(HIVDrug.D4T).put(ConsensusMutation.parseString("RT219Q"), 10.0);
		expected.get(HIVDrugClass.NRTI).get(HIVDrug.D4T).put(ConsensusMutation.parseString("RT67AN"), 15.0);
		expected.get(HIVDrugClass.NRTI).put(HIVDrug.TDF, new TreeMap<>());
		expected.get(HIVDrugClass.NRTI).get(HIVDrug.TDF).put(ConsensusMutation.parseString("RT184V"), -10.0);
		expected.get(HIVDrugClass.NRTI).get(HIVDrug.TDF).put(ConsensusMutation.parseString("RT219Q"), 5.0);
		expected.get(HIVDrugClass.NRTI).get(HIVDrug.TDF).put(ConsensusMutation.parseString("RT67AN"), 5.0);
		expected.get(HIVDrugClass.NRTI).put(HIVDrug.DDI, new TreeMap<>());
		expected.get(HIVDrugClass.NRTI).get(HIVDrug.DDI).put(ConsensusMutation.parseString("RT184V"), 10.0);
		expected.get(HIVDrugClass.NRTI).get(HIVDrug.DDI).put(ConsensusMutation.parseString("RT219Q"), 5.0);
		expected.get(HIVDrugClass.NRTI).get(HIVDrug.DDI).put(ConsensusMutation.parseString("RT67AN"), 5.0);
		expected.get(HIVDrugClass.NRTI).put(HIVDrug.LMV, new TreeMap<>());
		expected.get(HIVDrugClass.NRTI).get(HIVDrug.LMV).put(ConsensusMutation.parseString("RT184V"), 60.0);
		expected.get(HIVDrugClass.NRTI).put(HIVDrug.AZT, new TreeMap<>());
		expected.get(HIVDrugClass.NRTI).get(HIVDrug.AZT).put(ConsensusMutation.parseString("RT184V"), -10.0);
		expected.get(HIVDrugClass.NRTI).get(HIVDrug.AZT).put(ConsensusMutation.parseString("RT219Q"), 10.0);
		expected.get(HIVDrugClass.NRTI).get(HIVDrug.AZT).put(ConsensusMutation.parseString("RT67AN"), 15.0);
		expected.get(HIVDrugClass.NRTI).put(HIVDrug.FTC, new TreeMap<>());
		expected.get(HIVDrugClass.NRTI).get(HIVDrug.FTC).put(ConsensusMutation.parseString("RT184V"), 60.0);
		assertEquals(expected, asiObj.getDrugClassDrugMutScores());
	}

	@Test
	public void testGetDrugClassDrugComboMutScores() {
		AsiResult asiObj = new AsiHivdb(
			HIVGene.valueOf("HIV1RT"), new MutationSet("RT67AN,RT71R,RT100I,RT101E,RT181C,RT184V,RT219Q"));
		Map<HIVDrugClass, Map<HIVDrug, Map<MutationSet, Double>>> expected = new EnumMap<>(HIVDrugClass.class);
		expected.put(HIVDrugClass.NNRTI, new EnumMap<>(HIVDrug.class));
		expected.get(HIVDrugClass.NNRTI).put(HIVDrug.NVP, new HashMap<>());
		expected.get(HIVDrugClass.NNRTI).get(HIVDrug.NVP).put(new MutationSet("RT101E,RT181C"), 5.0);
		expected.get(HIVDrugClass.NNRTI).put(HIVDrug.EFV, new HashMap<>());
		expected.get(HIVDrugClass.NNRTI).get(HIVDrug.EFV).put(new MutationSet("RT101E,RT181C"), 5.0);
		expected.get(HIVDrugClass.NNRTI).put(HIVDrug.ETR, new HashMap<>());
		expected.get(HIVDrugClass.NNRTI).get(HIVDrug.ETR).put(new MutationSet("RT101E,RT181C"), 5.0);
		assertEquals(expected, asiObj.getDrugClassDrugComboMutScores());
	}

	@Test
	public void testGetTriggeredDrugRules() {
		AsiResult asiObj = new AsiAnrs(
			HIVGene.valueOf("HIV1RT"), new MutationSet("RT184V,RT219Q"));
		Map<HIVDrug, Map<String, String>> expected = new EnumMap<>(HIVDrug.class);
		expected.put(HIVDrug.ABC, new TreeMap<>());
		expected.get(HIVDrug.ABC).put("184VI", "Possible resistance");
		expected.put(HIVDrug.FTC, new TreeMap<>());
		expected.get(HIVDrug.FTC).put("184VI", "Resistance");
		expected.put(HIVDrug.LMV, new TreeMap<>());
		expected.get(HIVDrug.LMV).put("184VI", "Resistance");
		assertEquals(expected, asiObj.getTriggeredDrugRules());
	}

	@Test
	public void testGetDrugMutScores() {
		AsiResult asiObj = new AsiHivdb(
			HIVGene.valueOf("HIV1RT"), new MutationSet("RT184V,RT219Q"));
		Map<HIVDrug, Map<HIVAAMutation, Double>> expected = new EnumMap<>(HIVDrug.class);
		expected.put(HIVDrug.ABC, new TreeMap<>());
		expected.get(HIVDrug.ABC).put(ConsensusMutation.parseString("RT184V"), 15.0);
		expected.get(HIVDrug.ABC).put(ConsensusMutation.parseString("RT219Q"), 5.0);
		expected.put(HIVDrug.D4T, new TreeMap<>());
		expected.get(HIVDrug.D4T).put(ConsensusMutation.parseString("RT184V"), -10.0);
		expected.get(HIVDrug.D4T).put(ConsensusMutation.parseString("RT219Q"), 10.0);
		expected.put(HIVDrug.TDF, new TreeMap<>());
		expected.get(HIVDrug.TDF).put(ConsensusMutation.parseString("RT184V"), -10.0);
		expected.get(HIVDrug.TDF).put(ConsensusMutation.parseString("RT219Q"), 5.0);
		expected.put(HIVDrug.DDI, new TreeMap<>());
		expected.get(HIVDrug.DDI).put(ConsensusMutation.parseString("RT184V"), 10.0);
		expected.get(HIVDrug.DDI).put(ConsensusMutation.parseString("RT219Q"), 5.0);
		expected.put(HIVDrug.LMV, new TreeMap<>());
		expected.get(HIVDrug.LMV).put(ConsensusMutation.parseString("RT184V"), 60.0);
		expected.put(HIVDrug.AZT, new TreeMap<>());
		expected.get(HIVDrug.AZT).put(ConsensusMutation.parseString("RT184V"), -10.0);
		expected.get(HIVDrug.AZT).put(ConsensusMutation.parseString("RT219Q"), 10.0);
		expected.put(HIVDrug.FTC, new TreeMap<>());
		expected.get(HIVDrug.FTC).put(ConsensusMutation.parseString("RT184V"), 60.0);
		assertEquals(expected, asiObj.getDrugMutScores());
	}

	@Test
	public void testGetDrugComboMutScores() {
		AsiResult asiObj = new AsiHivdb(
			HIVGene.valueOf("HIV1RT"), new MutationSet("RT67AN,RT71R,RT100I,RT101E,RT181C,RT184V,RT219Q"));
		Map<HIVDrug, Map<MutationSet, Double>> expected = new EnumMap<>(HIVDrug.class);
		expected.put(HIVDrug.NVP, new HashMap<>());
		expected.get(HIVDrug.NVP).put(new MutationSet("RT101E,RT181C"), 5.0);
		expected.put(HIVDrug.EFV, new HashMap<>());
		expected.get(HIVDrug.EFV).put(new MutationSet("RT101E,RT181C"), 5.0);
		expected.put(HIVDrug.ETR, new HashMap<>());
		expected.get(HIVDrug.ETR).put(new MutationSet("RT101E,RT181C"), 5.0);
		assertEquals(expected, asiObj.getDrugComboMutScores());
	}
}
