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

import java.util.Collections;

import org.junit.Test;

import org.apache.commons.lang3.StringUtils;

import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.testutils.HIV1Sample;
import edu.stanford.hivdb.utilities.Sequence;

public class AlignedSequenceTest {

	@Test
	public void testEmptySeq() {
		Sequence seq = new Sequence("empty", "EMPTY");
		AlignedSequence alignedSeq = Aligner.align(seq);
		assertTrue(alignedSeq.isEmpty());
		assertEquals(Collections.emptyMap(), alignedSeq.getAlignedGeneSequenceMap());
		assertEquals(Collections.emptyList(), alignedSeq.getAlignedGeneSequences());
		assertEquals(null, alignedSeq.getAlignedGeneSequence(Gene.PR));
		assertEquals(seq, alignedSeq.getInputSequence());
		assertEquals(Collections.emptyList(), alignedSeq.getAvailableGenes());

		// do twice to test cache
		assertEquals(1, alignedSeq.getValidationResults().size());
		assertEquals(1, alignedSeq.getValidationResults().size());

		assertEquals(-1, alignedSeq.getAbsoluteFirstNA());
		assertEquals(-1, alignedSeq.getAbsoluteFirstNA());

		assertEquals(Collections.emptySet(), alignedSeq.getSdrms());
		assertEquals(Collections.emptySet(), alignedSeq.getSdrms());

		assertEquals(Collections.emptySet(), alignedSeq.getApobec().getApobecDRMs());
		assertEquals(Collections.emptySet(), alignedSeq.getApobec().getApobecMuts());

		assertEquals(Collections.emptyList(), alignedSeq.getFrameShifts());
		assertEquals(Collections.emptyList(), alignedSeq.getFrameShifts());

		assertEquals(null, alignedSeq.getSubtypeResult());

		assertEquals("NA", alignedSeq.getSubtypeText());

		assertEquals(0.0, alignedSeq.getMixturePcnt(), 1e-10);
		assertEquals(0.0, alignedSeq.getMixturePcnt(), 1e-10);
	}


	@Test
	public void testPRnRT() {
		// partial PR + ... + partial RT
		HIV1Sample sample = new HIV1Sample();

		// PR (nts): 33 to 282
		sample.addSubstitutionRule(Gene.PR, 282, 297, "");
		sample.addSubstitutionRule(Gene.PR, 0, 33, "");

		// RT (nts): 123 to 1653
		sample.addSubstitutionRule(Gene.RT, 1653, 1680, "");
		sample.addSubstitutionRule(Gene.RT, 0, 123, "");

		// IN: empty
		sample.addSubstitutionRule(Gene.IN, 0, 864, "");

		Sequence seq = sample.getSequence();

		AlignedSequence alignedSeq = Aligner.align(seq);

		assertEquals(Collections.emptyList(), alignedSeq.getValidationResults());

		String alignedSeqStr = alignedSeq.getConcatenatedSeq();

		// not start with "..."
		assertNotEquals(alignedSeqStr.substring(0, 3), "...");

		// there should be exactly 46 "..." between PR and RT
		int endPR = 282 - 33;
		int startRT = 297 - 33 + 123;
		assertEquals(
			alignedSeqStr.substring(endPR, startRT),
			StringUtils.repeat(".", startRT - endPR));
		assertNotEquals(
			alignedSeqStr.substring(endPR - 3, endPR), "...");
		assertNotEquals(
			alignedSeqStr.substring(startRT, startRT + 3), "...");

		// not end with "..."
		int seqLen = alignedSeqStr.length();
		assertNotEquals(
			alignedSeqStr.substring(seqLen - 3, seqLen), "...");
	}

