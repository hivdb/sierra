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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.hivdb.genotyper.HIVGenotypeResult;
import edu.stanford.hivdb.mutations.Apobec;
import edu.stanford.hivdb.mutations.FrameShift;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.Json;

public class SequenceValidator {

	private AlignedSequence alignedSequence;
	private List<ValidationResult> validationResults = new ArrayList<>();
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<String, ValidationLevel> VALIDATION_RESULT_LEVELS;
	private static final Map<String, String> VALIDATION_RESULT_MESSAGES;

	static {
		Map<String, ValidationLevel> levels = new HashMap<>();
		Map<String, String> messages = new HashMap<>();

		levels.put("no-gene-found", ValidationLevel.CRITICAL);
		messages.put("no-gene-found",
					"There were no Protease, Reverse Transcriptase, or " +
					"Integrase genes found, refuse to process.");

		levels.put("not-aligned-gene", ValidationLevel.SEVERE_WARNING);
		messages.put(
			"not-aligned-gene",
			"This sequence may also have nucleotides belonging to %s. " +
			"Analysis of this part of the input sequence was suppressed " +
			"due to poor quality, insufficient size or " +
			"improper concatenation of multiple partial sequences.");

		levels.put("gap-too-long", ValidationLevel.CRITICAL);
		messages.put(
			"gap-too-long",
			"This sequence has critical potentially correctable errors. It has a large sequence gap, " +
			"defined as an insertion or deletion of >30 bps. One possible cause of this error is that " +
			"the input sequence was concatenated from multiple partial sequences. Adding 'N's in place " +
			"of the missing sequence will allow the sequence to be processed.");

		levels.put("sequence-trimmed", ValidationLevel.WARNING);
		messages.put(
			"sequence-trimmed",
			"The %s sequence had %d amino acids trimmed from its %s-end due to poor quality.");

		levels.put("sequence-much-too-short", ValidationLevel.SEVERE_WARNING);
		messages.put(
			"sequence-much-too-short",
			"The %s sequence contains just %d codons, " +
			"which is not sufficient for a comprehensive interpretation.");

		levels.put("sequence-too-short", ValidationLevel.WARNING);
		messages.put(
			"sequence-too-short",
			"The %s sequence contains just %d codons, " +
			"which is not sufficient for a comprehensive interpretation.");

		levels.put("invalid-nas-removed", ValidationLevel.NOTE);
		messages.put(
			"invalid-nas-removed",
			"Non-NA character(s) %s were found and removed from the sequence.");

		levels.put("severe-warning-too-many-stop-codons", ValidationLevel.SEVERE_WARNING);
		messages.put("severe-warning-too-many-stop-codons", "There are %d stop codons in %s: %s.");

		levels.put("note-stop-codon", ValidationLevel.NOTE);
		messages.put("note-stop-codon", "There is %d stop codon in %s: %s.");

		levels.put("much-too-many-unusual-mutations", ValidationLevel.SEVERE_WARNING);
		messages.put("much-too-many-unusual-mutations", "There are %d unusual mutations in %s: %s.");

		levels.put("too-many-unusual-mutations", ValidationLevel.WARNING);
		messages.put("too-many-unusual-mutations", "There are %d unusual mutations in %s: %s.");

		levels.put("some-unusual-mutations", ValidationLevel.NOTE);
		messages.put("some-unusual-mutations", "There are %d unusual mutations in %s: %s.");

		levels.put("unusual-mutation-at-DRP-plural", ValidationLevel.WARNING);
		messages.put("unusual-mutation-at-DRP-plural",
				     "There are %d unusual mutations at drug-resistance positions in %s: %s.");

		levels.put("unusual-mutation-at-DRP", ValidationLevel.NOTE);
		messages.put("unusual-mutation-at-DRP",
				     "There is %d unusual mutation at a drug-resistance position in %s: %s.");

		levels.put("severe-APOBEC", ValidationLevel.SEVERE_WARNING);
		messages.put("severe-APOBEC", "%s");

		levels.put("definite-APOBEC", ValidationLevel.WARNING);
		messages.put("definite-APOBEC", "%s");

		levels.put("possible-APOBEC-influence", ValidationLevel.NOTE);
		messages.put("possible-APOBEC-influence", "%s");

		levels.put("multiple-apobec-at-DRP", ValidationLevel.SEVERE_WARNING);
		messages.put("multiple-apobec-at-DRP",
				     "There are %d APOBEC-associated mutations at drug-resistance positions: %s.");

		levels.put("single-apobec-at-DRP", ValidationLevel.WARNING);
		messages.put("single-apobec-at-DRP",
				     "There is %d APOBEC-associated mutation at a drug-resistance position: %s.");

		levels.put("two-or-more-unusual-indels-and-frameshifts", ValidationLevel.SEVERE_WARNING);
		messages.put("two-or-more-unusual-indels-and-frameshifts",
				"The %s gene has %d unusual indels and/or frameshifts. " +
				"The indels include %s. The frameshifts include %s.");

		levels.put("two-or-more-frameshifts", ValidationLevel.SEVERE_WARNING);
		messages.put("two-or-more-frameshifts", "The %s gene has %d frameshifts: %s.");

		levels.put("two-or-more-unusual-indels", ValidationLevel.SEVERE_WARNING);
		messages.put("two-or-more-unusual-indels", "The %s gene has %d unusual indels: %s.");

		levels.put("one-frameshift", ValidationLevel.WARNING);
		messages.put("one-frameshift", "The %s gene has a frameshift: %s.");

		levels.put("one-unusual-indel", ValidationLevel.WARNING);
		messages.put("one-unusual-indel", "The %s gene has an unusual indel: %s.");

		levels.put("hiv-2", ValidationLevel.WARNING);
		messages.put("hiv-2", "The sequence is from an HIV-2 virus");

		levels.put("overlap", ValidationLevel.WARNING);
		messages.put(
			"overlap", "Alignment overlap detected at the begining of %s " +
			"sequence (\"%s\"). Try insert Ns between partial sequences.");

		levels.put("reverse-complement", ValidationLevel.WARNING);
		messages.put(
			"reverse-complement", "This report was derived from the reverse complement of input sequence.");

		VALIDATION_RESULT_LEVELS = Collections.unmodifiableMap(levels);
		VALIDATION_RESULT_MESSAGES = Collections.unmodifiableMap(messages);

	}


