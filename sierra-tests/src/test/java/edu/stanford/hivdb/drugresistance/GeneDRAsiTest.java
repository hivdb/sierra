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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import edu.stanford.hivdb.comments.CommentType;
import edu.stanford.hivdb.drugs.DrugResistanceAlgorithm;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.sequences.AlignedGeneSeq;
import edu.stanford.hivdb.sequences.NucAminoAligner;
import edu.stanford.hivdb.sequences.Sequence;

public class GeneDRAsiTest {
	
	private final static HIV hiv = HIV.getInstance();
	
	@Test
	public void testGetResistanceByGeneFromAlignedGeneSeqs() {
		Sequence testSeq = Sequence.fromGenbank("AF096883");
		
		List<AlignedGeneSeq<HIV>> alignedGeneSeqs = NucAminoAligner.getInstance(hiv).align(testSeq).getAlignedGeneSequences();
		
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getLatestDrugResistAlgorithm("HIVDB");
		
		assertFalse(GeneDRAsi.getResistanceByGeneFromAlignedGeneSeqs(alignedGeneSeqs, algorithm).isEmpty());
	}
	
	@Test
	public void testGetResistanceByGeneFromReads() {
		
	}
	
	@Test
	public void testConstructor() {
		Sequence testSeq = Sequence.fromGenbank("AF096883");
		List<AlignedGeneSeq<HIV>> alignedGeneSeqs = NucAminoAligner.getInstance(hiv).align(testSeq).getAlignedGeneSequences();
		
		AlignedGeneSeq<HIV> alignedGeneSeq = alignedGeneSeqs.get(0);
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getLatestDrugResistAlgorithm("HIVDB");
		
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), alignedGeneSeq, algorithm);
		
		assertNotNull(geneDR);
	}
	
	@Test
	public void testConstructor2() {		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getLatestDrugResistAlgorithm("HIVDB");
		
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertNotNull(geneDR);
	}
	
	@Test
	public void testParallelConstructor() {
		Set<MutationSet<HIV>> mutationSets = new TreeSet<>();
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		mutationSets.add(mutations);
		
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getLatestDrugResistAlgorithm("HIVDB");
		
		assertFalse(GeneDRAsi.parallelConstructor(hiv.getGene("HIV1RT"), mutationSets, algorithm).isEmpty());
		
	}
	
	@Test
	public void testGetAsiObject() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getLatestDrugResistAlgorithm("HIVDB");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertNotNull(geneDR.getAsiObject());
	}
	
	@Test
	public void testGetAlgorithm() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getLatestDrugResistAlgorithm("HIVDB");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertEquals(geneDR.getAlgorithm(), algorithm);
	}

	@Test
	public void testGetVersion() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getLatestDrugResistAlgorithm("HIVDB");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertEquals(geneDR.getVersion(), geneDR.getAlgorithm());
	}
	
	@Test
	public void testGetAllComments() {
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getLatestDrugResistAlgorithm("HIVDB");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertFalse(geneDR.getAllComments().isEmpty());
		
	}
	
	@Test
	public void testGetDrugClassTotalDrugScores() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getLatestDrugResistAlgorithm("HIVDB");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertFalse(geneDR.getDrugClassTotalDrugScores(hiv.getDrugClass("NRTI")).isEmpty());
	}

	@Test
	public void testGetTotalDrugScore() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertEquals(geneDR.getTotalDrugScore(hiv.getDrug("EFV")), Double.valueOf(0.0));
	}
	
	@Test
	public void testGetDrugLevel() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertEquals(geneDR.getDrugLevel(hiv.getDrug("EFV")), Integer.valueOf(1));
	}
	
	@Test
	public void testGetDrugLevelText() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertEquals(geneDR.getDrugLevelText(hiv.getDrug("EFV")), "Susceptible");
	}
	
	@Test
	public void testGetDrugLevelSIR() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertEquals(geneDR.getDrugLevelSIR(hiv.getDrug("EFV")), "S");
	}

	@Test
	public void testGetGene() {
		GeneDR<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1IN"), new MutationSet<HIV>(), hiv.getDrugResistAlgorithm("HIVDB_8.9"));
		assertEquals(hiv.getGene("HIV1IN"), geneDR.getGene());
	}

	@Test
	public void testGetMutations() {
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertFalse(geneDR.getMutations().isEmpty());
	}
	
	@Test
	public void testGroupMutationsByTypes() {
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertFalse(geneDR.groupMutationsByTypes().isEmpty());
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
	
	@Test
	public void testGetIndividualMutAllDrugScoresForDrugClass() {
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT41L");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertFalse(geneDR.getIndividualMutAllDrugScoresForDrugClass(hiv.getDrugClass("NRTI")).isEmpty());
	
	}
	
	@Test
	public void testGetComboMutAllDrugScoresForDrugClass() {
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E, RT181C");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertFalse(geneDR.getComboMutAllDrugScoresForDrugClass(hiv.getDrugClass("NNRTI")).isEmpty());
	
	}

	@Test
	public void testDrugClassHasScoredMuts() {
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E, RT181C");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertTrue(geneDR.drugClassHasScoredMuts(hiv.getDrugClass("NNRTI")));

		assertFalse(geneDR.drugClassHasScoredMuts(hiv.getDrugClass("INI")));
	}
	
	@Test
	public void testDrugClassHasScoredIndividualMuts() {
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E, RT181C");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertTrue(geneDR.drugClassHasScoredIndividualMuts(hiv.getDrugClass("NNRTI")));

	}

	@Test
	public void testDrugClassHasScoredComboMuts() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E, RT181C");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertTrue(geneDR.drugClassHasScoredComboMuts(hiv.getDrugClass("NNRTI")));
	}
	
	@Test
	public void testGetScoredIndividualMutsForDrug() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E, RT181C");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertFalse(geneDR.getScoredIndividualMutsForDrug(hiv.getDrug("NVP")).isEmpty());
	}
	
	@Test
	public void testGetScoredComboMutsForDrug() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E, RT181C");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertFalse(geneDR.getScoredComboMutsForDrug(hiv.getDrug("NVP")).isEmpty());
	}
	
	@Test
	public void testDrugHasScoredMuts() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E, RT181C");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertTrue(geneDR.drugHasScoredMuts(hiv.getDrug("NVP")));
	}
	
	@Test
	public void testDrugHasScoredIndividualMuts() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E, RT181C");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertTrue(geneDR.drugHasScoredIndividualMuts(hiv.getDrug("NVP")));
		
		assertFalse(geneDR.drugHasScoredIndividualMuts(hiv.getDrug("ATV")));
				
	}
	
	@Test
	public void testDrugHasScoredComboMuts() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E, RT181C");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDRAsi<HIV> geneDR = new GeneDRAsi<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertTrue(geneDR.drugHasScoredComboMuts(hiv.getDrug("NVP")));
		
		assertFalse(geneDR.drugHasScoredComboMuts(hiv.getDrug("ATV")));
	}
}
