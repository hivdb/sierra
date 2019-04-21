package edu.stanford.hivdb.ngs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import edu.stanford.hivdb.mutations.CodonReads;
import edu.stanford.hivdb.mutations.PositionCodonReads;

public class SequenceReadsHistogram {
	
	private static double MIN_LOG10_LOWER_LIMIT = -100d;

	public static enum AggregationOption {
		Codon, AminoAcid, Position;
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
	final private boolean cumulative;
	final private AggregationOption aggregatesBy;
	final private transient Double binWidth;
	
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
		binWidth = (log10PcntUpperLimit - log10PcntLowerLimit) / numBins;
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
				Set<String> aggregatedSites = new HashSet<>();
				boolean[] excludedSites = new boolean[numBins];
				long total = pcr.getTotalReads();
				double log10Total = Math.log10(total);
				double[] binSteps = new double[numBins];
				for (int idx = 0; idx < numBins; idx ++) {
					binSteps[idx] = log10PcntLowerLimit + idx * binWidth;
				}
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
							if (testBetween(log10Pcnt, binSteps[idx], binSteps[idx] + binWidth)) {
								// TODO: can a set of tuples be faster?
								String idxKey = String.format("%d::%s", idx, key);
								aggregatedSites.add(idxKey);
							}
						}
					}
					else if (positionMatchAll) {
						for (int idx = 0; idx < numBins; idx ++) {
							if (testBetween(log10Pcnt, binSteps[idx], binSteps[idx] + binWidth)) {
								excludedSites[idx] = true;
							}
						}
					}
				}
				for (String idxKey : aggregatedSites) {
					int idx = Integer.parseInt(idxKey.split("::", 2)[0]);
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
				log10PcntLowerLimit + idx * binWidth,
				binWidth,
				binsCount[idx]));
		}
		return result;
	}
	
	public List<HistogramBin> getUsualSites() {
		boolean aggByPos = aggregatesBy == AggregationOption.Position;
		return getSites(cr -> !cr.isUnusual(), aggByPos);
	}
	
	public List<HistogramBin> getUnusualSites() {
		return getSites(cr -> cr.isUnusual(), false);
	}
	
	public List<HistogramBin> getUnusualApobecSites() {
		return getSites(cr -> cr.isUnusual() && cr.isApobecMutation(), false);
	}
	
	public List<HistogramBin> getDrmSites() {
		return getSites(cr -> cr.isDRM(), false);
	}

}
