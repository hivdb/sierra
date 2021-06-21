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

import static org.junit.Assert.*;

import java.io.InputStream;

import org.junit.Test;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.hivfacts.hiv2.HIV2;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.FastaUtils;

public class PostAlignAlignerTest {

	private final static HIV hiv = HIV.getInstance();
	private final static HIV2 hiv2 = HIV2.getInstance();


	/**
	 * Instead of fetching test fasta sequence from Genbank,
	 * use local fasta file
	 *
	 * @param  accession accession id
	 * @return     		 first sequence in fasta file
	 */
	private Sequence fallbackFromGenBank(String accession) {
		String filePath = "Sequences/Aligner/" + accession + ".fasta";
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream input = classLoader.getResourceAsStream(filePath);
		return FastaUtils.readStream(input).get(0);
	}

	@Test
	public void testSingleSequence() {
//		Sequence testSeq = Sequence.fromGenbank("AF096883");
		Sequence testSeq = fallbackFromGenBank("AF096883");
		AlignedSequence<HIV> alignedSeq = Aligner.getInstance(hiv).align(testSeq);
		assertEquals("B (4.76%)", alignedSeq.getGenotypeText());
		AlignedGeneSeq<HIV> seqPR = alignedSeq.getAlignedGeneSequence(hiv.getGene("HIV1PR"));
		assertEquals(1, seqPR.getFirstAA());
		assertEquals(1, seqPR.getFirstNA());
		assertEquals(99, seqPR.getLastAA());
		assertEquals(297, seqPR.getLastNA());
		assertEquals(
			MutationSet.parseString(
				hiv.getGene("HIV1PR"),
				"L10V,E35D,M36I,N37D,I54V,Q58E,I62IV,L63P,I64V,A71V,G73T,L90M"),
			seqPR.getMutations()
		);
		assertEquals(
			"PQITLWQRPVVTIKIGGQLKEALLDTGADDTVLEDIDLPGRWKPKMIGGI" +
			"GGFVKVREYDQVPVEICGHKVITTVLVGPTPVNIIGRNLMTQIGCTLNF",
			seqPR.getAlignedAAs()
		);
		AlignedGeneSeq<HIV> seqRT = alignedSeq.getAlignedGeneSequence(hiv.getGene("HIV1RT"));
		assertEquals(1, seqRT.getFirstAA());
		assertEquals(298, seqRT.getFirstNA());
		assertEquals(230, seqRT.getLastAA());
		assertEquals(993, seqRT.getLastNA());
		assertEquals(
			MutationSet.parseString(
				hiv.getGene("HIV1RT"),
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
	public void testHIV2ASequence() {
//		Sequence testSeq = Sequence.fromGenbank("Z48731");
		Sequence testSeq = fallbackFromGenBank("Z48731");
		AlignedSequence<HIV2> alignedSeq = Aligner.getInstance(hiv2).align(testSeq);
		assertEquals(hiv2.getStrain("HIV2A"), alignedSeq.getStrain());
		assertEquals("HIV-2 Group A (0.00%)", alignedSeq.getGenotypeText());
		AlignedGeneSeq<HIV2> seqPR = alignedSeq.getAlignedGeneSequence(hiv2.getGene("HIV2APR"));
		assertEquals(1, seqPR.getFirstAA());
		assertEquals(2081 + 1, seqPR.getFirstNA());
		assertEquals(99, seqPR.getLastAA());
		assertEquals(2081 + 297, seqPR.getLastNA());
		assertEquals(
			MutationSet.parseString(
				hiv2.getGene("HIV2APR"),
				"K7N, Y14H, N40S, N68G, K70R"),
			seqPR.getMutations()
		);
		AlignedGeneSeq<HIV2> seqRT = alignedSeq.getAlignedGeneSequence(hiv2.getGene("HIV2ART"));
		assertEquals(1, seqRT.getFirstAA());
		assertEquals(2081 + 298, seqRT.getFirstNA());
		assertEquals(559, seqRT.getLastAA());
		assertEquals(2081 + 1974, seqRT.getLastNA());
		assertEquals(
			MutationSet.parseString(
				hiv2.getGene("HIV2ART"),
				"V5I, K43R, K64R, P126Q, H162Y, K176Q, G211S, H228R, I251V, L270I, " +
				"V293I, I308V, E334D, I341V, E344G, I347T, I364V, N403D, A425V, G430K, " +
				"R460K, K462R, K467V, V492A, S505V, A506V, S507G, S514N, K515R"),
			seqRT.getMutations()
		);
		assertEquals(seqRT.getAdjustedAlignedAAs().length(), 559);
		AlignedGeneSeq<HIV2> seqIN = alignedSeq.getAlignedGeneSequence(hiv2.getGene("HIV2AIN"));
		assertEquals(1, seqIN.getFirstAA());
		assertEquals(2081 + 1975, seqIN.getFirstNA());
		assertEquals(293, seqIN.getLastAA());
		assertEquals(2081 + 2853, seqIN.getLastNA());
		assertEquals(
			MutationSet.parseString(
				hiv2.getGene("HIV2AIN"),
				"I28L, N30Q, S39T, E57D, I72V, S93T, E167D, I172V, I180V, D222N, E246D, " +
				"L250I, I260V, E276D, S281P, A286T, M292V"),
			seqIN.getMutations()
		);
		assertEquals(seqIN.getAlignedAAs().length(), 293);
		assertEquals(seqIN.getAdjustedAlignedAAs().length(), 293);
	}

	@Test
	public void testHIV2BSequence() {
//		Sequence testSeq = Sequence.fromGenbank("L07625");
		Sequence testSeq = fallbackFromGenBank("L07625");
		AlignedSequence<HIV2> alignedSeq = Aligner.getInstance(hiv2).align(testSeq);
		assertEquals(hiv2.getStrain("HIV2B"), alignedSeq.getStrain());
		assertEquals("HIV-2 Group B (0.00%)", alignedSeq.getGenotypeText());
		AlignedGeneSeq<HIV2> seqPR = alignedSeq.getAlignedGeneSequence(hiv2.getGene("HIV2BPR"));
		assertEquals(1, seqPR.getFirstAA());
		assertEquals(2618 + 1, seqPR.getFirstNA());
		assertEquals(99, seqPR.getLastAA());
		assertEquals(2618 + 297, seqPR.getLastNA());
		assertEquals(
			MutationSet.parseString(
				hiv2.getGene("HIV2BPR"),
				"K12R, T14C, S19P, N57K, N61D, V75I, S92T"),
			seqPR.getMutations()
		);
		AlignedGeneSeq<HIV2> seqRT = alignedSeq.getAlignedGeneSequence(hiv2.getGene("HIV2BRT"));
		assertEquals(1, seqRT.getFirstAA());
		assertEquals(2618 + 298, seqRT.getFirstNA());
		assertEquals(559, seqRT.getLastAA());
		assertEquals(2618 + 1974, seqRT.getLastNA());
		assertEquals(
			MutationSet.parseString(
				hiv2.getGene("HIV2BRT"),
				"R4K, Q11K, E15G, S58T, K66R, E86D, S102E, K104R, V118I, D123N, A134S, " +
				"V135I, L145I, T163S, A165R, N176S, T179I, V189I, N211D, K238R, F303L, " +
				"D345N, K346R, M387V, I422V, G435K, K447R, P467V, A507G, I516L, R518N, " +
				"E519Q, K529A, K539R, I558V"),
			seqRT.getMutations()
		);
		assertEquals(seqRT.getAdjustedAlignedAAs().length(), 559);
		AlignedGeneSeq<HIV2> seqIN = alignedSeq.getAlignedGeneSequence(hiv2.getGene("HIV2BIN"));
		assertEquals(1, seqIN.getFirstAA());
		assertEquals(2618 + 1975, seqIN.getFirstNA());
		assertEquals(296, seqIN.getLastAA());
		assertEquals(2618 + 2862, seqIN.getLastNA());
		assertEquals(
			MutationSet.parseString(
				hiv2.getGene("HIV2BIN"),
				"N17G, I28L, R34K, I50V, S56A, E146Q, T180A, I200L, L213F, T215A, R224Q, " +
				"D240E, I250L, N270H, S280G, A281T, V283M, M287R, V292M, N296G"),
			seqIN.getMutations()
		);
		assertEquals(seqIN.getAlignedAAs().length(), 296);
		assertEquals(seqIN.getAdjustedAlignedAAs().length(), 293);
	}

	@Test
	public void testInsertionGap() {
	 	// Sequence testSeq = new Sequence(
	 	// 	"SmallFSIns4_LargeFSdel17-18",
	 	// 	"CCTCAAATCACCTCTTTGGCAACGACCCATCGTCACAATAAAGATAGGGAGCTAARGGAAGCTCTATTAGATACAGGAGCAGATGATACAGTATTAGAAGATATAAATTTGCCAGGAAGATGGACACCAAAAATKATAGTGGGAATTGGAGGTTTTACCAAAGTAAGACAGTATGATCAGATACCTGTAGAAATTTGTGGACATAAAGCTATAGGTACAGTRTTAGTAGGACCTACACCTGCCAACATAATTGGAAGAAATCTGTTGACYCAGATTGGTTGCACTTTAAATTTTCCCATTAGTCCTATTGACACTGTACCAGTAAAATTAAAGCCAGGAATGGATGGCCCAAAAGTTAAACAATGGCCATTGACAGAAGAAAAAATAAAAGCATTAGTAGAAATTTGTGCAGAATTGGAASAGGACGGGAAAATTTCAAAAATTGGGCCTGAAAATCCATACAATACTCCAGTATTTGCCATAAAGAAAAAGAACAGYGATAAATGGAGAAAATTAGTAGATTTCAGAGAACTTAATAAGAGAACTCAAGACTTCTGGGAAGTTCAATTAGGAATACCACATCCCGGAGGGTTAAAAAAGAACAAATCAGTAACAGTACTGGATGTGGGTGATGCATATTTTTCARTTCCCTTAGATGAAGACTTCAGGAAGTATACTGCATTTACCATACCTAGTATAAACAATGAGACACCAGGGACTAGATATCAGTACAATGTGCTTCCACAGGGATGGAAAGGATCACCAGCAATATTCCAAAGTAGCATGACAAGAATCTTAGAACCTTTTAGAAAACAGAATCCAGACATAGTTATCTGTCAATAYGTGGATGATTTGTATGTAGGATCTGACTTAGAAATAGAGMAGCATAGAACAAAAGTAGAGGAACTGAGACAACATTTGTGGAAGTGGGGNTTTTACACACCAGACAAMAAACATCAGAAAGAACCTCCATTCCTTTGGATGGGTTATGAACTCCATCCTGATAAATGGACA");
		// AlignedSequence alignedSeq = NucAminoAligner.align(testSeq);
	}

}
