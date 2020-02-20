package edu.stanford.hivdb.comments;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.hivdb.comments.BoundComment;
import edu.stanford.hivdb.comments.CommentType;
import edu.stanford.hivdb.comments.ConditionType;
import edu.stanford.hivdb.comments.ConditionalComment;
import edu.stanford.hivdb.comments.ConditionalComments;
import edu.stanford.hivdb.drugresistance.algorithm.AlgorithmComparison;
import edu.stanford.hivdb.drugresistance.algorithm.AsiResult;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.drugs.DrugResistanceAlgorithm;
import edu.stanford.hivdb.mutations.ConsensusMutation;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.hivfacts.hiv2.HIV2;
import edu.stanford.hivdb.mutations.AAMutation;

import org.fstrf.stanfordAsiInterpreter.resistance.definition.CommentDefinition;


public class ConditionalCommentsTest {
	
	final static HIV hiv = HIV.getInstance();
	final static HIV2 hiv2 = HIV2.getInstance();
	
	@Test
	public void testGetInstance() {
		ConditionalComments<HIV> comments = hiv.getConditionalComments();
		assertNotNull(comments);

	}
	
	@Test
	public void testGetComments() {
		ConditionalComments<HIV> comments = hiv.getConditionalComments();
		Map<DrugClass<HIV>, MutationSet<HIV>> mutMap = hiv.getDrugResistMutations();
		// System.out.println(mutMap);
		DrugClass<HIV> drugclass = hiv.getDrugClass("PI");
		
		MutationSet<HIV> mutations = mutMap.get(drugclass);
		
		Gene<HIV> gene = hiv.getGene("HIV1PR");
		
		Mutation<HIV> mut = mutations.get(gene, 10);
		
		assertEquals(comments.getComments(mut).size(), 1);
	}
	
	@Test
	public void testFromAsiMutationCommets() {
		
		ConditionalComments<HIV> comments = hiv.getConditionalComments();
		
		// Mutation Type conditional comment
		CommentDefinition commentD = new CommentDefinition("RT184VI", "M184V/I cause high-level in vitro resistance to 3TC and FT");
		List<CommentDefinition> defs = new ArrayList<>();
		defs.add(commentD);
		
		MutationSet<HIV> muts = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");
		
		assertEquals(comments.fromAsiMutationComments(defs, muts).size(), 1);
		
	}
	
	@Test
	public void testFromAsiMutationCommentsNotMatch() {
		
		ConditionalComments<HIV> comments = hiv.getConditionalComments();
		
		// Mutation Type conditional comment
		CommentDefinition commentD = new CommentDefinition("RT184VI", "M184V/I cause high-level in vitro resistance to 3TC and FT");
		List<CommentDefinition> defs = new ArrayList<>();
		defs.add(commentD);
		
		
		// Mutation RT181V & Comment RT184V
		MutationSet<HIV> muts = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT181V");
		
		assertEquals(comments.fromAsiMutationComments(defs, muts).size(), 0);
	}
	
	@Test
	public void testFromAsiMutationCommentsForHIV2() {
		
		ConditionalComments<HIV2> comments = hiv2.getConditionalComments();
		
		// HIV2B RT201A comment
		CommentDefinition commentD = new CommentDefinition("HIV2BRT210ACDEFGHIKLMPQRSTVWY_",
				"210W is an HIV-1 associated TAM.");
		List<CommentDefinition> defs = new ArrayList<>();
		defs.add(commentD);
		
		// HIV2A RT210A mutation
		MutationSet<HIV2> muts = MutationSet.parseString(hiv2.getGene("HIV2ART"), "RT210A");
		
		assertEquals(comments.fromAsiMutationComments(defs, muts).size(), 0);
	}
	
	@Test(expected = RuntimeException.class)
	public void testFromAsiMutationCommentsWithException() {
		List<DrugResistanceAlgorithm<HIV>> hivdbAlgo = new ArrayList<>();
		hivdbAlgo.add(hiv.getLatestDrugResistAlgorithm("HIVDB"));


		MutationSet<HIV> muts = MutationSet.parseString(hiv.getGene("HIV1RT"), "RT184V");

		// DrugLevel type conditional comment
		CommentDefinition commentD = new CommentDefinition("DRVHighAndTPVIntermediate", "There is high-level DRV resistance and intermediate TPV resistance. ");
		List<CommentDefinition> defs = new ArrayList<>();
		defs.add(commentD);
		
		ConditionalComments<HIV> comments = hiv.getConditionalComments();
		
		comments.fromAsiMutationComments(defs, muts);
		
		
	}
	
//	@Test
//	public void testFromAsiDrugLevelComments() {
//		
//	}
}