package edu.stanford.hivdb.mutations;


import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.Sets;

import edu.stanford.hivdb.hivfacts.HIV;


public class GenePositionTest {
	static final int MAX_PR_POS = 99;
	static final int MAX_RT_POS = 560;
	static final int MAX_IN_POS = 288;
	static final int TOTAL_POS = MAX_PR_POS + MAX_RT_POS + MAX_IN_POS;

	static final HIV hiv = HIV.getInstance();

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	// Potential issues:
	// 1. Doesn't throw out of bounds exceptions.
    @Test
    public void testConstruction() {

    	GenePosition<HIV> prGP = new GenePosition<HIV>(hiv.getGene("HIV1PR"), MAX_PR_POS);
    	assertEquals(prGP.getGene(), hiv.getGene("HIV1PR"));
    	assertEquals(prGP.getPosition(), Integer.valueOf(MAX_PR_POS));

    	GenePosition<HIV> rtGP = new GenePosition<HIV>(hiv.getGene("HIV1RT"), MAX_RT_POS);
    	assertEquals(rtGP.getGene(), hiv.getGene("HIV1RT"));
    	assertEquals(rtGP.getPosition(), Integer.valueOf(MAX_RT_POS));

    	GenePosition<HIV> inGP = new GenePosition<HIV>(hiv.getGene("HIV1IN"), MAX_IN_POS);
    	assertEquals(inGP.getGene(), hiv.getGene("HIV1IN"));
    	assertEquals(inGP.getPosition(), Integer.valueOf(MAX_IN_POS));

    	GenePosition<HIV> inGPMin = new GenePosition<HIV>(hiv.getGene("HIV1IN"), Integer.MIN_VALUE);
    	assertEquals(inGPMin.getGene(), hiv.getGene("HIV1IN"));
    	assertEquals(inGPMin.getPosition(), Integer.valueOf(Integer.MIN_VALUE));

    	GenePosition<HIV> inGPMax = new GenePosition<HIV>(hiv.getGene("HIV1IN"), Integer.MAX_VALUE);
    	assertEquals(inGPMax.getGene(), hiv.getGene("HIV1IN"));
    	assertEquals(inGPMax.getPosition(), Integer.valueOf(Integer.MAX_VALUE));

    }

    @SuppressWarnings("unchecked")
	@Test
    public void testGetGenePositionsBetween() {
    	assertEquals(
            Sets.newHashSet(
                new GenePosition<HIV>(hiv.getGene("HIV1PR"), 97),
                new GenePosition<HIV>(hiv.getGene("HIV1PR"), 98),
                new GenePosition<HIV>(hiv.getGene("HIV1PR"), 99),
                new GenePosition<HIV>(hiv.getGene("HIV1RT"), 1),
                new GenePosition<HIV>(hiv.getGene("HIV1RT"), 2)
                ),
    	    GenePosition.getGenePositionsBetween(
                new GenePosition<HIV>(hiv.getGene("HIV1PR"), 97),
                new GenePosition<HIV>(hiv.getGene("HIV1RT"), 2)
            ));

    	Set<GenePosition<HIV>> gps1 = GenePosition.getGenePositionsBetween(
    			new GenePosition<HIV>(hiv.getGene("HIV1PR"), 50),
    			new GenePosition<HIV>(hiv.getGene("HIV1IN"), 50)
    		);
    	assertEquals(660, gps1.size());

    	Set<GenePosition<HIV>> gps2 = GenePosition.getGenePositionsBetween(
    			new GenePosition<HIV>(hiv.getGene("HIV2APR"), 50),
    			new GenePosition<HIV>(hiv.getGene("HIV2AIN"), 50)
    		);
    	assertEquals(659, gps2.size());

    	Set<GenePosition<HIV>> gps3 = GenePosition.getGenePositionsBetween(
    			new GenePosition<HIV>(hiv.getGene("HIV1PR"), 50),
    			new GenePosition<HIV>(hiv.getGene("HIV1PR"), 95)
    		);
    	assertEquals(46, gps3.size());

    	Set<GenePosition<HIV>> gps4 = GenePosition.getGenePositionsBetween(
    			new GenePosition<HIV>(hiv.getGene("HIV2BPR"), 50),
    			new GenePosition<HIV>(hiv.getGene("HIV2BRT"), 50)
    		);
    	assertEquals(100, gps4.size());

    }

    @Test
	public void testGetPositionInStrain() {
		assertEquals(1,   (int) new GenePosition<HIV>(hiv.getGene("HIV1PR"), 1).getPositionInStrain());
		assertEquals(100, (int) new GenePosition<HIV>(hiv.getGene("HIV1RT"), 1).getPositionInStrain());
		assertEquals(660, (int) new GenePosition<HIV>(hiv.getGene("HIV1IN"), 1).getPositionInStrain());
		assertEquals(444, (int) new GenePosition<HIV>(hiv.getGene("HIV2ART"), 345).getPositionInStrain());
		assertEquals(445, (int) new GenePosition<HIV>(hiv.getGene("HIV2ART"), 346).getPositionInStrain());
		assertEquals(658, (int) new GenePosition<HIV>(hiv.getGene("HIV2ART"), 559).getPositionInStrain());
		assertEquals(659, (int) new GenePosition<HIV>(hiv.getGene("HIV2AIN"), 1).getPositionInStrain());
	}

