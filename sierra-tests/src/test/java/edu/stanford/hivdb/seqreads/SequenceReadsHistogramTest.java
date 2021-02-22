package edu.stanford.hivdb.seqreads;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.PositionCodonReads;
import edu.stanford.hivdb.seqreads.SequenceReadsHistogram.HistogramBin;


public class SequenceReadsHistogramTest {
    private final static HIV hiv = HIV.getInstance();
    
    @Test
    public void testAggregationOption() {
    	assertEquals(SequenceReadsHistogram.AggregationOption.values().length, 3);
    	
    	SequenceReadsHistogram.AggregationOption.valueOf("Codon");
    	SequenceReadsHistogram.AggregationOption.valueOf("AminoAcid");
    	SequenceReadsHistogram.AggregationOption.valueOf("Position");
    }
    
    @Test
    public void testHistogramBin() {
    	HistogramBin bin = new HistogramBin(0.5, 0.1, 5);
    	assertNotNull(bin);
    	
    	assertEquals(bin.getPercentStart(), 316.22, 0.1);
    	assertEquals(bin.getPercentStop(), 398.10, 0.1);
    }
    
    @Test
    public void testSequenceReadsHistogram() {
    	
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
    	
    	GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
    			hiv.getGene("HIV1RT"), posList, 0.1, 0);
    	List<GeneSequenceReads<HIV>> allGeneSequenceReads = new ArrayList<>();
    	allGeneSequenceReads.add(seqReads);
    	