	public SequenceValidator(AlignedSequence alignedSequence) {
		this.alignedSequence = alignedSequence;
	}

	public List<ValidationResult> getValidationResults() {
		return validationResults;
	}

	public boolean validate() {
		boolean validated = true;
		if (!validateNotEmpty()) {
			return false;
		}
		validated = validateReverseComplement() && validated;
		validated = validateGene() && validated;
		validated = validateSequenceSize() && validated;
		validated = validateShrinkage() && validated;
		validated = validateLongGap() && validated;
		validated = validateNAs() && validated;
		validated = validateGaps() && validated;
		validated = validateNotApobec() && validated;
		validated = validateNoStopCodons() && validated;
		if (!validateNotHIV2()) {
			return false;
		}
		validated = validateNoTooManyUnusualMutations() && validated;
		return validated;
	}

	protected void addValidationResult(String key, Object... args) {
		ValidationLevel level = VALIDATION_RESULT_LEVELS.get(key);
		String message = String.format(
			VALIDATION_RESULT_MESSAGES.get(key),
			args);
		ValidationResult result = new ValidationResult(level, message);
		validationResults.add(result);
	}

	protected boolean validateNotEmpty() {
		boolean validated = true;
		if (alignedSequence.isEmpty()) {
			addValidationResult("no-gene-found");
			validated = false;
		}
		return validated;
	}

	protected boolean validateReverseComplement() {
		boolean validated = true;
		if (alignedSequence.isReverseComplement()) {
			addValidationResult("reverse-complement");
			validated = false;
		}
		return validated;
	}

