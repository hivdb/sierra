package edu.stanford.hivdb.sequences;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SequenceTest {
	
	@Test
	public void testSanitizeSequence() {
		Sequence seq = new Sequence("header", "NNNNNNACGT");
		assertEquals(6, seq.getRemovedLeadingNs());
		assertEquals(0, seq.getRemovedTrailingNs());
		assertEquals("ACGT", seq.getSequence());
	}

}
