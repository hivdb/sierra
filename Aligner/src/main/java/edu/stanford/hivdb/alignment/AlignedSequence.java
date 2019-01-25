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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.hivdb.genotyper.BoundGenotype;
import edu.stanford.hivdb.genotyper.HIVGenotypeReference;
import edu.stanford.hivdb.genotyper.HIVGenotypeResult;
import edu.stanford.hivdb.mutations.Apobec;
import edu.stanford.hivdb.mutations.FrameShift;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.mutations.Sdrms;
import edu.stanford.hivdb.mutations.Strain;
import edu.stanford.hivdb.utilities.SeqUtils;
import edu.stanford.hivdb.utilities.Sequence;

public class AlignedSequence {
	private static final Map<Gene, Integer> FIRST_NA_BY_GENE;
	private static final Map<Gene, Integer> NUM_NAS_BY_GENE;
	private static final String WILDCARD = ".";

	private final Strain strain;
	private final Sequence inputSequence;

	private List<ValidationResult> validationResults;
	private Map<Gene, AlignedGeneSeq> alignedGeneSequenceMap;
	private Map<Gene, String> discardedGenes;
	private String concatenatedSequence;
	private Integer absoluteFirstNA;
	private MutationSet mutations;
	private MutationSet sdrms;
	private HIVGenotypeResult subtypeResult;
	private Double mixturePcnt;
	private Apobec apobec;
	private transient List<FrameShift> frameShifts;
	private final Boolean isReverseComplement;
	private final Boolean isEmpty;

	private static final Logger LOGGER = LogManager.getLogger();

	static {
		// TODO: HIV2 support
		Map<Gene, Integer> firstNAByGene = new TreeMap<>();
		firstNAByGene.put(Gene.valueOf("HIV1PR"), new Integer(2253));
		firstNAByGene.put(Gene.valueOf("HIV1RT"), new Integer(2550));
		firstNAByGene.put(Gene.valueOf("HIV1IN"), new Integer(4230));
		FIRST_NA_BY_GENE = Collections.unmodifiableMap(firstNAByGene);

		Map<Gene, Integer> numNAsByGene = new TreeMap<>();
		numNAsByGene.put(Gene.valueOf("HIV1PR"), new Integer(297));
		numNAsByGene.put(Gene.valueOf("HIV1RT"), new Integer(1680));
		numNAsByGene.put(Gene.valueOf("HIV1IN"), new Integer(864));
		NUM_NAS_BY_GENE = Collections.unmodifiableMap(numNAsByGene);
	}

	public AlignedSequence(
			final Sequence unalignedSequence,
			final Map<Gene, AlignedGeneSeq> alignedGeneSequenceMap,
			final Map<Gene, String> discardedGenes,
			final boolean sequenceReversed) {
		inputSequence = unalignedSequence;
		// TODO: add HIV2 support
		this.strain = Strain.HIV1;
		this.alignedGeneSequenceMap = alignedGeneSequenceMap;
		this.discardedGenes = discardedGenes;
		isReverseComplement = sequenceReversed;
		isEmpty = alignedGeneSequenceMap.isEmpty();
	}

	public boolean isEmpty() {
		return isEmpty;
	}
	
	public Strain getStrain() {
		return strain;
	}

	public boolean isReverseComplement() {
		return isReverseComplement;
	}

	public Map<Gene, AlignedGeneSeq> getAlignedGeneSequenceMap() {
		return alignedGeneSequenceMap;
	}

	/**
	 * Return an instance of AlignedGeneSeq for giving gene or
	 * {@code null} if the instance doesn't exist.
	 *
	 * @param gene the gene to be fetched
	 * @return corresponding instance of AlignedGeneSeq
	 */
	public AlignedGeneSeq getAlignedGeneSequence(Gene gene) {
		return alignedGeneSequenceMap.get(gene);
	}

	public List<AlignedGeneSeq> getAlignedGeneSequences() {
		return Collections.unmodifiableList(
			new ArrayList<>(alignedGeneSequenceMap.values()));
	}

	public Sequence getInputSequence() {
		return inputSequence;
	}

