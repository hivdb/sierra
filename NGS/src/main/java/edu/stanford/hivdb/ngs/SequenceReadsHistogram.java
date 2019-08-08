package edu.stanford.hivdb.ngs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;

import edu.stanford.hivdb.mutations.CodonReads;
import edu.stanford.hivdb.mutations.PositionCodonReads;

public class SequenceReadsHistogram {
	
	private static double MIN_LOG10_LOWER_LIMIT = -100d;

	public static enum AggregationOption {
		Codon, AminoAcid, Position;
	}
	
	public static interface WithSequenceReadsHistogram {
		
		public SequenceReadsHistogram getHistogram(
			final Double pcntLowerLimit,
			final Double pcntUpperLimit,
			final Integer numBins,
			final Boolean cumulative,
			final AggregationOption aggregatesBy);

		public SequenceReadsHistogram getHistogram(
			final Double pcntLowerLimit,
			final Double pcntUpperLimit,
			final Double[] binTicks,
			final Boolean cumulative,
			final AggregationOption aggregatesBy);
		
	}
	
	public static class HistogramBin {
		final public Double log10Percent;
		final public Double binWidth;
		final public Integer count;
		// final public String type;
		
		public HistogramBin(double log10Percent, double binWidth, int count/*, String type*/) {
			this.log10Percent = log10Percent;
			this.binWidth = binWidth;
			this.count = count;
			// this.type = type;
		}
		
		public Double getPercentStart() {
			return Math.pow(10, log10Percent) * 100;
		}
		
		public Double getPercentStop() {
			return Math.pow(10, log10Percent + binWidth) * 100;
		}
	}
	
	final private List<GeneSequenceReads> allGeneSequenceReads;
	final private Double log10PcntLowerLimit;
	final private Double log10PcntUpperLimit;
	final private Integer numBins;
	final private Double[] binSteps;
	final private Boolean cumulative;
	final private AggregationOption aggregatesBy;
	
	public SequenceReadsHistogram(
		List<GeneSequenceReads> allGeneSequenceReads,
		double pcntLowerLimit, double pcntUpperLimit, int numBins,
		boolean cumulative, AggregationOption aggregatesBy
	) {
		this.allGeneSequenceReads = allGeneSequenceReads;
		this.numBins = numBins;
		this.cumulative = cumulative;
		this.aggregatesBy = aggregatesBy;
		log10PcntLowerLimit = Math.max(MIN_LOG10_LOWER_LIMIT, Math.log10(pcntLowerLimit));
		log10PcntUpperLimit = Math.log10(pcntUpperLimit);
		double binWidth = (log10PcntUpperLimit - log10PcntLowerLimit) / numBins;

		Double[] binSteps = new Double[numBins + 1];
		for (int idx = 0; idx < numBins + 1; idx ++) {
			binSteps[idx] = log10PcntLowerLimit + idx * binWidth;
		}
		this.binSteps = binSteps;
	}

	public SequenceReadsHistogram(
		List<GeneSequenceReads> allGeneSequenceReads,
		double pcntLowerLimit, double pcntUpperLimit, Double[] binTicks,
		boolean cumulative, AggregationOption aggregatesBy
	) {
		this.allGeneSequenceReads = allGeneSequenceReads;
		this.numBins = binTicks.length;
	
		this.cumulative = cumulative;
		this.aggregatesBy = aggregatesBy;
		log10PcntLowerLimit = Math.max(MIN_LOG10_LOWER_LIMIT, Math.log10(pcntLowerLimit));
		log10PcntUpperLimit = Math.log10(pcntUpperLimit);

		Double[] binSteps = new Double[numBins + 1];
		for (int idx = 0; idx < numBins + 1; idx ++) {
			if (idx == numBins) {
				binSteps[idx] = log10PcntUpperLimit;
			}
			else {
				binSteps[idx] = Math.log10(binTicks[idx]);
			}
		}
		this.binSteps = binSteps;
	}
	
	private boolean testBetween(double val, double lowerLimit, double upperLimit) {
		return val >= lowerLimit && (cumulative || val <= upperLimit);
	}

