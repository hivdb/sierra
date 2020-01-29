package edu.stanford.hivdb.mutations;

import static org.junit.Assert.*;
import org.junit.Test;

import edu.stanford.hivdb.mutations.CodonPercent;
import edu.stanford.hivdb.hivfacts.HIV;

public class CodonPercentTest {

	private static HIV hiv = HIV.getInstance();

	@Test
	public void testCodonPercent() {
		CodonPercent<HIV> mPR5A = new CodonPercent<HIV>(
			hiv.getGene("HIV1PR"), 5, "GCT", 'A',
			/* percent   = */ 5.664567000498482e-06,
			/* count     = */ 1,
			/* total     = */ 176536
		);

		assertEquals(hiv.getGene("HIV1PR"), mPR5A.getGene());
		assertEquals(5, (int) mPR5A.getPosition());
		assertEquals("GCT", (String) mPR5A.getCodon());
		assertEquals('A', (char) mPR5A.getAA());
		assertEquals(5.664567000498482e-06, mPR5A.getPercent(), 1e-18);
		assertEquals(1, (int) mPR5A.getCount());
		assertEquals(176536, (int) mPR5A.getTotal());
	}

}
