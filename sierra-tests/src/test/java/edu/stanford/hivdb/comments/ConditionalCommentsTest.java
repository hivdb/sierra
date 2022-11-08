package edu.stanford.hivdb.comments;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.AAMutation;


public class ConditionalCommentsTest {
	
	private final static HIV hiv = HIV.getInstance();
	private final static ConditionalComments<HIV> comments = hiv.getConditionalComments();
	
	@Rule
	public ExpectedException thrown = ExpectedException.none();
	
	@Test
	public void testGetInstance() {
		assertNotNull(comments);
	}
	
	@Test
	public void testGetComments() {
		
		Mutation<HIV> mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 215, 'V');
		
		assertEquals(comments.getComments(mutation).size(), 1);
		
		mutation = new AAMutation<HIV>(hiv.getGene("HIV1RT"), 211, 'T');
		
		assertEquals(comments.getComments(mutation).size(), 0);
	}
}