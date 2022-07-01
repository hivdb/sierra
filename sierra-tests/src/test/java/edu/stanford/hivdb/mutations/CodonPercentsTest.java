package edu.stanford.hivdb.mutations;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.stanford.hivdb.hivfacts.HIV;

public class CodonPercentsTest {

	private static HIV hiv = HIV.getInstance();

	@Rule
	public ExpectedException expectedEx = ExpectedException.none();

    @Test
    public void testGetInstance() {
        CodonPercents<HIV> allall01 = hiv.getCodonPercents(hiv.getStrain("HIV1"), "all", "all");
        CodonPercents<HIV> allall02 = hiv.getCodonPercents(hiv.getStrain("HIV1"), "all", "all");
        assertEquals("same singleton instance", allall01, allall02);
    }

	@Test
	public void testGetInstanceFailCase1() {
		expectedEx.expect(ExceptionInInitializerError.class);
		expectedEx.expectMessage("Invalid resource name (codonpcnt/rx-all_subtype-E.json)");
		hiv.getCodonPercents(hiv.getStrain("HIV1"), "all", "E");
	}

	@Test
	public void testGetInstanceFailCase2() {
		expectedEx.expect(ExceptionInInitializerError.class);
		expectedEx.expectMessage("Invalid resource name (codonpcnt/rx-aaaaaaa_subtype-all.json)");
		hiv.getCodonPercents(hiv.getStrain("HIV1"), "aaaaaaa", "all");
	}

    @Test
    public void testGet() {
        CodonPercents<HIV> allall = hiv.getCodonPercents(hiv.getStrain("HIV1"), "all", "all");
		CodonPercent<HIV> mutPR1ACA = allall.get().get(0);
		assertEquals(hiv.getGene("HIV1PR"), mutPR1ACA.getGene());
		assertEquals(Integer.valueOf(1), mutPR1ACA.getPosition());
		assertEquals(Character.valueOf('T'), mutPR1ACA.getAA());
		assertEquals("ACA", mutPR1ACA.getCodon());

    }

	@Test
	public void testGetByGene() {
		CodonPercents<HIV> allall = hiv.getCodonPercents(hiv.getStrain("HIV1"), "all", "all");
		List<CodonPercent<HIV>> gRTCdPcnts = allall.get(hiv.getGene("HIV1RT"));
		int i = 0;
		int pos = 1;
		char[] acgt = "ACGT".toCharArray();
		// Some of the codons might never occur. However the list
		// should be still in alphabetical order.
		for (char na1 : acgt) {
			for (char na2 : acgt) {
				for (char na3 : acgt) {
					if (i >= gRTCdPcnts.size()) {
						return;
					}
					CodonPercent<HIV> mut = gRTCdPcnts.get(i ++);
					assertEquals(hiv.getGene("HIV1RT"), mut.getGene());
					assertEquals(pos, (int) mut.getPosition());
					String expectedCodon = "" + na1 + na2 + na3;
					assertTrue(mut.getCodon().compareTo(expectedCodon) >= 0);
					if (mut.getCodon().compareTo(expectedCodon) != 0) {
						i --;
					}
					if (expectedCodon.equals("TTT")) {
						pos ++;
					}
				}
			}
		}
	}


	@Test
	public void testGetByGenePos() {
		CodonPercents<HIV> allall = hiv.getCodonPercents(hiv.getStrain("HIV1"), "all", "all");
		List<CodonPercent<HIV>> gpIN263CdPcnts = allall.get(hiv.getGene("HIV1IN"), 263);
		int i = 0;
		char[] acgt = "ACGT".toCharArray();
		// Some of the codons might never occur. However the list
		// should be still in alphabetical order.
		for (char na1 : acgt) {
			for (char na2 : acgt) {
				for (char na3 : acgt) {
					if (i >= gpIN263CdPcnts.size()) {
						return;
					}
					CodonPercent<HIV> mut = gpIN263CdPcnts.get(i ++);
					assertEquals(hiv.getGene("HIV1IN"), mut.getGene());
					assertEquals(263, (int) mut.getPosition());
					assertTrue(mut.getCodon().compareTo("" + na1 + na2 + na3) >= 0);
					if (mut.getCodon().compareTo("" + na1 + na2 + na3) != 0) {
						i --;
					}
				}
			}
		}
	}