	protected boolean validateGene() {
		boolean validated = true;
		Set<Gene> discardedGenes = new LinkedHashSet<>(alignedSequence.getDiscardedGenes().keySet());
		int leftIgnored = 0x7fffffff;
		int rightIgnored = 0;
		List<Gene> availableGenes = alignedSequence.getAvailableGenes();
		for (AlignedGeneSeq geneSeq : alignedSequence.getAlignedGeneSequences()) {
			leftIgnored = Math.min(leftIgnored, geneSeq.getFirstNA() - 1);
			rightIgnored = Math.max(rightIgnored, geneSeq.getLastNA());
		}
		rightIgnored = alignedSequence.getInputSequence().getLength() - rightIgnored;
		// TODO: HIV2 Support
		if (!availableGenes.contains(Gene.valueOf("HIV1PR")) && leftIgnored > 210) {
			discardedGenes.add(Gene.valueOf("HIV1PR"));
		}
		if (!availableGenes.contains(Gene.valueOf("HIV1RT")) && leftIgnored > 800) {
			discardedGenes.add(Gene.valueOf("HIV1RT"));
		} else if (!availableGenes.contains(Gene.valueOf("HIV1RT")) && rightIgnored > 800) {
			discardedGenes.add(Gene.valueOf("HIV1RT"));
		}
		if (!availableGenes.contains(Gene.valueOf("HIV1IN")) && rightIgnored > 600) {
			discardedGenes.add(Gene.valueOf("HIV1IN"));
		}
		if (!discardedGenes.isEmpty()) {
			String textDiscardedGenes = discardedGenes
				.stream().map(g -> g.getShortName())
				.collect(Collectors.joining(" or "));
			addValidationResult("not-aligned-gene", textDiscardedGenes);
			validated = false;
		}
		return validated;
	}

	protected boolean validateSequenceSize() {
		int size;
		AlignedGeneSeq geneSeq;
		boolean validated = true;
		geneSeq = alignedSequence.getAlignedGeneSequence(Gene.valueOf("HIV1PR"));
		if (geneSeq != null) {
			size = geneSeq.getSize();
			if (size < 60) {
				addValidationResult("sequence-much-too-short", Gene.valueOf("HIV1PR"), size);
				validated = false;
			} else if (size < 80) {
				addValidationResult("sequence-too-short", Gene.valueOf("HIV1PR"), size);
				validated = false;
			}
		}
		geneSeq = alignedSequence.getAlignedGeneSequence(Gene.valueOf("HIV1RT"));
		if (geneSeq != null) {
			size = geneSeq.getSize();
			if (size < 150) {
				addValidationResult("sequence-much-too-short", Gene.valueOf("HIV1RT"), size);
				validated = false;
			} else if (size < 200) {
				addValidationResult("sequence-too-short", Gene.valueOf("HIV1RT"), size);
				validated = false;
			}
		}
		geneSeq = alignedSequence.getAlignedGeneSequence(Gene.valueOf("HIV1IN"));
		if (geneSeq != null) {
			size = geneSeq.getSize();
			if (size < 100) {
				addValidationResult("sequence-much-too-short", Gene.valueOf("HIV1IN"), size);
				validated = false;
			} else if (size < 200) {
				addValidationResult("sequence-too-short", Gene.valueOf("HIV1IN"), size);
				validated = false;
			}
		}
		return validated;
	}

	protected boolean validateShrinkage() {
		boolean validated = true;
		for (AlignedGeneSeq geneSeq : alignedSequence.getAlignedGeneSequences()) {
			Gene gene = geneSeq.getGene();
			int[] trimmed = geneSeq.getShrinkage();
			int leftTrimmed = trimmed[0];
			int rightTrimmed = trimmed[1];
			if (leftTrimmed > 0) {
				addValidationResult("sequence-trimmed", gene, leftTrimmed, "5′");
				validated = false;
			}
			if (rightTrimmed > 0) {
				addValidationResult("sequence-trimmed", gene, rightTrimmed, "3′");
				validated = false;
			}
		}
		return validated;
	}

