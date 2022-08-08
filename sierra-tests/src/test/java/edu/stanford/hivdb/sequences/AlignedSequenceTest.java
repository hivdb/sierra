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

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import org.apache.commons.lang3.StringUtils;

import edu.stanford.hivdb.hivfacts.HIV;

import edu.stanford.hivdb.hivfacts.extras.HIV1Sample;

public class AlignedSequenceTest {

	final static HIV hiv = HIV.getInstance();

	@Test
	public void testEmptySeq() {
		Sequence seq = new Sequence("empty", "EMPTY");
		AlignedSequence<HIV> alignedSeq = Aligner.getInstance(hiv).align(seq);
		assertTrue(alignedSeq.isEmpty());
		assertEquals(Collections.emptyMap(), alignedSeq.getAlignedGeneSequenceMap());
		assertEquals(Collections.emptyList(), alignedSeq.getAlignedGeneSequences());
		assertEquals(null, alignedSeq.getAlignedGeneSequence(hiv.getGene("HIV1PR")));
		assertEquals(seq, alignedSeq.getInputSequence());
		assertEquals(Collections.emptyList(), alignedSeq.getAvailableGenes());

		// do twice to test cache
		assertEquals(1, alignedSeq.getValidationResults().size());
		assertEquals(1, alignedSeq.getValidationResults().size());

		assertEquals(Collections.emptySet(), alignedSeq.getMutations().getSDRMs());
		assertEquals(Collections.emptySet(), alignedSeq.getMutations().getSDRMs());

		assertEquals(Collections.emptySet(), alignedSeq.getMutations().getApobecDRMs());
		assertEquals(Collections.emptySet(), alignedSeq.getMutations().getApobecMutations());

		assertEquals(Collections.emptyList(), alignedSeq.getFrameShifts());
		assertEquals(Collections.emptyList(), alignedSeq.getFrameShifts());

		assertEquals(null, alignedSeq.getGenotypeResult());

		assertEquals("NA", alignedSeq.getGenotypeText());

		assertEquals(0.0, alignedSeq.getMixtureRate(), 1e-10);
		assertEquals(0.0, alignedSeq.getMixtureRate(), 1e-10);
	}
	
	@Test
	public void testReversedCompliment() {
		HIV1Sample sample = new HIV1Sample();
		Sequence seq = sample.getReversedCompliment();
		AlignedSequence<HIV> alignedSeq = Aligner.getInstance(hiv).align(seq);
		
		assertEquals("B (0.28%)", alignedSeq.getSubtypeResult().getBestMatch().getDisplay());
	}


	@Test
	public void testPRnRT() {
		// partial PR + NNN + partial RT
		HIV1Sample sample = new HIV1Sample();

		// PR (nts): 33 to 282
		sample.addSubstitutionRule(hiv.getGene("HIV1PR"), 282, 297, "");
		sample.addSubstitutionRule(hiv.getGene("HIV1PR"), 0, 33, "");

		// RT (nts): 123 to 1653
		//sample.addSubstitutionRule(hiv.getGene("HIV1RT"), 1653, 1680, "");
		sample.addSubstitutionRule(hiv.getGene("HIV1RT"), 0, 12, "");

		// IN: empty
		//sample.addSubstitutionRule(hiv.getGene("HIV1IN"), 0, 864, "");

		Sequence seq = sample.getSequence();

		AlignedSequence<HIV> alignedSeq = Aligner.getInstance(hiv).align(seq);

		assertEquals(
			"NOTE: 20 positions were not sequenced or aligned: PR 1-11, " +
			"95-99; RT 1-4. Of them, 2 are at drug-resistance positions: PR 10-11.",
			alignedSeq.getValidationResults(List.of("PR", "RT", "IN")).get(0).toString()
		);

		String alignedSeqStr = alignedSeq.getAssembledAlignment(false);

		// not start with "NNN"
		assertNotEquals("NNN", alignedSeqStr.substring(0, 3));

		// there should be exactly 9 "NNN" between PR and RT
		int endPR = 282 - 33;
		int startRT = 297 - 33 + 12;
		assertEquals(
			StringUtils.repeat("NNN", 9),
			alignedSeqStr.substring(endPR, startRT)
		);
		assertNotEquals("NNN", alignedSeqStr.substring(endPR - 3, endPR));
		
		assertNotEquals("NNN", alignedSeqStr.substring(startRT, startRT + 3));

		// not end with "NNN"
		int seqLen = alignedSeqStr.length();
		assertNotEquals("NNN", alignedSeqStr.substring(seqLen - 3, seqLen));
	}

