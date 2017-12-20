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

package edu.stanford.hivdb.drugresistance.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.primitives.Chars;

import edu.stanford.hivdb.drugresistance.database.MutationComboScores;
import edu.stanford.hivdb.drugresistance.database.MutationComboScores.ComboScore;
import edu.stanford.hivdb.drugresistance.database.MutationScores;
import edu.stanford.hivdb.drugresistance.database.MutationScores.MutScore;
import edu.stanford.hivdb.drugresistance.scripts.HivdbLevelDefinitions;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.GenePosition;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;

public class FastHivdb {

	private static class PositionNode {
		public Map<Character, AANode> childAAs;

		public PositionNode() {
			childAAs = new LinkedHashMap<>();
		}
	}

	private static class AANode {
		public Map<Integer, PositionNode> childPositions;
		public boolean isLeaf;
		public Map<Drug, Double> scores;

		public AANode() {
			childPositions = new LinkedHashMap<>();
			this.scores = new EnumMap<>(Drug.class);
		}

		public void setLeaf(Rule rule) {
			isLeaf = true;
			this.scores.put(rule.drug, rule.score);
		}
	}

	private static class Rule {
		public List<Integer> sortedPositions;
		public List<String> sortedAAs;
		public Drug drug;
		public Double score;

		public Rule(
				List<Integer> sortedPositions,
				List<String> sortedAAs, Drug drug, Double score) {
			this.sortedPositions = sortedPositions;
			this.sortedAAs = sortedAAs;
			this.drug = drug;
			this.score = score;
		}

	}

	private static Map<Gene, Map<Integer, PositionNode>> geneRuleTrees;

	private static void buildTreeInternal(
			List<Integer> sortedPositions, List<String> sortedAAs,
			Rule rule, Map<Integer, PositionNode> curMap) {
		boolean isLeaf = false;
		if (sortedPositions.size() == 1) {
			isLeaf = true;
		}
		sortedPositions = new ArrayList<>(sortedPositions);
		sortedAAs = new ArrayList<>(sortedAAs);
		int pos = sortedPositions.remove(0);
		String aas = sortedAAs.remove(0);
		curMap.putIfAbsent(pos, new PositionNode());
		PositionNode node = curMap.get(pos);
		for (Character aa : aas.toCharArray()) {
			node.childAAs.putIfAbsent(aa, new AANode());
			AANode childAA = node.childAAs.get(aa);
			if (isLeaf) {
				childAA.setLeaf(rule);
			} else {
				buildTreeInternal(sortedPositions, sortedAAs, rule, childAA.childPositions);
			}
		}
	}

	private static Map<Integer, PositionNode> buildTree(List<Rule> rules) {
		Map<Integer, PositionNode> root = new LinkedHashMap<>();
		for (Rule rule : rules) {
			buildTreeInternal(rule.sortedPositions, rule.sortedAAs, rule, root);
		}
		return root;
	}

	static {
		Map<Gene, List<Rule>> rulesByGenes = new EnumMap<>(Gene.class);
		for (Gene gene : Gene.values()) {
			rulesByGenes.put(gene, new ArrayList<>());
		}
		for (MutScore mutScore : MutationScores.getMutScores()) {
			List<Integer> pos = new ArrayList<>();
			pos.add(mutScore.pos);
			List<String> aas = new ArrayList<>();
			aas.add("" + mutScore.aa);
			Rule rule = new Rule(pos, aas, mutScore.drug, mutScore.score);
			rulesByGenes.get(mutScore.gene).add(rule);
		}
		for (ComboScore comboScore : MutationComboScores.getCombinationScores()) {
			MutationSet muts = new MutationSet(comboScore.gene, comboScore.rule);
			List<Integer> pos = new ArrayList<>();
			List<String> aas = new ArrayList<>();
			for (Mutation mut : muts) {
				pos.add(mut.getPosition());
				aas.add(mut.getAAs());
			}
			Rule rule = new Rule(pos, aas, comboScore.drug, comboScore.score);
			rulesByGenes.get(comboScore.gene).add(rule);
		}
		geneRuleTrees = rulesByGenes.entrySet()
			.stream()
			.collect(Collectors.toMap(
				e -> e.getKey(),
				e -> buildTree(e.getValue()),
				(a, b) -> a,
				LinkedHashMap::new
			));
	}

