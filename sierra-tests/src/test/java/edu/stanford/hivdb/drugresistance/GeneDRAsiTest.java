/*

    Copyright (C) 2019 Stanford HIVDB team

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

package edu.stanford.hivdb.drugresistance;

import static org.junit.Assert.*;
//import static org.mockito.Mockito.*;

import java.util.Collections;
import org.junit.Test;

import edu.stanford.hivdb.comments.CommentType;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.mutations.MutationType;

public class GeneDRAsiTest {
	
	private final static HIV hiv = HIV.getInstance();

	@Test
	public void testGetGene() {
		GeneDR<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1IN"), new MutationSet<HIV>(), hiv.getDrugResistAlgorithm("HIVDB_8.9"));
		assertEquals(hiv.getGene("HIV1IN"), geneDR.getGene());
	}

	@Test
	public void testGetMutationByType() {
		GeneDR<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1IN"), new MutationSet<HIV>(), hiv.getDrugResistAlgorithm("HIVDB_8.9"));
		assertEquals(Collections.emptySet(), geneDR.getMutationsByType(hiv.getMutationType("Major")));
		
		

		geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), MutationSet.parseString(hiv.getGene("HIV1RT"), "RT69T_TT, RT43E"), hiv.getDrugResistAlgorithm("HIVDB_8.9"));
		assertEquals(MutationSet.parseString(hiv.getGene("HIV1RT"), "RT69T_TT"), geneDR.getMutationsByType(hiv.getMutationType("NRTI")));
		assertEquals(MutationSet.parseString(hiv.getGene("HIV1RT"), "RT43E"), geneDR.getMutationsByType(hiv.getMutationType("Other")));
	}

	@Test
	public void testGetCommentsByType() {
		GeneDR<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1IN"), new MutationSet<HIV>(), hiv.getDrugResistAlgorithm("HIVDB_8.9"));
		assertEquals(Collections.emptyList(), geneDR.getCommentsByType(CommentType.Major));

		geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), MutationSet.parseString(hiv.getGene("HIV1RT"), "RT69T_TT, RT138D"), hiv.getDrugResistAlgorithm("HIVDB_8.9"));
		assertEquals(Collections.emptyList(), geneDR.getCommentsByType(CommentType.Major));
		assertEquals(1, geneDR.getCommentsByType(CommentType.NRTI).size());
		assertNotEquals(Collections.emptyList(), geneDR.getCommentsByType(CommentType.NRTI));
		assertEquals(1, geneDR.getCommentsByType(CommentType.Other).size());
		assertNotEquals(Collections.emptyList(), geneDR.getCommentsByType(CommentType.Other));
	}

//	@Test
//	public void testDrugClassHasScoredMuts() {
//		GeneDR geneDR = spy(new GeneDRAsi(hiv.getGene("HIV1IN"), new MutationSet()));
//		doReturn(true).when(geneDR).drugClassHasScoredIndividualMuts(HIVDrugClass.INSTI);
//		doReturn(true).when(geneDR).drugClassHasScoredComboMuts(HIVDrugClass.INSTI);
//		assertTrue(geneDR.drugClassHasScoredMuts(HIVDrugClass.INSTI));
//
//		doReturn(false).when(geneDR).drugClassHasScoredComboMuts(HIVDrugClass.INSTI);
//		assertTrue(geneDR.drugClassHasScoredMuts(HIVDrugClass.INSTI));
//
//		doReturn(false).when(geneDR).drugClassHasScoredIndividualMuts(HIVDrugClass.INSTI);
//		doReturn(true).when(geneDR).drugClassHasScoredComboMuts(HIVDrugClass.INSTI);
//		assertTrue(geneDR.drugClassHasScoredMuts(HIVDrugClass.INSTI));
//
//		doReturn(false).when(geneDR).drugClassHasScoredComboMuts(HIVDrugClass.INSTI);
//		assertFalse(geneDR.drugClassHasScoredMuts(HIVDrugClass.INSTI));
//	}
//
//	@Test
//	public void testDrugHasScoredMuts() {
//		GeneDR geneDR = spy(new GeneDRAsi(hiv.getGene("HIV1IN"), new MutationSet()));
//		doReturn(true).when(geneDR).drugHasScoredIndividualMuts(HIVDrug.RAL);
//		doReturn(true).when(geneDR).drugHasScoredComboMuts(HIVDrug.RAL);
//		assertTrue(geneDR.drugHasScoredMuts(HIVDrug.RAL));
//
//		doReturn(false).when(geneDR).drugHasScoredComboMuts(HIVDrug.RAL);
//		assertTrue(geneDR.drugHasScoredMuts(HIVDrug.RAL));
//
//		doReturn(false).when(geneDR).drugHasScoredIndividualMuts(HIVDrug.RAL);
//		doReturn(true).when(geneDR).drugHasScoredComboMuts(HIVDrug.RAL);
//		assertTrue(geneDR.drugHasScoredMuts(HIVDrug.RAL));
//
//		doReturn(false).when(geneDR).drugHasScoredComboMuts(HIVDrug.RAL);
//		assertFalse(geneDR.drugHasScoredMuts(HIVDrug.RAL));
//	}
}
