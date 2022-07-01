package edu.stanford.hivdb.viruses;

import static org.junit.Assert.*;
import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.hivfacts.hiv2.HIV2;

public class StrainTest {
	
	final static HIV hiv1 = HIV.getInstance();
	final static HIV2 hiv2 = HIV2.getInstance();
	final static Strain<HIV> hiv1s = hiv1.getStrain("HIV1");
	final static Strain<HIV2> hiv2a = hiv2.getStrain("HIV2A");
	final static Strain<HIV2> hiv2b = hiv2.getStrain("HIV2B");
	
	@Test
	public void testGetVirusInstance() {
		assertSame(hiv1s.getVirusInstance(), hiv1);
	}
	
	@Test
	public void testName() {
		assertEquals(hiv1s.name(), "HIV1");
	}
	
	@Test
	public void testGetName() {
		assertEquals(hiv1s.getName(), "HIV1");
		assertEquals(hiv2a.getName(), "HIV2A");
	}
	
	@Test
	public void testGetDisplayText() {
		assertEquals(hiv1s.getDisplayText(), "HIV-1");
		assertEquals(hiv2a.getDisplayText(), "HIV-2 Group A");
		assertEquals(hiv2b.getDisplayText(), "HIV-2 Group B");
	}
	
	/*@Test
	public void testGetNucaminoProfile() {
		assertEquals(hiv1s.getNucaminoProfile(), "hiv1b");
	
	}
	
	@Test
	public void testGetNucaminoGene() {
		assertEquals(hiv1s.getNucaminoGene(), "pol");
	}
	
	@Test
	public void testGetNucaminoGeneOffset() {
		assertEquals(hiv1s.getNucaminoGeneOffset(), Integer.valueOf(56));
	}*/
	
	@Test
	public void testAbsoluteFirstNA() {
		assertEquals(hiv1s.getAbsoluteFirstNA(), Integer.valueOf(1));
	}
	
	@Test
	public void testGetGene() {
		assertSame(hiv1s.getGene("PR"), hiv1.getGene("HIV1PR"));
	}
	
	@Test
	public void testGetDrugClasses() {
		assertEquals(hiv1s.getDrugClasses().size(), 5);
		assertEquals(hiv2a.getDrugClasses().size(), 3);
	}
	
	@Test
	public void testToString() {
		assertEquals(hiv1s.toString(), "HIV1");
	}
	
	@Test
	public void testEquals() {
		assertTrue(hiv1s.equals(hiv1.getStrain("HIV1")));
		assertFalse(hiv2a.equals(hiv2b));
	}
	
	@Test
	public void testhashCode() {
		assertEquals(hiv1s.hashCode(), 201518945);
	}
	
	@Test
	public void testCompareTo() {
		assertTrue(hiv2a.compareTo(hiv2b) < 0);
	}
}