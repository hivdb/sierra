package edu.stanford.hivdb.seqreads;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.PositionCodonReads;


public class CutoffSuggestionTest {
    private final static HIV hiv = HIV.getInstance();
    
    @Test
    public void testConstructor() {
    	
		Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(220));
		allCodonReads.put("AGA", Long.valueOf(12));
		allCodonReads.put("GGT", Long.valueOf(220));
		allCodonReads.put("GGA", Long.valueOf(100));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
    	
    	CutoffSuggestion<HIV> cutoff = new CutoffSuggestion<HIV>(allReads);
    	
    	assertNotNull(cutoff);
    	
    }
    
    @Test
    public void testGetLooserLimit() {
    	
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(220));
		allCodonReads.put("AGA", Long.valueOf(12));
		allCodonReads.put("GGT", Long.valueOf(220));
		allCodonReads.put("GGA", Long.valueOf(100));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
    	
    	CutoffSuggestion<HIV> cutoff = new CutoffSuggestion(allReads);
    	
    	assertEquals(cutoff.getLooserLimit(), 0.1, 0.1);
    }

    
    @Test
    public void testGetStricterLimit() {
    	
    	Map<String, Long> allCodonReads = new TreeMap<>();
		allCodonReads.put("AGT", Long.valueOf(220));
		allCodonReads.put("AGA", Long.valueOf(12));
		allCodonReads.put("GGT", Long.valueOf(220));
		allCodonReads.put("GGA", Long.valueOf(100));
		
		PositionCodonReads<HIV> posCodonReads = new PositionCodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, 1000, allCodonReads);
		
		List<PositionCodonReads<HIV>> allReads = new ArrayList<>();
		allReads.add(posCodonReads);
    	
    	CutoffSuggestion<HIV> cutoff = new CutoffSuggestion(allReads);
    	
    	assertEquals(cutoff.getStricterLimit(), 0.1, 0.1);
    }
}