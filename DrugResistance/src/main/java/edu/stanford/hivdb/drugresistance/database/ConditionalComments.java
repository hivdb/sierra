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

package edu.stanford.hivdb.drugresistance.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.reflect.TypeToken;

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.Cachable;
import edu.stanford.hivdb.utilities.JdbcDatabase;
import edu.stanford.hivdb.utilities.Json;

public class ConditionalComments {

	private static final String WILDCARD_REGEX = "\\$listMutsIn\\{.+?\\}";

	public static enum ConditionType {
		MUTATION, DRUGLEVEL
	}

	public static class ConditionalComment {
		final private String commentName;
		final private DrugClass drugClass;
		final private ConditionType conditionType;
		final private Map<String, Object> conditionValue;
		final private String comment;

		private ConditionalComment(
				String commentName, DrugClass drugClass,
				ConditionType conditionType,
				Map<String, Object> conditionValue, String comment) {
			this.commentName = commentName;
			this.drugClass = drugClass;
			this.conditionType = conditionType;
			this.conditionValue = conditionValue;
			this.comment = comment;
		}

		public Gene getMutationGene() {
			return Gene.valueOf((String) conditionValue.get("gene"));
		}

		public int getMutationPosition() {
			return ((Double) conditionValue.get("pos")).intValue();
		}

		public String getMutationAAs() {
			return (String) conditionValue.get("aas");
		}

		private Map<Drug, List<Integer>> getDrugLevels() {
			Map<Drug, List<Integer>> drugLevels = new LinkedHashMap<>();
			if (conditionValue.containsKey("and")) {
				for (Object dlevel : ((List<?>) conditionValue.get("and"))) {
					Map<?, ?> dlevelMap = (Map<?, ?>) dlevel;
					Drug drug = Drug.valueOf((String) dlevelMap.get("drug"));
					List<Integer> levels =
						((List<?>) dlevelMap.get("levels"))
						.stream().map(i -> ((Double) i).intValue())
						.collect(Collectors.toList());
					drugLevels.put(drug, levels);
				}
			}
			else {
				Drug drug = Drug.valueOf((String) conditionValue.get("drug"));
				List<Integer> levels =
					((List<?>) conditionValue.get("levels"))
					.stream().map(i -> ((Double) i).intValue())
					.collect(Collectors.toList());
				drugLevels.put(drug, levels);
			}
			return drugLevels;
		}

		public String getName() { return commentName; }
		public String getText() { return comment; }
		public DrugClass getDrugClass() { return drugClass; }
		public ConditionType getConditionType() { return conditionType; }
		public Gene getGene() { return drugClass.gene(); }
	}

	public static class BoundComment {
		final private Gene gene;
		final private DrugClass drugClass;
		final private String commentName;
		final private CommentType commentType;
		final private String comment;
		final private Collection<String> highlightText;
		final private Mutation mutation;

		private BoundComment(
				String commentName, DrugClass drugClass, CommentType commentType,
				String comment, Collection<String> highlightText,
				Mutation mutation) {
			this.gene = drugClass.gene();
			this.drugClass = drugClass;
			this.commentName = commentName;
			this.commentType = commentType;
			this.comment = comment;
			this.highlightText = highlightText;
			this.mutation = mutation;
		}

		public String getName() { return commentName; }
		public CommentType getType() { return commentType; }
		public String getText() { return comment; }
		public Collection<String> getHighlightText() { return highlightText; }
		public Mutation getBoundMutation() { return mutation; }
		public Gene getGene() { return gene; }
		public DrugClass drugClass() { return drugClass; }

		@Deprecated
		public String getConsensus() {
			if (mutation != null) {
				return mutation.getConsensus();
			}
			return null;
		}

		@Deprecated
		public String getTriggeredAAs() {
			if (mutation != null) {
				return mutation.getAAs();
			}
			return null;
		}
	}

	@Cachable.CachableField
	private static List<ConditionalComment> conditionalComments;

	static {
		Cachable.setup(ConditionalComments.class, () -> {
			try {
				populateComments();
			} catch (SQLException e) {
				throw new ExceptionInInitializerError(e);
			}
		});
	}

