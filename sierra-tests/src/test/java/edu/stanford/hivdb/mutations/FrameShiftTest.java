package edu.stanford.hivdb.mutations;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;

public class FrameShiftTest {
	
	private static final HIV hiv = HIV.getInstance();
	
	@Test
	public void testJoinFrameShifts() {
		FrameShift<HIV> fs1 = FrameShift.createDeletion(hiv.getGene("HIV1PR"), 10, 1);
		FrameShift<HIV> fs2 = FrameShift.createInsertion(hiv.getGene("HIV1PR"), 10, 1, "G");
		
		List<FrameShift<HIV>> fsList = new ArrayList<>();
		assertEquals(FrameShift.joinFrameShifts(fsList), "None");
		
		fsList.add(fs1);
		fsList.add(fs2);
		
		assertEquals(FrameShift.joinFrameShifts(fsList), "PR10del1bp, PR10ins1bp_G");
	}
	
	@Test
	public void testCompareTo() {
		FrameShift<HIV> fs1 = FrameShift.createDeletion(hiv.getGene("HIV1PR"), 10, 1);
		FrameShift<HIV> fs2 = FrameShift.createInsertion(hiv.getGene("HIV1PR"), 10, 1, "G");
		
		assertEquals(fs1.compareTo(fs2), 0);
	}
	
	@Test
	public void testIsInsertion() {
		FrameShift<HIV> fs1 = FrameShift.createInsertion(hiv.getGene("HIV1PR"), 10, 1, "G");
		assertTrue(fs1.isInsertion());
	}
	
	@Test
	public void testIsDeletion() {
		FrameShift<HIV> fs1 = FrameShift.createDeletion(hiv.getGene("HIV1PR"), 10, 1);
		assertTrue(fs1.isDeletion());
	}
	
	@Test
	public void testGetHumanFormat() {
		FrameShift<HIV> fs1 = FrameShift.createDeletion(hiv.getGene("HIV1PR"), 10, 1);
		FrameShift<HIV> fs2 = FrameShift.createInsertion(hiv.getGene("HIV1PR"), 10, 1, "G");
		
		assertEquals(fs1.getHumanFormat(), "PR10del1bp");
		assertEquals(fs2.getHumanFormat(), "PR10ins1bp_G");
		
	}
	
	@Test
	public void testCreateDeletion() {
		FrameShift.createDeletion(hiv.getGene("HIV1PR"), 10, 1);
	}
	
	@Test
	public void testCreateInsertion() {
		FrameShift.createInsertion(hiv.getGene("HIV1PR"), 10, 1, "G");
	}
	
	@Test
	public void testFromNucAminoFrameShift() {
		Map<String, Object> fs = new HashMap<>();
		
		fs.put("Position", 10.0);
		fs.put("GapLength", 1.0);
		fs.put("NucleicAcidsText", "G");
		fs.put("IsInsertion", true);
		
		FrameShift.fromNucAminoFrameShift(hiv.getGene("HIV1PR"), 10, fs);
	}
	
	@Test
	public void testGetStrain() {
		FrameShift<HIV> fs1 = FrameShift.createDeletion(hiv.getGene("HIV1PR"), 10, 1);
		assertEquals(fs1.getStrain(), hiv.getStrain("HIV1"));
		
	}
	
	@Test
	public void testGetGene() {
		FrameShift<HIV> fs1 = FrameShift.createDeletion(hiv.getGene("HIV1PR"), 10, 1);
		assertEquals(fs1.getGene(), hiv.getGene("HIV1PR"));
	}
	
	@Test
	public void testGetAbstractGene() {
		FrameShift<HIV> fs1 = FrameShift.createDeletion(hiv.getGene("HIV1PR"), 10, 1);
		assertEquals(fs1.getAbstractGene(), "PR");
	}
	
	@Test
	public void testGetPosition() {
		FrameShift<HIV> fs1 = FrameShift.createDeletion(hiv.getGene("HIV1PR"), 10, 1);
		assertEquals(fs1.getPosition(), 10);
	}
	
	@Test
	public void testGetType() {
		FrameShift<HIV> fs1 = FrameShift.createDeletion(hiv.getGene("HIV1PR"), 10, 1);
		assertEquals(fs1.getType(), FrameShift.Type.DELETION);
	}
	
	@Test
	public void testGetSize() {
		FrameShift<HIV> fs1 = FrameShift.createDeletion(hiv.getGene("HIV1PR"), 10, 2);
		assertEquals(fs1.getSize(), 2);
	}
	
	@Test
	public void testNAs() {
		FrameShift<HIV> fs1 = FrameShift.createDeletion(hiv.getGene("HIV1PR"), 10, 2);
		assertEquals(fs1.getNAs(), "");
	}
	
	@Test
	public void testGetText() {
		FrameShift<HIV> fs1 = FrameShift.createDeletion(hiv.getGene("HIV1PR"), 10, 2);
		assertEquals(fs1.getText(), "PR10del2bp");
	}
	
	@Test
	public void testToString() {
		FrameShift<HIV> fs1 = FrameShift.createDeletion(hiv.getGene("HIV1PR"), 10, 2);
		assertEquals(fs1.toString(), "PR10del2bp");
	}
	
	@Test
	public void testHashCode() {
		FrameShift<HIV> fs1 = FrameShift.createDeletion(hiv.getGene("HIV1PR"), 10, 2);
		assertEquals(fs1.hashCode(), 650457760);
	}
	
	@Test
	public void testEquals() {
		FrameShift<HIV> fs1 = FrameShift.createDeletion(hiv.getGene("HIV1PR"), 10, 1);
		FrameShift<HIV> fs2 = FrameShift.createInsertion(hiv.getGene("HIV1PR"), 10, 1, "G");
		
		assertFalse(fs1.equals(fs2));
		
		FrameShift<HIV> fs3 = FrameShift.createDeletion(hiv.getGene("HIV1PR"), 10, 1);
		assertTrue(fs1.equals(fs3));
	}
}