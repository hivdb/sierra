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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.fstrf.stanfordAsiInterpreter.resistance.definition.Gene;
import org.fstrf.stanfordAsiInterpreter.resistance.definition.LevelDefinition;
import org.fstrf.stanfordAsiInterpreter.resistance.evaluate.EvaluatedCondition;
import org.fstrf.stanfordAsiInterpreter.resistance.evaluate.EvaluatedDrug;
import org.fstrf.stanfordAsiInterpreter.resistance.evaluate.EvaluatedDrugClass;
import org.fstrf.stanfordAsiInterpreter.resistance.evaluate.EvaluatedGene;
import org.fstrf.stanfordAsiInterpreter.resistance.grammar.AsiGrammarAdapter.ScoredItem;
import org.fstrf.stanfordAsiInterpreter.resistance.grammar.MutationComparator;
import org.fstrf.stanfordAsiInterpreter.resistance.grammar.StringMutationComparator;
import org.fstrf.stanfordAsiInterpreter.resistance.xml.XmlAsiTransformer;

import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationMapUtils;
import edu.stanford.hivdb.mutations.MutationMapUtils.SortOrder;
import edu.stanford.hivdb.mutations.MutationSet;

abstract public class AsiBase implements Asi {

	private static volatile Map<String, Map<String, Gene>>
		cachedGeneMaps = new ConcurrentHashMap<>();

	// Use canonical name of HIVDB Gene class since
	// FSTRFAsi code has its own Gene class we call asiGene
	protected final edu.stanford.hivdb.mutations.Gene gene;
	protected final List<String> mutations;

	// Objects created by the ASI code.
	protected org.fstrf.stanfordAsiInterpreter.resistance.definition.Gene asiGene;
	protected EvaluatedGene evaluatedGene;

	protected MutationSet triggeredMutations;
	protected Map<DrugClass, MutationSet>
		triggeredMutationsByDrugClass = new EnumMap<>(DrugClass.class);

	// drugLevel: 1 to 5 corresponding to drugLevel
	// drugLevelText: Susceptible, Potential Low Level Res., Low-Level Res, Intermediate Res, High-level Res.
	// drugLevelSir: S (Susceptible), I (Intermediate), R (Resistance)
	protected Map<Drug, Integer> drugLevel = new EnumMap<>(Drug.class);
	protected Map<Drug, String> drugLevelText = new EnumMap<>(Drug.class);
	protected Map<Drug, String> drugLevelSir = new EnumMap<>(Drug.class);

	protected Map<Drug, Double> totalDrugScores = new EnumMap<>(Drug.class);
	protected Map<Drug, Map<Mutation, Double>> drugMutScores = new EnumMap<>(Drug.class);
	protected Map<Drug, Map<MutationSet, Double>> drugComboMutScores = new EnumMap<>(Drug.class);

	// List of rules that triggered for each drug mapped to the level of the corresponding action
	protected Map<Drug, Map<String, String>> triggeredDrugRules = new EnumMap<>(Drug.class);

	protected final static Map<String, Gene> readXML(String resourcePath) {
		if (!cachedGeneMaps.containsKey(resourcePath)) {
			InputStream resource =
				AsiBase.class.getClassLoader().getResourceAsStream(resourcePath);
			cachedGeneMaps.put(resourcePath, readXML(resource));
		}
		return cachedGeneMaps.get(resourcePath);
	}

