package edu.stanford.hivdb.mutations;

import static org.junit.Assert.*;
import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;


public class CodonReadsTest {
	
	private final static HIV hiv = HIV.getInstance();
	
	public void testNormalizeCodon() {
		assertEquals(CodonReads.normalizeCodon("ADK O"), "ADKO");
		assertEquals(CodonReads.normalizeCodon("ADK,O"), "ADKO");
		assertEquals(CodonReads.normalizeCodon("ADK;O"), "ADKO");
		assertEquals(CodonReads.normalizeCodon("ADK:O"), "ADKO");
		assertEquals(CodonReads.normalizeCodon("ADK-O"), "ADKO");
	}
	
	@Test
	public void testConstructor() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertNotNull(codonReads);
		
	}
	
	@Test
	public void testGetCodon() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertEquals(codonReads.getCodon(), "CGT");
		
	}
	
	@Test
	public void testGetReads() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertEquals(codonReads.getReads(), Long.valueOf(33));
	}
	
	@Test
	public void testGetTotalReads() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertEquals(codonReads.getTotalReads(), Long.valueOf(1000));
	}
	
	@Test
	public void testGetStrain() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertEquals(codonReads.getStrain(), hiv.getStrain("HIV1"));
	}
	
	@Test
	public void testGetGene() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertEquals(codonReads.getGene(), hiv.getGene("HIV1RT"));
	}
	
	@Test
	public void testGetAbstractGene() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertEquals(codonReads.getAbstractGene(), "RT");
	}
	
	@Test
	public void testGetRefAminoAcid() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertEquals(codonReads.getRefAminoAcid(), Character.valueOf('T'));
	}
	
	@Test
	public void testGetAminoAcid() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertEquals(codonReads.getAminoAcid(), Character.valueOf('R'));
		
		codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "VVV", 33, 1000);
		assertEquals(codonReads.getAminoAcid(), Character.valueOf('X'));
		
		codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "AAAAAA", 33, 1000);
		assertEquals(codonReads.getAminoAcid(), Character.valueOf('_'));
		
		codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "AA", 33, 1000);
		assertEquals(codonReads.getAminoAcid(), Character.valueOf('-'));
		
		assertEquals(codonReads.getAminoAcid(), Character.valueOf('-'));
	}
	
	
	@Test
	public void testGetProportion() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertEquals(codonReads.getProportion(), Double.valueOf(0.033));
	}
	
	@Test
	public void testGetCodonPercent() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertEquals(codonReads.getCodonPercent(), Double.valueOf(0.0));
		assertEquals(codonReads.getCodonPercent(), Double.valueOf(0.0));
		
		codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGTGGG", 33, 1000);
		assertNotNull(codonReads.getCodonPercent());
		
		codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CG", 33, 1000);
		assertNotNull(codonReads.getCodonPercent());
		
	}
	
	@Test
	public void testGetAAPercent() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertTrue(codonReads.getAAPercent() > 0.0);
	}
	
	@Test
	public void testIsReference() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertFalse(codonReads.isReference());
	}
	
	@Test
	public void testIsApobecMutation() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertFalse(codonReads.isApobecMutation());
	}
	
	@Test
	public void testIsApobecDRM() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertFalse(codonReads.isApobecDRM());
	}
	
	@Test
	public void testHasStop() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertFalse(codonReads.hasStop());
	}
	
	@Test
	public void testIsUnusual() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertTrue(codonReads.isUnusual());
	}
	
	@Test
	public void testIsUnusualByCodon() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertTrue(codonReads.isUnusualByCodon());
	}
	
	@Test
	public void testIsDRM() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		
		assertFalse(codonReads.isDRM());
	}
	
	@Test
	public void testExtMap() {
		CodonReads<HIV> codonReads= new CodonReads<HIV>(
				hiv.getGene("HIV1RT"), 215, "CGT", 33, 1000);
		assertNotNull(codonReads.extMap());
		
	}
}