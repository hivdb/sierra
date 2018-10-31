package edu.stanford.hivdb.mutations;

import static org.junit.Assert.*;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

public class GenePositionTest {
	static final int MAX_PR_POS = 99;
	static final int MAX_RT_POS = 560;
	static final int MAX_IN_POS = 288;
	static final int TOTAL_POS = MAX_PR_POS + MAX_RT_POS + MAX_IN_POS;
	
	// Potential issues: 
	// 1. Doesn't throw out of bounds exceptions. 
	@Test
	public void testConstruction() {
		final GenePosition prGP = new GenePosition(Gene.PR, MAX_PR_POS);
		final GenePosition rtGP = new GenePosition(Gene.RT, MAX_RT_POS);
		final GenePosition inGP = new GenePosition(Gene.IN, MAX_IN_POS);
		final GenePosition inGPMin = new GenePosition(Gene.IN, Integer.MIN_VALUE);
		final GenePosition inGPMax = new GenePosition(Gene.IN, Integer.MAX_VALUE);
		assertEquals(prGP.gene, Gene.PR);
		assertEquals(rtGP.gene, Gene.RT);
		assertEquals(inGP.gene, Gene.IN);
		assertEquals(prGP.position, Integer.valueOf(MAX_PR_POS));
		assertEquals(rtGP.position, Integer.valueOf(MAX_RT_POS));
		assertEquals(inGP.position, Integer.valueOf(MAX_IN_POS));
		assertEquals(inGPMax.position, Integer.valueOf(Integer.MAX_VALUE));
		assertEquals(inGPMin.position, Integer.valueOf(Integer.MIN_VALUE));
	}
	
	// Potential issues: 
	// 1. Case sensitive
	// 2. Doesn't throw exceptions for malformed strings.
	//	  Perhaps this method could parse the string with a regex and  
	//	  throw an exception if the input doesn't match it. 
	@Test
	public void testConstructionFromString() {
		final GenePosition prGP = new GenePosition("PR:99");
		final GenePosition rtGP = new GenePosition("RT:560");
		final GenePosition inGP = new GenePosition("IN:288");
		assertEquals(prGP.gene, Gene.PR);
		assertEquals(rtGP.gene, Gene.RT);
		assertEquals(inGP.gene, Gene.IN);
		assertEquals(prGP.position, Integer.valueOf(MAX_PR_POS));
		assertEquals(rtGP.position, Integer.valueOf(MAX_RT_POS));
		assertEquals(inGP.position, Integer.valueOf(MAX_IN_POS));
		assertEquals("PR:99", prGP.toString());
		assertEquals("RT:560", rtGP.toString());
		assertEquals("IN:288", inGP.toString());
	}
	
	@Test
	public void testEquals() {
		final GenePosition prGP = new GenePosition(Gene.PR, MAX_PR_POS);
		final GenePosition prGPFromStr = new GenePosition("PR:99");
		final GenePosition rtGPFromStr = new GenePosition("RT:560");
		assertTrue(prGPFromStr.equals(prGPFromStr));
		assertTrue(prGPFromStr.equals(prGP));
		assertFalse(prGPFromStr.equals(null));
		assertFalse(prGPFromStr.equals(rtGPFromStr));
		assertFalse(prGPFromStr.equals(Gene.PR));
	}
	
	@Test
	public void testCompareTo() {
		final GenePosition prGPMin = new GenePosition(Gene.PR, 1);
		final GenePosition prGPMid = new GenePosition(Gene.PR, 50);
		final GenePosition prGPMax = new GenePosition(Gene.PR, 99);
		final GenePosition rtGP = new GenePosition(Gene.RT, 99);
		assertEquals(prGPMin.compareTo(rtGP), -1);
		assertEquals(prGPMin.compareTo(prGPMin), 0);
		assertEquals(prGPMin.compareTo(prGPMid), -1);
		assertEquals(prGPMax.compareTo(prGPMid), 1);
	}
	
	@Test(expected = NullPointerException.class)
	public void testCompareToException() {
		final GenePosition gp = new GenePosition(Gene.PR, 1);
		assertEquals(gp.compareTo(null), 1);
	}
	
	@Test
	public void testHash() {		
		Set<Integer> hashCodes = new HashSet<Integer>();
		for (int pos = 1; pos <= MAX_RT_POS; pos++) {
			if (pos <= MAX_PR_POS) hashCodes.add(new GenePosition(Gene.PR, pos).hashCode());
			if (pos <= MAX_IN_POS) hashCodes.add(new GenePosition(Gene.IN, pos).hashCode());
			hashCodes.add(new GenePosition(Gene.RT, pos).hashCode());
		}
		assertEquals(hashCodes.size(), TOTAL_POS);
	}
}