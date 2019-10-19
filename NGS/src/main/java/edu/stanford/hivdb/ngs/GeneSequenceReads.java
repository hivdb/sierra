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
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.rank.Median;

import com.google.common.collect.Lists;

import edu.stanford.hivdb.mutations.CodonTranslation;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MultiCodonsMutation;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.mutations.PositionCodonReads;
import edu.stanford.hivdb.ngs.SequenceReadsHistogram.AggregationOption;
import edu.stanford.hivdb.ngs.SequenceReadsHistogram.WithSequenceReadsHistogram;

public class GeneSequenceReads implements WithSequenceReadsHistogram {

	private final Gene gene;
	private final int firstAA;
	private final int lastAA;
	private final List<PositionCodonReads> posCodonReads;
	private final double minPrevalence;
	private MutationSet mutations;
	private transient DescriptiveStatistics readDepthStats;

	public GeneSequenceReads(
			final Gene gene,
			final List<PositionCodonReads> posCodonReads,
			final double minPrevalence) {
		this.gene = gene;
		this.minPrevalence = minPrevalence;
		this.firstAA = Math.max(1, (int) posCodonReads.get(0).getPosition());
		this.lastAA = Math.min(gene.getLength(), (int) posCodonReads.get(posCodonReads.size() - 1).getPosition());
		this.posCodonReads = Collections.unmodifiableList(
			posCodonReads
			.stream()
			// remove illegal positions
			.filter(pcr -> {
				long pos = pcr.getPosition();
				return pos >= this.firstAA && pos <= this.lastAA;
			})
			.collect(Collectors.toList())
		);
	}

	/** initializes GeneSequence without specify gene
	 *
	 * Warning: This constructor is only intended to use internally
	 *
	 * @param posCodonReads
	 * @param minPrevalence
	 */
	protected GeneSequenceReads(
			final List<PositionCodonReads> posCodonReads,
			final double minPrevalence) {
		this(posCodonReads.get(0).getGene(), posCodonReads, minPrevalence);
	}

	public Gene getGene() { return gene; }
	public int getFirstAA() { return firstAA; }
	public int getLastAA() { return lastAA; }
	public int getSize() { return lastAA - firstAA; }
	public int getNumPositions() { return posCodonReads.size(); }
	public List<PositionCodonReads> getAllPositionCodonReads() { return posCodonReads; }

	public MutationSet getMutations(final double minPrevalence) {
		if (minPrevalence != this.minPrevalence || mutations == null) {
			List<Mutation> myMutations = new ArrayList<>();
			long prevPos = firstAA - 1;
			for (PositionCodonReads pcr : posCodonReads) {
				long curPos = pcr.getPosition();
				for (Long pos = prevPos + 1; pos < curPos - 1; pos ++) {
					// add unsequenced regions
					myMutations.add(MultiCodonsMutation.initUnsequenced(
						gene, pos.intValue()
					));
				}
				prevPos = curPos;
				Mutation mut = MultiCodonsMutation
					.fromPositionCodonReads(pcr, minPrevalence);
				if (mut != null) {
					myMutations.add(mut);
				}
			}
			if (minPrevalence == this.minPrevalence) {
				mutations = new MutationSet(myMutations);
			}
			else {
				return new MutationSet(myMutations);
			}
		}
		return mutations;
	}

	public Double getMedianReadDepth() {
		Median median = new Median();
		double[] ReadDepths = (
			posCodonReads.stream()
			.mapToDouble(read -> read.getTotalReads())
			.toArray()
		);
		double medianReadDepth = -1;
		if (ReadDepths.length > 0) {
			medianReadDepth = median.evaluate(ReadDepths);
		}
		return medianReadDepth;
	}

	@Override
	public SequenceReadsHistogram getHistogram(
		final Double pcntLowerLimit,
		final Double pcntUpperLimit,
		final Integer numBins,
		final Boolean cumulative,
		final AggregationOption aggregatesBy) {
		return new SequenceReadsHistogram(
			Lists.asList(this, new GeneSequenceReads[0]),
			pcntLowerLimit, pcntUpperLimit,
			numBins, cumulative, aggregatesBy);
	}

	@Override
	public SequenceReadsHistogram getHistogram(
		final Double pcntLowerLimit,
		final Double pcntUpperLimit,
		final Double[] binTicks,
		final Boolean cumulative,
		final AggregationOption aggregatesBy) {
		return new SequenceReadsHistogram(
			Lists.asList(this, new GeneSequenceReads[0]),
			pcntLowerLimit / 100, pcntUpperLimit / 100,
			binTicks, cumulative, aggregatesBy);
	}

	public DescriptiveStatistics getReadDepthStats() {
		if (readDepthStats == null) {
			double[] readDepthArray = posCodonReads.stream()
				.mapToDouble(pcr -> pcr.getTotalReads())
				.toArray();
			
			if (readDepthArray.length > 2) {
				readDepthStats = new DescriptiveStatistics(readDepthArray);
			}
			else {
				readDepthStats = new DescriptiveStatistics(new double[] {0, 0, 0});
			}
		}
		return readDepthStats;
	}

	public MutationSet getMutations() {
		return getMutations(this.minPrevalence);
	}

	/** Returns consensus sequence aligned to subtype B reference.
	 *  All insertions are removed from the result.
	 *
	 * @param autoComplete specify <tt>true</tt> to prepend and/or append
	 * wildcard "." to incomplete sequence
	 * @return the aligned consensus sequence
	 */
	public String getAlignedNAs(boolean autoComplete) {
		return getAlignedNAs(minPrevalence, autoComplete);
	}

	/** Returns consensus sequence aligned to subtype B reference.
	 *  All insertions are removed from the result.
	 *
	 * @param threshold specify the minimal prevalence requirement for
	 * creating codon consensus
	 * @param autoComplete specify <tt>true</tt> to prepend and/or append
	 * wildcard "." to incomplete sequence
	 * @return the aligned consensus sequence
	 */
	public String getAlignedNAs(double threshold, boolean autoComplete) {
		StringBuilder seq = new StringBuilder();
		if (autoComplete) {
			seq.append(StringUtils.repeat("...", firstAA - 1));
		}
		long prevPos = firstAA - 1;
		for (PositionCodonReads pcr : posCodonReads) {
			long curPos = pcr.getPosition();
			if (curPos - prevPos > 1) {
				seq.append(StringUtils.repeat("...", (int) (curPos - prevPos - 1)));
			}
			prevPos = curPos;
			seq.append(pcr.getCodonConsensus(threshold));
		}
		if (autoComplete) {
			seq.append(StringUtils.repeat("...", gene.getLength() - lastAA));
		}
		return seq.toString();
	}

	/** Returns consensus sequence aligned to subtype B reference without
	 *  initial and trailing "." for incomplete sequence. All insertions are
	 *  removed from the result. The result is equivalent to the result of
	 *  <tt>getAlignedSequence(false)</tt>.
	 *
	 * @return the aligned consensus NA sequence
	 */
	public String getAlignedNAs() {
		return getAlignedNAs(false);
	}

	/** Returns consensus sequence aligned to subtype B reference in amino
	 *  acid form. Unsequenced region(s) are ignored. All insertions are
	 *  removed from the result.
	 *
	 * @return the aligned consensus AA sequence
	 */
	public String getAlignedAAs() {
		return CodonTranslation.simpleTranslate(
			this.getAlignedNAs(false), firstAA, gene.getReference());
	}

}