	public List<Gene> getAvailableGenes() {
		return new ArrayList<>(alignedGeneSequenceMap.keySet());
	}

	public List<ValidationResult> getValidationResults() {
		if (validationResults == null) {
			SequenceValidator sequenceValidator = new SequenceValidator(this);
			if (!sequenceValidator.validate()) {
				validationResults = sequenceValidator.getValidationResults();
			} else {
				validationResults = new ArrayList<>();
			}
		}
		return validationResults;
	}

	public Map<Gene, String> getDiscardedGenes() {
		return discardedGenes;
	}

	public String getConcatenatedSeq() {

		if (concatenatedSequence == null) {

			String geneSeqNAs;
			AlignedGeneSeq geneSeq;
			int numCurPrefixNAs = 0;
			int numPrevPrefixNAs = 0;
			boolean isFirstGeneSeq = true;
			StringBuilder concatSeq = new StringBuilder();

			for (Gene gene: getAvailableGenes()) {
				geneSeq = alignedGeneSequenceMap.get(gene);
				numCurPrefixNAs = (geneSeq.getFirstAA() - 1) * 3;
				if (!isFirstGeneSeq) {
					// append missing trailing NAs of prev geneSeq
					// and missing leading NAs of current geneSeq
					concatSeq.append(StringUtils.repeat(
							WILDCARD, numPrevPrefixNAs + numCurPrefixNAs));
				}
				geneSeqNAs = geneSeq.getAlignedNAs();
				concatSeq.append(geneSeqNAs);
				// save number of missing trailing NAs for next geneSeq
				numPrevPrefixNAs =
					NUM_NAS_BY_GENE.get(gene) -
					geneSeqNAs.length() - numCurPrefixNAs;
				isFirstGeneSeq = false;
			}

			concatenatedSequence = concatSeq.toString();
		}
		return concatenatedSequence;
	}

	public MutationSet getMutations() {
		if (mutations == null) {
			mutations = new MutationSet();
			alignedGeneSequenceMap.values()
				.stream()
				.forEach(geneSeq -> {
					mutations = mutations.mergesWith(geneSeq.getMutations());
				});
		}
		return mutations;
	}

	public int getAbsoluteFirstNA() {
		if (absoluteFirstNA == null) {
			absoluteFirstNA = -1;
			for (Gene gene : getAvailableGenes()) {
				// use the first available gene only
				absoluteFirstNA =
					FIRST_NA_BY_GENE.get(gene) +
					(alignedGeneSequenceMap.get(gene).getFirstAA() - 1) * 3;
				break;
			}
			LOGGER.debug("First HXB2 NA: firstNA:" + absoluteFirstNA);
		}
		return absoluteFirstNA;
	}

	public MutationSet getSdrms() {
		if (sdrms == null) {
			sdrms = Sdrms.getSdrms(getMutations());
		}
		return sdrms;
	}

	public HIVGenotypeResult getSubtypeResult() {
		if (!isEmpty && subtypeResult == null) {
			subtypeResult = HIVGenotypeReference.compareAll(
				getConcatenatedSeq(), getAbsoluteFirstNA());
		}
		return subtypeResult;
	}

	public BoundGenotype getBestMatchingGenotype() {
		return getBestMatchingSubtype();
	}

	public BoundGenotype getBestMatchingSubtype() {
		if (isEmpty) {
			return null;
		}
		return getSubtypeResult().getBestMatch();
	}

	public String getSubtypeText() {
		if (isEmpty) {
			return "NA";
		}
		return getBestMatchingGenotype().getDisplay();
	}

	public double getMixturePcnt() {
		if (mixturePcnt == null) {
			mixturePcnt = SeqUtils.mixturePcnt(getConcatenatedSeq());
		}
		return mixturePcnt;
	}

	public Apobec getApobec() {
		if (apobec == null) {
			apobec = new Apobec(getMutations());
		}
		return apobec;
	}

	public List<FrameShift> getFrameShifts() {
		if (frameShifts == null) {
			frameShifts = new ArrayList<>();
			for (AlignedGeneSeq seq: alignedGeneSequenceMap.values()) {
				frameShifts.addAll(seq.getFrameShifts());
			}
		}
		return frameShifts;
	}

}
