/*
    
    Copyright (C) 2017 Stanford HIVDB team
    
    Sierra is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    
    Sierra is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package edu.stanford.hivdb.alignment;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.hivdb.alignment.CodonAligner;


public class CodonAlignerTest {

	@Before
	public void setUp() throws Exception {};

	@Test
	public void testCase1() {
		// case 1: deletion should be fixed
		CodonAligner alignResult = CodonAligner.process(
				"AAAAT   GTTT",
				":::..---.:::",
				"LysIleLeuPhe");
		assertEquals("AAAATG   TTT", alignResult.getAlignedNAs());
		assertEquals(":::.. ---:::", alignResult.getControlLine());
		assertEquals("LysIleLeuPhe", alignResult.getAATripletLine());
	}

	@Test
	public void testCase2() {
		// case 2: deletion shouldn't be fixed
		CodonAligner alignResult = CodonAligner.process(
				"AAAAT    GTT", ":::..---- ..", "LysMetAsnIle");
		assertEquals("AAAAT    GTT", alignResult.getAlignedNAs());
		assertEquals(":::..---- ..", alignResult.getControlLine());
		assertEquals("LysMetAsnIle", alignResult.getAATripletLine());
	}

	@Test
	public void testCase6() {
		CodonAligner alignResult = CodonAligner.process(
				"TCAGTTCC        AAAGCTTCAGA",
				"::::::..--------..  .::::::",
				"SerValProLeuAspLysAspPheArg");
		assertEquals("TCAGTTCCAA        AGCTTCAGA", alignResult.getAlignedNAs());
		assertEquals(":::::::::.--------  .::::::", alignResult.getControlLine());
		assertEquals("SerValProLeuAspLysAspPheArg", alignResult.getAATripletLine());
	}

	@Test
	public void testCase7() {
		CodonAligner alignResult = CodonAligner.process(
				"TGGATGTCACACA",
				":::  ..- .:::",
				"TrpGlyP heThr");
		assertEquals("TGGATGTCACACA", alignResult.getAlignedNAs());
		assertEquals(":::  .-  .:::", alignResult.getControlLine());
		assertEquals("TrpGly PheThr", alignResult.getAATripletLine());

		alignResult = CodonAligner.process(
				"GATAAATGGTCAGGCTAAAAGGG",
				"::::::..--.  .:::::::::",
				"AspLysCy  sGlnLeuLysGly");
		assertEquals("GATAAATGGTCAGGCTAAAAGGG", alignResult.getAlignedNAs());
		assertEquals("::::::.. --  .:::::::::", alignResult.getControlLine());
		assertEquals("AspLysCys  GlnLeuLysGly", alignResult.getAATripletLine());
	}

	@Test
	public void testCase8() {
		CodonAligner alignResult = CodonAligner.process(
				"GATAAAT GACAGTA",
				"::::::.-.::::::",
				"AspLysTrpThrVal");
		assertEquals("GATAAAT GACAGTA", alignResult.getAlignedNAs());
		assertEquals("::::::.-.::::::", alignResult.getControlLine());
		assertEquals("AspLysTrpThrVal", alignResult.getAATripletLine());
	}

	@Test
	public void testCase3() {
		// case 3: mixed deletion and insertion which are should be fixed
		CodonAligner alignResult = CodonAligner.process(
				"AA AA   TGTTTCCCCA",
				"..-..---.:::.---..",
				"LysAsnProValS   er");
		assertEquals("AA AAT   GTTTCCCCA", alignResult.getAlignedNAs());
		assertEquals("..-:::---:::--- ..", alignResult.getControlLine());
		assertEquals("LysAsnProVal   Ser", alignResult.getAATripletLine());
	}

	@Test
	public void testCase4() {
		CodonAligner alignResult = CodonAligner.process(
				"TTTGCTATTAAAAAGAAAGACATAATCAGTCGTAAATGGCGC",
				"::::::::::::::::::..------.:::  .:::::::::",
				"PheAlaIleLysLysLysAs      pSerThrLysTrpArg");
		assertEquals(
				"TTTGCTATTAAAAAGAAAGACATAATCAGTCGTAAATGGCGC",
				alignResult.getAlignedNAs());
		assertEquals(
				":::::::::::::::::::::------:::  .:::::::::",
				alignResult.getControlLine());
		assertEquals(
				"PheAlaIleLysLysLysAsp      SerThrLysTrpArg",
				alignResult.getAATripletLine());
	}

	@Test
	public void testCase5() {
		CodonAligner alignResult = CodonAligner.process(
				"TTTGTTATTAAAAAGAAAGACAGTAGATGCACTAGATGGCGC",
				":::. .::::::::::::::::::.------... .::::::",
				"PheAlaIleLysLysLysAspSerT      hrLysTrpArg");
		assertEquals(
				"TTTGTTATTAAAAAGAAAGACAGTAGATGCACTAGATGGCGC",
				alignResult.getAlignedNAs());
		assertEquals(
				":::. .::::::::::::::::::------:::. .::::::",
				alignResult.getControlLine());
		assertEquals(
				"PheAlaIleLysLysLysAspSer      ThrLysTrpArg",
				alignResult.getAATripletLine());
	}

}
