package edu.stanford.hivdb.ngs;

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
		
		boolean reachedPointAllReal = false;
		
		double prevFold = 1, curFold = 1,
				lowerLimit = 0, upperLimit = 0;
		
		for (CodonReads r : sortedCodonReads) {
			double pcnt = r.getProportion();
			long curReads = r.getReads();
			if (pcnt > 0.2) {
				remainCount -= curReads;
				continue;
			}
			prevFold = curFold;
			curFold = (double) r.getTotalReads() * remainCount / totalCount / curReads;
			if (Double.isNaN(curFold)) {
				curFold = 0;
			}
			remainCount -= curReads;
			if (!reachedPointAllReal && prevFold < curFold) {
				reachedPointAllReal = true;
			}
			if (reachedPointAllReal) {
				if (curFold < 0.5) {
					upperLimit = pcnt;
				}
				else if (curFold < 1) {
					lowerLimit = pcnt;
				}
				else /*if (curFold >= 1) */ {
					break;
				}
			}
		}
		this.looserLimit = lowerLimit;
		this.stricterLimit = upperLimit;
	}
	
	public Double getLooserLimit() { return looserLimit; }
	public Double getStricterLimit() { return stricterLimit; }

}