	 @Test
	 public void testGetGenePositionsBetweenMismatchedStrain() {
	 	expectedEx.expect(IllegalArgumentException.class);
	 	expectedEx.expectMessage("Virus strain of `start` and `end` positions must be the same.");
	 	GenePosition.getGenePositionsBetween(
	 		new GenePosition<HIV>(hiv.getGene("HIV1PR"), 50),
	 		new GenePosition<HIV>(hiv.getGene("HIV2AIN"), 50)
	 	);
	}

	@Test(expected = NullPointerException.class)
	public void testCompareToException() {
		final GenePosition<HIV> gp = new GenePosition<HIV>(hiv.getGene("HIV1PR"), 1);
		assertEquals(gp.compareTo(null), 1);
	}

    @Test
	public void testCompareTo() {
		assertEquals(0, new GenePosition<HIV>(hiv.getGene("HIV1PR"), 5).compareTo(
                        new GenePosition<HIV>(hiv.getGene("HIV1PR"), 5)));
		assertEquals(1, new GenePosition<HIV>(hiv.getGene("HIV1RT"), 5).compareTo(
                        new GenePosition<HIV>(hiv.getGene("HIV1PR"), 5)));
		assertEquals(1, new GenePosition<HIV>(hiv.getGene("HIV1PR"), 6).compareTo(
                        new GenePosition<HIV>(hiv.getGene("HIV1PR"), 5)));
		expectedEx.expect(NullPointerException.class);
		expectedEx.expectMessage("Null is incomprable.");
		new GenePosition<HIV>(hiv.getGene("HIV1PR"), 5).compareTo(null);

		 final GenePosition<HIV> prGPMin = new GenePosition<HIV>(hiv.getGene("HIV1PR"), 1);
		 final GenePosition<HIV> prGPMid = new GenePosition<HIV>(hiv.getGene("HIV1PR"), 50);
		 final GenePosition<HIV> prGPMax = new GenePosition<HIV>(hiv.getGene("HIV1PR"), 99);
		 final GenePosition<HIV> rtGP 	 = new GenePosition<HIV>(hiv.getGene("HIV1RT"), 99);
		 assertEquals(prGPMin.compareTo(rtGP), -1);
		 assertEquals(prGPMin.compareTo(prGPMin), 0);
		 assertEquals(prGPMin.compareTo(prGPMid), -1);
		 assertEquals(prGPMax.compareTo(prGPMid), 1);
	}

	@Test
	 @SuppressWarnings("unlikely-arg-type")
	public void testEquals() {
		GenePosition<HIV> gpPR5 = new GenePosition<HIV>(hiv.getGene("HIV1PR"), 5);
		assertFalse(gpPR5.equals(null));
		assertTrue(gpPR5.equals(gpPR5));
		assertTrue(gpPR5.equals(new GenePosition<HIV>(hiv.getGene("HIV1PR"), 5)));
		assertFalse(gpPR5.equals(new GenePosition<HIV>(hiv.getGene("HIV1PR"), 6)));
		assertFalse(gpPR5.equals(new GenePosition<HIV>(hiv.getGene("HIV1RT"), 5)));
		assertFalse(gpPR5.equals(new GenePosition<HIV>(hiv.getGene("HIV1RT"), 6)));
		
		assertFalse(gpPR5.equals("HIV1PR:5"));
	}

	@Test
	public void testToString() {
		assertEquals("HIV1PR:5", new GenePosition<HIV>(hiv.getGene("HIV1PR"), 5).toString());
	}

	@Test
	public void testHashCode() {
		assertEquals(1, new GenePosition<HIV>(hiv.getGene("HIV1PR"), 6).hashCode() -
                        new GenePosition<HIV>(hiv.getGene("HIV1PR"), 5).hashCode());
		assertEquals(new GenePosition<HIV>(hiv.getGene("HIV1RT"), 5).hashCode(),
                     new GenePosition<HIV>(hiv.getGene("HIV1RT"), 5).hashCode());
		assertNotEquals(new GenePosition<HIV>(hiv.getGene("HIV1PR"), 5).hashCode(),
                        new GenePosition<HIV>(hiv.getGene("HIV1RT"), 5).hashCode());

		Set<Integer> hashCodes = new HashSet<Integer>();
		for (int pos = 1; pos <= MAX_RT_POS; pos++) {
			if (pos <= MAX_PR_POS) hashCodes.add(new GenePosition<HIV>(hiv.getGene("HIV1PR"), pos).hashCode());
			if (pos <= MAX_IN_POS) hashCodes.add(new GenePosition<HIV>(hiv.getGene("HIV1IN"), pos).hashCode());
			hashCodes.add(new GenePosition<HIV>(hiv.getGene("HIV1RT"), pos).hashCode());
		}
		assertEquals(hashCodes.size(), TOTAL_POS);
	}


}