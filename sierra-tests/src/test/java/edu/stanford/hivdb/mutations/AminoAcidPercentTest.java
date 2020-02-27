package edu.stanford.hivdb.mutations;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.AminoAcidPercent;

public class AminoAcidPercentTest {

	private final static HIV hiv = HIV.getInstance();
	private static AminoAcidPercent<HIV> aaPcnt;
	
	private static void initInstance() {
		aaPcnt = new AminoAcidPercent<HIV>(
				hiv.getGene("HIV1PR"),
				1,
				'A',
				2.6901969224147208e-05,
				4,
				148688,
				"PCNT",
				true
			);
	}
	
	static {
		initInstance();
	}

	@Test
	public void testAminoAcidPercent() {

		assertNotNull(aaPcnt);

	}
	
	@Test
	public void testGetStrain() {
		assertEquals(aaPcnt.getStrain(), hiv.getStrain("HIV1"));
	}
	
	@Test
	public void testGetGene() {
		assertEquals(aaPcnt.getGene(), hiv.getGene("HIV1PR"));
	}
	
	@Test
	public void testGetAbstractGene() {
		assertEquals(aaPcnt.getAbstractGene(), "PR");
	}

	@Test
	public void testGetPosition() {
		assertEquals(aaPcnt.getPosition(), Integer.valueOf(1));
	}
	
	@Test
	public void testGetAA() {
		assertEquals(aaPcnt.getAA(), Character.valueOf('A'));
	}
	
	@Test
	public void testGetRefChar() {
		assertEquals(aaPcnt.getRefChar(), Character.valueOf('P'));
	}
	
	@Test
	public void testGetMutation() {
		assertTrue(aaPcnt.getMutation() instanceof AAMutation<?>);
	}
	
	@Test
	public void testGetPercent() {
		assertEquals(aaPcnt.getPercent(), 2.6901969224147208e-05, 1e-05);
	}
	
	@Test
	public void testGetCount() {
		assertEquals(aaPcnt.getCount(), Integer.valueOf(4));
	}
	
	@Test
	public void testGetTotal() {
		assertEquals(aaPcnt.getTotal(), Integer.valueOf(148688));
	}
	
	@Test
	public void testGetReason() {
		assertEquals(aaPcnt.getReason(), "PCNT");
	}

}
