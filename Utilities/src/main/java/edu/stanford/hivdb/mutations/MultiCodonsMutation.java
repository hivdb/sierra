package edu.stanford.hivdb.mutations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.stanford.hivdb.mutations.PositionCodonReads;
import edu.stanford.hivdb.mutations.PositionCodonReads.CodonReads;

/**
 * An implementation of the Mutation interface to accept
 * multiple codon reads. In this class, a codon is strictly
 * restricted to non-ambiguous nucleotide code (ACGT).
 */
public class MultiCodonsMutation extends AAMutation {

	private final Map<String, Long> codonCounts;
	private final Map<Character, Long> aaCounts;
	private final long totalCount;

	private transient String compatTriplet;
	
	public static class AAPercent {
		private Character aa;
		private Double percent;
		
		protected AAPercent(char aa, double percent) {
			this.aa = aa;
			this.percent = percent;
		}
		
		public Character getAA() { return aa; }
		public Double getPercent() { return percent; }
	}
	
	public static MultiCodonsMutation initUnsequenced(Gene gene, int position) {
		return new MultiCodonsMutation(
			gene, position, Collections.emptyMap(), Collections.emptyMap(), 0
		);
	}
	
	public static MultiCodonsMutation fromPositionCodonReads(
		PositionCodonReads posCodonReads, double minPrevalence
	) {
		Gene gene = posCodonReads.getGene();
		int position = (int) posCodonReads.getPosition();
		long totalCount = posCodonReads.getTotalReads();
		long minReads = Math.round(totalCount * minPrevalence + 0.5);

		Map<String, Long> codonCounts = new TreeMap<>();
		Map<Character, Long> aaCounts = new TreeMap<>();
		
		for (CodonReads codonReads : posCodonReads.getCodonReads()) {
			// Tolerant spaces and dashes
			String codon = codonReads.codon.replaceAll("[ -]", "");
			long count = codonReads.reads;
			if (count <= minReads) {
				// remove minor variants below min-prevalence
				continue;
			}
			if (!codon.matches("^[ACGT]*$")) {
				// do not allow ambiguous codes
				continue;
			}

			int codonLen = codon.length();
			char aa;
			if (codonLen < 3) {
				// a deletion
				aa = '-';
			}
			else if (codonLen < 6) {
				aa = CodonTranslation.translateNATriplet(
					codon.substring(0, 3)
				).charAt(0);
			}
			else {
				// an insertion
				aa = '_';
			}
			long prevCount = aaCounts.getOrDefault(aa, 0l);
			aaCounts.put(aa, prevCount + count);
		}
		char ref = gene.getReference(position).charAt(0);
		if (aaCounts.isEmpty() ||
			(aaCounts.size() == 1 && aaCounts.containsKey(ref))
		) {
			return null;
		}
		return new MultiCodonsMutation(
			gene, position, aaCounts,
			codonCounts, totalCount);
	}
	
	private MultiCodonsMutation(
		Gene gene, int position,
		Map<Character, Long> aaCounts,
		Map<String, Long> codonCounts, 
		long totalCount
	) {
		super(gene, position, aaCounts.keySet(), /* maxDisplayAAs = */6);
		this.aaCounts = aaCounts;
		this.codonCounts = codonCounts;
		this.totalCount = totalCount;
	}

	/**
	 * Gets total read count of all codons (include codons of reference AA)
	 *  
	 * @return a Long number
	 */
	public Long getTotalCount() { return totalCount; }
	
	/**
	 * Gets amino acid percent (max: 100.0)
	 * 
	 * @return a List<AAPercent> object
	 */
	public List<AAPercent> getAAPercents() {
		List<AAPercent> aaPcnts = new ArrayList<>();
		for (Map.Entry<Character, Long> entry : aaCounts.entrySet()) {
			char aa = entry.getKey();
			long count = entry.getValue();
			double pcnt = count * 100.0 / totalCount;
			aaPcnts.add(new AAPercent(aa, pcnt));
		}
		aaPcnts.sort(new Comparator<AAPercent>() {

			@Override
			public int compare(AAPercent o1, AAPercent o2) {
				// Descending sort
				return o2.getPercent().compareTo(o1.getPercent());
			}
			
		});
		return aaPcnts;
	}
	
	@Override
	public boolean isUnsequenced() { return this.totalCount == 0; }

	@Override
	public String getTriplet() {
		if (compatTriplet == null) {
			List<String> cleanCodons = new ArrayList<>();
			for (String codon : codonCounts.keySet()) {
				int codonLen = codon.length();
				if (codonLen < 3 || codonLen > 5) {
					// skip indels
					continue;
				}
				cleanCodons.add(codon.substring(0, 3));
			}
			compatTriplet = CodonTranslation.getMergedCodon(cleanCodons);
		}
		return compatTriplet;
	}

	/**
	 * There's no way to tell about inserted NAs for multiple codons without
	 * an alignment tools. Therefore we simply returns an empty result.
	 */
	@Override
	public String getInsertedNAs() { return ""; }

	@Override
	public boolean hasBDHVN() {
		return getTriplet().matches(".*[BDHVN].*");
	}

}