	private static void calcScores(
			List<Integer> sortedPositions,
			List<Set<Character>> sortedAAs,
			Map<Integer, PositionNode> ruleTree,
			Map<Drug, Map<String, Double>> scores,
			Map<Drug, Map<String, MutationSet>> triggeredMuts,
			String prevKey,
			MutationSet prevMuts,
			Gene gene) {
		if (sortedPositions.isEmpty()) {
			return;
		}
		int pos = sortedPositions.remove(0);
		Set<Character> aas = sortedAAs.remove(0);
		if (!ruleTree.containsKey(pos)) {
			calcScores(
				sortedPositions, sortedAAs, ruleTree, scores,
				triggeredMuts, prevKey, prevMuts, gene);
			return;
		}
		PositionNode node = ruleTree.get(pos);
		for (Character aa : aas) {
			if (!node.childAAs.containsKey(aa)) {
				continue;
			}
			AANode childAA = node.childAAs.get(aa);
			String curKey = prevKey + "+" + pos;
			MutationSet curMuts = prevMuts.mergesWith(new Mutation(gene, pos, "" + aa));

			if (childAA.isLeaf) {
				for (Drug drug : childAA.scores.keySet()) {
					scores.putIfAbsent(drug, new LinkedHashMap<>());
					triggeredMuts.putIfAbsent(drug, new LinkedHashMap<>());
					Double score = scores.get(drug)
						.getOrDefault(curKey, Double.NEGATIVE_INFINITY);
					Double newScore = childAA.scores.getOrDefault(drug, .0);
					if (newScore > score) {
						scores.get(drug).put(curKey, newScore);
						triggeredMuts.get(drug).put(curKey, curMuts);
					}
				}
			}
			if (!childAA.childPositions.isEmpty()) {
				calcScores(
					new ArrayList<>(sortedPositions),
					new ArrayList<>(sortedAAs),
					childAA.childPositions,
					scores, triggeredMuts,
					curKey, curMuts, gene
				);
			}
		}
		if (!sortedPositions.isEmpty()) {
			calcScores(
				sortedPositions, sortedAAs, ruleTree, scores,
				triggeredMuts, prevKey, prevMuts, gene);
		}
	}

	private final Gene gene;
	private final Map<Drug, Map<String, Double>> separatedScores;
	private final Map<Drug, Map<String, MutationSet>> triggeredMuts;

	public FastHivdb (Gene gene, MutationSet mutations) {
		this.gene = gene;
		separatedScores = new EnumMap<>(Drug.class);
		triggeredMuts = new EnumMap<>(Drug.class);
		List<Integer> sortedPositions = new ArrayList<>();
		List<Set<Character>> sortedAAs = new ArrayList<>();
		for (GenePosition gp : mutations.getPositions()) {
			Set<Mutation> posMuts = mutations.get(gp.gene, gp.position);
			sortedPositions.add(gp.position);
			Set<Character> setAAs = new TreeSet<>();
			for (Mutation mut : posMuts) {
				if (mut.isInsertion()) {
					setAAs.add('_');
				}
				else if (mut.isDeletion()) {
					setAAs.add('-');
				}
				else {
					setAAs.addAll(Chars.asList(mut.getAAs().toCharArray()));
				}
			}
			sortedAAs.add(setAAs);
		}
		calcScores(
			sortedPositions, sortedAAs, geneRuleTrees.get(gene),
			separatedScores, triggeredMuts, "", new MutationSet(), gene);
	}

	public Gene getGene() {
		return this.gene;
	}

	public String getAlgorithmName() {
		return "HIVDB";
	}

	public int getDrugLevel(Drug drug) {
		double score = getTotalScore(drug).intValue();
		int level;
		if (score >= 60) 	{
			level = 5;
		} else if (score >= 30) {
			level = 4;
		} else if (score >= 15) {
			level = 3;
		} else if (score >= 10) {
			level = 2;
		} else {
			level = 1;
		}
		return level;
	}

