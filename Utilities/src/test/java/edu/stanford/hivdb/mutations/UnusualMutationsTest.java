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

import org.junit.BeforeClass;
import org.junit.Test;

public class UnusualMutationsTest {

	@BeforeClass
	public static void testInit() {
		assertFalse(UnusualMutations.getUnusualMuts().isEmpty());
	}
	
	@Test
	public void testGetHighestMutPrevalence() {

		Mutation mut1 = new Mutation(Gene.RT, 67, "N");
		assertEquals(10.373, UnusualMutations.getHighestMutPrevalence(mut1), 1e-4);

		Mutation mut2 = new Mutation(Gene.RT, 69, "KS");
		assertEquals(0.813, UnusualMutations.getHighestMutPrevalence(mut2), 1e-4);

		Mutation mut3 = new Mutation(Gene.PR, 82, "VIA");
		assertEquals(5.33, UnusualMutations.getHighestMutPrevalence(mut3), 1e-4);

		Mutation mut4 = new Mutation(Gene.RT, 67, "W");
		assertEquals(0.0, UnusualMutations.getHighestMutPrevalence(mut4), 1e-4);

		Mutation mut5 = new Mutation(Gene.RT, 67, "N*");
		assertEquals(10.373, UnusualMutations.getHighestMutPrevalence(mut5), 1e-4);

		Mutation mut6 = new Mutation(Gene.RT, 67, "*");
		assertEquals(0.0, UnusualMutations.getHighestMutPrevalence(mut6), 1e-4);

		Mutation mut7 = new Mutation(Gene.IN, 1, "F");
		assertEquals(0.0, UnusualMutations.getHighestMutPrevalence(mut7), 1e-4);
	}

	@Test
	public void testContainsUnusualMut() {
		Mutation mut1 = new Mutation(Gene.RT, 67, "N");
		assertFalse(UnusualMutations.containsUnusualMut(mut1));

		Mutation mut2 = new Mutation(Gene.RT, 69, "KS");
		assertFalse(UnusualMutations.containsUnusualMut(mut2));

		Mutation mut3 = new Mutation(Gene.PR, 82, "VIAD");
		assertTrue(UnusualMutations.containsUnusualMut(mut3));

		Mutation mut4 = new Mutation(Gene.RT, 67, "W");
		assertTrue(UnusualMutations.containsUnusualMut(mut4));

		Mutation mut5 = new Mutation(Gene.RT, 69, "_");
		assertFalse(UnusualMutations.containsUnusualMut(mut5));

		Mutation mut6 = new Mutation(Gene.PR, 69, "_");
		assertTrue(UnusualMutations.containsUnusualMut(mut6));

		Mutation mut7 = new Mutation(Gene.RT, 67, "-");
		assertFalse(UnusualMutations.containsUnusualMut(mut7));

		Mutation mut8 = new Mutation(Gene.PR, 67, "-");
		assertTrue(UnusualMutations.containsUnusualMut(mut8));
	}
}