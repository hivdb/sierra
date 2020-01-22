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

import edu.stanford.hivdb.hivfacts.HIVDrug;
import edu.stanford.hivdb.hivfacts.HIVDrugClass;
import edu.stanford.hivdb.hivfacts.HIVGene;
import edu.stanford.hivdb.mutations.MutationSet;

public class FastHivdbTest {

	@Test
	public void testGetGene() {
		FastHivdb asiObj = new FastHivdb(HIVGene.valueOf("HIV1RT"), new MutationSet("RT184V"));
		assertEquals(HIVGene.valueOf("HIV1RT"), asiObj.getGene());

		asiObj = new FastHivdb(HIVGene.valueOf("HIV1PR"), new MutationSet("PR24I,PR46L,PR54V"));
		assertEquals(HIVGene.valueOf("HIV1PR"), asiObj.getGene());

		asiObj = new FastHivdb(HIVGene.valueOf("HIV1IN"), new MutationSet("IN140S,IN148H"));
		assertEquals(HIVGene.valueOf("HIV1IN"), asiObj.getGene());
	}

	@Test
	public void testGetDrugLevel() {
		FastHivdb asiObj = new FastHivdb(HIVGene.valueOf("HIV1RT"), new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals(3, asiObj.getDrugLevel(HIVDrug.TDF));

		asiObj = new FastHivdb(HIVGene.valueOf("HIV1IN"), new MutationSet("IN184A"));
		assertEquals(1, asiObj.getDrugLevel(HIVDrug.DTG));
		assertEquals(1, asiObj.getDrugLevel(HIVDrug.ABC));
	}

	@Test
	public void testGetDrugLevelText() {
		FastHivdb asiObj = new FastHivdb(HIVGene.valueOf("HIV1RT"), new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals("Low-Level Resistance", asiObj.getDrugLevelText(HIVDrug.TDF));

		asiObj = new FastHivdb(HIVGene.valueOf("HIV1IN"), new MutationSet("IN184A"));
		assertEquals("Susceptible", asiObj.getDrugLevelText(HIVDrug.DTG));
		assertEquals("Susceptible", asiObj.getDrugLevelText(HIVDrug.ABC));
	}

	@Test
	public void testGetDrugLevelSir() {
		FastHivdb asiObj;

		asiObj = new FastHivdb(HIVGene.valueOf("HIV1PR"), new MutationSet("PR73V,PR76V,PR84V,PR88S"));
		assertEquals("R", asiObj.getDrugLevelSir(HIVDrug.FPV));

		asiObj = new FastHivdb(HIVGene.valueOf("HIV1RT"), new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals("I", asiObj.getDrugLevelSir(HIVDrug.TDF));

		asiObj = new FastHivdb(HIVGene.valueOf("HIV1IN"), new MutationSet("IN184A"));
		assertEquals("S", asiObj.getDrugLevelSir(HIVDrug.DTG));
		assertEquals("S", asiObj.getDrugLevelSir(HIVDrug.ABC));

	}

	@Test
	public void testGetTotalScore() {
		FastHivdb asiObj = new FastHivdb(HIVGene.valueOf("HIV1RT"), new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
		assertEquals(15.0, asiObj.getTotalScore(HIVDrug.TDF), 1e-6);

		asiObj = new FastHivdb(HIVGene.valueOf("HIV1IN"), new MutationSet("IN184A"));
		assertEquals(0.0, asiObj.getTotalScore(HIVDrug.DTG), 1e-6);
		assertEquals(0.0, asiObj.getTotalScore(HIVDrug.ABC), 1e-6);
	}

	@Test
	public void testGetDrugClassTotalDrugScores() {
		FastHivdb asiObj = new FastHivdb(HIVGene.valueOf("HIV1RT"), new MutationSet("RT67N,RT70R,RT184V,RT219Q"));
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
		expected.get(HIVDrugClass.NNRTI).put(HIVDrug.NVP, 0.0);
		expected.get(HIVDrugClass.NNRTI).put(HIVDrug.RPV, 0.0);
		expected.get(HIVDrugClass.NNRTI).put(HIVDrug.ETR, 0.0);
		expected.get(HIVDrugClass.NNRTI).put(HIVDrug.EFV, 0.0);
		expected.get(HIVDrugClass.NNRTI).put(HIVDrug.DOR, 0.0);
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
		FastHivdb asiObj = new FastHivdb(
			HIVGene.valueOf("HIV1RT"), new MutationSet("RT67AN,RT71R,RT100I,RT101E,RT181C,RT184IV,RT219Q"));
		// RT67A is not a DRM
		MutationSet expected = new MutationSet("RT67N,RT100I,RT101E,RT181C,RT184IV,RT219Q");
		assertEquals(expected, asiObj.getTriggeredMutations());
		assertEquals(expected, asiObj.getTriggeredMutations());
		expected = new MutationSet("RT67N,RT184IV,RT219Q");
		assertEquals(expected, asiObj.getTriggeredMutations(HIVDrugClass.NRTI));
		assertEquals(expected, asiObj.getTriggeredMutations(HIVDrugClass.NRTI));
		expected = new MutationSet("RT100I,RT101E,RT181C,RT184I");
		assertEquals(expected, asiObj.getTriggeredMutations(HIVDrugClass.NNRTI));
		assertEquals(expected, asiObj.getTriggeredMutations(HIVDrugClass.NNRTI));
	}

}
