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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutType;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.utilities.JdbcDatabase;
import edu.stanford.hivdb.utilities.MyStringUtils;
import edu.stanford.hivdb.utilities.Cachable;


public class MutationTypePairs {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = LogManager.getLogger();

	@Cachable.CachableField
	private static List<MutationTypePair> mutationTypePairs;

	public static class MutationTypePair {
		public final Gene gene;
		public final DrugClass drugClass;
		public final Integer position;
		public final String consensus;
		public final String aas;
		public final MutType type;
		private final Boolean isUnusual;

		public MutationTypePair(
				final Gene gene, final DrugClass drugClass,
				final int position, final String aas,
				final MutType mutType, final Boolean isUnusual) {
			this.gene = gene;
			this.drugClass = drugClass;
			this.position = position;
			this.consensus = gene.getConsensus(position);
			this.aas = aas;
			this.type = mutType;
			this.isUnusual = isUnusual;
		}

		public Gene getGene() { return gene; }
		public DrugClass getDrugClass() { return drugClass; }
		public Integer getPosition() { return position; }
		public String getConsensus() { return consensus; }
		public String getTriggeredAAs() { return aas; }
		public MutType getType() { return type; }
		public boolean isUnusual() { return isUnusual; }

		public boolean isMutationMatched(Mutation mut) {
			String mutAAs = mut.getAAsWithoutConsensus();
			if (mut.isInsertion()) {
				// remove the insertion AAs which could interfere the intersection
				mutAAs = "_";
			}
			return
				mut.getGene() == gene &&
				mut.getPosition() == position &&
				MyStringUtils.hasSharedChar(mutAAs, aas);
		}

		public String getUniqueID() {
			String typeStr = type.toString();
			if (type == MutType.Major && drugClass == DrugClass.PI) {
				typeStr = "PIMajor";
			}
			else if (type == MutType.Accessory && drugClass == DrugClass.PI) {
				typeStr = "PIMinor";
			}
			return String.format(
				"%s_POS%d%s_%s", gene.toString(), position,
				AA.toASIFormat(aas), typeStr);
		}
	}

	static {
		Cachable.setup(MutationTypePairs.class, () -> {
			try {
				populateTypes();
			} catch (SQLException e) {
				throw new ExceptionInInitializerError(e);
			}
		});
	}

	public static List<MutationTypePair> getMutationTypes() {
		return mutationTypePairs;
	}

	/**
	 * Looks up mutation types for each given mutations.
	 *
	 * @param geneMuts
	 * @return Map<Mutation, List<MutationType>> Map of types for each mutation.
	 */
	public static Map<Mutation, List<MutType>> lookupByMutations(MutationSet geneMuts) {
		Map<Mutation, List<MutType>> r = new TreeMap<>();
		for (Mutation mut: geneMuts) {
			List<MutType> types = lookupByMutation(mut);
			if (types.size() == 0) {
				continue;
			}
			r.put(mut, types);
		}
		return r;
	}

	/**
	 * Looks up mutation types for each given mutations in a specified gene.
	 *
	 * @param gene
	 * @param mutations
	 * @return Map<Mutation, List<MutationType>>
	 */
	public static Map<Mutation, List<MutType>> lookupByMutations(Gene gene, MutationSet mutations) {
		MutationSet geneMuts = mutations.getGeneMutations(gene);
		return lookupByMutations(geneMuts);
	}

	/**
	 * Looks up mutation types for a single mutation.
	 *
	 * @param mut
	 * @return List<MutationType>, ordered by rank
	 */
	public static List<MutType> lookupByMutation(Mutation mut) {
		return mutationTypePairs
			.stream()
			.filter(mc -> mc.isMutationMatched(mut))
			.map(mc -> mc.type)
			.collect(Collectors.toList());
	}

	/**
	 * Looks up mutation types by a position.
	 *
	 * @param gene
	 * @param pos
	 * @return List<MutationType>
	 */
	public static List<MutType> lookupByPosition(Gene gene, int pos) {
		return mutationTypePairs
			.stream()
			.filter(mc -> mc.gene == gene && mc.position == pos)
			.map(mc -> mc.type)
			.collect(Collectors.toList());
	}

	/**
	 * Populate types from HIVDB_Results database to static variable.
	 */
	private static void populateTypes() throws SQLException {

		final JdbcDatabase db = JdbcDatabase.getResultsDB();

		// TODO: The version is hard-coded here.
		final String sqlStatement =
			"SELECT Gene, DrugClass, Pos, AAs, Type, IsUnusual " +
			"FROM tblMutationTypesWithVersions WHERE Version='V8_6' " +
			"ORDER BY Gene, DrugClass, Pos, " +
			"(CASE Type WHEN 'Major' THEN 0" +
			" WHEN 'Accessory' THEN 1" +
			" WHEN 'NRTI' THEN 0" +
			" WHEN 'NNRTI' THEN 1" +
			" ELSE 3 END), AAs";

		mutationTypePairs = db.iterate(sqlStatement, rs -> {
			Gene gene = Gene.valueOf(rs.getString("Gene"));
			DrugClass drugClass = DrugClass.valueOf(rs.getString("DrugClass"));
			int pos = rs.getInt("Pos");
			String aas = rs.getString("AAs");
			MutType mutType = MutType.valueOf(rs.getString("Type"));
			Boolean isUnusual = rs.getBoolean("isUnusual");
			aas = aas
				.replaceAll("#","_")
				.replaceAll("~", "-");
			return new MutationTypePair(
				gene, drugClass, pos, aas,
				mutType, isUnusual);
		});
	}
}
