package edu.stanford.hivdb.viruses;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.Test;

import com.google.common.collect.Lists;

import edu.stanford.hivdb.hivfacts.HIV;


public class VirusTest {
	
	final HIV hiv = HIV.getInstance();
	
	@Test
	public void testGetInstance() {
		assertNotNull(Virus.getInstance("HIV"));
	}
	
	@Test
	public void testGetInstance2() {
		assertNotNull(Virus.getInstance(HIV.class));
	}
	
	@Test
	public void testGetAbstractGenes() {
		assertNotNull(hiv.getAbstractGenes());
	}
	
	@Test
	public void testGetDrugs() {
		assertNotNull(hiv.getDrugs(hiv.getDrugClass("PI")));
	}
	
	@Test
	public void testGetDrug() {
		assertNotNull(hiv.getDrug("3TC"));
	}
	
	@Test
	public void testExtractMutationGenesInvaid() {
		assertEquals(Collections.emptySet(), hiv.extractMutationGenes(Lists.newArrayList("RT:69B")));
	}
	
	@Test
	public void testGetLatestDrugResistAlgorithm() {
		assertEquals("9.2", hiv.getLatestDrugResistAlgorithm("HIVDB").getVersion());
		assertEquals("33", hiv.getLatestDrugResistAlgorithm("ANRS").getVersion());
		assertEquals("10.0", hiv.getLatestDrugResistAlgorithm("Rega").getVersion());
		
	}
}