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

package edu.stanford.hivdb.sequences;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;

public class AlignedGeneSeqTest {

	final static HIV hiv = HIV.getInstance();
	// private AlignedGeneSeq getAlignedGeneSeq() {
	// 	Sequence seq = new Sequence(
	// 		"SmallFSIns4_LargeFSdel17-18",
	// 		"CCTCAAATCACCTCTTTGGCAACGACCCATCGTCACAATAAAGATAGGGAGCTAARGGAAGCTCTATTAGATACAGGAGCAGATGATACAGTATTAGAAGATATAAATTTGCCAGGAAGATGGACACCAAAAATKATAGTGGGAATTGGAGGTTTTACCAAAGTAAGACAGTATGATCAGATACCTGTAGAAATTTGTGGACATAAAGCTATAGGTACAGTRTTAGTAGGACCTACACCTGCCAACATAATTGGAAGAAATCTGTTGACYCAGATTGGTTGCACTTTAAATTTTCCCATTAGTCCTATTGACACTGTACCAGTAAAATTAAAGCCAGGAATGGATGGCCCAAAAGTTAAACAATGGCCATTGACAGAAGAAAAAATAAAAGCATTAGTAGAAATTTGTGCAGAATTGGAASAGGACGGGAAAATTTCAAAAATTGGGCCTGAAAATCCATACAATACTCCAGTATTTGCCATAAAGAAAAAGAACAGYGATAAATGGAGAAAATTAGTAGATTTCAGAGAACTTAATAAGAGAACTCAAGACTTCTGGGAAGTTCAATTAGGAATACCACATCCCGGAGGGTTAAAAAAGAACAAATCAGTAACAGTACTGGATGTGGGTGATGCATATTTTTCARTTCCCTTAGATGAAGACTTCAGGAAGTATACTGCATTTACCATACCTAGTATAAACAATGAGACACCAGGGACTAGATATCAGTACAATGTGCTTCCACAGGGATGGAAAGGATCACCAGCAATATTCCAAAGTAGCATGACAAGAATCTTAGAACCTTTTAGAAAACAGAATCCAGACATAGTTATCTGTCAATAYGTGGATGATTTGTATGTAGGATCTGACTTAGAAATAGAGMAGCATAGAACAAAAGTAGAGGAACTGAGACAACATTTGTGGAAGTGGGGNTTTTACACACCAGACAAMAAACATCAGAAAGAACCTCCATTCCTTTGGATGGGTTATGAACTCCATCCTGATAAATGGACA");
	// 	AlignedGeneSeq alignedGeneSeq = new AlignedGeneSeq(
	// 		seq, Gene.PR, /* int firstAA */1, /* int lastAA */99,
	// 		/* int firstNA */1, /* int lastNA */294, /* int pcnt */84,
	// 		/* String originalAlignedNAs	*/"CCTCAAATCACCTCTTTGGCAACGACCCATCGTCACAATAAAGATAGGG	AGCTAARGGAAGCTCTATTAGATACAGGAGCAGATGATACAGTATTAGAAGATATAAATTTGCCAGGAAGATGGACACCAAAAATKATAGTGGGAATTGGAGGTTTTACCAAAGTAAGACAGTATGATCAGATACCTGTAGAAATTTGTGGACATAAAGCTATAGGTACAGTRTTAGTAGGACCTACACCTGCCAACATAATTGGAAGAAATCTGTTGACYCAGATTGGTTGCACTTTAAATTTT",
	// 		/* String originalControlLine   */"::::::::::::-::::::::::::::: ..::::::::::::::::::----..:::. .::::::::::::::::::::::::::::::::::::::::::.. .. ::::::::::::::::::. .::::::.. :::. .:::::::::::::::. .::::::::::::::::::::::::. . ..::::::::::::::::::::::::::::::...::::::::::::::::::. .::::::::::::::::::::::::...::::::::::::::::::::::::",
	// 		/* String originalAATripletLine */"ProGlnIleThr LeuTrpGlnArgProLeuValThrIleLysIleGlyGlyGlnLeuLysGluAlaLeuLeuAspThrGlyAlaAspAspThrValLeuGluGluMetAsnLeuProGlyArgTrpLysProLysMetIleGlyGlyIleGlyGlyPheIleLysValArgGlnTyrAspGlnIleLeuIleGluIleCysGlyHisLysAlaIleGlyThrValLeuValGlyProThrProValAsnIleIleGlyArgAsnLeuLeuThrGlnIleGlyCysThrLeuAsnPhe");
	// 	return alignedGeneSeq;
	// }

	// @Test
	// public void testConstructor() {
	// 	getAlignedGeneSeq();
	// }


	@Test
	public void testReversedSeqGetAlignedNAs() {
		Sequence revSeq = new Sequence("ReversedSeq", "CCAAAGAGTGATTTGAGG", true);
		
		// AlignedGeneSeq shouldn't handle reversed compliment
		AlignedGeneSeq<HIV> alignedGeneSeq = new AlignedGeneSeq<HIV>(
			revSeq, hiv.getGene("HIV1PR"),
			1, 6, 1, 18, Arrays.asList(
				new AlignedSite(1, 1, 3),
				new AlignedSite(2, 4, 3),
				new AlignedSite(3, 7, 3),
				new AlignedSite(4, 10, 3),
				new AlignedSite(5, 13, 3),
				new AlignedSite(6, 16, 3)
			),
			Collections.emptyList(), Collections.emptyList());
		assertEquals("CCAAAGAGTGATTTGAGG", alignedGeneSeq.getAlignedNAs());
	}

	@Test
	public void testReversedSeqGetAlignedAAs() {
		Sequence revSeq = new Sequence("ReversedSeq", "CCAAAGAGTGATTTGAGG", true);

		// AlignedGeneSeq shouldn't handle reversed compliment
		AlignedGeneSeq<HIV> alignedGeneSeq = new AlignedGeneSeq<HIV>(
			revSeq, hiv.getGene("HIV1PR"),
			1, 6, 1, 18, Arrays.asList(
				new AlignedSite(1, 1, 3),
				new AlignedSite(2, 4, 3),
				new AlignedSite(3, 7, 3),
				new AlignedSite(4, 10, 3),
				new AlignedSite(5, 13, 3),
				new AlignedSite(6, 16, 3)
			),
			Collections.emptyList(), Collections.emptyList());
		assertEquals("PKSDLR", alignedGeneSeq.getAlignedAAs());
	}

}
