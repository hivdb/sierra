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
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

import edu.stanford.hivdb.genotyper.BoundGenotype;
import edu.stanford.hivdb.genotyper.HIVGenotypeReference;
import edu.stanford.hivdb.genotyper.HIVGenotypeResult;
import edu.stanford.hivdb.mutations.Apobec;
import edu.stanford.hivdb.mutations.FrameShift;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.GeneEnum;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.mutations.Sdrms;
import edu.stanford.hivdb.mutations.Strain;
import edu.stanford.hivdb.utilities.SeqUtils;
import edu.stanford.hivdb.utilities.Sequence;

public class AlignedSequence {
	private static final Character WILDCARD = '.';
	private static final Character WILDCARD_PLACEHOLDER = '\b';

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
	private transient Integer numMatchedNAs;

	// private static final Logger LOGGER = LogManager.getLogger();

	public AlignedSequence(
			final Strain strain,
			final Sequence unalignedSequence,
			final Map<Gene, AlignedGeneSeq> alignedGeneSequenceMap,
			final Map<Gene, String> discardedGenes,
			final boolean sequenceReversed) {
		inputSequence = unalignedSequence;
		this.strain = strain;
		this.alignedGeneSequenceMap = alignedGeneSequenceMap;
		this.discardedGenes = discardedGenes;
		isReverseComplement = sequenceReversed;
		isEmpty = alignedGeneSequenceMap.isEmpty();
		numMatchedNAs = null;
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
			concatenatedSequence = getConcatenatedSeq(false, true);
		}
		return concatenatedSequence;
	}
	
	private String getConcatenatedSeq(boolean fitWithHXB2, boolean trimResult) {
		// TODO: should be replaced by Gene.adjustNAAlignment
		String geneSeqNAs;
		AlignedGeneSeq geneSeq;
		int numPrefixNAs = 0;
		int numSuffixNAs = 0;
		StringBuilder concatSeq = new StringBuilder();

		for (Gene gene: getAvailableGenes()) {
			geneSeq = alignedGeneSequenceMap.get(gene);
			numPrefixNAs = (geneSeq.getFirstAA() - 1) * 3;
			geneSeqNAs = geneSeq.getAlignedNAs();
			numSuffixNAs =
				gene.getNASize() -
				geneSeqNAs.length() - numPrefixNAs;

			geneSeqNAs =
				StringUtils.repeat(WILDCARD_PLACEHOLDER, numPrefixNAs) +
				geneSeqNAs +
				StringUtils.repeat(WILDCARD_PLACEHOLDER, numSuffixNAs);

			if (fitWithHXB2 && (gene.getStrain() == Strain.HIV2A ||
				gene.getStrain() == Strain.HIV2B)) {
				GeneEnum geneEnum = gene.getGeneEnum();
				if (geneEnum == GeneEnum.RT) {
					// RT346: 1AA Deletion comparing to HXB2
					geneSeqNAs =
						geneSeqNAs.substring(0, 345 * 3) +
						StringUtils.repeat(WILDCARD_PLACEHOLDER, 3) +
						geneSeqNAs.substring(345 * 3);
				}
				else if (geneEnum == GeneEnum.IN) {
					// IN272: 2AAs Insertion comparing to HXB2
					geneSeqNAs = 
						geneSeqNAs.substring(0, 272 * 3) +
						geneSeqNAs.substring(274 * 3);
					// IN283: 1AA Insertion comparing to HXB2
					geneSeqNAs = 
						geneSeqNAs.substring(0, 283 * 3) +
						geneSeqNAs.substring(284 * 3);
					// Anything after IN288
					geneSeqNAs = geneSeqNAs.substring(0, 288 * 3); 
				}
			}
			concatSeq.append(geneSeqNAs);
		}
		if (trimResult) {
			return concatSeq.toString().trim().replace(WILDCARD_PLACEHOLDER, WILDCARD);
		} else {
			return concatSeq.toString().replace(WILDCARD_PLACEHOLDER, WILDCARD);
		}
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
					gene.getFirstNA() +
					(alignedGeneSequenceMap.get(gene).getFirstAA() - 1) * 3;
				break;
			}
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
			// HIV-Genotyper only accepts HXB2-aligned sequence.
			// Therefore we need to tweak the alignment.
			String hxb2ConcatSeq = getConcatenatedSeq(true, false);
			int hxb2FirstNA = Gene.valueOf("HIV1PR").getFirstNA();
			subtypeResult = HIVGenotypeReference.compareAll(
				hxb2ConcatSeq, hxb2FirstNA);
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
	
	protected int getNumMatchedNAs() {
		if (numMatchedNAs == null) {
			numMatchedNAs = 0;
			for (Gene gene : alignedGeneSequenceMap.keySet()) {
				numMatchedNAs += gene.getNASize();
				AlignedGeneSeq geneSeq = alignedGeneSequenceMap.get(gene);
				numMatchedNAs -=
					geneSeq.getFirstAA() * 3 - 3 + // left missing NAs
					geneSeq.getNumDiscordantNAs() +
					gene.getNASize() - geneSeq.getLastAA() * 3; // right missing NAs
			}
		}
		return numMatchedNAs;
	}
}
