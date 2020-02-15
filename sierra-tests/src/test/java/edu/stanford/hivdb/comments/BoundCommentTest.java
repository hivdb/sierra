package edu.stanford.hivdb.comments;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.AAMutation;
import edu.stanford.hivdb.mutations.ConsensusMutation;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.viruses.Gene;


public class BoundCommentTest {
	
	final static HIV hiv = HIV.getInstance();

	@Test
	public void testGetInstance() {
		ConditionalComments<HIV> comments = hiv.getConditionalComments();

		Map<DrugClass<HIV>, MutationSet<HIV>> mutMap = hiv.getDrugResistMutations();
		DrugClass<HIV> drugclass = hiv.getDrugClass("PI");
		MutationSet<HIV> mutations = mutMap.get(drugclass);

		Gene<HIV> gene = hiv.getGene("HIV1PR");
		Mutation<HIV> mut = mutations.get(gene, 10);

		assertTrue(comments.getComments(mut).get(0) instanceof BoundComment);
	}



	@Test
	public void testGetCommentsFromMutOfInsertion() {
		final AAMutation<HIV> mut = new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 69, "_SS");
		final List<BoundComment<HIV>> result = hiv.getConditionalComments().getComments(mut);
		for (BoundComment<HIV> cmt : result) {
			assertEquals(cmt.getBoundMutation().getAAs(), "_");
		}
	}

	@Test
	public void testGetCommentsFromMutOfDeletion() {
		final AAMutation<HIV> mut = new ConsensusMutation<HIV>(hiv.getGene("HIV1RT"), 67, "-");
		final List<BoundComment<HIV>> result = hiv.getConditionalComments().getComments(mut);
		for (BoundComment<HIV> cmt : result) {
			assertEquals(cmt.getBoundMutation().getAAs(), "-");
		}
	}
}