/*
    
    Copyright (C) 2017 Stanford HIVDB team
    
    Sierra is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    Sierra is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package edu.stanford.hivdb.utilities;

import static org.junit.Assert.*;

import org.junit.Test;

public class SeqUtilsTest {

	@Test
	public void testTrimDownstreamNAs() {
		assertEquals("ABCEFG", SeqUtils.trimDownstreamNAs("ABCEFG"));
		assertEquals("ABCEFG", SeqUtils.trimDownstreamNAs("ABCEFGH"));
		assertEquals("ABCEFG", SeqUtils.trimDownstreamNAs("ABCEFGHI"));
		assertEquals("ABCEFGHIJ", SeqUtils.trimDownstreamNAs("ABCEFGHIJ"));
	}

	@Test
	public void testTrimUpstreamNAs() {
		assertEquals("ABCEFG", SeqUtils.trimUpstreamNAs("ABCEFG"));
		assertEquals("BCEFGH", SeqUtils.trimUpstreamNAs("ABCEFGH"));
		assertEquals("CEFGHI", SeqUtils.trimUpstreamNAs("ABCEFGHI"));
		assertEquals("ABCEFGHIJ", SeqUtils.trimUpstreamNAs("ABCEFGHIJ"));
	}

	@Test
	public void testNumRYMWKS() {
		assertEquals(0, SeqUtils.numRYMWKS("ABCDEFG"));
		assertEquals(6, SeqUtils.numRYMWKS("RYMWKS"));
		assertEquals(7, SeqUtils.numRYMWKS("RYRMCCEFSSS"));
	}

	@Test
	public void testNumBDHVN() {
		assertEquals(2, SeqUtils.numBDHVN("ABCDEFG"));
		assertEquals(0, SeqUtils.numBDHVN("RYMWKS"));
		assertEquals(4, SeqUtils.numBDHVN("RYRMCCEFVVVVSSS"));
	}

	@Test
	public void testReplaceCodon() {
		assertEquals("CCTACGTCC", SeqUtils.replaceCodon("CCTTTTTCC", 3, "ACG"));
		assertEquals("CCTTTTTACG", SeqUtils.replaceCodon("CCTTTTTCC", 7, "ACG"));
	}

	@Test
	public void testMixturePcnt() {
		assertEquals(.0, SeqUtils.mixturePcnt("ACGTACGTACGTACGT"), 0.001);
		assertEquals(33.333, SeqUtils.mixturePcnt("AGTRYMKCGAGT"), 0.001);
		assertEquals(6.25, SeqUtils.mixturePcnt("AGTRACGTACGTACGT"), 0.001);
	}
	
	@Test
	public void testMixturePcntWithNullSeq() {
		assertEquals(0.0, SeqUtils.mixturePcnt(""), 0.001);
	}

	@Test(expected=RuntimeException.class)
	public void TestConstructor() {
		new SeqUtils();
	}
}