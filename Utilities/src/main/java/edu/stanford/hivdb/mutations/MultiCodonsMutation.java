package edu.stanford.hivdb.mutations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import edu.stanford.hivdb.mutations.PositionCodonReads;

/**
 * An implementation of the Mutation interface to accept
 * multiple codon reads. In this class, a codon is strictly
 * restricted to non-ambiguous nucleotide code (ACGT).
 */
public class MultiCodonsMutation extends AAMutation {

	private static final int DEFAULT_MAX_DISPLAY_AAS = 6;

	private final Map<Character, Long> aaCounts;
	private final long totalCount;
	private final String compatTriplet;

	public static MultiCodonsMutation initUnsequenced(Gene gene, int position) {
		return new MultiCodonsMutation(gene, position, Collections.emptyMap(), 0, "NNN");
	}

	private static Map<Character, Long>
	getAACounts(PositionCodonReads posCodonReads, long minReads) {
		Map<Character, Long> aaCounts = new TreeMap<>();

		for (CodonReads codonReads : posCodonReads.getCodonReads()) {
			char aa = codonReads.getAminoAcid();
			if (aa == 'X') {
				continue;
			}
			long count = codonReads.getReads();
			if (count <= minReads) {
				// remove minor variants below min-prevalence
				continue;
			}
			long prevCount = aaCounts.getOrDefault(aa, 0l);
			aaCounts.put(aa, prevCount + count);
		}
		return aaCounts;
	}

	private static String getCompatTriplet(
		PositionCodonReads posCodonReads, long minReads
	) {
		List<String> cleanCodons = new ArrayList<>();
		for (CodonReads codonReads : posCodonReads.getCodonReads()) {
			// Tolerant spaces and dashes
			String codon = codonReads.getCodon().replaceAll("[ -]", "");
			long count = codonReads.getReads();
			if (count <= minReads) {
				// remove minor variants below min-prevalence
				continue;
			}
			if (!codon.matches("^[ACGT]*$")) {
				// do not allow ambiguous codes
				continue;
			}
			int codonLen = codon.length();
			if (codonLen < 3 || codonLen > 5) {
				// skip indels
				continue;
			}
			cleanCodons.add(codon.substring(0, 3));
		}
		return CodonTranslation.getMergedCodon(cleanCodons);
	}

	public static MultiCodonsMutation fromPositionCodonReads(
		PositionCodonReads posCodonReads, double minPrevalence
	) {
		Gene gene = posCodonReads.getGene();
		int position = (int) posCodonReads.getPosition();
		long totalCount = posCodonReads.getTotalReads();
		long minReads = Math.round(totalCount * minPrevalence + 0.5);
		Map<Character, Long> aaCounts = getAACounts(posCodonReads, minReads);
		char ref = gene.getReference(position).charAt(0);
		if (aaCounts.isEmpty() ||
			(aaCounts.size() == 1 && aaCounts.containsKey(ref))
		) {
			return null;
		}
		String compatTriplet = getCompatTriplet(posCodonReads, minReads);
		return new MultiCodonsMutation(
			gene, position, aaCounts, totalCount, compatTriplet);
	}

	private MultiCodonsMutation(
		Gene gene, int position,
		Map<Character, Long> aaCounts,
		long totalCount, String compatTriplet
	) {
		super(gene, position, aaCounts.keySet(), DEFAULT_MAX_DISPLAY_AAS);
		this.aaCounts = aaCounts;
		this.totalCount = totalCount;
		this.compatTriplet = compatTriplet;
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
			if (aa == getRefChar()) {
				continue;
			}
			long count = entry.getValue();
			double pcnt = count * 100.0 / totalCount;
			Mutation singleMut = new AAMutation(getGene(), getPosition(), aa);
			aaPcnts.add(new AAPercent(
				aa, pcnt,
				singleMut.isDRM(),
				singleMut.isUnusual(),
				singleMut.isApobecMutation(),
				singleMut.isApobecDRM()
			));
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
	public String getTriplet() { return compatTriplet; }

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