    	SequenceReadsHistogram<HIV> histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, 2, false, SequenceReadsHistogram.AggregationOption.Codon);
    	
    	assertNotNull(histogram);
    }
 
    @Test
    public void testSequenceReadsHistogram2() {
    	
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
    	
    	GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
    			hiv.getGene("HIV1RT"), posList, 0.1, 0);
    	List<GeneSequenceReads<HIV>> allGeneSequenceReads = new ArrayList<>();
    	allGeneSequenceReads.add(seqReads);
    	
    	SequenceReadsHistogram<HIV> histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, new Double[] {0.01, 0.1}, false, SequenceReadsHistogram.AggregationOption.Codon);
    	
    	assertNotNull(histogram);
    }
    
    @Test
    public void testGetUsualSites() {
    	
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		allCodonReads.put("ACA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
    	
    	GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
    			hiv.getGene("HIV1RT"), posList, 0.1, 0);
    	List<GeneSequenceReads<HIV>> allGeneSequenceReads = new ArrayList<>();
    	allGeneSequenceReads.add(seqReads);
    	
    	SequenceReadsHistogram<HIV> histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, 2, false, SequenceReadsHistogram.AggregationOption.Codon);
    	
    	assertFalse(histogram.getUsualSites().isEmpty());
    	
    	histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, 2, false, SequenceReadsHistogram.AggregationOption.Position);
    	
    	assertFalse(histogram.getUsualSites().isEmpty());
    	
    	histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, 2, false, SequenceReadsHistogram.AggregationOption.AminoAcid);
    	
    	assertFalse(histogram.getUsualSites().isEmpty());
    	
    }
    
    @Test
    public void testGetUnusualSites() {
    	
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		allCodonReads.put("ACA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
    	
    	GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
    			hiv.getGene("HIV1RT"), posList, 0.1, 0);
    	List<GeneSequenceReads<HIV>> allGeneSequenceReads = new ArrayList<>();
    	allGeneSequenceReads.add(seqReads);
    	
    	SequenceReadsHistogram<HIV> histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, 2, false, SequenceReadsHistogram.AggregationOption.Codon);
    	
    	assertFalse(histogram.getUnusualSites().isEmpty());
    	
    	histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, 2, false, SequenceReadsHistogram.AggregationOption.Position);
    	
    	assertFalse(histogram.getUnusualSites().isEmpty());
    	
    	histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, 2, false, SequenceReadsHistogram.AggregationOption.AminoAcid);
    	
    	assertFalse(histogram.getUnusualSites().isEmpty());
    	
    }
    
    @Test
    public void testGetUnusualApobecSites() {
    	
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		allCodonReads.put("ACA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
    	
    	GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
    			hiv.getGene("HIV1RT"), posList, 0.1, 0);
    	List<GeneSequenceReads<HIV>> allGeneSequenceReads = new ArrayList<>();
    	allGeneSequenceReads.add(seqReads);
    	
    	SequenceReadsHistogram<HIV> histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, 2, false, SequenceReadsHistogram.AggregationOption.Codon);
    	
    	assertFalse(histogram.getUnusualApobecSites().isEmpty());
    	
    	histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, 2, false, SequenceReadsHistogram.AggregationOption.Position);
    	
    	assertFalse(histogram.getUnusualApobecSites().isEmpty());
    	
    	histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, 2, false, SequenceReadsHistogram.AggregationOption.AminoAcid);
    	
    	assertFalse(histogram.getUnusualApobecSites().isEmpty());
    	
    }
    
    @Test
    public void testGetUnusualNonApobecSites() {
    	
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		allCodonReads.put("ACA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
    	
    	GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
    			hiv.getGene("HIV1RT"), posList, 0.1, 0);
    	List<GeneSequenceReads<HIV>> allGeneSequenceReads = new ArrayList<>();
    	allGeneSequenceReads.add(seqReads);
    	
    	SequenceReadsHistogram<HIV> histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, 2, false, SequenceReadsHistogram.AggregationOption.Codon);
    	
    	assertFalse(histogram.getUnusualNonApobecSites().isEmpty());
    	
    	histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, 2, false, SequenceReadsHistogram.AggregationOption.Position);
    	
    	assertFalse(histogram.getUnusualNonApobecSites().isEmpty());
    	
    	histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, 2, false, SequenceReadsHistogram.AggregationOption.AminoAcid);
    	
    	assertFalse(histogram.getUnusualNonApobecSites().isEmpty());
    	
    }
    
    @Test
    public void testGetApobecSites() {
    	
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		allCodonReads.put("ACA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
    	
    	GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
    			hiv.getGene("HIV1RT"), posList, 0.1, 0);
    	List<GeneSequenceReads<HIV>> allGeneSequenceReads = new ArrayList<>();
    	allGeneSequenceReads.add(seqReads);
    	
    	SequenceReadsHistogram<HIV> histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, 2, false, SequenceReadsHistogram.AggregationOption.Codon);
    	
    	assertFalse(histogram.getApobecSites().isEmpty());
    }
    
    @Test
    public void testGetApobecDrmSites() {
    	
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		allCodonReads.put("ACA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
    	
    	GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
    			hiv.getGene("HIV1RT"), posList, 0.1, 0);
    	List<GeneSequenceReads<HIV>> allGeneSequenceReads = new ArrayList<>();
    	allGeneSequenceReads.add(seqReads);
    	
    	SequenceReadsHistogram<HIV> histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, 2, false, SequenceReadsHistogram.AggregationOption.Codon);
    	
    	assertFalse(histogram.getApobecDrmSites().isEmpty());
    }
    
    @Test
    public void testGetStopCodonSites() {
    	
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		allCodonReads.put("ACA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
    	
    	GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
    			hiv.getGene("HIV1RT"), posList, 0.1, 0);
    	List<GeneSequenceReads<HIV>> allGeneSequenceReads = new ArrayList<>();
    	allGeneSequenceReads.add(seqReads);
    	
    	SequenceReadsHistogram<HIV> histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, 2, false, SequenceReadsHistogram.AggregationOption.Codon);
    	
    	assertFalse(histogram.getStopCodonSites().isEmpty());
    }
    
    @Test
    public void testGetDrmSites() {
    	
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		allCodonReads.put("ACA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
    	
    	GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
    			hiv.getGene("HIV1RT"), posList, 0.1, 0);
    	List<GeneSequenceReads<HIV>> allGeneSequenceReads = new ArrayList<>();
    	allGeneSequenceReads.add(seqReads);
    	
    	SequenceReadsHistogram<HIV> histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, 2, false, SequenceReadsHistogram.AggregationOption.Codon);
    	
    	assertFalse(histogram.getDrmSites().isEmpty());
    }
    
    @Test
    public void testGetNumPositions() {
    	
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(12));
		allCodonReads.put("AGA", Long.valueOf(12));
		allCodonReads.put("ACA", Long.valueOf(12));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		List<PositionCodonReads<HIV>> posList = new ArrayList<>();
		posList.add(posCodonReads);
    	
    	GeneSequenceReads<HIV> seqReads = new GeneSequenceReads<HIV>(
    			hiv.getGene("HIV1RT"), posList, 0.1, 0);
    	List<GeneSequenceReads<HIV>> allGeneSequenceReads = new ArrayList<>();
    	allGeneSequenceReads.add(seqReads);
    	
    	SequenceReadsHistogram<HIV> histogram = new SequenceReadsHistogram<HIV>(
    			allGeneSequenceReads, 0.01, 0.1, 2, false, SequenceReadsHistogram.AggregationOption.Codon);
    	
    	assertEquals(histogram.getNumPositions(), Integer.valueOf(1));
    }
}