	private static BoundComment findMutationComment(
			Gene gene, MutationSet mutations, ConditionalComment cc) {
		Gene ccgene = cc.getMutationGene();
		if (!ccgene.equals(gene)) {
			// skip if it's other gene
			return null;
		}

		int pos = cc.getMutationPosition();
		Mutation mut = mutations.get(gene, pos);
		if (mut == null) {
			// skip if there's no mutation for current condition
			return null;
		}

		String aas = cc.getMutationAAs();
		if (mut.isInsertion()) {
			if (!aas.contains("_")) {
				// skip if the corresponding mutation is insertion
				// but not insertion in current condition
				return null;
			}
			// remove details
			mut = new Mutation(gene, pos, '_');
		}
		else {
			aas = aas.replace("_", "");
			if (!mut.containsSharedAA(aas)) {
				return null;
			}
			mut = mut.intersectsWith(new Mutation(gene, pos, aas));
		}
		List<String> highlight = new ArrayList<>();
		highlight.add(mut.getHumanFormat());

		return new BoundComment(
			cc.commentName, cc.drugClass,
			CommentType.fromMutType(mut.getPrimaryType()),
			cc.comment.replaceAll(WILDCARD_REGEX, mut.getHumanFormat()),
			highlight,
			mut
		);
	}

	private static BoundComment findDrugLevelComment(
			GeneDR geneDR, ConditionalComment cc) {
		Map<Drug, List<Integer>> drugLevels = cc.getDrugLevels();
		for (Map.Entry<Drug, List<Integer>> e : drugLevels.entrySet()) {
			Integer level = geneDR.getDrugLevel(e.getKey());
			if (!e.getValue().contains(level)) {
				return null;
			}
		}
		Set<String> highlights = drugLevels.keySet().stream()
			.map(d -> d.getDisplayAbbr())
			.collect(Collectors.toSet());
		highlights.addAll(
			drugLevels.keySet().stream()
			.map(d -> d.name())
			.collect(Collectors.toSet())
		);

		return new BoundComment(
			cc.commentName, cc.drugClass,
			CommentType.Dosage,
			cc.comment,
			highlights,
			null
		);
	}

	public static List<BoundComment> getComments(GeneDR geneDR) {
		Gene gene = geneDR.getGene();
		MutationSet mutations = geneDR.getMutations();
		List<BoundComment> comments = new ArrayList<>();
		for (ConditionalComment cc : conditionalComments) {
			BoundComment comment = null;
			if (cc.conditionType == ConditionType.MUTATION) {
				comment = findMutationComment(gene, mutations, cc);
			}
			else if (cc.conditionType == ConditionType.DRUGLEVEL) {
				comment = findDrugLevelComment(geneDR, cc);
			}
			if (comment != null) {
				comments.add(comment);
			}
		}
		return comments;
	}

	public static List<BoundComment> getComments(Mutation mutation) {
		Gene gene = mutation.getGene();
		MutationSet mutations = new MutationSet(mutation);
		List<BoundComment> comments = new ArrayList<>();
		for (ConditionalComment cc : conditionalComments) {
			BoundComment comment = null;
			if (cc.conditionType == ConditionType.MUTATION) {
				comment = findMutationComment(gene, mutations, cc);
			}
			if (comment != null) {
				comments.add(comment);
			}
		}
		return comments;
	}

	public static List<ConditionalComment> getAllComments() {
		return Collections.unmodifiableList(conditionalComments);
	}


	/**
	 * Populate comments from HIVDB_Results database to static variable.
	 */
	private static void populateComments() throws SQLException {

		final JdbcDatabase db = JdbcDatabase.getResultsDB();

		// TODO: The version is hard-coded here.
		final String sqlStatement =
			"SELECT CommentName, DrugClass, ConditionType, ConditionValue, Comment " +
			"FROM tblConditionalCommentsWithVersions WHERE Version=? " +
			"ORDER BY CommentName";

		conditionalComments = db.iterate(sqlStatement, rs -> {
			String name = rs.getString("CommentName");
			DrugClass drugClass = DrugClass.valueOf(rs.getString("DrugClass"));
			ConditionType type = ConditionType.valueOf(rs.getString("ConditionType"));
			Map<String, Object> value = Json.loads(
				rs.getString("ConditionValue"),
				new TypeToken<Map<String, Object>>(){}.getType());
			String commentText = rs.getString("Comment");
			return new ConditionalComment(name, drugClass, type, value, commentText);
		}, HivdbVersion.getLatestVersion().name());
	}

}
