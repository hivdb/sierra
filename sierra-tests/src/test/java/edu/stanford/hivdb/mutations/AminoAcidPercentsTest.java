package edu.stanford.hivdb.mutations;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.AminoAcidPercents;

public class AminoAcidPercentsTest {

	private final static HIV hiv = HIV.getInstance();
	private static AminoAcidPercents<HIV> aaPcnts = null;
	
	@Rule
	public ExpectedException expectedEx = ExpectedException.none();
	
	private static void initInstance() {
		aaPcnts = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "all", "all");
	}
	
	static {
		initInstance();
	}
	

	@Test
	public void testAminoAcidPercents() {
		assertEquals(
				hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "all", "all"),
				hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "all", "all"));
	}

	@Test
	public void testAminoAcidPercentsWithException() {
		expectedEx.expect(ExceptionInInitializerError.class);
		expectedEx.expectMessage("Invalid resource name (aapcnt/rx-all_subtype-E.json)");
		hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "all", "E");
	}

	@Test
	public void testAminoAcidPercentsWithException2() {
		expectedEx.expect(ExceptionInInitializerError.class);
		expectedEx.expectMessage("Invalid resource name (aapcnt/rx-aaaaaaa_subtype-all.json)");
        hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "aaaaaaa", "all");
	}
	
	@Test
	public void testNewEmptyInstance() {
		assertTrue(AminoAcidPercents.newEmptyInstance() instanceof AminoAcidPercents<?>);
	}

	@Test
	public void testGetByGene() {
		AminoAcidPercents<HIV> allall = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "all", "all");
		List<AminoAcidPercent<HIV>> gRTAAPcnts = allall.get(hiv.getGene("HIV1RT"));
		assertEquals(560 * 23, gRTAAPcnts.size());
		int i = 0;
		for (int p = 1; p <= 560; p ++) {
			for (char aa : "ACDEFGHIKLMNPQRSTVWY_-*".toCharArray()) {
				AminoAcidPercent<HIV> mut = gRTAAPcnts.get(i++);
				assertEquals(hiv.getGene("HIV1RT"), mut.getGene());
				assertEquals(p, (int) mut.getPosition());
				assertEquals(aa, (char) mut.getAA());
			}
		}
	}
	
	@Test
	public void testGet() {
		List<AminoAcidPercent<HIV>> aaPcntList = aaPcnts.get();
		
		// all gene positions * all AAs+indel+stop codon
		assertEquals(aaPcntList.size(), (99 + 560 + 288) * 23);
	}
	
	@Test
	public void testGet$Gene() {
		List<AminoAcidPercent<HIV>> aaPcntList = aaPcnts.get(hiv.getGene("HIV1PR"));
		assertEquals(aaPcntList.size(), 99 * 23);
	}
	
	@Test
	public void testGet$Gene$int() {
		List<AminoAcidPercent<HIV>> gpIN263AAPcnts = aaPcnts.get(hiv.getGene("HIV1IN"), 263);
		assertEquals(gpIN263AAPcnts.size(), 23);
		
		int i = 0;
		for (char aa : "ACDEFGHIKLMNPQRSTVWY_-*".toCharArray()) {
			AminoAcidPercent<HIV> mutation = gpIN263AAPcnts.get(i ++);
			assertEquals(hiv.getGene("HIV1IN"), mutation.getGene());
			assertEquals(263, (int) mutation.getPosition());
			assertEquals(aa, (char) mutation.getAA());
		}
	}
	
	@Test
	public void testGet$GenePosition() {
		List<AminoAcidPercent<HIV>> gpIN263AAPcnts = aaPcnts.get(new GenePosition<HIV>(hiv.getGene("HIV1IN"), 263));
		assertEquals(23, gpIN263AAPcnts.size());
		
		int i = 0;
		for (char aa : "ACDEFGHIKLMNPQRSTVWY_-*".toCharArray()) {
			AminoAcidPercent<HIV> mutation = gpIN263AAPcnts.get(i ++);
			assertEquals(hiv.getGene("HIV1IN"), mutation.getGene());
			assertEquals(263, (int) mutation.getPosition());
			assertEquals(aa, (char) mutation.getAA());
		}
	}

	@Test
	public void testGet$Gene$int$char() {
		AminoAcidPercent<HIV> IN263R = aaPcnts.get(hiv.getGene("HIV1IN"), 263, 'R');
		assertNotNull(IN263R);
	}
		
	@Test
	public void testGet$GenePosition$char() {
		
		AminoAcidPercent<HIV> IN263R = aaPcnts.get(new GenePosition<HIV>(hiv.getGene("HIV1IN"), 263), 'R');
		assertNotNull(IN263R);
	}

	@Test
	public void testGetHighestPercentValue() {
		double highestVal = aaPcnts.getHighestAAPercentValue(hiv.getGene("HIV1PR"), 23, "AHI");
		
		double expectedHighestVal = .0;
		for (char aa : "AHI".toCharArray()) {
			double pcntVal = aaPcnts.get(hiv.getGene("HIV1PR"), 23, aa).getPercent();
			expectedHighestVal = Math.max(expectedHighestVal, pcntVal);
		}
		
		assertEquals(expectedHighestVal, highestVal, 1e-18);

		// These are intended to fail for every version update.
		// You must manually check and correct these numbers.
		assertEquals(0.00190681, highestVal, 1e-8);
		assertEquals(0.07966083, aaPcnts.getHighestAAPercentValue(hiv.getGene("HIV1RT"), 67, "N"), 1e-8);
		assertEquals(0.00836295, aaPcnts.getHighestAAPercentValue(hiv.getGene("HIV1RT"), 69, "KS"), 1e-8);
		assertEquals(0.04738156, aaPcnts.getHighestAAPercentValue(hiv.getGene("HIV1PR"), 82, "IA"), 1e-8);
		assertEquals(0.07966083, aaPcnts.getHighestAAPercentValue(hiv.getGene("HIV1RT"), 67, "N*"), 1e-8);
		assertEquals(0.0, aaPcnts.getHighestAAPercentValue(hiv.getGene("HIV1RT"), 67, "*"), 1e-8);
		assertEquals(0.0, aaPcnts.getHighestAAPercentValue(hiv.getGene("HIV1IN"), 1, ""), 1e-8);
	}

	@Test
	public void testContainsUnusualAA() {
		assertTrue(aaPcnts.containsUnusualAA(hiv.getGene("HIV1PR"), 1, "A"));
		assertFalse(aaPcnts.containsUnusualAA(hiv.getGene("HIV1RT"), 184, "M"));
		assertFalse(aaPcnts.containsUnusualAA(hiv.getGene("HIV1RT"), 184, "T"));
		// "O" is an invalid amino acid
		assertTrue(aaPcnts.containsUnusualAA(hiv.getGene("HIV1RT"), 184, "O"));
	}

}
