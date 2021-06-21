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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import edu.stanford.hivdb.comments.CommentType;
import edu.stanford.hivdb.drugresistance.algorithm.DrugResistanceAlgorithm;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.sequences.AlignedGeneSeq;
import edu.stanford.hivdb.sequences.Aligner;
import edu.stanford.hivdb.sequences.Sequence;

public class GeneDRTest {
	
	private final static HIV hiv = HIV.getInstance();
	
	@Test
	public void testNewFromAlignedGeneSeqs() {
		Sequence testSeq = Sequence.fromGenbank("AF096883");
		
		List<AlignedGeneSeq<HIV>> alignedGeneSeqs = Aligner.getInstance(hiv).align(testSeq).getAlignedGeneSequences();
		
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getLatestDrugResistAlgorithm("HIVDB");
		
		assertFalse(GeneDR.newFromAlignedGeneSeqs(alignedGeneSeqs, algorithm).isEmpty());
	}
	
	@Test
	public void testNewFromReads() {
		
	}
	
	@Test
	public void testConstructor$AlignedGeneSeq() {
		Sequence testSeq = Sequence.fromGenbank("AF096883");
		List<AlignedGeneSeq<HIV>> alignedGeneSeqs = Aligner.getInstance(hiv).align(testSeq).getAlignedGeneSequences();
		
		AlignedGeneSeq<HIV> alignedGeneSeq = alignedGeneSeqs.get(0);
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getLatestDrugResistAlgorithm("HIVDB");
		
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), alignedGeneSeq, algorithm);
		
		assertNotNull(geneDR);
	}
	
	@Test
	public void testConstructor$MutationSet() {		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getLatestDrugResistAlgorithm("HIVDB");
		
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertNotNull(geneDR);
	}
	
	@Test
	public void testNewFromMutationSets() {
		Set<MutationSet<HIV>> mutationSets = new TreeSet<>();
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		mutationSets.add(mutations);
		
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getLatestDrugResistAlgorithm("HIVDB");
		
		assertFalse(GeneDR.newFromMutationSets(hiv.getGene("HIV1RT"), mutationSets, algorithm).isEmpty());
		
	}
	
	@Test
	public void testGetAlgorithm() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getLatestDrugResistAlgorithm("HIVDB");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertEquals(geneDR.getAlgorithm(), algorithm);
	}

	@Test
	public void testGetVersion() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getLatestDrugResistAlgorithm("HIVDB");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertEquals(geneDR.getVersion(), geneDR.getAlgorithm());
	}
	
	@Test
	public void testGetAllComments() {
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getLatestDrugResistAlgorithm("HIVDB");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertFalse(geneDR.getComments().isEmpty());
		
	}
	
	@Test
	public void testGetTotalDrugScores() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getLatestDrugResistAlgorithm("HIVDB");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertFalse(geneDR.getTotalDrugScores(hiv.getDrugClass("NRTI")).isEmpty());
	}

	@Test
	public void testGetTotalDrugScore() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertEquals(geneDR.getTotalDrugScore(hiv.getDrug("EFV")), Double.valueOf(0.0));
	}
	
	@Test
	public void testGetDrugLevel() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertEquals(geneDR.getDrugLevel(hiv.getDrug("EFV")), Integer.valueOf(1));
	}
	
	@Test
	public void testGetDrugLevelText() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertEquals(geneDR.getDrugLevelText(hiv.getDrug("EFV")), "Susceptible");
	}
	
	@Test
	public void testGetDrugLevelSIR() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertEquals(geneDR.getDrugLevelSIR(hiv.getDrug("EFV")), "S");
	}

	@Test
	public void testGetGene() {
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1IN"), new MutationSet<HIV>(), hiv.getDrugResistAlgorithm("HIVDB_8.9"));
		assertEquals(hiv.getGene("HIV1IN"), geneDR.getGene());
	}

	@Test
	public void testGetMutations() {
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertFalse(geneDR.getMutations().isEmpty());
	}
	
	@Test
	public void testGroupMutationsByTypes() {
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertFalse(geneDR.groupMutationsByTypes().isEmpty());
	}

	
	@Test
	public void testGetMutationByType() {
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1IN"), new MutationSet<HIV>(), hiv.getDrugResistAlgorithm("HIVDB_8.9"));
		assertEquals(Collections.emptySet(), geneDR.getMutations(hiv.getMutationType("Major")));

		geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), MutationSet.parseString(hiv.getGene("HIV1RT"), "RT69T_TT, RT43E"), hiv.getDrugResistAlgorithm("HIVDB_8.9"));
		assertEquals(MutationSet.parseString(hiv.getGene("HIV1RT"), "RT69T_TT"), geneDR.getMutations(hiv.getMutationType("NRTI")));
		assertEquals(MutationSet.parseString(hiv.getGene("HIV1RT"), "RT43E"), geneDR.getMutations(hiv.getMutationType("Other")));
	}

	@Test
	public void testGetCommentsByType() {
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1IN"), new MutationSet<HIV>(), hiv.getDrugResistAlgorithm("HIVDB_8.9"));
		assertEquals(Collections.emptyList(), geneDR.getComments(CommentType.Major));

		geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), MutationSet.parseString(hiv.getGene("HIV1RT"), "RT69T_TT, RT138D"), hiv.getDrugResistAlgorithm("HIVDB_8.9"));
		assertEquals(Collections.emptyList(), geneDR.getComments(CommentType.Major));
		assertEquals(1, geneDR.getComments(CommentType.NRTI).size());
		assertNotEquals(Collections.emptyList(), geneDR.getComments(CommentType.NRTI));
		assertEquals(1, geneDR.getComments(CommentType.Other).size());
		assertNotEquals(Collections.emptyList(), geneDR.getComments(CommentType.Other));
	}
	
	@Test
	public void testGetDrugSuscsCase1() {
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT41L");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertFalse(geneDR.getDrugSuscs(ds -> ds.drugClassIs("NRTI")).isEmpty());
	
	}
	
	@Test
	public void testGetDrugComboMutScores$DrugClass() {
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E, RT181C");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertFalse(geneDR.getDrugComboMutScores(hiv.getDrugClass("NNRTI")).isEmpty());
	
	}

	@Test
	public void testHasScoredMuts$DrugClass() {
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E, RT181C");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertTrue(geneDR.hasScoredMuts(hiv.getDrugClass("NNRTI")));

		assertFalse(geneDR.hasScoredMuts(hiv.getDrugClass("INI")));
	}
	
	@Test
	public void testHasScoredIndividualMuts$DrugClass() {
		
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E, RT181C");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertTrue(geneDR.hasScoredIndividualMuts(hiv.getDrugClass("NNRTI")));

	}

	@Test
	public void testHasScoredComboMuts$DrugClass() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E, RT181C");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertTrue(geneDR.hasScoredComboMuts(hiv.getDrugClass("NNRTI")));
	}
	
	@Test
	public void testGetMutScores$Drug() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E, RT181C");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertFalse(geneDR.getMutScores(hiv.getDrug("NVP")).isEmpty());
	}
	
	@Test
	public void testGetMutScores$UseMaxScore() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184IV");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		Map<MutationSet<HIV>, Double> expects = new HashMap<>();
		expects.put(mutations, -10.0);
		assertEquals(expects, geneDR.getDrugSusc(hiv.getDrug("AZT")).getPartialScores());
	}
	
	@Test
	public void testGetComboMutScores$Drug() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E, RT181C");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertFalse(geneDR.getComboMutScores(hiv.getDrug("NVP")).isEmpty());
	}
	
	@Test
	public void testHasScoredMuts() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E, RT181C");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertTrue(geneDR.hasScoredMuts(hiv.getDrug("NVP")));
	}
	
	@Test
	public void testHasScoredIndividualMuts$Drug() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E, RT181C");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertTrue(geneDR.hasScoredIndividualMuts(hiv.getDrug("NVP")));
		
		assertFalse(geneDR.hasScoredIndividualMuts(hiv.getDrug("ATV")));
				
	}
	
	@Test
	public void testHasScoredComboMuts$Drug() {
		MutationSet<HIV> mutations = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT101E, RT181C");
		DrugResistanceAlgorithm<HIV> algorithm = hiv.getDrugResistAlgorithm("HIVDB_8.9");
		GeneDR<HIV> geneDR = new GeneDR<HIV>(hiv.getGene("HIV1RT"), mutations, algorithm);
		
		assertTrue(geneDR.hasScoredComboMuts(hiv.getDrug("NVP")));

		assertFalse(geneDR.hasScoredComboMuts(hiv.getDrug("ATV")));
	}

}
