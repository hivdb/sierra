package edu.stanford.hivdb.genotypes;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;

public class GenotypeReferenceTest {
	
	private final static HIV hiv = HIV.getInstance();
	private final GenotypeReference<HIV> genotype = hiv.getGenotypeReferences().get(0);
	
	@Test
	public void testLoadJson() {
		assertNotNull(hiv.getGenotypeReferences());
		assertEquals(hiv.getGenotypeReferences().size(), 718);
	}
	
	@Test
	public void testGetBoundGenotype() {
		
		BoundGenotype<HIV> boundGenotype = genotype.getBoundGenotype(genotype.getSequence(),
				genotype.getFirstNA(),
				genotype.getLastNA(),
				new ArrayList<Integer>());
		
		assertNotNull(boundGenotype);	
	}
	
	@Test
	public void testGetFirstNA() {
		assertTrue(genotype.getFirstNA() instanceof Integer);
	}
	
	@Test
	public void testGetLastNA() {
		assertTrue(genotype.getLastNA() instanceof Integer);
	}
	
	@Test
	public void testGetGenotype() {
		assertTrue(genotype.getGenotype() instanceof Genotype);
	}
	
	@Test
	public void testGetCountry() {
		assertTrue(genotype.getCountry() instanceof String);
		
		/* All reference genotype has country code */
		
		// for (GenotypeReference<HIV> geno:hiv.getGenotypeReferences()) {
		// 	System.out.println(geno.getCountry());
		// }
	}
	
	@Test
	public void testGetAuthorYear() {
		assertNull(genotype.getAuthorYear());
	}
	
	@Test
	public void testGetYear() {
		assertTrue(genotype.getYear() instanceof Integer);
	}
	
	@Test
	public void testGetAccession() {
		assertTrue(genotype.getAccession() instanceof String);
	}
	
	@Test
	public void testGetSequence() {
		assertTrue(genotype.getSequence() instanceof String);
	}
	
	@Test
	public void testToString() {
		assertTrue(genotype.toString().contains(genotype.getAccession()));
		assertTrue(genotype.toString().contains(genotype.getGenotype().getIndexName()));
	}
}