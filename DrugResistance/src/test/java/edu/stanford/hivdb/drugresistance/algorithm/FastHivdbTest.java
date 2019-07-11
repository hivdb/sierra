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
import java.util.Map;
import org.junit.Test;

import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;

public class FastHivdbTest {

	@Test
	public void testGetGene() {
		FastHivdb asiObj = new FastHivdb(Gene.RT, new MutationSet("RT184V"));
		assertEquals(Gene.RT, asiObj.getGene());

		asiObj = new FastHivdb(Gene.PR, new MutationSet("PR24I,PR46L,PR54V"));
		assertEquals(Gene.PR, asiObj.getGene());

		asiObj = new FastHivdb(Gene.IN, new MutationSet("IN140S,IN148H"));
		assertEquals(Gene.IN, asiObj.getGene());
	}

	@Test
	public void testGetDrugLevel() {
		FastHivdb asiObj = new FastHivdb(Gene.RT, new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals(3, asiObj.getDrugLevel(Drug.TDF));

		asiObj = new FastHivdb(Gene.IN, new MutationSet("IN184A"));
		assertEquals(1, asiObj.getDrugLevel(Drug.DTG));
		assertEquals(1, asiObj.getDrugLevel(Drug.ABC));
	}

	@Test
	public void testGetDrugLevelText() {
		FastHivdb asiObj = new FastHivdb(Gene.RT, new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals("Low-Level Resistance", asiObj.getDrugLevelText(Drug.TDF));

		asiObj = new FastHivdb(Gene.IN, new MutationSet("IN184A"));
		assertEquals("Susceptible", asiObj.getDrugLevelText(Drug.DTG));
		assertEquals("Susceptible", asiObj.getDrugLevelText(Drug.ABC));
	}

	@Test
	public void testGetDrugLevelSir() {
		FastHivdb asiObj;

		asiObj = new FastHivdb(Gene.PR, new MutationSet("PR73V,PR76V,PR84V,PR88S"));
		assertEquals("R", asiObj.getDrugLevelSir(Drug.FPV));

		asiObj = new FastHivdb(Gene.RT, new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals("I", asiObj.getDrugLevelSir(Drug.TDF));

		asiObj = new FastHivdb(Gene.IN, new MutationSet("IN184A"));
		assertEquals("S", asiObj.getDrugLevelSir(Drug.DTG));
		assertEquals("S", asiObj.getDrugLevelSir(Drug.ABC));

	}

	@Test
	public void testGetTotalScore() {
		FastHivdb asiObj = new FastHivdb(Gene.RT, new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals(15.0, asiObj.getTotalScore(Drug.TDF), 1e-6);

		asiObj = new FastHivdb(Gene.IN, new MutationSet("IN184A"));
		assertEquals(0.0, asiObj.getTotalScore(Drug.DTG), 1e-6);
		assertEquals(0.0, asiObj.getTotalScore(Drug.ABC), 1e-6);
	}

	@Test
	public void testGetDrugClassTotalDrugScores() {
		FastHivdb asiObj = new FastHivdb(Gene.RT, new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
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
		expected.get(DrugClass.NNRTI).put(Drug.DOR, 0.0);
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
		FastHivdb asiObj = new FastHivdb(
			Gene.RT, new MutationSet("RT67AN,RT71R,RT100I,RT101E,RT181C,RT184IV,RT219Q"));
		// RT67A is not a DRM
		MutationSet expected = new MutationSet("RT67N,RT100I,RT101E,RT181C,RT184IV,RT219Q");
		assertEquals(expected, asiObj.getTriggeredMutations());
		assertEquals(expected, asiObj.getTriggeredMutations());
		expected = new MutationSet("RT67N,RT184IV,RT219Q");
		assertEquals(expected, asiObj.getTriggeredMutations(DrugClass.NRTI));
		assertEquals(expected, asiObj.getTriggeredMutations(DrugClass.NRTI));
		expected = new MutationSet("RT100I,RT101E,RT181C,RT184I");
		assertEquals(expected, asiObj.getTriggeredMutations(DrugClass.NNRTI));
		assertEquals(expected, asiObj.getTriggeredMutations(DrugClass.NNRTI));
	}

}
