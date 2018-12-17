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

import org.junit.Test;

import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.Sequence;

public class NucAminoAlignerTest {

	@Test
	public void testSingleSequence() {
		Sequence testSeq = Sequence.fromGenbank("AF096883");
		AlignedSequence alignedSeq = NucAminoAligner.align(testSeq);
		AlignedGeneSeq seqPR = alignedSeq.getAlignedGeneSequence(Gene.PR);
		assertEquals(1, seqPR.getFirstAA());
		assertEquals(1, seqPR.getFirstNA());
		assertEquals(99, seqPR.getLastAA());
		assertEquals(297, seqPR.getLastNA());
		assertEquals(
			new MutationSet(
				Gene.PR,
				"L10V,E35D,M36I,N37D,I54V,Q58E,I62IV,L63P,I64V,A71V,G73T,L90M"),
			seqPR.getMutations()
		);
		assertEquals(
			"PQITLWQRPVVTIKIGGQLKEALLDTGADDTVLEDIDLPGRWKPKMIGGI" +
			"GGFVKVREYDQVPVEICGHKVITTVLVGPTPVNIIGRNLMTQIGCTLNF",
			seqPR.getAlignedAAs()
		);
		AlignedGeneSeq seqRT = alignedSeq.getAlignedGeneSequence(Gene.RT);
		assertEquals(1, seqRT.getFirstAA());
		assertEquals(298, seqRT.getFirstNA());
		assertEquals(230, seqRT.getLastAA());
		assertEquals(993, seqRT.getLastNA());
		assertEquals(
			new MutationSet(
				Gene.RT,
				"E6D,K20R,M41L,K43Q,A62V,T69S_SA,K103N, " +
				"V118I,S162A,Y181C,Q207E,R211K,T215Y,L228H"),
			seqRT.getMutations()
		);
		assertEquals(
			"PISPIDTVPVKLKPGMDGPRVKQWPLTEEKIKALVEICTELEQEGKISKI" +
			"GPENPYNTPVFVIKKKDSSKWRKLVDFRELNKRTQDFWEVQLGIPHPAGL" +
			"KKNKSVTVLDVGDAYFSIPLDKDFRKYTAFTIPSINNETPGIRYQYNVLP" +
			"QGWKGSPAIFQASMTKILEPFRKQNPDIVICQYMDDLYVGSDLEIGQHRT" +
			"KIEELREHLLKWGFYTPDKKHQKEPPFHWM",
			seqRT.getAlignedAAs()
		);
	}

	@Test
	public void testInsertionGap() {
	 	Sequence testSeq = new Sequence(
	 		"SmallFSIns4_LargeFSdel17-18",
	 		"CCTCAAATCACCTCTTTGGCAACGACCCATCGTCACAATAAAGATAGGGAGCTAARGGAAGCTCTATTAGATACAGGAGCAGATGATACAGTATTAGAAGATATAAATTTGCCAGGAAGATGGACACCAAAAATKATAGTGGGAATTGGAGGTTTTACCAAAGTAAGACAGTATGATCAGATACCTGTAGAAATTTGTGGACATAAAGCTATAGGTACAGTRTTAGTAGGACCTACACCTGCCAACATAATTGGAAGAAATCTGTTGACYCAGATTGGTTGCACTTTAAATTTTCCCATTAGTCCTATTGACACTGTACCAGTAAAATTAAAGCCAGGAATGGATGGCCCAAAAGTTAAACAATGGCCATTGACAGAAGAAAAAATAAAAGCATTAGTAGAAATTTGTGCAGAATTGGAASAGGACGGGAAAATTTCAAAAATTGGGCCTGAAAATCCATACAATACTCCAGTATTTGCCATAAAGAAAAAGAACAGYGATAAATGGAGAAAATTAGTAGATTTCAGAGAACTTAATAAGAGAACTCAAGACTTCTGGGAAGTTCAATTAGGAATACCACATCCCGGAGGGTTAAAAAAGAACAAATCAGTAACAGTACTGGATGTGGGTGATGCATATTTTTCARTTCCCTTAGATGAAGACTTCAGGAAGTATACTGCATTTACCATACCTAGTATAAACAATGAGACACCAGGGACTAGATATCAGTACAATGTGCTTCCACAGGGATGGAAAGGATCACCAGCAATATTCCAAAGTAGCATGACAAGAATCTTAGAACCTTTTAGAAAACAGAATCCAGACATAGTTATCTGTCAATAYGTGGATGATTTGTATGTAGGATCTGACTTAGAAATAGAGMAGCATAGAACAAAAGTAGAGGAACTGAGACAACATTTGTGGAAGTGGGGNTTTTACACACCAGACAAMAAACATCAGAAAGAACCTCCATTCCTTTGGATGGGTTATGAACTCCATCCTGATAAATGGACA");
		AlignedSequence alignedSeq = NucAminoAligner.align(testSeq);
	}

}