	public String getDrugLevelText(Drug drug) {
		return HivdbLevelDefinitions.getByNumber(getDrugLevel(drug)).getDescription();
	}

	public String getDrugLevelSir(Drug drug) {
		return HivdbLevelDefinitions.getByNumber(getDrugLevel(drug)).getSir();
	}

	public Double getTotalScore(Drug drug) {
		if (separatedScores.containsKey(drug)) {
			return separatedScores.get(drug).values()
				.stream().mapToDouble(Double::doubleValue).sum();
		} else {
			return .0;
		}
	}

	public Map<DrugClass, Map<Drug, Double>> getDrugClassTotalDrugScores() {
		Map<DrugClass, Map<Drug, Double>> result = new EnumMap<>(DrugClass.class);
		for (DrugClass drugClass : gene.getDrugClasses()) {
			result.put(drugClass, new EnumMap<>(Drug.class));
			Map<Drug, Double> drugResult = result.get(drugClass);
			for (Drug drug : drugClass.getDrugsForHivdbTesting()) {
				drugResult.put(drug, getTotalScore(drug));
			}
		}
		return result;
	}

	public Map<Drug, Double> getDrugClassTotalDrugScores(DrugClass drugClass) {
		return getDrugClassTotalDrugScores().get(drugClass);
	}

	public MutationSet getTriggeredMutations() {
		MutationSet result = new MutationSet();
		for (Map<String, MutationSet> partials : triggeredMuts.values()) {
			for (MutationSet muts : partials.values()) {
				result = result.mergesWith(muts);
			}
		}
		return result;
	}

	public MutationSet getTriggeredMutations(DrugClass drugClass) {
		MutationSet result = new MutationSet();
		for (Drug drug : triggeredMuts.keySet()) {
			if (drug.getDrugClass() != drugClass) {
				continue;
			}
			Map<String, MutationSet> partials = triggeredMuts.get(drug);
			for (MutationSet muts : partials.values()) {
				result = result.mergesWith(muts);
			}
		}
		return result;

	}

	public Map<DrugClass, Map<Drug, Map<Mutation, Double>>> getDrugClassDrugMutScores() {
		Map<DrugClass, Map<Drug, Map<Mutation, Double>>> result = new EnumMap<>(DrugClass.class);
		for (DrugClass drugClass : gene.getDrugClasses()) {
			result.put(drugClass, new EnumMap<>(Drug.class));
			Map<Drug, Map<Mutation, Double>> drugClassResult = result.get(drugClass);
			for (Drug drug : drugClass.getDrugsForHivdbTesting()) {
				drugClassResult.put(drug, new LinkedHashMap<>());
				Map<Mutation, Double> drugResult = drugClassResult.get(drug);
				Map<String, Double> drugScores = separatedScores.getOrDefault(drug, Collections.emptyMap());
				for (String key : drugScores.keySet()) {
					if (StringUtils.countMatches(key, "+") > 1) {
						continue;
					}
					Mutation mut = triggeredMuts.get(drug).get(key).first();
					drugResult.put(mut, drugScores.get(key));
				}
			}
		}
		return result;
	}

	public Map<DrugClass, Map<Drug, Map<MutationSet, Double>>> getDrugClassDrugComboMutScores() {
		Map<DrugClass, Map<Drug, Map<MutationSet, Double>>> result = new EnumMap<>(DrugClass.class);
		for (DrugClass drugClass : gene.getDrugClasses()) {
			result.put(drugClass, new EnumMap<>(Drug.class));
			Map<Drug, Map<MutationSet, Double>> drugClassResult = result.get(drugClass);
			for (Drug drug : drugClass.getDrugsForHivdbTesting()) {
				drugClassResult.put(drug, new LinkedHashMap<>());
				Map<MutationSet, Double> drugResult = drugClassResult.get(drug);
				Map<String, Double> drugScores = separatedScores.getOrDefault(drug, Collections.emptyMap());
				for (String key : drugScores.keySet()) {
					if (StringUtils.countMatches(key, "+") == 1) {
						continue;
					}
					MutationSet muts = triggeredMuts.get(drug).get(key);
					drugResult.put(muts, drugScores.get(key));
				}
			}
		}
		return result;

	}

}
