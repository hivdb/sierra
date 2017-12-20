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

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.stanford.hivdb.utilities.JdbcDatabase;
import edu.stanford.hivdb.utilities.Cachable;

/**
 * There are two public methods: getHighestMutPrevalence, unusualMutations
 *
 */
public class UnusualMutations {
	@Cachable.CachableField
	private static Map<GenePosition, Map<Character, Double>> genePosAAPcnt = new LinkedHashMap<>();
	@Cachable.CachableField
	private static Map<GenePosition, Map<Character, Boolean>> unusualMuts = new LinkedHashMap<>();

	static {
		Cachable.setup(UnusualMutations.class, () -> {
			try {
				populateMutPrevalence();
			} catch (SQLException e) {
				throw new ExceptionInInitializerError(e);
			}
		});
	}

	public static Map<GenePosition, Map<Character, Boolean>> getUnusualMuts() {
		return unusualMuts;
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

		Map<Character, Boolean> empty = Collections.emptyMap();
		for (char aaChar : aaString.toCharArray()) {
			if (unusualMuts
					.getOrDefault(gpos, empty)
					.getOrDefault(aaChar, false)) {
				return true;
			}
		}
		return false;
	}


	// Receives a single amino acid at a position. Returns prevalence
	// TODO: use Gene object
	private static Double getMutPrevalence(GenePosition gpos, char aa) {
		return genePosAAPcnt
			.getOrDefault(gpos, new LinkedHashMap<>())
			.getOrDefault(aa, 0.0);
	}


	// Populate the Map genePosAAPcnts using tblMutPrevalences in HIVDBScores
	// For now most positions do not have entries for insertions and deletions none have entries for 'X'.
	//   These are assigned a prevalence of 0.0
	// TODO use Gene object
	// TODO add in prevalences for known indels
	private static void populateMutPrevalence() throws SQLException{
		final JdbcDatabase db = JdbcDatabase.getDefault();
		String sqlStatement =
			"SELECT Gene, Pos, AA, Pcnt, Unusual FROM tblMutPrevalences " +
			"ORDER BY Gene, Pos, AA";
		db.iterate(sqlStatement, rs -> {
			Gene gene = Gene.valueOf(rs.getString("Gene"));
			int pos = rs.getInt("Pos");
			GenePosition gpos = new GenePosition(gene, pos);

			String aa = rs.getString("AA");
			aa = aa.replace("#","_");
			aa = aa.replace("~", "-");
			char aaChar = aa.charAt(0);

			double pcnt = rs.getDouble("Pcnt");
			boolean unusual = rs.getString("Unusual").equals("T");

			genePosAAPcnt.putIfAbsent(gpos, new LinkedHashMap<>());
			genePosAAPcnt.get(gpos).put(aaChar, pcnt);

			unusualMuts.putIfAbsent(gpos,  new LinkedHashMap<>());
			unusualMuts.get(gpos).put(aaChar, unusual);
			return null;
		});
	}

}
