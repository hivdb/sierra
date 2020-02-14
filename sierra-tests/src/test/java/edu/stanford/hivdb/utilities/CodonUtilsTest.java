package edu.stanford.hivdb.utilities;


import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;


public class CodonUtilsTest {
	
	@Test
	public void translateNATripletTest() {
		assertEquals(CodonUtils.translateNATriplet("A"), "X");
		assertEquals(CodonUtils.translateNATriplet("AA"), "X");
		assertEquals(CodonUtils.translateNATriplet("AAA"), "K");
	}
	
	@Test
	public void translateToTripletAATest() {
		assertEquals(CodonUtils.translateToTripletAA(""), "");
		assertEquals(CodonUtils.translateToTripletAA(null), "");
		assertEquals(CodonUtils.translateToTripletAA("K"), "Lys");
	}
	
	@Test
	public void generateControlStringTest() {
		assertEquals(CodonUtils.generateControlString("TTT", "Asn"), "  .");
		assertEquals(CodonUtils.generateControlString("TCA", "Met"), "   ");
		assertEquals(CodonUtils.generateControlString("AAT", "Asn"), ":::");
	}
	
	@Test
	public void translateAATest() {
		List<String> test = new ArrayList<>();
		test.add("AAC");
		test.add("AAT");
		assertEquals(CodonUtils.translateAA('N'), test);
	}
	
	@Test
	public void simpleTranslateTest() {
		assertEquals(CodonUtils.simpleTranslate("TTA"), "L");
		assertEquals(CodonUtils.simpleTranslate("RAA"), "X");
		assertEquals(CodonUtils.simpleTranslate("TTAT"), "L");
		assertEquals(CodonUtils.simpleTranslate("TTARAATTA"), "LXL");
	}
	
	@Test
	public void simpleTranslate2Test() {
		assertEquals(CodonUtils.simpleTranslate("AGTRAA", 1, "SE"), "SK");
	}
	
	@Test
	public void expandAmbiguityNATest() {
		assertEquals(CodonUtils.expandAmbiguityNA('N'), "ACGT");
	}
	
	@Test
	public void getMergedCodonTest() {
		List<String> codons = new ArrayList<>();
		codons.add("-AT");
		codons.add("GAT");
		codons.add("GTT");
		assertEquals(CodonUtils.getMergedCodon(codons), "-WT");
	}
}