	protected boolean validateLongGap() {
		boolean validated = true;
		int gapLenThreshold = 10;
		int continuousDels = 0;
		for (Mutation mut : alignedSequence.getMutations()) {
			if (continuousDels > gapLenThreshold) {
				addValidationResult("gap-too-long");
				validated = false;
				break;
			}
			if (mut.getInsertedNAs().length() > gapLenThreshold * 3) {
				addValidationResult("gap-too-long");
				validated = false;
				break;
			}
			if (mut.isDeletion()) {
				continuousDels ++;
			} else {
				continuousDels = 0;
			}
		}
		return validated;
	}

	protected boolean validateNAs() {
		boolean validated = true;
		List<String> invalids =
			alignedSequence.getInputSequence().removedInvalidChars()
			.stream().map(c -> "" + c)
			.collect(Collectors.toList());
		if (!invalids.isEmpty()) {
			addValidationResult(
				"invalid-nas-removed", Json.dumps(String.join("", invalids)));
			validated = false;
		}
		return validated;
	}

	protected boolean validateNoStopCodons() {
		boolean validated = true;
		Map<Gene, AlignedGeneSeq> alignedGeneSeqs = alignedSequence.getAlignedGeneSequenceMap();

		for (Gene gene : alignedGeneSeqs.keySet()) {
			MutationSet stopCodons = alignedGeneSeqs.get(gene).getStopCodons();
			String stops = stopCodons.join(", ");
			int numStopCodons = stopCodons.size();
			if (numStopCodons > 1) {
				addValidationResult("severe-warning-too-many-stop-codons",
							numStopCodons, gene.getShortName(), stops);
				validated = false;
			} else if (numStopCodons > 0) {
				addValidationResult("note-stop-codon",
						    numStopCodons, gene.getShortName(), stops);
				validated = false;
			}
		}
		return validated;
	}

	protected boolean validateNoTooManyUnusualMutations() {
		boolean validated = true;
		Map<Gene, AlignedGeneSeq> alignedGeneSeqs = alignedSequence.getAlignedGeneSequenceMap();

		for (Gene gene : alignedGeneSeqs.keySet()) {
			AlignedGeneSeq alignedGeneSeq = alignedGeneSeqs.get(gene);
			MutationSet unusualMutations = alignedGeneSeq.getUnusualMutations();
			String text = unusualMutations.join(", ");
			int numUnusual = unusualMutations.size();
			if (numUnusual > 8) {
				addValidationResult("much-too-many-unusual-mutations",
									 numUnusual, gene.getShortName(), text);
				validated = false;
			} else if (numUnusual > 4) {
				addValidationResult("too-many-unusual-mutations",
						 numUnusual, gene.getShortName(), text);
				validated = false;
			} else if (numUnusual > 2) {
				addValidationResult("some-unusual-mutations",
						 numUnusual, gene.getShortName(), text);
				validated = false;
			}
			MutationSet unusualMutAtDRP = alignedGeneSeq.getUnusualMutationsAtDrugResistancePositions();
			int numUnusualAtDRP = unusualMutAtDRP.size();
			if (numUnusualAtDRP > 1) {
				addValidationResult("unusual-mutation-at-DRP-plural",
						numUnusualAtDRP, gene.getShortName(), unusualMutAtDRP.join(", "));
				validated = false;
			} else if (numUnusualAtDRP == 1) {
				addValidationResult("unusual-mutation-at-DRP",
						numUnusualAtDRP, gene.getShortName(), unusualMutAtDRP.join(", "));
				validated = false;
			}
		}
		return validated;

	}

