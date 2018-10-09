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

package edu.stanford.hivdb.mutations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.stanford.hivdb.utilities.Cachable;

/**
 * There are two public methods: getHighestMutPrevalence, unusualMutations
 *
 */
public class UnusualMutations {
	
	public static class AminoAcidPercent {
		public Gene gene;
		public Integer position;
		public Character aa;
		public Double percent;
		public Integer count;
		public Integer total;
		public String reason;
		public Boolean isAPOBEC;
		public Boolean isUsual;
		
		public GenePosition getGenePosition() {
			return new GenePosition(gene, position);
		}
	}
	
	@Cachable.CachableField
	private static List<AminoAcidPercent> aminoAcidPcnts = new ArrayList<>();

	private static Map<GenePosition, Map<Character, AminoAcidPercent>> aminoAcidPcntMap = new HashMap<>();

	static {
		Cachable.setup(UnusualMutations.class);

		for (AminoAcidPercent aaPcnt : aminoAcidPcnts) {
			GenePosition gp = aaPcnt.getGenePosition();
			aminoAcidPcntMap.putIfAbsent(gp, new LinkedHashMap<>());
			aminoAcidPcntMap.get(gp).put(aaPcnt.aa, aaPcnt);
		}
	}

	public static Map<GenePosition, Map<Character, Boolean>> getUnusualMuts() {
		Map<GenePosition, Map<Character, Boolean>> unusualMuts = new LinkedHashMap<>();
		for (AminoAcidPercent aaPcnt : aminoAcidPcnts) {
			GenePosition gp = aaPcnt.getGenePosition();
			unusualMuts.putIfAbsent(gp, new LinkedHashMap<>());
			unusualMuts.get(gp).put(aaPcnt.aa, aaPcnt.isUsual);
		}
		return unusualMuts;
	}
	
	public static List<AminoAcidPercent> getAminoAcidPercents() {
		return aminoAcidPcnts;
	}
	
	public static List<AminoAcidPercent> getAminoAcidPercents(Gene gene) {
		return (aminoAcidPcnts
				.stream().filter(aap -> aap.gene == gene)
				.collect(Collectors.toList()));
	}

	public static List<AminoAcidPercent> getAminoAcidPercents(Gene gene, int pos) {
		return new ArrayList<>(aminoAcidPcntMap.get(new GenePosition(gene, pos)).values());
	}

	public static AminoAcidPercent getAminoAcidPercent(Gene gene, int pos, char aa) {
		return aminoAcidPcntMap.get(new GenePosition(gene, pos)).get(aa);
	}

	/**
	 * Returns the highest HIVDB mutation prevalence associated with each of
	 * the AA in a mixture. Consensus AAs are ignored. If a stop codon is
	 * present and there is one or more other AAs, the it is ignored.
	 *
	 * @param mut
	 * @return Double mutation prevalence
	 */
	public static Double getHighestMutPrevalence(Mutation mut) {
		Double prevalence = 0.0;
		String aas = mut.getAAs();
		String cons = mut.getConsensus();

		// ignore consensus
		if (aas.contains(cons)) {
			aas = aas.replace(cons, "");
		}

		// ignore stop codon when there's one or more other AAs
		if (aas.contains("*") && aas.length() > 1) {
			aas = aas.replace("*", "");
		}

		for (char aa : aas.toCharArray()) {
			double aaPrevalence = getMutPrevalence(mut.getGenePosition(), aa);
			prevalence = Math.max(prevalence, aaPrevalence);
		}
		return prevalence;
	}

	// Receives a single mutation. Looks up all of the non-consensus AA's in the HashMap usualMuts
	// If any of the AAs are not in the HashMap, the mutation is considered to have an unusual mutation
	public static boolean containsUnusualMut(Mutation mut) {
		GenePosition gpos = mut.getGenePosition();
		String aaString = mut.getAAsWithoutConsensus();
		if (mut.isInsertion()) {
			aaString = "_";
		}
		if (mut.isDeletion()) {
			aaString = "-";
		}

		for (char aaChar : aaString.toCharArray()) {
			AminoAcidPercent aaPcnt = aminoAcidPcntMap.get(gpos).get(aaChar);
			if (aaPcnt != null && !aaPcnt.isUsual) {
				return true;
			}
		}
		return false;
	}


	// Receives a single amino acid at a position. Returns prevalence
	private static Double getMutPrevalence(GenePosition gpos, char aa) {
		AminoAcidPercent aaPcnt = aminoAcidPcntMap.get(gpos).get(aa);
		if (aaPcnt != null) {
			return aaPcnt.percent * 100;
		}
		return 0.0;
	}

}
