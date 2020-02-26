package edu.stanford.hivdb.comments;

import static org.junit.Assert.*;

import java.util.ArrayList;
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
	private static BoundComment<HIV> comment = null;
	private final static Mutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 215, 'V');
	
	private static void initInstance() {
		List<String> highlightTest = new ArrayList<>();
		
		comment = new BoundComment<HIV>(
				hiv.getStrain("HIV1"),
				"Comment Name",
				hiv.getDrugClass("NRTI"),
				CommentType.NRTI,
				"Comment",
				highlightTest,
				mutation
				);
	}
	
	static {
		initInstance();
	}
	
	@Test
	public void testBoundComment() {
		assertNotNull(comment);
	}
	
	@Test
	public void testGetName() {
		assertEquals(comment.getName(), "Comment Name");
	}
	
	@Test
	public void testGetType() {
		assertEquals(comment.getType(), CommentType.NRTI);
	}
	
	@Test
	public void testGetText() {
		assertEquals(comment.getText(), "Comment");
	}
	
	@Test
	public void testGetHighlightText() {
		assertTrue(comment.getHighlightText().isEmpty());
	}
	
	@Test
	public void testGetBoundMutation() {
		assertEquals(comment.getBoundMutation(), mutation);
	}
	
	@Test
	public void testGetGene() {
		assertEquals(comment.getGene(), hiv.getGene("HIV1RT"));
	}
	
	@Test
	public void testDrugClass() {
		assertEquals(comment.drugClass(), hiv.getDrugClass("NRTI"));
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