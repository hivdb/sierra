package edu.stanford.hivdb.comments;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.MutationType;


public class CommentTypeTest {
	
	final static HIV hiv = HIV.getInstance();
	
	@Test
	public void testCommentType() {
		assertEquals(CommentType.values().length, 7);
		
		CommentType.valueOf("Major");
		CommentType.valueOf("Accessory");
		CommentType.valueOf("Other");
		CommentType.valueOf("NRTI");
		CommentType.valueOf("NNRTI");
		CommentType.valueOf("Major");
		CommentType.valueOf("Dosage");
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCommentTypeWithException() {
		CommentType.valueOf("");
	}
	
	@Test
	public void testFromMutType() {
		for(MutationType<HIV> mutationType : hiv.getMutationTypes()) {
			CommentType.fromMutType(mutationType);
		}
		
		// Explain:
		//     No assertion because CommentType.fromMutType would raise an error
		//     if the mutationType is invalid.
	}
}