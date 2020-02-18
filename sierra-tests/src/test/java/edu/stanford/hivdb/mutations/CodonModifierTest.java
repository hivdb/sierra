package edu.stanford.hivdb.mutations;

import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.hivfacts.hiv2.HIV2;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.viruses.Strain;

import static org.junit.Assert.*;

import java.util.List;

public class CodonModifierTest {
	
	
	private final static HIV2 hiv2 = HIV2.getInstance();
	
	@Test
	public void testConstructor() {
		Gene<HIV2> gene = hiv2.getGene("HIV2BIN");
		
		List<CodonModifier<HIV2>> codonModifiers = gene.getTargetCodonModifiers(hiv2.getStrain("HIV2A"));
				
		CodonModifier<HIV2> codonModifier = codonModifiers.get(0);
		assertNotNull(codonModifier);
	}
	
	@Test
	public void testGetTargetStrain() {
		Gene<HIV2> gene = hiv2.getGene("HIV2BIN");
		Strain<HIV2> strain = hiv2.getStrain("HIV2A");
		List<CodonModifier<HIV2>> codonModifiers = gene.getTargetCodonModifiers(strain);		
		CodonModifier<HIV2> codonModifier = codonModifiers.get(0);
		
		assertEquals(codonModifier.getTargetStrain(), strain);
	}
	
	@Test
	public void testGetPosition() {
		Gene<HIV2> gene = hiv2.getGene("HIV2BIN");
		Strain<HIV2> strain = hiv2.getStrain("HIV2A");
		List<CodonModifier<HIV2>> codonModifiers = gene.getTargetCodonModifiers(strain);		
		CodonModifier<HIV2> codonModifier = codonModifiers.get(0);
		
		assertEquals(codonModifier.getPosition(), 293);
	}
	
	@Test
	public void testGetInsertAfter() {
		Gene<HIV2> gene = hiv2.getGene("HIV2BIN");
		Strain<HIV2> strain = hiv2.getStrain("HIV2A");
		List<CodonModifier<HIV2>> codonModifiers = gene.getTargetCodonModifiers(strain);		
		CodonModifier<HIV2> codonModifier = codonModifiers.get(0);
		
		assertEquals(codonModifier.getInsertAfter(), null);
		
	}
	
	@Test
	public void testGetDeleteAfter() {
		Gene<HIV2> gene = hiv2.getGene("HIV2BIN");
		Strain<HIV2> strain = hiv2.getStrain("HIV2A");
		List<CodonModifier<HIV2>> codonModifiers = gene.getTargetCodonModifiers(strain);		
		CodonModifier<HIV2> codonModifier = codonModifiers.get(0);
		
		assertEquals(codonModifier.getDeleteAfter(), Integer.valueOf(0));
		
	}
	
	
}