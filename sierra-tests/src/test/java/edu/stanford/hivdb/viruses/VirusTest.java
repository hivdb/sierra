package edu.stanford.hivdb.viruses;

import static org.junit.Assert.*;
import org.junit.Test;

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
}