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

import java.io.InputStream;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import edu.stanford.hivdb.filetestutils.TestSequencesFiles;
import edu.stanford.hivdb.filetestutils.TestSequencesFiles.TestSequencesProperties;
import edu.stanford.hivdb.genotyper.BoundGenotype;
import edu.stanford.hivdb.genotyper.HIVGenotypeResult;
import edu.stanford.hivdb.mutations.Apobec;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.FastaUtils;
import edu.stanford.hivdb.utilities.Sequence;

public class SequenceValidatorTest {

	@Test
	public void test() {
		final InputStream testSequenceInputStream =
				TestSequencesFiles.getTestSequenceInputStream(TestSequencesProperties.JUST_IN);
		final List<Sequence> sequences = FastaUtils.readStream(testSequenceInputStream);
		List<AlignedSequence> allAligneds =	Aligner.parallelAlign(sequences);
		for (AlignedSequence alignedSeq : allAligneds) {
			System.out.println("\n" + alignedSeq.getInputSequence().getHeader());
			SequenceValidator sequenceValidator = new SequenceValidator(alignedSeq);
			if (!sequenceValidator.validate()) {
				List<ValidationResult> validationResults = sequenceValidator.getValidationResults();
				for (ValidationResult vr : validationResults) {
					System.out.println(vr.getLevel().toString() + ": " + vr.getMessage());
				}
			}
		}
	}

	public void testValidateOverlap() {
		// sequence from https://github.com/hivdb/sierra/issues/5
		String seqStr =
			"ACTCTTTGGCAACGACCCCTTGTCACAATAAAGATAGGGGGGCAGCTAAAGGAAGCTCTATTA" +
			"GATACAGGAGCAGATGATACAGTATTAGAAGAAATGAATTTGCCAGGAAGATGGAAACCAAAA" +
			"ATGATAGGGGGAATTGGAGGTTTTATCAAAGTAAGACAGTATGATCAGATACCTATAGAAATC" +
			"TGTGGACATAAAGCTATAGGTACAGTATTARTAGGACCTACACCTGTCAACATAATTGGMAGA" +
			"AATCTGTTGACTCAGMTTGGTTGCACTTTAAATTTTTGTACAGAAATGGAAAAGGAAGGRAAA" +
			"ATTTCAAAAATTGGGCCTGAAAATCCATACAATACTCCAGTATTTGCYATAAAGAAAAAAGAC" +
			"AGTACTAAATGGAGAAAATTAGTAGATTTCAGAGAACTTAATAAGAGAACTCAAGACTTCTGG" +
			"GARGTTCAATTAGGAATACCACATCCCKCAGGGYTAAAAAAGAAAAAATCAGTAACAGTACTG" +
			"GATGTGGGTGATGCATATTTTTCAGTTCCCTTAGATGAAGAYTTCAGGAAGTATACTGCATTY" +
			"ACCATACCTAGTATAAACAATGAGACACCAGGGATTAGATATCAGTACAATGTGCTTCCACAG" +
			"GGATGGAAAGGATCACCAGCRATATTCCAAAGTAGCATGACAAAAATCTTAGAGCCTTTTAGA" +
			"AAACAAAATCCAGACATGGTTATCTATCAGTACATGGATGATTTGTATGTAGGATCTGAYTTA" +
			"GAAATAGGGCAGCATAGAACAAAAATAGAGGAACTGAGRCAACATCTGTTGAGGTGGGGRTTT" +
			"ACCACACCAGACAAAAAACATCAGAAAGAACCTCCATTCCTTTGGATGGGTTATGAACTCCAT" +
			"CCTGATAAATGGACAGTACAGCCTATAMTRTTGCCA";
		Sequence seq1 = new Sequence("overlap_test", seqStr);
		Sequence seq2 = new Sequence(
			"overlap_test_2", seqStr.replace("MTTGGTTGCACT", "MTTGGTAAAACT"));
		AlignedSequence alignedSeq = NucAminoAligner.align(seq1);
		SequenceValidator validator = spy(new SequenceValidator(alignedSeq));
		assertFalse(validator.validateLongGap());
		verify(validator, times(1))
		.addValidationResult("overlap", Gene.RT, "CAGMTTGGTTGC...");

		alignedSeq = NucAminoAligner.align(seq2);
		validator = spy(new SequenceValidator(alignedSeq));
		assertFalse(validator.validateLongGap());
		verify(validator, times(1))
		.addValidationResult("overlap", Gene.RT, "ACTTTAAATTTT");
	}

