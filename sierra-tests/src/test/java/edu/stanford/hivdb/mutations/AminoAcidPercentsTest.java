package edu.stanford.hivdb.mutations;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.AminoAcidPercents;

public class AminoAcidPercentsTest {

	final static HIV hiv = HIV.getInstance();

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

	@Test
	public void testGetInstance() {

		AminoAcidPercents<HIV> allall01 = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "all", "all");
		AminoAcidPercents<HIV> allall02 = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "all", "all");
		assertEquals(allall01, allall02);
	}

	@Test
	public void testGetInstanceFailCase1() {
		expectedEx.expect(ExceptionInInitializerError.class);
		expectedEx.expectMessage("Invalid resource name (aapcnt/rx-all_subtype-E.json)");
		hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "all", "E");
	}

	@Test
	public void testGetInstanceFailCase2() {
		expectedEx.expect(ExceptionInInitializerError.class);
		expectedEx.expectMessage("Invalid resource name (aapcnt/rx-aaaaaaa_subtype-all.json)");
        hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "aaaaaaa", "all");
	}

	@Test
	public void testGsonLoad() {
		AminoAcidPercents<HIV> allall = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "all", "all");
		assertEquals((99 + 560 + 288) * 23, allall.get().size());
		AminoAcidPercent<HIV> mutPR1A = allall.get().get(0);
		assertEquals(hiv.getGene("HIV1PR"), mutPR1A.getGene());
		assertEquals(1, (int) mutPR1A.getPosition());
		assertEquals('A', (char) mutPR1A.getAA());
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
	public void testGetByGenePos() {
		AminoAcidPercents<HIV> allall = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "all", "all");
		List<AminoAcidPercent<HIV>> gpIN263AAPcnts = allall.get(hiv.getGene("HIV1IN"), 263);
		assertEquals(23, gpIN263AAPcnts.size());
		int i = 0;
		for (char aa : "ACDEFGHIKLMNPQRSTVWY_-*".toCharArray()) {
			AminoAcidPercent<HIV> mut = gpIN263AAPcnts.get(i ++);
			assertEquals(hiv.getGene("HIV1IN"), mut.getGene());
			assertEquals(263, (int) mut.getPosition());
			assertEquals(aa, (char) mut.getAA());
		}
	}

	@Test
	public void testGetByMut() {
		AminoAcidPercents<HIV> allall = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "all", "all");
		AminoAcidPercent<HIV> mutIN263R = allall.get(hiv.getGene("HIV1IN"), 263, 'R');
		assertEquals(hiv.getGene("HIV1IN"), mutIN263R.getGene());
		assertEquals(263, (int) mutIN263R.getPosition());
		assertEquals('R', (char) mutIN263R.getAA());
	}

	@Test
	public void testGetHighestPercentValue() {
		AminoAcidPercents<HIV> allall = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "all", "all");
		double highestVal = allall.getHighestAAPercentValue(hiv.getGene("HIV1PR"), 23, "AHI");
		double expectedHighestVal = .0;
		for (char aa : "AHI".toCharArray()) {
			double pcntVal = allall.get(hiv.getGene("HIV1PR"), 23, aa).getPercent();
			expectedHighestVal = Math.max(expectedHighestVal, pcntVal);
		}
		assertEquals(expectedHighestVal, highestVal, 1e-18);

		// These are intended to fail for every version update.
		// You must manually check and correct these numbers.
		assertEquals(0.00193290, highestVal, 1e-8);
		assertEquals(0.08111958, allall.getHighestAAPercentValue(hiv.getGene("HIV1RT"), 67, "N"), 1e-8);
		assertEquals(0.00822416, allall.getHighestAAPercentValue(hiv.getGene("HIV1RT"), 69, "KS"), 1e-8);
		assertEquals(0.04712358, allall.getHighestAAPercentValue(hiv.getGene("HIV1PR"), 82, "IA"), 1e-8);
		assertEquals(0.08111958, allall.getHighestAAPercentValue(hiv.getGene("HIV1RT"), 67, "N*"), 1e-8);
		assertEquals(0.0, allall.getHighestAAPercentValue(hiv.getGene("HIV1RT"), 67, "*"), 1e-8);
		assertEquals(0.0, allall.getHighestAAPercentValue(hiv.getGene("HIV1IN"), 1, ""), 1e-8);
	}

	@Test
	public void testContainsUnusualAA() {
		AminoAcidPercents<HIV> allall = hiv.getAminoAcidPercents(hiv.getStrain("HIV1"), "all", "all");
		assertTrue(allall.containsUnusualAA(hiv.getGene("HIV1RT"), 5, "I*"));
		assertFalse(allall.containsUnusualAA(hiv.getGene("HIV1RT"), 67, "N"));
		assertTrue(allall.containsUnusualAA(hiv.getGene("HIV1PR"), 82, "VIAD"));
		assertFalse(allall.containsUnusualAA(hiv.getGene("HIV1RT"), 69, "_"));
		assertTrue(allall.containsUnusualAA(hiv.getGene("HIV1PR"), 67, "-"));
	}

}
