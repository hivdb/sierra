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

package edu.stanford.hivdb.ngs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.hivdb.mutations.CodonReads;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.utilities.ValidationLevel;
import edu.stanford.hivdb.utilities.ValidationResult;

public class SequenceReadsValidator {

	private SequenceReads sequenceReads;
	private List<ValidationResult> validationResults = new ArrayList<>();
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<String, ValidationLevel> VALIDATION_RESULT_LEVELS;
	private static final Map<String, String> VALIDATION_RESULT_MESSAGES;
	private static final Double PROPORTION_TRIMMED_POSITIONS_THRESHOLD = 0.05;
	private static final Double GAP_LEN_THRESHOLD = 0.1;
	private static final Double UNUSUAL_THRESHOLD = 0.01;
	private static final Integer APOBEC_THRESHOLD = 2;

	static {
		Map<String, ValidationLevel> levels = new HashMap<>();
		Map<String, String> messages = new HashMap<>();

		levels.put("no-gene-found", ValidationLevel.CRITICAL);
		messages.put("no-gene-found",
					"There were no Protease, Reverse Transcriptase, or " +
					"Integrase genes found, refuse to process.");

		levels.put("gap-too-long", ValidationLevel.WARNING);
		messages.put(
			"gap-too-long",
			"More than 10%% of intermediate codon positions are not included " +
			"in the submitted codon frequency table. This may indicates " +
			"preprocess issue and lead to incorrect subtyping detection.");
		
		levels.put("min-read-depth-too-low", ValidationLevel.WARNING);
		messages.put(
			"min-read-depth-too-low",
			"You have selected a minimal read-depth of %d. However, " +
			"%.1f%% of the positions in your sequence have fewer than 1000 " +
			"reads. Click the read coverage button to review.");

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

		levels.put("severe-warning-too-many-stop-codons", ValidationLevel.SEVERE_WARNING);
		messages.put("severe-warning-too-many-stop-codons", "There are %d stop codons in %s: %s.");

		levels.put("note-stop-codon", ValidationLevel.NOTE);
		messages.put("note-stop-codon", "There is %d stop codon in %s: %s.");

		levels.put("too-many-unusual-mutations", ValidationLevel.WARNING);
		messages.put(
			"too-many-unusual-mutations",
			"At this threshold (%.1f%%), >= 1.0%% of positions have a " +
			"highly unusual mutation (defined as a prevalence <0.01%% in " +
			"published group M direct PCR sequences). This indicates that " +
			"there may be an unacceptably high risk that some mutations at " +
			"this threshold represent sequence artifacts.");

		
		levels.put("too-many-apobec-mutations", ValidationLevel.WARNING);
		messages.put(
			"too-many-apobec-mutations",
			"At this threshold (%.1f%%), >=3 positions with signature " +
			"APOBEC mutations. At this threshold, the sequence also contains " +
			"%d drug-resistance mutations that could be caused by " +
			"APOBEC-mediated G-to-A hypermutation (list APOBEC DRMs). These " +
			"DRMs therefore should be considered possible sequence artifacts.");

		VALIDATION_RESULT_LEVELS = Collections.unmodifiableMap(levels);
		VALIDATION_RESULT_MESSAGES = Collections.unmodifiableMap(messages);

	}


	public SequenceReadsValidator(SequenceReads sequenceReads) {
		this.sequenceReads = sequenceReads;
	}

	public List<ValidationResult> getValidationResults() {
		return validationResults;
	}

	public boolean validate() {
		boolean validated = true;
		if (!validateNotEmpty()) {
			return false;
		}
		validated = validatePorportionTrimmedPositions() && validated;
		validated = validateSequenceSize() && validated;
		validated = validateLongGap() && validated;
		validated = validateNoStopCodons() && validated;
		validated = validateNoTooManyUnusualMutations() && validated;
		validated = validateNoTooManyApobec() && validated;
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
		if (sequenceReads.isEmpty()) {
			addValidationResult("no-gene-found");
			validated = false;
		}
		return validated;
	}
	
	protected boolean validatePorportionTrimmedPositions() {
		boolean validated = true;
		double pcnt = sequenceReads.getProportionTrimmedPositions();
		if (pcnt > PROPORTION_TRIMMED_POSITIONS_THRESHOLD) {
			addValidationResult(
				"min-read-depth-too-low",
				sequenceReads.getMinReadDepth(), pcnt * 100);
			validated = false;
		}
		return validated;
	}