	@Test
	public void testPRnIN() {
		// partial PR + NNN + partial IN
		HIV1Sample sample = new HIV1Sample();

		// PR (nts): 33 to 282
		sample.addSubstitutionRule(hiv.getGene("HIV1PR"), 282, 297, StringUtils.repeat("N", 15));
		sample.addSubstitutionRule(hiv.getGene("HIV1PR"), 0, 33, "");

		// RT: empty
		sample.addSubstitutionRule(hiv.getGene("HIV1RT"), 0, 1680, StringUtils.repeat("N", 1680));

		// IN (nts): 78 to 831
		sample.addSubstitutionRule(hiv.getGene("HIV1IN"), 831, 864, "");
		sample.addSubstitutionRule(hiv.getGene("HIV1IN"), 0, 78, StringUtils.repeat("N", 78));

		Sequence seq = sample.getSequence();

		AlignedSequence<HIV> alignedSeq = Aligner.getInstance(hiv).align(seq);

		String alignedSeqStr = alignedSeq.getAssembledAlignment(false);

		// not start with "NNN"
		assertNotEquals(alignedSeqStr.substring(0, 3), "NNN");

		// there should be exactly 5 + 560 + 26 = 591 "NNN" between PR and IN
		int endPR = 282 - 33;
		int startIN = 297 - 33 + 1680 + 78;

		assertEquals(
			StringUtils.repeat("NNN", 591),
			alignedSeqStr.substring(endPR, startIN));
		assertNotEquals(
			alignedSeqStr.substring(endPR - 3, endPR), "NNN");
		assertNotEquals(
			alignedSeqStr.substring(startIN, startIN + 3), "NNN");

		// not end with "NNN"
		int seqLen = alignedSeqStr.length();
		assertNotEquals(
			alignedSeqStr.substring(seqLen - 3, seqLen), "NNN");
	}

	@Test
	public void testRTnIN() {
		// partial RT + NNN + partial IN
		HIV1Sample sample = new HIV1Sample();

		// PR: Empty
		sample.addSubstitutionRule(hiv.getGene("HIV1PR"), 0, 297, "");

		// RT (nts): 123 to 1653
		sample.addSubstitutionRule(hiv.getGene("HIV1RT"), 1653, 1680, "");
		sample.addSubstitutionRule(hiv.getGene("HIV1RT"), 0, 123, "");

		// IN (nts): 78 to 831
		sample.addSubstitutionRule(hiv.getGene("HIV1IN"), 831, 864, "");
		sample.addSubstitutionRule(hiv.getGene("HIV1IN"), 0, 78, "");

		Sequence seq = sample.getSequence();

		AlignedSequence<HIV> alignedSeq = Aligner.getInstance(hiv).align(seq);

		String alignedSeqStr = alignedSeq.getAssembledAlignment(false);

		// not start with "NNN"
		assertNotEquals(alignedSeqStr.substring(0, 3), "NNN");

		// there should be exactly 35 "NNN" between RT and IN
		int endRT = 1653 - 123;
		int startIN = 1680 - 123 + 78;
		assertEquals(
			alignedSeqStr.substring(endRT, startIN),
			StringUtils.repeat("NNN", 35));
		assertNotEquals(
			alignedSeqStr.substring(endRT - 3, endRT), "NNN");
		assertNotEquals(
			alignedSeqStr.substring(startIN, startIN + 3), "NNN");

		// not end with "NNN"
		int seqLen = alignedSeqStr.length();
		assertNotEquals(
			alignedSeqStr.substring(seqLen - 3, seqLen), "NNN");
	}

	@Test
	public void testPRnRTnIN() {
		// partial PR + partial RT + partial IN
		HIV1Sample sample = new HIV1Sample();

		// PR (nts): 33 to 282
		sample.addSubstitutionRule(hiv.getGene("HIV1PR"), 282, 297, "");
		sample.addSubstitutionRule(hiv.getGene("HIV1PR"), 0, 33, "");

		// RT (nts): 123 to 1653
		sample.addSubstitutionRule(hiv.getGene("HIV1RT"), 1653, 1680, "");
		sample.addSubstitutionRule(hiv.getGene("HIV1RT"), 0, 123, "");

		// IN (nts): 78 to 831
		sample.addSubstitutionRule(hiv.getGene("HIV1IN"), 831, 864, "");
		sample.addSubstitutionRule(hiv.getGene("HIV1IN"), 0, 78, "");

		Sequence seq = sample.getSequence();

		AlignedSequence<HIV> alignedSeq = Aligner.getInstance(hiv).align(seq);

		String alignedSeqStr = alignedSeq.getAssembledAlignment(false);

		// not start with "NNN"
		assertNotEquals(alignedSeqStr.substring(0, 3), "NNN");

		// there should be exactly 46 "NNN" between PR and RT
		int endPR = 282 - 33;
		int startRT = 297 - 33 + 123;
		assertEquals(
			alignedSeqStr.substring(endPR, startRT),
			StringUtils.repeat("NNN", 46));
		assertNotEquals(
			alignedSeqStr.substring(endPR - 3, endPR), "NNN");
		assertNotEquals(
			alignedSeqStr.substring(startRT, startRT + 3), "NNN");

		// there should be exactly 35 "NNN" between RT and IN
		int endRT = 1653 + 297 - 33;
		int startIN = 1680 + 78 + 297 - 33;
		//System.out.println(alignedGeneSeqs.get(Gene.IN).getOriginalAATripletLine());
		//System.out.println(alignedGeneSeqs.get(Gene.IN).getOriginalControlLine());
		//System.out.println(alignedGeneSeqs.get(Gene.IN).getOriginalAlignedNAs());
		//System.out.println(concatSeqStr);
		assertEquals(
			alignedSeqStr.substring(endRT, startIN),
			StringUtils.repeat("NNN", 35));
		assertNotEquals(
			alignedSeqStr.substring(endRT - 3, endRT), "NNN");
		assertNotEquals(
			alignedSeqStr.substring(startIN, startIN + 3), "NNN");

		// not end with "NNN"
		int seqLen = alignedSeqStr.length();
		assertNotEquals(
			alignedSeqStr.substring(seqLen - 3, seqLen), "NNN");
	}

