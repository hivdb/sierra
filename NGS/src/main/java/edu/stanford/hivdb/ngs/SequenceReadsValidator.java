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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.hivdb.mutations.Apobec;
import edu.stanford.hivdb.mutations.CodonReads;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.GeneEnum;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;
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
	private static final Map<GeneEnum, Pair<Integer, Integer>> SEQUENCE_DRM_RANGES;
	private static final Double SEQUENCE_DRM_MIN1 = 0.4;
	private static final Double SEQUENCE_DRM_MIN2 = 0.6;

	static {
		Map<String, ValidationLevel> levels = new HashMap<>();
		Map<String, String> messages = new HashMap<>();

		levels.put("no-gene-found", ValidationLevel.CRITICAL);
		messages.put("no-gene-found",
					"There were no Protease, Reverse Transcriptase, or " +
					"Integrase genes found, refuse to process.");

		levels.put("gap-too-long", ValidationLevel.SEVERE_WARNING);
		messages.put(
			"gap-too-long",
			"More than 10%% of intermediate continuous positions are absent " +
			"in the submitted codon frequency table. This may indicates " +
			"preprocess issue and lead to incorrect subtyping detection. Click " +
			"the ‘Read Coverage’ button to review.");
		
		levels.put("min-read-depth-too-low", ValidationLevel.WARNING);
		messages.put(
			"min-read-depth-too-low",
			"You have selected a minimal read-depth of %d. However, " +
			"%.1f%% of the positions in your sequence have fewer than %d " +
			"reads. Click the ‘Read Coverage’ button to review.");

		levels.put("sequence-much-too-short", ValidationLevel.SEVERE_WARNING);
		messages.put(
			"sequence-much-too-short",
			"The %s sequence contains just %d codon(s) in DRM region, " +
			"which is not sufficient for a comprehensive interpretation.");

		levels.put("sequence-too-short", ValidationLevel.WARNING);
		messages.put(
			"sequence-too-short",
			"The %s sequence contains just %d codon(s) in DRM region, " +
			"which is not sufficient for a comprehensive interpretation.");

		levels.put("severe-warning-too-many-stop-codons", ValidationLevel.SEVERE_WARNING);
		messages.put("severe-warning-too-many-stop-codons", "There are %d stop codons in %s: %s.");

		levels.put("note-stop-codon", ValidationLevel.NOTE);
		messages.put("note-stop-codon", "There is %d stop codon in %s: %s.");

		levels.put("too-many-unusual-mutations", ValidationLevel.SEVERE_WARNING);
		messages.put(
			"too-many-unusual-mutations",
			"At this threshold (%.1f%%), >= 1.0%% of positions have a " +
			"highly unusual mutation (defined as a prevalence <0.01%% in " +
			"published group M direct PCR sequences). This indicates that " +
			"there may be an unacceptably high risk that some mutations at " +
			"this threshold represent sequence artifacts.");

		
		levels.put("too-many-apobec-mutations-one-apobec-drm", ValidationLevel.SEVERE_WARNING);
		messages.put(
			"too-many-apobec-mutations-one-apobec-drm",
			"At this threshold (%.1f%%), >=3 positions with signature " +
			"APOBEC mutations. At this threshold, the sequence also contains " +
			"one drug-resistance mutation that could be caused by " +
			"APOBEC-mediated G-to-A hypermutation (%s). This " +
			"DRM therefore should be considered possible sequence artifacts.");
		
		levels.put("too-many-apobec-mutations-multiple-apobec-drms", ValidationLevel.SEVERE_WARNING);
		messages.put(
			"too-many-apobec-mutations-multiple-apobec-drms",
			"At this threshold (%.1f%%), >=3 positions with signature " +
			"APOBEC mutations. At this threshold, the sequence also contains " +
			"%d drug-resistance mutations that could be caused by " +
			"APOBEC-mediated G-to-A hypermutation (%s). These " +
			"DRMs therefore should be considered possible sequence artifacts.");

		levels.put("too-many-apobec-mutations-no-apobec-drm", ValidationLevel.WARNING);
		messages.put(
			"too-many-apobec-mutations-no-apobec-drm",
			"At this threshold (%.1f%%), >=3 positions with signature " +
			"APOBEC mutations. At this threshold, the sequence contains no" +
			"drug-resistance mutations that could be caused by " +
			"APOBEC-mediated G-to-A hypermutation.");

		VALIDATION_RESULT_LEVELS = Collections.unmodifiableMap(levels);
		VALIDATION_RESULT_MESSAGES = Collections.unmodifiableMap(messages);

		Map<GeneEnum, Pair<Integer, Integer>> seqDrmRanges = new EnumMap<>(GeneEnum.class);
		seqDrmRanges.put(GeneEnum.PR, Pair.of(10, 90));
		seqDrmRanges.put(GeneEnum.RT, Pair.of(41, 348));
		seqDrmRanges.put(GeneEnum.IN, Pair.of(51, 263));
		SEQUENCE_DRM_RANGES = Collections.unmodifiableMap(seqDrmRanges);

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
			long minReadDepth = sequenceReads.getMinReadDepth();
			addValidationResult(
				"min-read-depth-too-low",
				minReadDepth, pcnt * 100, minReadDepth);
			validated = false;
		}
		return validated;
	}

	protected boolean validateSequenceSize() {
		long size;
		Gene[] genes = sequenceReads.getStrain().getGenes();
		GeneSequenceReads geneSeqReads;
		boolean validated = true;
		
		for (Gene gene : genes) {
			geneSeqReads = sequenceReads.getGeneSequenceReads(gene);
			if (geneSeqReads != null) {
				Pair<Integer, Integer> drmRange = SEQUENCE_DRM_RANGES.get(gene.getGeneEnum());
				Integer left = drmRange.getLeft();
				Integer right = drmRange.getRight();

				size = geneSeqReads.getAllPositionCodonReads().stream()
					.filter(pcr -> {
						long pos = pcr.getPosition();
						return pos >= left && pos <= right;
					})
					.count();
				double ratio = (double) size / (double) (right - left + 1);
				if (ratio < SEQUENCE_DRM_MIN1) {
					addValidationResult("sequence-much-too-short", gene, size);
					validated = false;
				}
				else if (ratio < SEQUENCE_DRM_MIN2) {
					addValidationResult("sequence-too-short", gene, size);
					validated = false;
				}
			}
		}
		return validated;
	}

	protected boolean validateLongGap() {
		boolean validated = true;
		List<OneCodonReadsCoverage> crcs = sequenceReads.getCodonReadsCoverage();
		if (crcs.isEmpty()) {
			return validated;
		}
		long leftMost = crcs.get(0).getPolPosition();
		long rightMost = crcs.get(crcs.size() - 1).getPolPosition();
		double maxGapWidth = Math.ceil((rightMost - leftMost + 1) * GAP_LEN_THRESHOLD);
		
		long prevPos = leftMost;
		for (OneCodonReadsCoverage crc : crcs) {
			long curPos = crc.getPolPosition();
			double posDiff = curPos - prevPos;
			if (posDiff > maxGapWidth) {
				addValidationResult("gap-too-long");
				validated = false;
				break;
			}
			prevPos = curPos;
		}
		for (Mutation mut : sequenceReads.getMutations()) {
			if (mut.getInsertedNAs().length() > maxGapWidth * 3) {
				addValidationResult("gap-too-long");
				validated = false;
				break;
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
		MutationSet apobecDRMs = new MutationSet();
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
			apobecDRMs = apobecDRMs.mergesWith(new Apobec(gsr.getMutations()).getApobecDRMs());
		}
		int numApobecDRMs = apobecDRMs.size();
		if (numAPOBECs > APOBEC_THRESHOLD) {
			String apobecs = apobecDRMs.join(", ", Mutation::getHumanFormatWithGene);
			
			if (numApobecDRMs > 1) {
				addValidationResult(
					"too-many-apobec-mutations-multiple-apobec-drms",
					cutoff * 100, numApobecDRMs, apobecs);
			}
			else if (numApobecDRMs == 1) {
				addValidationResult(
					"too-many-apobec-mutations-one-apobec-drm",
					cutoff * 100, apobecs);
			}
			else {
				addValidationResult(
					"too-many-apobec-mutations-no-apobec-drm",
					cutoff * 100);
			}
			validated = false;
		}
		return validated;
	}

}