	@Test
	public void testPRnIN() {
		// partial PR + ... + partial IN
		HIV1Sample sample = new HIV1Sample();

		// PR (nts): 33 to 282
		sample.addSubstitutionRule(Gene.PR, 282, 297, StringUtils.repeat("N", 15));
		sample.addSubstitutionRule(Gene.PR, 0, 33, "");

		// RT: empty
		sample.addSubstitutionRule(Gene.RT, 0, 1680, StringUtils.repeat("N", 1680));

		// IN (nts): 78 to 831
		sample.addSubstitutionRule(Gene.IN, 831, 864, "");
		sample.addSubstitutionRule(Gene.IN, 0, 78, StringUtils.repeat("N", 78));

		Sequence seq = sample.getSequence();

		AlignedSequence alignedSeq = Aligner.align(seq);

		String alignedSeqStr = alignedSeq.getConcatenatedSeq();

		// not start with "..."
		assertNotEquals(alignedSeqStr.substring(0, 3), "...");

		// there should be exactly 31 "..." between PR and IN
		int endPR = 282 - 33;
		int startIN = 297 - 33 + 78;
		assertEquals(
			StringUtils.repeat(".", startIN - endPR),
			alignedSeqStr.substring(endPR, startIN));
		assertNotEquals(
			alignedSeqStr.substring(endPR - 3, endPR), "...");
		assertNotEquals(
			alignedSeqStr.substring(startIN, startIN + 3), "...");

		// not end with "..."
		int seqLen = alignedSeqStr.length();
		assertNotEquals(
			alignedSeqStr.substring(seqLen - 3, seqLen), "...");
	}

	@Test
	public void testRTnIN() {
		// partial RT + ... + partial IN
		HIV1Sample sample = new HIV1Sample();

		// PR: Empty
		sample.addSubstitutionRule(Gene.PR, 0, 297, "");

		// RT (nts): 123 to 1653
		sample.addSubstitutionRule(Gene.RT, 1653, 1680, "");
		sample.addSubstitutionRule(Gene.RT, 0, 123, "");

		// IN (nts): 78 to 831
		sample.addSubstitutionRule(Gene.IN, 831, 864, "");
		sample.addSubstitutionRule(Gene.IN, 0, 78, "");

		Sequence seq = sample.getSequence();

		AlignedSequence alignedSeq = Aligner.align(seq);

		String alignedSeqStr = alignedSeq.getConcatenatedSeq();

		// not start with "..."
		assertNotEquals(alignedSeqStr.substring(0, 3), "...");

		// there should be exactly 35 "..." between RT and IN
		int endRT = 1653 - 123;
		int startIN = 1680 - 123 + 78;
		assertEquals(
			alignedSeqStr.substring(endRT, startIN),
			StringUtils.repeat(".", startIN - endRT));
		assertNotEquals(
			alignedSeqStr.substring(endRT - 3, endRT), "...");
		assertNotEquals(
			alignedSeqStr.substring(startIN, startIN + 3), "...");

		// not end with "..."
		int seqLen = alignedSeqStr.length();
		assertNotEquals(
			alignedSeqStr.substring(seqLen - 3, seqLen), "...");
	}

	@Test
	public void testPRnRTnIN() {
		// partial PR + partial RT + partial IN
		HIV1Sample sample = new HIV1Sample();

		// PR (nts): 33 to 282
		sample.addSubstitutionRule(Gene.PR, 282, 297, "");
		sample.addSubstitutionRule(Gene.PR, 0, 33, "");

		// RT (nts): 123 to 1653
		sample.addSubstitutionRule(Gene.RT, 1653, 1680, "");
		sample.addSubstitutionRule(Gene.RT, 0, 123, "");

		// IN (nts): 78 to 831
		sample.addSubstitutionRule(Gene.IN, 831, 864, "");
		sample.addSubstitutionRule(Gene.IN, 0, 78, "");

		Sequence seq = sample.getSequence();

		AlignedSequence alignedSeq = Aligner.align(seq);

		String alignedSeqStr = alignedSeq.getConcatenatedSeq();

		// not start with "..."
		assertNotEquals(alignedSeqStr.substring(0, 3), "...");

		// there should be exactly 46 "..." between PR and RT
		int endPR = 282 - 33;
		int startRT = 297 - 33 + 123;
		assertEquals(
			alignedSeqStr.substring(endPR, startRT),
			StringUtils.repeat(".", startRT - endPR));
		assertNotEquals(
			alignedSeqStr.substring(endPR - 3, endPR), "...");
		assertNotEquals(
			alignedSeqStr.substring(startRT, startRT + 3), "...");

		// there should be exactly 35 "..." between RT and IN
		int endRT = 1653 + 297 - 33;
		int startIN = 1680 + 78 + 297 - 33;
		//System.out.println(alignedGeneSeqs.get(Gene.IN).getOriginalAATripletLine());
		//System.out.println(alignedGeneSeqs.get(Gene.IN).getOriginalControlLine());
		//System.out.println(alignedGeneSeqs.get(Gene.IN).getOriginalAlignedNAs());
		//System.out.println(concatSeqStr);
		assertEquals(
			alignedSeqStr.substring(endRT, startIN),
			StringUtils.repeat(".", startIN - endRT));
		assertNotEquals(
			alignedSeqStr.substring(endRT - 3, endRT), "...");
		assertNotEquals(
			alignedSeqStr.substring(startIN, startIN + 3), "...");

		// not end with "..."
		int seqLen = alignedSeqStr.length();
		assertNotEquals(
			alignedSeqStr.substring(seqLen - 3, seqLen), "...");
	}

