package edu.stanford.hivdb.genotypes;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.stanford.hivdb.genotypes.Genotype.RegionalGenotype;
import edu.stanford.hivdb.hivfacts.HIV;

public class GenotypeTest {
	
	private static final HIV hiv = HIV.getInstance();

	@Test
	public void testSingleton() {
		Genotype<HIV> typeB = hiv.getGenotype("B");
		assertEquals(typeB, hiv.getGenotype("B"));
	}
	
	@Test
	public void testGetPrimaryRegionalGenotype() {
		Genotype<HIV> typeB = hiv.getGenotype("B");
		assertTrue(typeB.getPrimaryRegionalGenotype(2333, 4555) instanceof RegionalGenotype);
		assertEquals("B (100%)", typeB.getPrimaryRegionalGenotype(1, 1000).toString());
	}
	
	@Test
	public void testGetMinPrimaryRegionalGenotypeProportion() {
		Genotype<HIV> typeB = hiv.getGenotype("B");
		assertEquals(typeB.getMinPrimaryRegionalGenotypeProportion(), Double.valueOf(0.9));
	}

	@Test
	public void testGetIndexName() {
		Genotype<HIV> typeX01 = hiv.getGenotype("X01");
		assertEquals("X01", typeX01.getIndexName());
		
	}
	
	@Test
	public void testGetDisplayName() {
		Genotype<HIV> typeX01 = hiv.getGenotype("X01");
		assertEquals("CRF01_AE", typeX01.getDisplayName());
	}

	@Test
	public void testGetParentGenotypes() {
		Genotype<HIV> typeA = hiv.getGenotype("A1");
		Genotype<HIV> typeX01 = hiv.getGenotype("X01");
		assertEquals(typeA, typeX01.getParentGenotypes().get(0));
		
		
		assertNull(typeA.getParentGenotypes());
	}
	
	@Test
	public void testCheckDistance() {
		Genotype<HIV> typeA = hiv.getGenotype("A1");
		assertFalse(typeA.checkDistance(0.4));
	}

	@Test
	public void testGetRegionalGenotype() {
		Genotype<HIV> typeX51 = hiv.getGenotype("X51");
		Genotype<HIV> typeB = hiv.getGenotype("B");
		assertEquals("B (100%)", typeX51.getPrimaryRegionalGenotype(2263, 4100).toString());
		assertEquals("B (97.2%)", typeX51.getPrimaryRegionalGenotype(2213, 4320).toString());
		assertEquals("CRF01_AE (92.9%)", typeX51.getPrimaryRegionalGenotype(4266, 5120).toString());
		assertEquals("B (95.4%)", typeX51.getPrimaryRegionalGenotype(2255, 4400).toString());
		Genotype<HIV> typeX04 = hiv.getGenotype("X04");
		assertEquals("CRF04_cpx (100%)", typeX04.getPrimaryRegionalGenotype(2263, 3600).toString());
		assertEquals("B (100%)", typeB.getPrimaryRegionalGenotype(2333, 4555).toString());
		
		RegionalGenotype<HIV> regional = typeB.getPrimaryRegionalGenotype(2333, 4555);
		
		assertEquals(typeB, regional.getGenotype());
		
		assertTrue(regional.getProportion() instanceof Double);
		assertNotNull(regional.toString());
	}
	
	
	@Test
	public void testHasParentGenotypes() {
		Genotype<HIV> typeB = hiv.getGenotype("B");
		Genotype<HIV> typeX51 = hiv.getGenotype("X51");
		assertFalse(typeB.hasParentGenotypes());
		assertTrue(typeX51.hasParentGenotypes());
	}
	
	@Test
	public void testToString() {
		Genotype<HIV> typeB = hiv.getGenotype("B");
		assertNotNull(typeB.toString());
	}
	
	@Test
	public void testGetClassificationLevel() {
		Genotype<HIV> typeB = hiv.getGenotype("B");
		assertEquals(typeB.getClassificationLevel(), GenotypeClassificationLevel.SUBTYPE);
	}
}