	private List<HistogramBin> getSites(
		Function<CodonReads, Boolean> filter,
		boolean positionMatchAll
	) {
		/* initialize binsCount */
		int[] binsCount = new int[numBins];

		/* iterate all codon reads; remove filtered ones; save result in binsCount */
		for (GeneSequenceReads geneSeqReads : allGeneSequenceReads) {
			for (PositionCodonReads pcr : geneSeqReads.getAllPositionCodonReads()) {
				Set<Pair<Integer, String>> aggregatedSites = new HashSet<>();
				boolean[] excludedSites = new boolean[numBins];
				long total = pcr.getTotalReads();
				double log10Total = Math.log10(total);
				for (CodonReads cr : pcr.getCodonReads()) {
					long reads = cr.getReads();
					double log10Reads = Math.log10(reads);
					double log10Pcnt = log10Reads - log10Total;
					if (cr.isReference()) {
						continue;
					}
					if (!testBetween(log10Pcnt, log10PcntLowerLimit, log10PcntUpperLimit)) {
						continue;
					}
					if (filter.apply(cr)) {
						String key;
						switch (aggregatesBy) {
							case AminoAcid:
								key = cr.getAminoAcid().toString();
								break;
							case Position:
								key = "" + pcr.getPosition();
								break;
							default:
								key = cr.getCodon();
						}
						for (int idx = 0; idx < numBins; idx ++) {
							if (testBetween(log10Pcnt, binSteps[idx], binSteps[idx + 1])) {
								aggregatedSites.add(Pair.of(idx, key));
							}
						}
					}
					else if (positionMatchAll) {
						for (int idx = 0; idx < numBins; idx ++) {
							if (testBetween(log10Pcnt, binSteps[idx], binSteps[idx + 1])) {
								excludedSites[idx] = true;
							}
						}
					}
				}
				for (Pair<Integer, String> idxKey : aggregatedSites) {
					int idx = idxKey.getLeft();
					if (positionMatchAll && excludedSites[idx]) {
						continue;
					}
					binsCount[idx] ++;
				}
			}
		}
		
		List<HistogramBin> result = new ArrayList<>();
		for (int idx = 0; idx < numBins; idx ++) {
			result.add(new HistogramBin(
				binSteps[idx],
				binSteps[idx + 1] - binSteps[idx],
				binsCount[idx]));
		}
		return result;
	}
	
	public List<HistogramBin> getUsualSites() {
		switch (aggregatesBy) {
			case Position:
				return getSites(cr -> !cr.isUnusual(), true);
			case AminoAcid:
				return getSites(cr -> !cr.isUnusual(), false);
			default:  // case Codon:
				return getSites(cr -> !cr.isUnusualByCodon(), false);
		}
	}
	
	public List<HistogramBin> getUnusualSites() {
		switch (aggregatesBy) {
			case Position:
			case AminoAcid:
				return getSites(cr -> cr.isUnusual(), false);
			default:  // case Codon:
				return getSites(cr -> cr.isUnusualByCodon(), false);
		}
	}
	
	public List<HistogramBin> getUnusualApobecSites() {
		switch (aggregatesBy) {
			case Position:
			case AminoAcid:
				return getSites(cr -> cr.isUnusual() && cr.isApobecMutation(), false);
			default:  // case Codon:
				return getSites(cr -> cr.isUnusualByCodon() && cr.isApobecMutation(), false);
		}
	}
	
	public List<HistogramBin> getApobecSites() {
		return getSites(cr -> cr.isApobecMutation(), false);
	}
	
	public List<HistogramBin> getApobecDrmSites() {
		return getSites(cr -> cr.isApobecDRM(), false);
	}

	public List<HistogramBin> getStopCodonSites() {
		return getSites(cr -> cr.hasStop(), false);
	}
	
	public List<HistogramBin> getDrmSites() {
		return getSites(cr -> cr.isDRM(), false);
	}
	
	public Integer getNumPositions() {
		return (allGeneSequenceReads
				.stream()
				.mapToInt(gsr -> gsr.getAllPositionCodonReads().size())
				.sum());
	}
		
}