	protected boolean validateNotHIV2() {
		HIVGenotypeResult genotypeResult = alignedSequence.getSubtypeResult();
		boolean validated = true;
		if (genotypeResult.getBestMatch().getDisplayWithoutDistance().equals("HIV2")) {
			validated = false;
			addValidationResult("hiv-2");
		}
		return validated;
	}

	/*@SuppressWarnings("unused")
	private boolean validateGroupO() {
		Subtyper subtyper = alignedSequence.getSubtyper();
		return subtyper.getClosestSubtype() == Subtype.O;
	}

	@SuppressWarnings("unused")
	private boolean validateGroupN() {
		Subtyper subtyper = alignedSequence.getSubtyper();
		return subtyper.getClosestSubtype() == Subtype.N;
	}*/

	protected boolean validateNotApobec() {
		Apobec apobec = alignedSequence.getApobec();
		boolean validated = true;
		int numApobecMuts = apobec.getNumApobecMuts();

		if (numApobecMuts > 4) {
			addValidationResult("severe-APOBEC", apobec.generateComment());
			validated = false;
		} else if (numApobecMuts > 2) {
			addValidationResult("definite-APOBEC", apobec.generateComment());
			validated = false;
		} else if (numApobecMuts == 2) {
			addValidationResult("possible-APOBEC-influence", apobec.generateComment());
			validated = false;
		}

		MutationSet apobecMutsAtDRP = apobec.getApobecMutsAtDRP();
		int numApobecMutsAtDRP = apobecMutsAtDRP.size();
		if (numApobecMutsAtDRP > 1) {
			addValidationResult("multiple-apobec-at-DRP", numApobecMutsAtDRP,
				apobecMutsAtDRP.join(", ", Mutation::getHumanFormatWithGene));
			validated = false;
		} else if (numApobecMutsAtDRP == 1) {
			addValidationResult("single-apobec-at-DRP", numApobecMutsAtDRP,
				apobecMutsAtDRP.join(", ", Mutation::getHumanFormatWithGene));
			validated = false;
		}
		return validated;
	}

	private boolean validateGaps() {
		boolean validated = true;
		Map<Gene, AlignedGeneSeq> alignedGeneSeqs = alignedSequence.getAlignedGeneSequenceMap();
		List<Gene> seqGenes = alignedSequence.getAvailableGenes();

		for (Gene gene : seqGenes) {
			AlignedGeneSeq alignedGeneSeq = alignedGeneSeqs.get(gene);
			List<FrameShift> frameShifts = alignedGeneSeq.getFrameShifts();
			MutationSet insertions = alignedGeneSeq.getInsertions();
			MutationSet deletions = alignedGeneSeq.getDeletions();
			MutationSet unusualInsertions = insertions.getUnusualMutations();
			MutationSet unusualDeletions = deletions.getUnusualMutations();
			MutationSet unusualIndels = unusualInsertions.mergesWith(unusualDeletions);
			int numTotal = frameShifts.size() + unusualInsertions.size() + unusualDeletions.size();
			String frameShiftListText = FrameShift.getHumanReadableList(frameShifts);
			String unusualIndelsListText = unusualIndels.join(", ");

			if (numTotal > 1) {
				validated = false;
				if (frameShifts.size() > 0 && unusualIndels.size() > 0) {
					addValidationResult("two-or-more-unusual-indels-and-frameshifts", gene.getShortName(),
								numTotal, unusualIndelsListText, frameShiftListText);
				} else if (frameShifts.size() > 0) {
					addValidationResult("two-or-more-frameshifts", gene.getShortName(), numTotal,
								frameShiftListText);
				} else {
					addValidationResult("two-or-more-unusual-indels", gene.getShortName(), numTotal,
							    unusualIndelsListText);
				}

			} else if (numTotal >0 ) {
				validated = false;
				if (frameShifts.size() > 0) {
					addValidationResult("one-frameshift", gene.getShortName(), frameShiftListText);
				} else {
					addValidationResult("one-unusual-indel", gene.getShortName(), unusualIndelsListText);
				}

			}
		}
		return validated;
	}



}
