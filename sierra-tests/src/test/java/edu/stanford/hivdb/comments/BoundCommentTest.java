package edu.stanford.hivdb.comments;

import static org.junit.Assert.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;

import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.AAMutation;
import edu.stanford.hivdb.mutations.GenePosition;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;


public class BoundCommentTest {
	
	final static HIV hiv = HIV.getInstance();
	private static BoundComment<HIV> mutationComment = null;
	private static BoundComment<HIV> dosageComment = null;
	private final static Mutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 215, 'V');
	
	private static void initInstance() {
		mutationComment = new BoundComment<HIV>(
				hiv.getStrain("HIV1"),
				"Comment Name",
				hiv.getDrugClass("NRTI"),
				CommentType.NRTI,
				"Comment",
				Collections.emptyList(),
				mutation);
		dosageComment = new BoundComment<HIV>(
				hiv.getStrain("HIV1"),
				"Dossage Comment",
				hiv.getDrugClass("NRTI"),
				CommentType.Dosage,
				"Dosage comment content",
				Collections.emptyList(),
				null);
	}
	
	static {
		initInstance();
	}
	
	@Test
	public void testBoundComment() {
		assertNotNull(mutationComment);
	}
	
	@Test
	public void testGetName() {
		assertEquals(mutationComment.getName(), "Comment Name");
	}
	
	@Test
	public void testGetType() {
		assertEquals(mutationComment.getType(), CommentType.NRTI);
	}
	
	@Test
	public void testGetText() {
		assertEquals(mutationComment.getText(), "Comment");
	}
	
	@Test
	public void testGetHighlightText() {
		assertTrue(mutationComment.getHighlightText().isEmpty());
	}
	
	@Test
	public void testGetBoundMutation() {
		assertEquals(mutationComment.getBoundMutation(), mutation);
	}
	
	@Test
	public void testGetBoundMutationFromDosageComment() {
		assertNull(dosageComment.getBoundMutation());
	}
	
	@Test
	public void testGetTriggeredAAs() {
		assertEquals("V", mutationComment.getTriggeredAAs());
		assertNull(dosageComment.getTriggeredAAs());
	}
	
	@Test
	public void testGetGene() {
		assertEquals(mutationComment.getGene(), hiv.getGene("HIV1RT"));
	}
	
	@Test
	public void testDrugClass() {
		assertEquals(mutationComment.drugClass(), hiv.getDrugClass("NRTI"));
	}

	// Common Usage Test Case
	@Test
	public void testGetFromDRM() {
		Map<DrugClass<HIV>, MutationSet<HIV>> drms = hiv.getDrugResistMutations();
		DrugClass<HIV> drugclass = hiv.getDrugClass("PI");
		MutationSet<HIV> mutations = drms.get(drugclass);
		GenePosition<HIV> genePos = new GenePosition<HIV>(hiv.getGene("HIV1PR"), 10);
		Mutation<HIV> mutation = mutations.get(genePos);

		ConditionalComments<HIV> comments = hiv.getConditionalComments();
		List<BoundComment<HIV>> boundComments = comments.getComments(mutation);	
		
		assertTrue(boundComments.get(0) instanceof BoundComment);
	}

	@Test
	public void testGetFromInsertion() {
		final AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 69, new char[] {'_', 'G', 'D', 'C'});
				
		final List<BoundComment<HIV>> boundComments = hiv.getConditionalComments().getComments(mutation);
				
		assertEquals(boundComments.size(), 4);
	}

	@Test
	public void testGetFromDeletion() {
		final AAMutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 68, new char[] {'-', 'G'});
		final List<BoundComment<HIV>> boundComments = hiv.getConditionalComments().getComments(mutation);
		
		assertEquals(boundComments.size(), 2);
	}
}