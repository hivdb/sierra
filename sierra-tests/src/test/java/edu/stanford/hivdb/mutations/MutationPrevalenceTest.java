package edu.stanford.hivdb.mutations;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;


public class MutationPrevalenceTest {
	
	
	final static private HIV hiv = HIV.getInstance();
	
	@Test
	public void testConstructor() {
		AminoAcidPercent<HIV> naive = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "naive", "B").get().get(0);
		AminoAcidPercent<HIV> art = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "art", "B").get().get(0);
		
		new MutationPrevalence<HIV>("B", naive, art);
	}
	
	@Test
	public void testGetMutation() {
		
		AminoAcidPercent<HIV> naive = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "naive", "B").get().get(0);
		AminoAcidPercent<HIV> art = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "art", "B").get().get(0);
		
		MutationPrevalence<HIV> mutPrev = new MutationPrevalence<HIV>("B", naive, art);
		assertEquals(mutPrev.getMutation().getHumanFormat(), "P1A");
		
	}
	
	@Test
	public void testGetSubType() {
		AminoAcidPercent<HIV> naive = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "naive", "B").get().get(0);
		AminoAcidPercent<HIV> art = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "art", "B").get().get(0);
		
		MutationPrevalence<HIV> mutPrev = new MutationPrevalence<HIV>("B", naive, art);
		assertEquals(mutPrev.getSubtype(), "B");
	}
	
	@Test
	public void testGetTotalNaive() {
		AminoAcidPercent<HIV> naive = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "naive", "B").get().get(0);
		AminoAcidPercent<HIV> art = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "art", "B").get().get(0);
		
		MutationPrevalence<HIV> mutPrev = new MutationPrevalence<HIV>("B", naive, art);
		assertNotNull(mutPrev.getTotalNaive());
	}
	
	@Test
	public void testGetTotalTreated() {
		AminoAcidPercent<HIV> naive = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "naive", "B").get().get(0);
		AminoAcidPercent<HIV> art = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "art", "B").get().get(0);
		
		MutationPrevalence<HIV> mutPrev = new MutationPrevalence<HIV>("B", naive, art);
		assertNotNull(mutPrev.getTotalTreated());
	}
	
	@Test
	public void testGetFrequencyNaive() {
		AminoAcidPercent<HIV> naive = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "naive", "B").get().get(0);
		AminoAcidPercent<HIV> art = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "art", "B").get().get(0);
		
		MutationPrevalence<HIV> mutPrev = new MutationPrevalence<HIV>("B", naive, art);
		assertNotNull(mutPrev.getFrequencyNaive());
	}
	
	@Test
	public void testGetPercentageNaive() {
		AminoAcidPercent<HIV> naive = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "naive", "B").get().get(0);
		AminoAcidPercent<HIV> art = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "art", "B").get().get(0);
		
		MutationPrevalence<HIV> mutPrev = new MutationPrevalence<HIV>("B", naive, art);
		assertNotNull(mutPrev.getFrequencyNaive());
	}
	
	@Test
	public void testGetPercentageTreated() {
		AminoAcidPercent<HIV> naive = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "naive", "B").get().get(0);
		AminoAcidPercent<HIV> art = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "art", "B").get().get(0);
		
		MutationPrevalence<HIV> mutPrev = new MutationPrevalence<HIV>("B", naive, art);
		assertNotNull(mutPrev.getFrequencyTreated());
	}
	
	@Test
	public void testGetAAs() {
		AminoAcidPercent<HIV> naive = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "naive", "B").get().get(0);
		AminoAcidPercent<HIV> art = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "art", "B").get().get(0);
		
		MutationPrevalence<HIV> mutPrev = new MutationPrevalence<HIV>("B", naive, art);
		assertEquals(mutPrev.getAA(), "A");
	}
	
	@Test
	public void testToString() {
		AminoAcidPercent<HIV> naive = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "naive", "B").get().get(0);
		AminoAcidPercent<HIV> art = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "art", "B").get().get(0);
		
		MutationPrevalence<HIV> mutPrev = new MutationPrevalence<HIV>("B", naive, art);
		assertTrue(mutPrev.toString().startsWith("P1A"));
	}

	@Test
	public void testIsRare() {
		AminoAcidPercent<HIV> naive = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "naive", "B").get().get(0);
		AminoAcidPercent<HIV> art = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "art", "B").get().get(0);
		
		MutationPrevalence<HIV> mutPrev = new MutationPrevalence<HIV>("B", naive, art);
		assertTrue(mutPrev.isRare());
	}
}