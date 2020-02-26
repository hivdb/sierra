package edu.stanford.hivdb.comments;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;


public class ConditionTypeTest {
	
	final static HIV hiv = HIV.getInstance();
	
	@Test
	public void testConditionType() {
		assertEquals(CommentType.values().length, 6);
		
		ConditionType.valueOf("MUTATION");
		ConditionType.valueOf("DRUGLEVEL");

		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testConditionTypeWithException() {
		ConditionType.valueOf("");
	}
}