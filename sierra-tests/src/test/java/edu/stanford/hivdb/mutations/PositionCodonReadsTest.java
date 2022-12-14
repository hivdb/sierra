package edu.stanford.hivdb.mutations;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;


public class PositionCodonReadsTest {
	
	private final static HIV hiv = HIV.getInstance();
	
	@Test
	public void testConstructor() {
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		assertNotNull(posCodonReads);
	}
	
	@Test
	public void testGetStrain() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		assertEquals(posCodonReads.getStrain(), hiv.getStrain("HIV1"));
		
	}
	
	@Test
	public void testGetGene() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		assertEquals(posCodonReads.getGene(), hiv.getGene("HIV1RT"));
		
	}
	
	@Test
	public void testGetAbstractGene() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		assertEquals(posCodonReads.getAbstractGene(), "RT");
		
	}
	
	@Test
	public void testGetPosition() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		assertEquals(posCodonReads.getPosition(), 215);
		
	}
	
	@Test
	public void testGetGenePositon() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		assertEquals(posCodonReads.getGenePosition(), new GenePosition<HIV>(hiv.getGene("HIV1RT"), 215));
		
	}
	
	@Test
	public void testGetTotalReads() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		assertEquals(posCodonReads.getTotalReads(), 1000);
		
	}
	
	@Test
	public void testGetCodonReads() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		assertFalse(posCodonReads.getCodonReads().isEmpty());
		
	}
	
	@Test
	public void testGetCodonReads2() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		assertTrue(posCodonReads.getCodonReads(true, 0.2, 0.1).isEmpty());
		
	}
	
	@Test
	public void testGetCodonWithPrevalence() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		assertTrue(posCodonReads.getCodonWithPrevalence(0.1, 0L).isEmpty());
		
	}
	
	@Test
	public void testGetCodonConsensusWithoutIns() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGTAAA", 500L);
		allCodonReads.put("AGA", 500L);
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
			hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		assertEquals("AGW", posCodonReads.getCodonConsensusWithoutIns(0.2, 0L));

		allCodonReads.put("AGTAAA", 850L);
		allCodonReads.put("AGA", 150L);

		assertEquals("AGW", posCodonReads.getCodonConsensusWithoutIns(0.2, 0L));

		posCodonReads = new PositionCodonReads<HIV>(
			hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		assertEquals("AGT", posCodonReads.getCodonConsensusWithoutIns(0.2, 0L));
	}

	@Test
	public void testGetCodonConsensusWithIns() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", 500L);
		allCodonReads.put("AGAAAA", 500L);
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
			hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		assertEquals("AGWAAA", posCodonReads.getCodonConsensusWithIns(0.2, 0L));


		allCodonReads.put("AGT", 850L);
		allCodonReads.put("AGAAAA", 150L);

		assertEquals("AGWAAA", posCodonReads.getCodonConsensusWithIns(0.2, 0L));

		posCodonReads = new PositionCodonReads<HIV>(
			hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		assertEquals("AGT", posCodonReads.getCodonConsensusWithIns(0.2, 0L));
	}

	@Test
	public void testGetCodonConsensusWithInsWithDelFs() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("-GG", 1235L);
		allCodonReads.put("AGG", 1L);
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
			hiv.getGene("HIV1RT"), 215, 1236, allCodonReads);
		
		// deliberately change consensus frameshift to NNN
		assertEquals("-GG", posCodonReads.getCodonConsensusWithIns(0.2, 0L));
		
		allCodonReads = new TreeMap<>();
		allCodonReads.put("AG-", 123L);
		allCodonReads.put("AGG", 124L);

		posCodonReads = new PositionCodonReads<HIV>(
			hiv.getGene("HIV1RT"), 215, 227, allCodonReads);
		
		// suppress frameshift when it's not the majority one; even its prevalence qualified
		assertEquals("AGG", posCodonReads.getCodonConsensusWithIns(0.2, 0L));
	}
	
	@Test
	public void testExtMap() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		assertFalse(posCodonReads.getCodonReads(true, 1, 0).isEmpty());
		
	}
	
	@Test
	public void testCalcMinReads() {
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("GAC", Long.valueOf(6));
		allCodonReads.put("TCT", Long.valueOf(1));

		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 7, allCodonReads);
		// the minReads of 20% should be 2 but not 1, since 1/7 = 0.14 < 20%
		assertEquals(2, posCodonReads.calcMinReads(0.2, 1));

		// the second parameter should override the minReads when is greater 
		assertEquals(3, posCodonReads.calcMinReads(0.2, 3));
	}
}