	@Test
	public void testPROnly() {
		HIV1Sample sample = new HIV1Sample();

		// PR (nts): 33 to 282
		sample.addSubstitutionRule(Gene.PR, 282, 297, "");
		sample.addSubstitutionRule(Gene.PR, 0, 33, "");

		// RT: empty
		sample.addSubstitutionRule(Gene.RT, 0, 1680, "");

		// IN: empty
		sample.addSubstitutionRule(Gene.IN, 0, 864, "");

		Sequence seq = sample.getSequence();

		AlignedSequence alignedSeq = Aligner.align(seq);

		String alignedSeqStr = alignedSeq.getConcatenatedSeq();

		// not start with "..."
		assertNotEquals(alignedSeqStr.substring(0, 3), "...");

		assertEquals(alignedSeqStr.length(), 282 - 33);

		// not end with "..."
		int seqLen = alignedSeqStr.length();
		assertNotEquals(
			alignedSeqStr.substring(seqLen - 3, seqLen), "...");
	}

	@Test
	public void testRTOnly() {
		HIV1Sample sample = new HIV1Sample();

		// PR: empty
		sample.addSubstitutionRule(Gene.PR, 0, 297, "");

		// RT: 45 to 1626
		sample.addSubstitutionRule(Gene.RT, 1626, 1680, "");
		sample.addSubstitutionRule(Gene.RT, 0, 45, "");

		// IN: empty
		sample.addSubstitutionRule(Gene.IN, 0, 864, "");

		Sequence seq = sample.getSequence();

		AlignedSequence alignedSeq = Aligner.align(seq);

		String alignedSeqStr = alignedSeq.getConcatenatedSeq();

		// not start with "..."
		assertNotEquals(alignedSeqStr.substring(0, 3), "...");

		assertEquals(alignedSeqStr.length(), 1626 - 45);

		// not end with "..."
		int seqLen = alignedSeqStr.length();
		assertNotEquals(
			alignedSeqStr.substring(seqLen - 3, seqLen), "...");
	}

	@Test
	public void testINOnly() {
		HIV1Sample sample = new HIV1Sample();

		// PR: empty
		sample.addSubstitutionRule(Gene.PR, 0, 297, "");

		// RT: empty
		sample.addSubstitutionRule(Gene.RT, 0, 1680, "");

		// IN: 12 to 837
		sample.addSubstitutionRule(Gene.IN, 837, 864, "");
		sample.addSubstitutionRule(Gene.IN, 0, 12, "");

		Sequence seq = sample.getSequence();

		AlignedSequence alignedSeq = Aligner.align(seq);

		String alignedSeqStr = alignedSeq.getConcatenatedSeq();

		// not start with "..."
		assertNotEquals(alignedSeqStr.substring(0, 3), "...");

		assertEquals(alignedSeqStr.length(), 837 - 12);

		// not end with "..."
		int seqLen = alignedSeqStr.length();
		assertNotEquals(
			alignedSeqStr.substring(seqLen - 3, seqLen), "...");
	}

}