	protected boolean validateSequenceSize() {
		int size;
		GeneSequenceReads geneSeqReads;
		boolean validated = true;
		geneSeqReads = sequenceReads.getGeneSequenceReads(Gene.valueOf("HIV1PR"));
		if (geneSeqReads != null) {
			size = geneSeqReads.getSize();
			if (size < 60) {
				addValidationResult("sequence-much-too-short", Gene.valueOf("HIV1PR"), size);
				validated = false;
			} else if (size < 80) {
				addValidationResult("sequence-too-short", Gene.valueOf("HIV1PR"), size);
				validated = false;
			}
		}
		geneSeqReads = sequenceReads.getGeneSequenceReads(Gene.valueOf("HIV1RT"));
		if (geneSeqReads != null) {
			size = geneSeqReads.getSize();
			if (size < 150) {
				addValidationResult("sequence-much-too-short", Gene.valueOf("HIV1RT"), size);
				validated = false;
			} else if (size < 200) {
				addValidationResult("sequence-too-short", Gene.valueOf("HIV1RT"), size);
				validated = false;
			}
		}
		geneSeqReads = sequenceReads.getGeneSequenceReads(Gene.valueOf("HIV1IN"));
		if (geneSeqReads != null) {
			size = geneSeqReads.getSize();
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

	protected boolean validateLongGap() {
		boolean validated = true;
		double gapLenThreshold = GAP_LEN_THRESHOLD;
		double continuousDels = 0.;
		double numTotalPositions = sequenceReads.getSize();
		for (Mutation mut : sequenceReads.getMutations()) {
			if (continuousDels / numTotalPositions > gapLenThreshold) {
				addValidationResult("gap-too-long");
				validated = false;
				break;
			}
			if (
				(double) mut.getInsertedNAs().length() /
				numTotalPositions > gapLenThreshold * 3
			) {
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

	protected boolean validateNoStopCodons() {
		boolean validated = true;
		List<GeneSequenceReads> allGeneSeqReads = sequenceReads.getAllGeneSequenceReads();

		for (GeneSequenceReads gsr : allGeneSeqReads) {
			Gene gene = gsr.getGene();
			MutationSet stopCodons = gsr.getMutations().getStopCodons();
			String stops = stopCodons.join(", ");
			int numStopCodons = stopCodons.size();
			if (numStopCodons > 1) {
				addValidationResult("severe-warning-too-many-stop-codons",
							numStopCodons, gene.getName(), stops);
				validated = false;
			} else if (numStopCodons > 0) {
				addValidationResult("note-stop-codon",
						    numStopCodons, gene.getName(), stops);
				validated = false;
			}
		}
		return validated;
	}

	protected boolean validateNoTooManyUnusualMutations() {
		boolean validated = true;
		List<GeneSequenceReads> allGeneSeqReads = sequenceReads.getAllGeneSequenceReads();
		double numUnusuals = 0;
		double numPositions = 0;
		double cutoff = sequenceReads.getMinPrevalence();
		
		for (GeneSequenceReads gsr : allGeneSeqReads) {
			numUnusuals += (
				gsr.getAllPositionCodonReads()
				.stream()
				.mapToInt(pcr -> {
					for (CodonReads cr : pcr.getCodonReads(true, 1., cutoff)) {
						if (cr.isUnusual()) {
							return 1;
						}
					}
					return 0;
				})
				.sum());
			numPositions += gsr.getAllPositionCodonReads().size();
		}
		double unusualPcnt = numUnusuals / numPositions;
		if (unusualPcnt > UNUSUAL_THRESHOLD) {
			addValidationResult("too-many-unusual-mutations",
								 cutoff * 100);
			validated = false;
		}
		return validated;
	}

	protected boolean validateNoTooManyApobec() {
		boolean validated = true;
		List<GeneSequenceReads> allGeneSeqReads = sequenceReads.getAllGeneSequenceReads();
		int numAPOBECs = 0;
		int numApobecDRMs = 0;
		double cutoff = sequenceReads.getMinPrevalence();

		for (GeneSequenceReads gsr : allGeneSeqReads) {
			numAPOBECs += (
				gsr.getAllPositionCodonReads()
				.stream()
				.mapToInt(pcr -> {
					for (CodonReads cr : pcr.getCodonReads(true, 1., cutoff)) {
						if (cr.isApobecMutation()) {
							return 1;
						}
					}
					return 0;
				})
				.sum());
			numApobecDRMs += (
				gsr.getAllPositionCodonReads()
				.stream()
				.mapToInt(pcr -> {
					for (CodonReads cr : pcr.getCodonReads(true, 1., cutoff)) {
						if (cr.isApobecDRM()) {
							return 1;
						}
					}
					return 0;
				})
				.sum());
		}
		if (numAPOBECs > APOBEC_THRESHOLD) {
			addValidationResult("too-many-apobec-mutations",
								 cutoff * 100, numApobecDRMs);
			validated = false;
		}
		return validated;
	}

}