	@Test
	public void testPROnly() {
		HIV1Sample sample = new HIV1Sample();

		// PR (nts): 33 to 282
		sample.addSubstitutionRule(hiv.getGene("HIV1PR"), 282, 297, "");
		sample.addSubstitutionRule(hiv.getGene("HIV1PR"), 0, 33, "");

		// RT: empty
		sample.addSubstitutionRule(hiv.getGene("HIV1RT"), 0, 1680, "");

		// IN: empty
		sample.addSubstitutionRule(hiv.getGene("HIV1IN"), 0, 864, "");

		Sequence seq = sample.getSequence();

		AlignedSequence<HIV> alignedSeq = Aligner.getInstance(hiv).align(seq);

		String alignedSeqStr = alignedSeq.getAssembledAlignment(false);

		// not start with "NNN"
		assertNotEquals(alignedSeqStr.substring(0, 3), "NNN");

		assertEquals(alignedSeqStr.length(), 282 - 33);

		// not end with "NNN"
		int seqLen = alignedSeqStr.length();
		assertNotEquals(
			alignedSeqStr.substring(seqLen - 3, seqLen), "NNN");
	}

	@Test
	public void testRTOnly() {
		HIV1Sample sample = new HIV1Sample();

		// PR: empty
		sample.addSubstitutionRule(hiv.getGene("HIV1PR"), 0, 297, "");

		// RT: 45 to 1626
		sample.addSubstitutionRule(hiv.getGene("HIV1RT"), 1626, 1680, "");
		sample.addSubstitutionRule(hiv.getGene("HIV1RT"), 0, 45, "");

		// IN: empty
		sample.addSubstitutionRule(hiv.getGene("HIV1IN"), 0, 864, "");

		Sequence seq = sample.getSequence();

		AlignedSequence<HIV> alignedSeq = Aligner.getInstance(hiv).align(seq);

		String alignedSeqStr = alignedSeq.getAssembledAlignment(false);

		// not start with "NNN"
		assertNotEquals(alignedSeqStr.substring(0, 3), "NNN");

		assertEquals(alignedSeqStr.length(), 1626 - 45);

		// not end with "NNN"
		int seqLen = alignedSeqStr.length();
		assertNotEquals(
			alignedSeqStr.substring(seqLen - 3, seqLen), "NNN");
	}

	@Test
	public void testINOnly() {
		HIV1Sample sample = new HIV1Sample();

		// PR: empty
		sample.addSubstitutionRule(hiv.getGene("HIV1PR"), 0, 297, "");

		// RT: empty
		sample.addSubstitutionRule(hiv.getGene("HIV1RT"), 0, 1680, "");

		// IN: 12 to 837
		sample.addSubstitutionRule(hiv.getGene("HIV1IN"), 837, 864, "");
		sample.addSubstitutionRule(hiv.getGene("HIV1IN"), 0, 12, "");

		Sequence seq = sample.getSequence();

		AlignedSequence<HIV> alignedSeq = Aligner.getInstance(hiv).align(seq);

		String alignedSeqStr = alignedSeq.getAssembledAlignment(false);

		// not start with "NNN"
		assertNotEquals(alignedSeqStr.substring(0, 3), "NNN");

		assertEquals(alignedSeqStr.length(), 837 - 12);

		// not end with "NNN"
		int seqLen = alignedSeqStr.length();
		assertNotEquals(
			alignedSeqStr.substring(seqLen - 3, seqLen), "NNN");
	}

}
