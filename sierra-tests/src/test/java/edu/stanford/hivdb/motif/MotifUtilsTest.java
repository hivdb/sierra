package edu.stanford.hivdb.motif;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Test;

public class MotifUtilsTest {
	
	@Test
	public void testParseMotif() {
		List<MotifPosition> motif = MotifUtils.parseMotif("N-~P-[ST]");
		assertEquals(motif.stream().map(MotifPosition::toString).collect(Collectors.joining()), "N~P[ST]");
	}
	
	@Test
	public void testParseMotifWithoutDash() {
		List<MotifPosition> motif = MotifUtils.parseMotif("N~P[ST]");
		assertEquals(motif.stream().map(MotifPosition::toString).collect(Collectors.joining()), "N~P[ST]");
	}
	
	@Test
	public void testFindMotifMatches() {
		Map<Integer, String> aaLookup = new LinkedHashMap<>();
		aaLookup.put(49, "N");
		aaLookup.put(50, "T");
		aaLookup.put(51, "T");
		List<MotifMatch> results = MotifUtils.findMotifMatches(aaLookup, "N~P[ST]");
		assertEquals(results.size(), 1);
	}

}
