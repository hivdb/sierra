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

package edu.stanford.hivdb.drugresistance;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import org.junit.Test;

import edu.stanford.hivdb.drugresistance.database.CommentType;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutType;
import edu.stanford.hivdb.mutations.MutationSet;

public class GeneDRTest {

	@Test
	public void testGetGene() {
		GeneDR geneDR = new GeneDRFast(Gene.valueOf("HIV1IN"), new MutationSet());
		assertEquals(Gene.valueOf("HIV1IN"), geneDR.getGene());
	}

	@Test
	public void testGetMutationByType() {
		GeneDR geneDR = new GeneDRFast(Gene.valueOf("HIV1IN"), new MutationSet());
		assertEquals(Collections.emptySet(), geneDR.getMutationsByType(MutType.Major));

		geneDR = new GeneDRFast(Gene.valueOf("HIV1RT"), new MutationSet("RT69T_TT, RT43E"));
		assertEquals(new MutationSet("RT69T_TT"), geneDR.getMutationsByType(MutType.NRTI));
		assertEquals(new MutationSet("RT43E"), geneDR.getMutationsByType(MutType.Other));
	}

	@Test
	public void testGetCommentsByType() {
		GeneDR geneDR = new GeneDRFast(Gene.valueOf("HIV1IN"), new MutationSet());
		assertEquals(Collections.emptyList(), geneDR.getCommentsByType(CommentType.Major));

		geneDR = new GeneDRFast(Gene.valueOf("HIV1RT"), new MutationSet("RT69T_TT, RT138D"));
		assertEquals(Collections.emptyList(), geneDR.getCommentsByType(CommentType.Major));
		assertEquals(1, geneDR.getCommentsByType(CommentType.NRTI).size());
		assertNotEquals(Collections.emptyList(), geneDR.getCommentsByType(CommentType.NRTI));
		assertEquals(1, geneDR.getCommentsByType(CommentType.Other).size());
		assertNotEquals(Collections.emptyList(), geneDR.getCommentsByType(CommentType.Other));
	}

	@Test
	public void testDrugClassHasScoredMuts() {
		GeneDR geneDR = spy(new GeneDRFast(Gene.valueOf("HIV1IN"), new MutationSet()));
		doReturn(true).when(geneDR).drugClassHasScoredIndividualMuts(DrugClass.INSTI);
		doReturn(true).when(geneDR).drugClassHasScoredComboMuts(DrugClass.INSTI);
		assertTrue(geneDR.drugClassHasScoredMuts(DrugClass.INSTI));

		doReturn(false).when(geneDR).drugClassHasScoredComboMuts(DrugClass.INSTI);
		assertTrue(geneDR.drugClassHasScoredMuts(DrugClass.INSTI));

		doReturn(false).when(geneDR).drugClassHasScoredIndividualMuts(DrugClass.INSTI);
		doReturn(true).when(geneDR).drugClassHasScoredComboMuts(DrugClass.INSTI);
		assertTrue(geneDR.drugClassHasScoredMuts(DrugClass.INSTI));

		doReturn(false).when(geneDR).drugClassHasScoredComboMuts(DrugClass.INSTI);
		assertFalse(geneDR.drugClassHasScoredMuts(DrugClass.INSTI));
	}

	@Test
	public void testDrugHasScoredMuts() {
		GeneDR geneDR = spy(new GeneDRFast(Gene.valueOf("HIV1IN"), new MutationSet()));
		doReturn(true).when(geneDR).drugHasScoredIndividualMuts(Drug.RAL);
		doReturn(true).when(geneDR).drugHasScoredComboMuts(Drug.RAL);
		assertTrue(geneDR.drugHasScoredMuts(Drug.RAL));

		doReturn(false).when(geneDR).drugHasScoredComboMuts(Drug.RAL);
		assertTrue(geneDR.drugHasScoredMuts(Drug.RAL));

		doReturn(false).when(geneDR).drugHasScoredIndividualMuts(Drug.RAL);
		doReturn(true).when(geneDR).drugHasScoredComboMuts(Drug.RAL);
		assertTrue(geneDR.drugHasScoredMuts(Drug.RAL));

		doReturn(false).when(geneDR).drugHasScoredComboMuts(Drug.RAL);
		assertFalse(geneDR.drugHasScoredMuts(Drug.RAL));
	}
}
