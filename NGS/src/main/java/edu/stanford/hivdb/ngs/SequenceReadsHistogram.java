package edu.stanford.hivdb.ngs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import edu.stanford.hivdb.mutations.CodonReads;
import edu.stanford.hivdb.mutations.PositionCodonReads;

public class SequenceReadsHistogram {
	
	private static double MIN_LOG10_LOWER_LIMIT = -100d;

	public static enum AggregationOption {
		Codon, AminoAcid;
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
	final private Double pcntLowerLimit;
	final private Double pcntUpperLimit;
	final private Integer numBins;
	final private AggregationOption aggregatesBy;
	final private transient Double log10PcntLowerLimit;
	final private transient Double log10PcntUpperLimit;
	final private transient Double binWidth;
	
	public SequenceReadsHistogram(
		List<GeneSequenceReads> allGeneSequenceReads,
		double pcntLowerLimit, double pcntUpperLimit, int numBins,
		AggregationOption aggregatesBy
	) {
		this.allGeneSequenceReads = allGeneSequenceReads;
		this.pcntLowerLimit = pcntLowerLimit;
		this.pcntUpperLimit = pcntUpperLimit;
		this.numBins = numBins;
		this.aggregatesBy = aggregatesBy;
		log10PcntLowerLimit = Math.max(MIN_LOG10_LOWER_LIMIT, Math.log10(pcntLowerLimit));
		log10PcntUpperLimit = Math.log10(pcntUpperLimit);
		binWidth = (log10PcntUpperLimit - log10PcntLowerLimit) / numBins;
	}
	
	private List<HistogramBin> getSites(Function<CodonReads, Boolean> filter) {
		
		/* initialize binsCount */
		int[] binsCount = new int[numBins];

		/* iterate all codon reads; remove filtered ones; save result in binsCount */
		boolean byAA = aggregatesBy == AggregationOption.AminoAcid;
		for (GeneSequenceReads geneSeqReads : allGeneSequenceReads) {
			for (PositionCodonReads pcr : geneSeqReads.getAllPositionCodonReads()) {
				Map<String, Long> aggregatedReads = new HashMap<>();
				for (CodonReads cr : pcr.getCodonReads()) {
					if (filter.apply(cr)) {
						String key = byAA ? cr.getAminoAcid().toString() : cr.getCodon();
						Long reads = cr.getReads() + aggregatedReads.getOrDefault(key, 0L);
						aggregatedReads.put(key, reads);
					}
				}
				Long total = pcr.getTotalReads();
				for (Long reads : aggregatedReads.values()) {
					double pcnt = reads.doubleValue() / total;
					if (pcnt <= pcntUpperLimit && pcnt >= pcntLowerLimit) {
						double log10Pcnt = Math.max(MIN_LOG10_LOWER_LIMIT , Math.log10(pcnt));
						int idx = (int) Math.floor((log10Pcnt - log10PcntLowerLimit) / binWidth);
						binsCount[idx] ++;
					}
				}
			}
		}
		
		List<HistogramBin> result = new ArrayList<>();
		for (int idx = 0; idx < numBins; idx ++) {
			result.add(new HistogramBin(
				log10PcntLowerLimit + (idx) * binWidth,
				binWidth,
				binsCount[idx]));
		}
		return result;
	}
	
	public List<HistogramBin> getUsualSites() {
		return getSites(cr -> !cr.isUnusual());
	}
	
	public List<HistogramBin> getUnusualSites() {
		return getSites(cr -> cr.isUnusual());
	}
	
	public List<HistogramBin> getUnusualApobecSites() {
		return getSites(cr -> cr.isUnusual() && cr.isApobecMutation());
	}

}
