package edu.stanford.hivdb.sequences;

import static org.junit.Assert.*;

import org.junit.Test;

public class AlignmentMessageTest {
	
	@Test
	public void testEquals() {
		assertEquals(
			new AlignmentMessage("WARNING", "message 1"),
			new AlignmentMessage("WARNING", "message 1")
		);
		
		assertNotEquals(
			new AlignmentMessage("WARNING", "message 1"),
			null
		);
		
		assertNotEquals(
			new AlignmentMessage("WARNING", "message 1"),
			new AlignmentMessage("WARNING", "message 2")
		);
	}

}
