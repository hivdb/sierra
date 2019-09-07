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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import com.google.common.collect.Streams;

import edu.stanford.hivdb.genotyper.BoundGenotype;
import edu.stanford.hivdb.genotyper.HIVGenotypeReference;
import edu.stanford.hivdb.genotyper.HIVGenotypeResult;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.mutations.PositionCodonReads;
import edu.stanford.hivdb.mutations.Strain;
import edu.stanford.hivdb.ngs.SequenceReadsHistogram.AggregationOption;
import edu.stanford.hivdb.ngs.SequenceReadsHistogram.WithSequenceReadsHistogram;
import edu.stanford.hivdb.utilities.SeqUtils;

public class SequenceReads implements WithSequenceReadsHistogram {

	private final static int HXB2_PR_FIRST_NA = 2253;
	private final static double MIN_PREVALENCE_FOR_SUBTYPING = 0.05;
	private final Strain strain;
	private final Map<Gene, GeneSequenceReads> allGeneSequenceReads;
	private final CutoffSuggestion cutoffSuggestion;
	private String name;
	private HIVGenotypeResult subtypeResult;
	private MutationSet mutations;
	private String concatenatedSeq;
	private Double mixturePcnt;
	private Double minPrevalence;
	private Long minReadDepth;
	private transient DescriptiveStatistics readDepthStats;

	public static SequenceReads fromCodonReadsTable(
			String name, List<PositionCodonReads> allReads,
			Double minPrevalence, Long minReadDepth) {
		// TODO: dynamic cutoff
		CutoffSuggestion cutoffSuggestion = new CutoffSuggestion(allReads);
		double finalMinPrevalence = minPrevalence >= 0 ? minPrevalence : cutoffSuggestion.getStricterLimit();
		long finalMinReadDepth = minReadDepth > 0 ? minReadDepth : (long) 1000;
		Map<Gene, GeneSequenceReads> geneSequences = allReads.stream()
			// remove all codons with their read depth < minReadDepth
			.filter(read -> read.getTotalReads() >= finalMinReadDepth)
			.collect(
				Collectors.groupingBy(
					PositionCodonReads::getGene,
					TreeMap::new,
					Collectors.collectingAndThen(
						Collectors.toList(),
						list -> new GeneSequenceReads(list, finalMinPrevalence)
					)
				)
			);

		// TODO: add support for HIV2
		return new SequenceReads(
				name, Strain.HIV1, geneSequences,
				finalMinPrevalence, finalMinReadDepth, cutoffSuggestion);
	}

	protected SequenceReads(
			final String name, final Strain strain,
			final Map<Gene, GeneSequenceReads> allGeneSequenceReads,
			final double minPrevalence, final long minReadDepth,
			final CutoffSuggestion cutoffSuggestion) {
		this.name = name;
		this.strain = strain;
		this.allGeneSequenceReads = allGeneSequenceReads;
		this.minPrevalence = minPrevalence;
		this.minReadDepth = minReadDepth;
		this.cutoffSuggestion = cutoffSuggestion;
	}

	public Double getCutoffSuggestionLooserLimit() { return cutoffSuggestion.getLooserLimit(); }

	public Double getCutoffSuggestionStricterLimit() { return cutoffSuggestion.getStricterLimit(); }

	public String getName() { return name; }
	
	public Strain getStrain() { return strain; }

	public boolean isEmpty() { return allGeneSequenceReads.isEmpty(); }

	public double getMinPrevalence() { return minPrevalence; }

	public long getMinReadDepth() { return minReadDepth; }

	public DescriptiveStatistics getReadDepthStats() {
		if (readDepthStats == null) {
			Optional<DoubleStream> readDepthStream = allGeneSequenceReads.values().stream()
				.map(gsr -> gsr.getAllPositionCodonReads())
				.map(pcrs -> pcrs.stream().mapToDouble(pcr -> pcr.getTotalReads()))
				.reduce((a, b) -> Streams.concat(a, b));
			if (readDepthStream.isPresent()) {
				double[] readDepthArray = readDepthStream.get().toArray();
				readDepthStats = new DescriptiveStatistics(readDepthArray);
			}
			else {
				readDepthStats = new DescriptiveStatistics(new double[] {0, 0, 0});
			}
		}
		return readDepthStats;
	}
	
