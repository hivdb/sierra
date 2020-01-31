package edu.stanford.hivdb.mutations;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.AminoAcidPercent;

public class AminoAcidPercentTest {

	final static HIV hiv = HIV.getInstance();

	@Test
	public void test() {
		AminoAcidPercent<HIV> mPR5A = new AminoAcidPercent<HIV>(
			hiv.getGene("HIV1PR"), 5, 'A',
			/* percent   = */ 6.7597712493409226e-06,
			/* count     = */ 1,
			/* total     = */ 147934,
			/* reason    = */ "PCNT",
			/* isUnusual = */ true
		);
		assertEquals(hiv.getGene("HIV1PR"), mPR5A.getGene());
		assertEquals(5, (int) mPR5A.getPosition());
		assertEquals('A', (char) mPR5A.getAA());
		assertEquals(6.7597712493409226e-06, mPR5A.getPercent(), 1e-18);
		assertEquals(1, (int) mPR5A.getCount());
		assertEquals(147934, (int) mPR5A.getTotal());
		assertEquals("PCNT", mPR5A.getReason());
		assertTrue(mPR5A.isUnusual());
	}

}