	@Test
	public void testGetByMut() {
		CodonPercents<HIV> allall = hiv.getCodonPercents(hiv.getStrain("HIV1"), "all", "all");
		CodonPercent<HIV> mutIN263AGG = allall.get(hiv.getGene("HIV1IN"), 263, "AGG");
		assertEquals(hiv.getGene("HIV1IN"), mutIN263AGG.getGene());
		assertEquals(263, (int) mutIN263AGG.getPosition());
		assertEquals('R', (char) mutIN263AGG.getAA());
		assertEquals("AGG", mutIN263AGG.getCodon());
		CodonPercent<HIV> mutIN5ins = allall.get(hiv.getGene("HIV1IN"), 5, "ins");
		assertEquals(hiv.getGene("HIV1IN"), mutIN5ins.getGene());
		assertEquals(5, (int) mutIN5ins.getPosition());
		assertEquals('_', (int) mutIN5ins.getAA());
		assertEquals("ins", mutIN5ins.getCodon());
		CodonPercent<HIV> mutIN5del = allall.get(hiv.getGene("HIV1IN"), 5, "del");
		assertEquals(hiv.getGene("HIV1IN"), mutIN5del.getGene());
		assertEquals(5, (int) mutIN5del.getPosition());
		assertEquals('-', (int) mutIN5del.getAA());
		assertEquals("del", mutIN5del.getCodon());
	}

	@Test
	public void testGetByMutNotOccur() {
		CodonPercents<HIV> allall = hiv.getCodonPercents(hiv.getStrain("HIV1"), "all", "all");
		CodonPercent<HIV> mutIN263GTG = allall.get(hiv.getGene("HIV1IN"), 263, "GTG");
		assertEquals(hiv.getGene("HIV1IN"), mutIN263GTG.getGene());
		assertEquals(263, (int) mutIN263GTG.getPosition());
		assertEquals('V', (char) mutIN263GTG.getAA());
		assertEquals("GTG", mutIN263GTG.getCodon());
	}

	@Test
	public void testGetByInvalidMut() {
		CodonPercents<HIV> allall = hiv.getCodonPercents(hiv.getStrain("HIV1"), "all", "all");
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Invalid argument codon \"EEE\" at HIV1IN1");
		allall.get(hiv.getGene("HIV1IN"), 1, "EEE");
	}

	@Test
	public void testGetByInvalidMut2() {
		CodonPercents<HIV> allall = hiv.getCodonPercents(hiv.getStrain("HIV1"), "all", "all");
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Invalid argument codon \"\" at HIV1IN1");
		allall.get(hiv.getGene("HIV1IN"), 1, "");
	}

	@Test
	public void testGetOutOfRange() {
		CodonPercents<HIV> allall = hiv.getCodonPercents(hiv.getStrain("HIV1"), "all", "all");
		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Argument 'pos' is out of range: 100");
		allall.get(hiv.getGene("HIV1PR"), 100, "AAA");

	}

	@Test
	public void testGetHighestPercentValue() {
		CodonPercents<HIV> allall = hiv.getCodonPercents(hiv.getStrain("HIV1"), "all", "all");
		double highestVal = allall.getHighestCodonPercentValue(hiv.getGene("HIV1PR"), 23, "AAA", "AAG", "ATA", "ATC");
		double expectedHighestVal = .0;
		for (String codon : new String[] {"AAA", "AAG", "ATA", "ATC"}) {
			double pcntVal = allall.get(hiv.getGene("HIV1PR"), 23, codon).getPercent();
			expectedHighestVal = Math.max(expectedHighestVal, pcntVal);
		}
		assertEquals(expectedHighestVal, highestVal, 1e-18);

		// These are intended to fail for every version update.
		// You must manually check and correct these numbers.
		assertEquals(0.002, highestVal, 1e-3);
		assertEquals(0.8, allall.getHighestCodonPercentValue(hiv.getGene("HIV1RT"), 67, "GAC"), 1e-1);
		assertEquals(0.004, allall.getHighestCodonPercentValue(hiv.getGene("HIV1RT"), 69, "AAA", "AGC", "AGT"), 1e-3);
		assertEquals(0.0, allall.getHighestCodonPercentValue(hiv.getGene("HIV1RT"), 67, "TGG"), 1e-8);
		assertEquals(0.07, allall.getHighestCodonPercentValue(hiv.getGene("HIV1RT"), 67, "AAC", "TGA"), 1e-2);
		assertEquals(0.0, allall.getHighestCodonPercentValue(hiv.getGene("HIV1RT"), 67, "TGA"), 1e-8);

		expectedEx.expect(IllegalArgumentException.class);
		expectedEx.expectMessage("Invalid argument codon \"EEE\" at HIV1IN1");
		allall.getHighestCodonPercentValue(hiv.getGene("HIV1IN"), 1, "EEE");
	}


}