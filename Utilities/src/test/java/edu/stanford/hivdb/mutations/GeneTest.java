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

package edu.stanford.hivdb.mutations;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import edu.stanford.hivdb.drugs.DrugClass;

public class GeneTest {
	@Test
	public void testGetDrugClasses() {
		assertEquals(
			Arrays.asList(new DrugClass[] {DrugClass.PI}),
			Gene.PR.getDrugClasses());
		assertEquals(
			Arrays.asList(new DrugClass[] {DrugClass.NRTI, DrugClass.NNRTI}),
			Gene.RT.getDrugClasses());
		assertEquals(
			Arrays.asList(new DrugClass[] {DrugClass.INSTI}),
			Gene.IN.getDrugClasses());
	}

	@Test
	public void testGetMutationTypes() {
		assertEquals(
			Arrays.asList(new MutType[] {MutType.Major, MutType.Accessory, MutType.Other}),
			Gene.PR.getMutationTypes());
		assertEquals(
			Arrays.asList(new MutType[] {MutType.NRTI, MutType.NNRTI, MutType.Other}),
			Gene.RT.getMutationTypes());
		assertEquals(
			Arrays.asList(new MutType[] {MutType.Major, MutType.Accessory, MutType.Other}),
			Gene.IN.getMutationTypes());
	}

	@Test
	public void testGetScoredMutTypes() {
		assertEquals(
			Arrays.asList(new MutType[] {MutType.Major, MutType.Accessory}),
			Gene.PR.getScoredMutTypes());
		assertEquals(
			Arrays.asList(new MutType[] {MutType.NRTI, MutType.NNRTI}),
			Gene.RT.getScoredMutTypes());
		assertEquals(
			Arrays.asList(new MutType[] {MutType.Major, MutType.Accessory}),
			Gene.IN.getScoredMutTypes());
	}

	@Test
	public void testGetLength() {
		assertEquals(99, Gene.PR.getLength());
		assertEquals(560, Gene.RT.getLength());
		assertEquals(288, Gene.IN.getLength());
	}

	@Test
	public void testGetConsensus() {
		assertEquals("T", Gene.PR.getConsensus(4));
		assertEquals("IDK", Gene.IN.getConsensus(5, 3));
		assertEquals(560, Gene.RT.getConsensus().length());
	}
}
