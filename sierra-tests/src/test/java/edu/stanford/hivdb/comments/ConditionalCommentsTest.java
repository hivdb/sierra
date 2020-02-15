package edu.stanford.hivdb.comments;

import static org.junit.Assert.*;

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
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.ConsensusMutation;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.AAMutation;

public class ConditionalCommentsTest {
	
	final static HIV hiv = HIV.getInstance();
	
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
	
	// Test in GeneDRAsiTest.java
//	@Test
//	public void testFromAsiMutationComments() {
//		
//	}
//	
//	@Test
//	public void testFromAsiDrugLevelComments() {
//		
//	}
}