	protected final static Map<String, Gene> readXML(InputStream resource) {
		Map<?, ?> geneMap;
		XmlAsiTransformer transformer = new XmlAsiTransformer(true);
		try {
			geneMap = transformer.transform(resource);
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
		return Collections.unmodifiableMap(geneMap
			.entrySet()
			.stream()
			.collect(Collectors.toMap(
				e -> (String) e.getKey(),
				e -> (Gene) e.getValue()
			)));
	}

	private AsiBase (
			final edu.stanford.hivdb.mutations.Gene submittedGene,
			final MutationSet mutations,
			final Map<String, Gene> geneMap) {
		this.gene = submittedGene;
		this.mutations = mutations.toASIFormat();

		asiGene = geneMap.get(submittedGene.getName());

		MutationComparator mutationComparator = new StringMutationComparator(false);
		if (!mutationComparator.areMutationsValid(this.mutations)){
			throw new RuntimeException("Invalid list of mutations: " + this.mutations.toString());
		}

		try {
			this.evaluatedGene = asiGene.evaluate(this.mutations, mutationComparator);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		populateEvaluatedResults();
	}

	/**
	 * Instantiated by a gene and list of mutations in a sequence.
	 * asiGene is an object created by the XMLAsiTransformer that contains all of the
	 *   specifications and rules in the algorithm for the submitted gene
	 * evaluatedGene is an object that contains all of the results obtained by applying
	 *   the asiGene rules to the submitted mutations
	 *
	 * @param submittedGene
	 * @param mutations
	 * @param resourcePath
	 */
	public AsiBase(
			final edu.stanford.hivdb.mutations.Gene submittedGene,
			final MutationSet mutations,
			final String resourcePath) {
		this(submittedGene, mutations, readXML(resourcePath));
	}

	public AsiBase(
			final edu.stanford.hivdb.mutations.Gene submittedGene,
			final MutationSet mutations,
			final InputStream resource) {
		this(submittedGene, mutations, readXML(resource));
	}

	@Override
	public edu.stanford.hivdb.mutations.Gene getGene() {
		return this.gene;
	}

	/**
	 * @param drug
	 * @return
	 */
	@Override
	public final int getDrugLevel(Drug drug) {
		if (!drugLevel.containsKey(drug)) {
			return 1;
		} else {
			return drugLevel.get(drug);
		}
	}

	@Override
	public String getDrugLevelText(Drug drug) {
		if (!drugLevelText.containsKey(drug)) {
			return "Susceptible";
		} else {
			return drugLevelText.get(drug);
		}
	}

	@Override
	public final String getDrugLevelSir(Drug drug) {
		if (!drugLevelSir.containsKey(drug)) {
			return "S";
		} else {
			return drugLevelSir.get(drug);
		}
	}

	@Override
	public final Double getTotalScore(Drug drug) {
		if (!totalDrugScores.containsKey(drug)) {
			return .0;
		} else {
			return totalDrugScores.get(drug);
		}
	}

	private final <T> Map<DrugClass, Map<Drug, T>> groupingByDrugClass(Map<Drug, T> input) {
		return input
			.entrySet()
			.stream()
			.collect(Collectors.groupingBy(
				e -> e.getKey().getDrugClass(),
				() -> new EnumMap<>(DrugClass.class),
				Collectors.toMap(
					e -> e.getKey(),
					e -> e.getValue(),
					(v1, v2) -> v1,
					() -> new EnumMap<>(Drug.class)
				)
			));
	}

	private final <T> Map<Drug, T> filterByDrugClass(Map<Drug, T> input, DrugClass drugClass) {
		return input
			.entrySet()
			.stream()
			.filter(e -> e.getKey().getDrugClass() == drugClass)
			.collect(Collectors.toMap(
				e -> e.getKey(),
				e -> e.getValue()
			));
	}

	/**
	 * Data structure:
	 *   DrugClass => Drug => totalScore (obtained from adding up the individual and combination scores
	 *   for each mutation in a sequence
	 *
	 * @return Map: DrugClass => Drug => totalScore
	 */
	@Override
	public final Map<DrugClass, Map<Drug, Double>> getDrugClassTotalDrugScores() {
		return groupingByDrugClass(totalDrugScores);
	}

	@Override
	public final Map<Drug, Double> getDrugClassTotalDrugScores(DrugClass drugClass) {
		return filterByDrugClass(totalDrugScores, drugClass);
	}

	@Override
	public final MutationSet getTriggeredMutations() {
		if (triggeredMutations == null) {
			MutationSet allMuts = new MutationSet();
			for (Map<Mutation, Double> mutScores : drugMutScores.values()) {
				allMuts = allMuts.mergesWith(mutScores.keySet());
			}
			for (Map<MutationSet, Double> comboMutScores : drugComboMutScores.values()) {
				for (MutationSet muts : comboMutScores.keySet()) {
					allMuts = allMuts.mergesWith(muts);
				}
			}
			allMuts.displayAmbiguities();
			triggeredMutations = allMuts;
		}
		return triggeredMutations;
	}

	@Override
	public final MutationSet getTriggeredMutations(DrugClass drugClass) {
		if (!triggeredMutationsByDrugClass.containsKey(drugClass)) {
			MutationSet allMuts = new MutationSet();
			for (Drug drug : drugClass.getDrugsForHivdbTesting()) {
				allMuts = allMuts.mergesWith(drugMutScores
					.getOrDefault(drug, new HashMap<>()).keySet());
				for (MutationSet muts : drugComboMutScores
					 .getOrDefault(drug, new HashMap<>()).keySet()) {
					allMuts = allMuts.mergesWith(muts);
				}
			}
			allMuts = allMuts.displayAmbiguities();
			triggeredMutationsByDrugClass.put(drugClass, allMuts);
		}
		return triggeredMutationsByDrugClass.get(drugClass);
	}

	/**
	 * This data map only has entries for Drugs with scored Mutations
	 * @return
	 */
	@Override
	public final Map<DrugClass, Map<Drug, Map<Mutation, Double>>> getDrugClassDrugMutScores() {
		return groupingByDrugClass(drugMutScores);
	}

	/**
	 * This data map only has entries for Drugs with scored Mutation Combinations
	 * @return
	 */
	@Override
	public final Map<DrugClass, Map<Drug, Map<MutationSet, Double>>> getDrugClassDrugComboMutScores() {
		return groupingByDrugClass(drugComboMutScores);
	}

	@Override
	public final Map<Drug, Map<String, String>> getTriggeredDrugRules() {
		return triggeredDrugRules;
	}

	@Override
	public final Map<Drug, Map<Mutation, Double>> getDrugMutScores() {
		return drugMutScores;
	}

	@Override
	public final Map<Drug, Map<MutationSet, Double>> getDrugComboMutScores() {
		return drugComboMutScores;
	}

	@Override
	public AsiDrugComparableResult getDrugComparableResult(Drug drug) {
		String sir = getDrugLevelSir(drug);
		String level = getDrugLevelText(drug);
		String explanation = getDrugExplanation(drug);
		return new AsiDrugComparableResult(sir, level, explanation);
	}

	protected String getDrugExplanation(Drug drug) {
		String explanation = "";
		Boolean hasMuts = false;
		String individualMuts = "";
		String comboMuts = "";
		Double totalScore = totalDrugScores.get(drug);

		Map<Mutation, Double> mutScores =
			drugMutScores.getOrDefault(drug, new HashMap<>());

		Map<MutationSet, Double> comboMutScores =
			drugComboMutScores.getOrDefault(drug, new HashMap<>());

		if (!mutScores.isEmpty()) {
			Map<Mutation, Double> mutScoresSortedByScore =
					MutationMapUtils.sortByComparator(mutScores, SortOrder.DESC);
			individualMuts = MutationMapUtils.printMutScoresAsInts(mutScoresSortedByScore);
			hasMuts = true;
		}
		if (!comboMutScores.isEmpty()) {
			Map<MutationSet, Double> comboMutsSortedByScore =
					MutationMapUtils.sortByComparator(comboMutScores, SortOrder.DESC);
			comboMuts = MutationMapUtils.printMutSetScoresAsInts(comboMutsSortedByScore);
			hasMuts = true;
		}

		if (hasMuts) {
			explanation = String.format(
				"Total score: %d\n%s %s",
				totalScore.intValue(), individualMuts, comboMuts);
		}
		else if (triggeredDrugRules.containsKey(drug)) {
			Map<String, String> rules = triggeredDrugRules.get(drug);
			List<String> explanationList = new ArrayList<>();
			for (String rule : rules.keySet()) {
				explanationList.add(String.format("%s (%s)",
					rule.replace("+", " + ").replace(",", ", "), rules.get(rule)));
			}
			explanation = String.join("\n", explanationList);
		} else {
			explanation = "No rules were triggered";
		}
		return explanation;
	}

	private final DrugClass convertDrugClass(EvaluatedDrugClass evalDrugClass) {
		String drugClassName = evalDrugClass.getDrugClass().getClassName();
		return DrugClass.getSynonym(drugClassName);
	}

	private final Drug convertDrug(EvaluatedDrug evalDrug) {
		String drugName = evalDrug.getDrug().toString();
		return Drug.getSynonym(drugName);
	}

	protected void scoreHandler(
			DrugClass drugClass, Drug drug, double score, MutationSet mutations) {
		// Populate map for individual mutation scores
		if (mutations.size() == 1) {
			Mutation scoredMut = mutations.first();
			drugMutScores.putIfAbsent(drug, new LinkedHashMap<Mutation, Double>());
			double drugMutScore = drugMutScores.get(drug).getOrDefault(scoredMut, -99.0);
			drugMutScores.get(drug).put(scoredMut, Math.max(score, drugMutScore));

		// Populate map for combination mutation scores
		} else {
			drugComboMutScores.putIfAbsent(drug, new LinkedHashMap<>());
			double drugComboMutScore = drugComboMutScores.get(drug).getOrDefault(mutations, -99.0);
			drugComboMutScores.get(drug).put(mutations, Math.max(score, drugComboMutScore));
		}
	}

	protected void susceptibilityHandler(
			DrugClass drugClass, Drug drug, String condition, String susceptibility) {
		triggeredDrugRules.putIfAbsent(drug, new LinkedHashMap<String, String>());
		triggeredDrugRules.get(drug).putIfAbsent(condition, susceptibility);
	}
	
	@Override
	public EvaluatedGene getEvaluatedGene() {
		return evaluatedGene;
	}

	// drugClasstotalDrugScores: DrugClass => Drug => totalScore
	// drugClassDrugMutScores: DrugClass => Drug => Mutation=> score
	// drugClassDrugComboMutScores DrugClass => Drug => Mutation combination (List<Mutation>) => score
	protected void populateEvaluatedResults() {
		for(Object drugClassObj : evaluatedGene.getEvaluatedDrugClasses()) {
			EvaluatedDrugClass evalDrugClass = (EvaluatedDrugClass) drugClassObj;
			DrugClass drugClass = convertDrugClass(evalDrugClass);

			for (Object drugObj : evalDrugClass.getEvaluatedDrugs()) {
				EvaluatedDrug evalDrug = (EvaluatedDrug) drugObj;
				Drug drug = convertDrug(evalDrug);
				if (drug == null) {
					// skip unknown drug
					continue;
				}

				LevelDefinition levelDef = evalDrug.getHighestLevelDefinition();

				int levelDefOrder = levelDef == null ? 1 : levelDef.getOrder();
				String levelDefText = levelDef == null ? "Susceptible" : levelDef.getText();
				String levelDefSir = levelDef == null ? "S" : levelDef.getSir();

				drugLevel.put(drug, levelDefOrder);
				drugLevelText.put(drug, levelDefText);
				drugLevelSir.put(drug, levelDefSir);

				for(Object condObj : evalDrug.getEvaluatedConditions()) {
					EvaluatedCondition evalCond = (EvaluatedCondition) condObj;
					Object o = evalCond.getEvaluator().getResult();
					if (o instanceof Double) {
						totalDrugScores.put(drug, (Double) o);
						for (Object scoredItemObj : evalCond.getEvaluator().getScoredItems()) {
							ScoredItem scoredItem = (ScoredItem) scoredItemObj;
							Set<?> muts = scoredItem.getMutations();
							MutationSet mutations = new MutationSet(
								gene, muts.stream()
								.map(m -> (String) m)
								.collect(Collectors.toSet()))
								.displayAmbiguities();
							this.scoreHandler(drugClass, drug, scoredItem.getScore(), mutations);
						}
					}
					else if (o instanceof Boolean) {
						String ruleCondition = evalCond
							.getRuleCondition().toString()
							.replaceAll("\\s+", " ");
						Boolean triggered = (Boolean) o;
						if (!triggered) {
							continue;
						}
						for (Object defObj : evalCond.getDefinitions()) {
							String ruleSusceptibilityText = "";
							if (defObj instanceof LevelDefinition) {
								LevelDefinition ruleLevelDefinition = (LevelDefinition) defObj;
								ruleSusceptibilityText = ruleLevelDefinition.getText();
							}
							this.susceptibilityHandler(
								drugClass, drug, ruleCondition, ruleSusceptibilityText);
						}
					}
				}
			}
		}
	}
}
