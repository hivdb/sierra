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
    
    private GeneSequenceReads<HIV> createTestGeneSeqReads() {
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
		CutoffCalculator<HIV> cutoff = new CutoffCalculator<>(
			posList,
			/* maxMixturePcnt */ 1.,
			/* minPrevalence */ 0.1,
			/* minCodonReads */ 0L,
			/* minPositionReads */ 0L
		);
    	
    	return new GeneSequenceReads<HIV>(hiv.getGene("HIV1RT"), posList, cutoff);
    	
    }
    
    @Test
    public void testConstructor() {
    	GeneSequenceReads<HIV> seqReads = createTestGeneSeqReads();
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
		CutoffCalculator<HIV> cutoff = new CutoffCalculator<>(
			posList,
			1.,
			0.1,
			0L,
			0L
		);
		
		GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
				posList, cutoff);
		
		assertNotNull(seqReads);
    }
    
    @Test
    public void testGetGene() {
    	GeneSequenceReads<HIV> seqReads = createTestGeneSeqReads();
		assertEquals(seqReads.getGene(), hiv.getGene("HIV1RT"));  	
    }
    
    @Test
    public void testGetFirstNA() {
    	GeneSequenceReads<HIV> seqReads = createTestGeneSeqReads();
		assertEquals(seqReads.getFirstAA(), 215);  	
    }
    
    @Test
    public void testGetLastNA() {
    	GeneSequenceReads<HIV> seqReads = createTestGeneSeqReads();
		assertEquals(seqReads.getLastAA(), 215);  	
    }
    
    @Test
    public void testGetSize() {
    	GeneSequenceReads<HIV> seqReads = createTestGeneSeqReads();
		assertEquals(seqReads.getSize(), 0);  	
    }
    
    @Test
    public void testGetNumPositions() {
    	GeneSequenceReads<HIV> seqReads = createTestGeneSeqReads();
		assertEquals(seqReads.getNumPositions(), 1);  	
    }
    
    @Test
    public void testGetAllPositionCodonReads() {
    	GeneSequenceReads<HIV> seqReads = createTestGeneSeqReads();
		assertEquals(seqReads.getAllPositionCodonReads().size(), 1);  	
    }
    
    @Test
    public void testGetMutations() {
    	GeneSequenceReads<HIV> seqReads = createTestGeneSeqReads();
		assertTrue(seqReads.getMutations(0.01, 0) instanceof MutationSet);
		
		assertTrue(seqReads.getMutations(0.1, 0) instanceof MutationSet);
    }
    
    @Test
    public void testGetMedianReadDepth() {
    	GeneSequenceReads<HIV> seqReads = createTestGeneSeqReads();
		assertEquals(seqReads.getMedianReadDepth(), 1000, 1);  	
    }
    
    @Test
    public void testGetHistogram() {
    	GeneSequenceReads<HIV> seqReads = createTestGeneSeqReads();
		SequenceReadsHistogram<HIV> histogram = seqReads.getHistogram(
				0.01, 0.2, 2, false, SequenceReadsHistogram.AggregationOption.Codon);
		assertNotNull(histogram);
    }
    
    @Test
    public void testGetHistogram2() {
    	GeneSequenceReads<HIV> seqReads = createTestGeneSeqReads();

		SequenceReadsHistogram<HIV> histogram = seqReads.getHistogram(
				0.01, 0.2, new Double[] {0.01, 0.1}, false, SequenceReadsHistogram.AggregationOption.Codon);
		
		assertNotNull(histogram);
    }
    
    @Test
    public void testGetReadDepthStats() {
    	GeneSequenceReads<HIV> seqReads = createTestGeneSeqReads();
		assertTrue(seqReads.getReadDepthStats() instanceof DescriptiveStatistics);
    }
    
    @Test
    public void testGetMutations2() {
    	GeneSequenceReads<HIV> seqReads = createTestGeneSeqReads();
		assertTrue(seqReads.getMutations() instanceof MutationSet);
    }
    
    @Test
    public void testGetAlignedNAs() {
    	GeneSequenceReads<HIV> seqReads = createTestGeneSeqReads();
		assertEquals(seqReads.getAlignedNAs(false), "NNN");
    }
    
    @Test
    public void testGetAlignedNAs2() {
    	GeneSequenceReads<HIV> seqReads = createTestGeneSeqReads();

		assertEquals(seqReads.getAlignedNAs(0.1, 0, false), "NNN");
		
		assertEquals(seqReads.getAlignedNAs(0.001, 0, false), "AGW");
		
		assertTrue(seqReads.getAlignedNAs(0.1, 0, true).contains("NNN"));
    }
    
    @Test
    public void testGetAlignedNAs3() {
    	GeneSequenceReads<HIV> seqReads = createTestGeneSeqReads();
		assertEquals(seqReads.getAlignedNAs(), "NNN");
    }
    
    @Test
    public void testGetAlignedAAs() {
    	GeneSequenceReads<HIV> seqReads = createTestGeneSeqReads();
		assertEquals(seqReads.getAlignedAAs(), "X");
    }
}