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

public class Codon69HandlerTest {

	@Before
	public void setUp() throws Exception {
	}

	// This test is broken due to refactoring of Codon69Handler
	@Test
	public void test() {
		final int firstAA = 64;
		final String alignedNAs =    "AAGAAAAAAGACAGTACTAAATGGAGAAAAGTAGTAGATTTCAGAGAGCTTAATAAAAGAACTCAAGACTTCTGGGAAGTTCAATTAGGAATACCACATCCAGCAGGGTTAAAAAAGAAAAAATCAGTAACAGTACTGGATGTGGGTGATGCATATTTTTCAGTTCCCTTAGATGAAGACTTCAGGAAGTATACTGCATTTACCATACCTAGCATAAACAATGAGACACCAGGGATTAGATATCAGTACAATGTGCTTCCACAGGGATGGAAAGGATCACCAGCGATATTCCAAAGTAGCATGACAAAAATCTTAGAGCCTTTTAGAAAACAAAATCCAGACATGGTTATCTATCAATACATGGATGATTTGTATGTAGGATCTGACTTAGAAATAGGGCAGCATAGAACAAAAATAGAGCAACTGAGAGAACATCTGTTGAAGTGG";
		final String controlLine =   ":::::::::::::::::::::::::::::: ..::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: ..:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::.. ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: ..:::::: ..:::::::::. .:::";
		final String aaTripletLine = "LysLysLysAspSerThrLysTrpArgLysLeuValAspPheArgGluLeuAsnLysArgThrGlnAspPheTrpGluValGlnLeuGlyIleProHisProAlaGlyLeuLysLysLysLysSerValThrValLeuAspValGlyAspAlaTyrPheSerValProLeuAspLysAspPheArgLysTyrThrAlaPheThrIleProSerIleAsnAsnGluThrProGlyIleArgTyrGlnTyrAsnValLeuProGlnGlyTrpLysGlySerProAlaIlePheGlnSerSerMetThrLysIleLeuGluProPheArgLysGlnAsnProAspIleValIleTyrGlnTyrMetAspAspLeuTyrValGlySerAspLeuGluIleGlyGlnHisArgThrLysIleGluGluLeuArgGlnHisLeuLeuArgTrp";
		CLapAlignResult r = Codon69Handler.process(firstAA, alignedNAs, controlLine, aaTripletLine);
		assertEquals(r.aaTripletLine, aaTripletLine);
		assertEquals(r.controlLine, controlLine);
		assertEquals(r.alignedNAs, alignedNAs);

		r = Codon69Handler.process(69, alignedNAs.substring(15), controlLine.substring(15), aaTripletLine.substring(15));
		assertEquals(r.aaTripletLine, aaTripletLine.substring(15));
		assertEquals(r.controlLine, controlLine.substring(15));
		assertEquals(r.alignedNAs, alignedNAs.substring(15));

		r = Codon69Handler.process(70, alignedNAs.substring(18), controlLine.substring(18), aaTripletLine.substring(18));
		assertEquals(r.aaTripletLine, aaTripletLine.substring(18));
		assertEquals(r.controlLine, controlLine.substring(18));
		assertEquals(r.alignedNAs, alignedNAs.substring(18));
	}

	@Test
	public void testNoInsertions() {
		int firstAA = 61;
		String alignedNAs =    "TTTGTCATAAAGAAAAAAGACAGTTCCAAATGGAGA";
		String controlLine =   "::: V :::::::::::::::::: S :::::::::";
		String aaTripletLine = "PheAlaIleLysLysLysAspSerThrLysTrpArg";
		CLapAlignResult r = Codon69Handler.process(firstAA, alignedNAs, controlLine, aaTripletLine);
		assertEquals(alignedNAs, r.alignedNAs);
		assertEquals(controlLine, r.controlLine);
		assertEquals(aaTripletLine, r.aaTripletLine);
	}

	@Test
	public void testLastAASmallerThan69() {
		int firstAA = 61;
		String alignedNAs =    "TTTGTCATAAAGAAAAAAGACAGT";
		String controlLine =   "::: V ::::::::::::::::::";
		String aaTripletLine = "PheAlaIleLysLysLysAspSer";
		CLapAlignResult r = Codon69Handler.process(firstAA, alignedNAs, controlLine, aaTripletLine);
		assertEquals(alignedNAs, r.alignedNAs);
		assertEquals(controlLine, r.controlLine);
		assertEquals(aaTripletLine, r.aaTripletLine);
	}

	@Test
	public void testFirstAALargerThan69() {
		int firstAA = 70;
		String alignedNAs =    "AAATGGAGA";
		String controlLine =   ":::::::::";
		String aaTripletLine = "LysTrpArg";
		CLapAlignResult r = Codon69Handler.process(firstAA, alignedNAs, controlLine, aaTripletLine);
		assertEquals(alignedNAs, r.alignedNAs);
		assertEquals(controlLine, r.controlLine);
		assertEquals(aaTripletLine, r.aaTripletLine);
	}

}
