package edu.stanford.hivdb.ngs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import edu.stanford.hivdb.mutations.CodonReads;
import edu.stanford.hivdb.mutations.PositionCodonReads;

public class CutoffSuggestion {

	private final double looserLimit;
	private final double stricterLimit;
	
	
	public CutoffSuggestion(List<PositionCodonReads> allReads) {
		List<CodonReads> sortedCodonReads = allReads
			.stream()
			.flatMap(pcr -> pcr.getCodonReads().stream())
			.sorted((r1, r2) -> r2.getProportion().compareTo(r1.getProportion()))
			.collect(Collectors.toList());
		long totalCount = sortedCodonReads
			.stream()
			.mapToLong(r -> r.getReads())
			.sum();
		long remainCount = totalCount;
		
		boolean started = false;
		double fold = 1;
		List<Double> proportionInWindow = new ArrayList<>();
		List<Double> codonPcntsInWindow = new ArrayList<>();
		List<Double> foldsInWindow = new ArrayList<>();
		
		for (CodonReads r : sortedCodonReads) {
			double proportion = r.getProportion();
			long curReads = r.getReads();
			if (proportion > 0.2) {
				// ignore codons with proportion > 20%
				remainCount -= curReads;
				continue;
			}
			fold = (double) r.getTotalReads() * remainCount / totalCount / curReads;
			if (Double.isNaN(fold)) {
				fold = 0;
			}
			if (!started && fold < 0.8) {
				started = true;
			}
			if (started) {
				if (fold >= 1) {
					break;
				}
				remainCount -= curReads;
				proportionInWindow.add(proportion);
				double codonPcnt = r.getCodonPercent();
				// codon percent is more precise to tell if the codon/AA is rare
				codonPcntsInWindow.add(codonPcnt);
				foldsInWindow.add(fold);
			}
		}
		
		if (foldsInWindow.isEmpty()) {
			this.stricterLimit = .0;
			this.looserLimit = .0;
			return;
		}
		
		// sometime the fold >= 1 didn't reached and we went all the way to the end
		double maxFold = Collections.max(foldsInWindow);
		// tolerant fluctuation
		double nearMaxFold = maxFold * 0.95;
		maxFold = foldsInWindow.stream().filter(f -> f > nearMaxFold).findFirst().get();
		int maxFoldIndex = foldsInWindow.indexOf(maxFold);
		
		proportionInWindow = proportionInWindow.subList(0, maxFoldIndex + 1);
		codonPcntsInWindow = codonPcntsInWindow.subList(0 , maxFoldIndex + 1);
		foldsInWindow = foldsInWindow.subList(0, maxFoldIndex + 1);
		
		int minFoldIndex = foldsInWindow.indexOf(Collections.min(foldsInWindow));
		int tmpsize = foldsInWindow.size();
		proportionInWindow = proportionInWindow.subList(minFoldIndex, tmpsize);
		codonPcntsInWindow = codonPcntsInWindow.subList(minFoldIndex, tmpsize);
		foldsInWindow = foldsInWindow.subList(minFoldIndex, tmpsize);

		double totalRareCodonInWindow = codonPcntsInWindow.stream()
			.mapToDouble(pcnt -> 1 - Math.sqrt(pcnt))
			.sum();
		long totalCodonInWindow = codonPcntsInWindow.size(); //stream().filter(pcnt -> pcnt < 0.5).count();
		double rareRate = totalRareCodonInWindow / totalCodonInWindow;
		if (totalCodonInWindow == 0) {
			rareRate = 0;
		}

		int newsize = proportionInWindow.size();
		double[] proportionInWindowArr = new double[newsize];
		for (int i = 0; i < newsize; i ++) {
			proportionInWindowArr[i] = proportionInWindow.get(i);
		}
		// this is similar to finding percentile, however it uses ceil() to
		// final a smaller cutoff (especially useful when the distribution of
		// proportion is very sparse)
		int indexFromRareRate = (int) Math.ceil(newsize * (1 - rareRate));
		if (proportionInWindow.size() > indexFromRareRate) {
			this.stricterLimit = proportionInWindow.get(indexFromRareRate);
			this.looserLimit = proportionInWindow.get(proportionInWindow.size() - 1);
		}
		else {
			this.stricterLimit = .0;
			this.looserLimit = .0;
		}
		// this.stricterLimit = upperLimit;
	}
	
	public Double getLooserLimit() { return looserLimit; }
	public Double getStricterLimit() { return stricterLimit; }

}
