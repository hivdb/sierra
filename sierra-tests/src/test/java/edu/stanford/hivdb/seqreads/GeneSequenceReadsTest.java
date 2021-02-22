package edu.stanford.hivdb.seqreads;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.mutations.PositionCodonReads;


public class GeneSequenceReadsTest {
    private final static HIV hiv = HIV.getInstance();
    
    @Test
    public void testConstructor() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
    	
    	GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
    			hiv.getGene("HIV1RT"), posList, 0.1, 0);
    	
    	assertNotNull(seqReads);
    }
    
    @Test
    public void testProtectedConstructor() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
		
		GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
				posList, 0.1, 0);
		
		assertNotNull(seqReads);
    }
    
    @Test
    public void testGetGene() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
		
		GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
				posList, 0.1, 0);
		
		assertEquals(seqReads.getGene(), hiv.getGene("HIV1RT"));  	
    }
    
    @Test
    public void testGetFirstNA() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
		
		GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
				posList, 0.1, 0);
		
		assertEquals(seqReads.getFirstAA(), 215);  	
    }
    
    @Test
    public void testGetLastNA() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
		
		GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
				posList, 0.1, 0);
		
		assertEquals(seqReads.getLastAA(), 215);  	
    }
    
    @Test
    public void testGetSize() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
		
		GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
				posList, 0.1, 0);
		
		assertEquals(seqReads.getSize(), 0);  	
    }
    
    @Test
    public void testGetNumPositions() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
		
		GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
				posList, 0.1, 0);
		
		assertEquals(seqReads.getNumPositions(), 1);  	
    }
    
    @Test
    public void testGetAllPositionCodonReads() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
		
		GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
				posList, 0.1, 0);
		
		assertEquals(seqReads.getAllPositionCodonReads().size(), 1);  	
    }
    
    @Test
    public void testGetMutations() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
		
		GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
				posList, 0.1, 0);
		
		assertTrue(seqReads.getMutations(0.01, 0) instanceof MutationSet);
		
		assertTrue(seqReads.getMutations(0.1, 0) instanceof MutationSet);
    }
    
    @Test
    public void testGetMedianReadDepth() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
		
		GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
				posList, 0.1, 0);
		
		assertEquals(seqReads.getMedianReadDepth(), 1000, 1);  	
    }
    
    @Test
    public void testGetHistogram() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
		
		GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
				posList, 0.1, 0);
		
		SequenceReadsHistogram<HIV> histogram = seqReads.getHistogram(
				0.01, 0.2, 2, false, SequenceReadsHistogram.AggregationOption.Codon);
		
		assertNotNull(histogram);
    }
    
    @Test
    public void testGetHistogram2() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
		
		GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
				posList, 0.1, 0);
		
		SequenceReadsHistogram<HIV> histogram = seqReads.getHistogram(
				0.01, 0.2, new Double[] {0.01, 0.1}, false, SequenceReadsHistogram.AggregationOption.Codon);
		
		assertNotNull(histogram);
    }
    
    @Test
    public void testGetReadDepthStats() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
		
		GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
				posList, 0.1, 0);
		
		
		assertTrue(seqReads.getReadDepthStats() instanceof DescriptiveStatistics);
    }
    
    @Test
    public void testGetMutations2() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
		
		GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
				posList, 0.1, 0);
		
		
		assertTrue(seqReads.getMutations() instanceof MutationSet);
    }
    
    @Test
    public void testGetAlignedNAs() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
		
		GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
				posList, 0.1, 0);
		
		
		assertEquals(seqReads.getAlignedNAs(false), "NNN");
    }
    
    @Test
    public void testGetAlignedNAs2() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
		
		GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
				posList, 0.1, 0);
		
		
		assertEquals(seqReads.getAlignedNAs(0.1, 0, false), "NNN");
		
		assertEquals(seqReads.getAlignedNAs(0.001, 0, false), "AGW");
		
		assertTrue(seqReads.getAlignedNAs(0.1, 0, true).contains("NNN"));
    }
    
    @Test
    public void testGetAlignedNAs3() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
		
		GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
				posList, 0.1, 0);
		
		
		assertEquals(seqReads.getAlignedNAs(), "NNN");
    }
    
    @Test
    public void testGetAlignedAAs() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
		
		GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
				posList, 0.1, 0);
		
		
		assertEquals(seqReads.getAlignedAAs(), "X");
    }
}