	@Test
	public void testValidateNAs() {
		AlignedSequence alignedSeq = mock(AlignedSequence.class);
		when(alignedSeq.getInputSequence()).thenReturn(new Sequence("test", "...not ATCG"));
		SequenceValidator validator = new SequenceValidator(alignedSeq);
		assertFalse(validator.validateNAs());
		assertEquals(1, validator.getValidationResults().size());
		assertEquals(
			ValidationLevel.NOTE,
			validator.getValidationResults().get(0).getLevel());

		alignedSeq = mock(AlignedSequence.class);
		when(alignedSeq.getInputSequence()).thenReturn(new Sequence("test", "ATCG"));
		validator = new SequenceValidator(alignedSeq);
		assertTrue(validator.validateNAs());
		assertEquals(0, validator.getValidationResults().size());
	}

	@Test
	public void testValidateNotHIV2() {
		// case 1: HIV-2
		AlignedSequence alignedSeq = mock(AlignedSequence.class);
		HIVGenotypeResult genotypeResult = mock(HIVGenotypeResult.class);
		BoundGenotype genotype = mock(BoundGenotype.class);
		when(genotypeResult.getBestMatch()).thenReturn(genotype);
		when(genotype.getDisplayWithoutDistance()).thenReturn("HIV2");
		when(alignedSeq.getSubtypeResult()).thenReturn(genotypeResult);
		SequenceValidator validator = new SequenceValidator(alignedSeq);
		assertFalse(validator.validateNotHIV2());
		assertEquals(1, validator.getValidationResults().size());
		assertEquals(
			ValidationLevel.WARNING,
			validator.getValidationResults().get(0).getLevel());

		// case 2: type B
		alignedSeq = mock(AlignedSequence.class);
		genotypeResult = mock(HIVGenotypeResult.class);
		genotype = mock(BoundGenotype.class);
		when(genotypeResult.getBestMatch()).thenReturn(genotype);
		when(genotype.getDisplayWithoutDistance()).thenReturn("B");
		when(alignedSeq.getSubtypeResult()).thenReturn(genotypeResult);
		validator = new SequenceValidator(alignedSeq);
		assertTrue(validator.validateNotHIV2());
		assertEquals(0, validator.getValidationResults().size());
	}

	//@Test
	public void testValidateGene() {
		// case 1: there's no gene
		AlignedSequence alignedSeq = mock(AlignedSequence.class);
		when(alignedSeq.isEmpty()).thenReturn(true);
		SequenceValidator validator = new SequenceValidator(alignedSeq);
		assertFalse(validator.validateGene());
		assertEquals(1, validator.getValidationResults().size());
		assertEquals(
			ValidationLevel.CRITICAL,
			validator.getValidationResults().get(0).getLevel());

		// case 2: there're genes
		alignedSeq = mock(AlignedSequence.class);
		when(alignedSeq.isEmpty()).thenReturn(false);
		validator = new SequenceValidator(alignedSeq);
		assertTrue(validator.validateGene());
		assertEquals(0, validator.getValidationResults().size());
	}

	@Test
	public void testValidateNotApobec() {

		// case 1: severe warning (> 4)
		assertValidateNotApobec(5, "", ValidationLevel.SEVERE_WARNING);

		// case 2: warning (> 2)
		assertValidateNotApobec(3, "", ValidationLevel.WARNING);

		// case 3: note (== 2)
		assertValidateNotApobec(2, "", ValidationLevel.NOTE);

		// case 4: ok (< 2)
		assertValidateNotApobec(1, "", null);

		// case 5: 2 ApobecDRMs
		assertValidateNotApobec(0, "PR30N,RT67E", ValidationLevel.SEVERE_WARNING);

		// case 6: 1 ApobecDRMs
		assertValidateNotApobec(0, "RT67E", ValidationLevel.WARNING);
	}

