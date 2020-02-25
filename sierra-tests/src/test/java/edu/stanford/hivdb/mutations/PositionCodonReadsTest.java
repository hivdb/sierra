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
		
		assertEquals(posCodonReads.getGenePositon(), new GenePosition<HIV>(hiv.getGene("HIV1RT"), 215));
		
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
		
		assertTrue(posCodonReads.getCodonWithPrevalence(0.1).isEmpty());
		
	}
	
	@Test
	public void testGetCodonConsensus() {
		
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		assertEquals(posCodonReads.getCodonConsensus(0.2), "NNN");
		
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
}