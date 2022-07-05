package edu.stanford.hivdb.seqreads;

import static org.junit.Assert.*;

import java.util.Map;
import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.GenePosition;


public class OneCodonReadsCoverageTest {
    private final static HIV hiv = HIV.getInstance();
    
    @Test
    public void testOneCodonReadsCoverage() {
    	OneCodonReadsCoverage<HIV> reads = new OneCodonReadsCoverage<HIV>(
    			hiv.getGene("HIV1PR"), 89, 1000, false);
    	
    	assertNotNull(reads);
    }
    
    @Test
    public void testGetGene() {
    	OneCodonReadsCoverage<HIV> reads = new OneCodonReadsCoverage<HIV>(
    			hiv.getGene("HIV1PR"), 89, 1000, false);
    	
    	assertEquals(reads.getGene(), hiv.getGene("HIV1PR"));
    }
    
    @Test
    public void testGetPosition() {
    	OneCodonReadsCoverage<HIV> reads = new OneCodonReadsCoverage<HIV>(
    			hiv.getGene("HIV1PR"), 89, 1000, false);
    	
    	assertEquals(reads.getPosition(), Long.valueOf(89));
    }
    
    @Test
    public void testGetGenePosition() {
    	OneCodonReadsCoverage<HIV> reads = new OneCodonReadsCoverage<HIV>(
    			hiv.getGene("HIV1PR"), 89, 1000, false);
    	
    	assertTrue(reads.getGenePosition() instanceof GenePosition);
    }
    
    @Test
    public void testGetTotalReads() {
    	OneCodonReadsCoverage<HIV> reads = new OneCodonReadsCoverage<HIV>(
    			hiv.getGene("HIV1PR"), 89, 1000, false);
    	
    	assertEquals(reads.getTotalReads(), Long.valueOf(1000));
    }
    
    @Test
    public void testIsTrimmed() {
    	OneCodonReadsCoverage<HIV> reads = new OneCodonReadsCoverage<HIV>(
    			hiv.getGene("HIV1PR"), 89, 1000, false);
    	
    	assertFalse(reads.isTrimmed());
    }
    
    @Test
    public void testGetAbsoluteNAPosition() {
    	OneCodonReadsCoverage<HIV> reads = new OneCodonReadsCoverage<HIV>(
    			hiv.getGene("HIV1PR"), 89, 1000, false);
    	
    	assertEquals(Integer.valueOf(88 * 3 + 2253), reads.getAbsoluteNAPosition());
    }
    
    @Test
    public void testExtMap() {
    	OneCodonReadsCoverage<HIV> reads = new OneCodonReadsCoverage<HIV>(
    			hiv.getGene("HIV1PR"), 89, 1000, false);
    	
    	assertTrue(reads.extMap() instanceof Map);
    }
}