	@Test
	public void testValidateNoTooManyUnusualMutations() {
		assertValidateNoTooManyUnusualMutations(
			"1A,2C,3D,4E,5G,6F,7I,8H,9K", "", ValidationLevel.SEVERE_WARNING);
		assertValidateNoTooManyUnusualMutations(
			"1A,2C,3D,4E,5G,6F,7I,8H", "", ValidationLevel.WARNING);
		assertValidateNoTooManyUnusualMutations(
			"1A,2C,3D,4E,5G", "", ValidationLevel.WARNING);
		assertValidateNoTooManyUnusualMutations(
			"1A,2C,3D,4E", "", ValidationLevel.NOTE);
		assertValidateNoTooManyUnusualMutations(
			"1A,2C,3D", "", ValidationLevel.NOTE);
		assertValidateNoTooManyUnusualMutations("1A,2C", "", null);
		assertValidateNoTooManyUnusualMutations("", "1A,2C", ValidationLevel.WARNING);
		assertValidateNoTooManyUnusualMutations("", "1A", ValidationLevel.NOTE);
		assertValidateNoTooManyUnusualMutations("", "", null);
	}

	@Test
	public void testValidateNoStopCodons() {
		assertValidateNoStopCodons("1*,2*", ValidationLevel.SEVERE_WARNING);
		assertValidateNoStopCodons("1*", ValidationLevel.NOTE);
		assertValidateNoStopCodons("", null);
	}


	private static void assertValidateNoStopCodons(
			String stopCodonsStr, ValidationLevel level) {
		AlignedSequence alignedSeq = mock(AlignedSequence.class);
		AlignedGeneSeq geneSeq = mock(AlignedGeneSeq.class);
		Map<Gene, AlignedGeneSeq> geneSeqs = new EnumMap<>(Gene.class);
		geneSeqs.put(Gene.PR, geneSeq);
		when(geneSeq.getStopCodons())
		.thenReturn(new MutationSet(Gene.PR, stopCodonsStr));
		when(alignedSeq.getAlignedGeneSequenceMap()).thenReturn(geneSeqs);
		SequenceValidator validator = new SequenceValidator(alignedSeq);
		if (level == null) {
			assertTrue(validator.validateNoStopCodons());
			assertEquals(0, validator.getValidationResults().size());
		}
		else {
			assertFalse(validator.validateNoStopCodons());
			assertEquals(1, validator.getValidationResults().size());
			assertEquals(
				level,
				validator.getValidationResults().get(0).getLevel());
		}
	}

	private static void assertValidateNoTooManyUnusualMutations(
			String unusualMutsStr, String unusualMutsAtDRP, ValidationLevel level) {
		AlignedSequence alignedSeq = mock(AlignedSequence.class);
		AlignedGeneSeq geneSeq = mock(AlignedGeneSeq.class);
		Map<Gene, AlignedGeneSeq> geneSeqs = new EnumMap<>(Gene.class);
		geneSeqs.put(Gene.PR, geneSeq);
		when(geneSeq.getUnusualMutations())
		.thenReturn(new MutationSet(Gene.PR, unusualMutsStr));
		when(geneSeq.getUnusualMutationsAtDrugResistancePositions())
		.thenReturn(new MutationSet(Gene.PR, unusualMutsAtDRP));
		when(alignedSeq.getAlignedGeneSequenceMap()).thenReturn(geneSeqs);
		SequenceValidator validator = new SequenceValidator(alignedSeq);
		if (level == null) {
			assertTrue(validator.validateNoTooManyUnusualMutations());
			assertEquals(0, validator.getValidationResults().size());
		}
		else {
			assertFalse(validator.validateNoTooManyUnusualMutations());
			assertEquals(1, validator.getValidationResults().size());
			assertEquals(
				level,
				validator.getValidationResults().get(0).getLevel());
		}
	}

	private static void assertValidateNotApobec(
			int numApobecMuts, String apobecDRMs,
			ValidationLevel level) {
		AlignedSequence alignedSeq = mock(AlignedSequence.class);
		Apobec apobec = mock(Apobec.class);
		MutationSet apobecMutsAtDRP = new MutationSet(apobecDRMs);
		when(apobec.getNumApobecMuts()).thenReturn(numApobecMuts);
		when(apobec.getApobecMutsAtDRP()).thenReturn(apobecMutsAtDRP);
		when(alignedSeq.getApobec()).thenReturn(apobec);
		SequenceValidator validator = new SequenceValidator(alignedSeq);
		if (level == null) {
			assertTrue(validator.validateNotApobec());
			assertEquals(0, validator.getValidationResults().size());
		}
		else {
			assertFalse(validator.validateNotApobec());
			assertEquals(1, validator.getValidationResults().size());
			assertEquals(
				level,
				validator.getValidationResults().get(0).getLevel());
		}
	}

}