	public List<GeneSequenceReads> getAllGeneSequenceReads() {
		return new ArrayList<>(allGeneSequenceReads.values());
	}

	public GeneSequenceReads getGeneSequenceReads(Gene gene) {
		return allGeneSequenceReads.get(gene);
	}

	public List<Gene> getAvailableGenes() {
		return new ArrayList<>(allGeneSequenceReads.keySet());
	}

	public String getConcatenatedSeq() {
		if (concatenatedSeq == null) {
			StringBuilder concatSeq = new StringBuilder();
			for (Gene gene : Gene.values(strain)) {
				GeneSequenceReads geneSeq = allGeneSequenceReads.get(gene);
				if (geneSeq == null) {
					concatSeq.append(StringUtils.repeat("...", gene.getLength()));
				} else {
					concatSeq.append(geneSeq.getAlignedNAs(true));
				}
			}
			concatenatedSeq = concatSeq.toString();
		}
		return concatenatedSeq;
	}
	
	protected String getConcatenatedSeqForSubtyping() {
		StringBuilder concatSeq = new StringBuilder();
		for (Gene gene : Gene.values(strain)) {
			GeneSequenceReads geneSeq = allGeneSequenceReads.get(gene);
			if (geneSeq == null) {
				concatSeq.append(StringUtils.repeat("...", gene.getLength()));
			} else {
				concatSeq.append(
					geneSeq.getAlignedNAs(MIN_PREVALENCE_FOR_SUBTYPING, true));
			}
		}
		return concatSeq.toString();
	}
	
	@Override
	public SequenceReadsHistogram getHistogram(
		final Double pcntLowerLimit,
		final Double pcntUpperLimit,
		final Integer numBins,
		final Boolean cumulative,
		final AggregationOption aggregatesBy) {
		return new SequenceReadsHistogram(
			getAllGeneSequenceReads(),
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
			getAllGeneSequenceReads(),
			pcntLowerLimit / 100, pcntUpperLimit / 100,
			binTicks, cumulative, aggregatesBy);
	}

	public MutationSet getMutations(final double minPrevalence) {
		if (!isEmpty()) {
			return allGeneSequenceReads.values().stream()
				.map(gs -> gs.getMutations(minPrevalence))
				.reduce((m1, m2) -> m1.mergesWith(m2))
				.get();
		} else {
			return new MutationSet();
		}
	}

	public MutationSet getMutations() {
		if (!isEmpty() && mutations == null) {
			mutations = getMutations(this.minPrevalence);
		}
		return mutations;
	}

	public HIVGenotypeResult getSubtypeResult() {
		if (!isEmpty() && subtypeResult == null) {
			subtypeResult = HIVGenotypeReference.compareAll(
				getConcatenatedSeqForSubtyping(), HXB2_PR_FIRST_NA);
		}
		return subtypeResult;
	}

	public BoundGenotype getBestMatchingSubtype() {
		if (isEmpty()) {
			return null;
		}
		return getSubtypeResult().getBestMatch();
	}

	public String getSubtypeText() {
		if (isEmpty()) {
			return "NA";
		}
		return getBestMatchingSubtype().getDisplay();
	}

	public double getMixturePcnt() {
		if (mixturePcnt == null) {
			StringBuilder concatSeq = new StringBuilder();
			for (GeneSequenceReads geneSeqReads : allGeneSequenceReads.values()) {
				concatSeq.append(geneSeqReads.getAlignedNAs(false));
			}
			mixturePcnt = SeqUtils.mixturePcnt(concatSeq.toString());
		}
		return mixturePcnt;
	}

}
