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

package edu.stanford.hivdb.graphql;

import graphql.schema.*;
import static graphql.Scalars.*;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLCodeRegistry.newCodeRegistry;
import static graphql.schema.FieldCoordinates.coordinates;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.algorithm.SIREnum;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.mutations.Mutation;

import static edu.stanford.hivdb.graphql.DrugDef.oDrug;
import static edu.stanford.hivdb.graphql.DrugClassDef.oDrugClass;
import static edu.stanford.hivdb.graphql.DrugClassDef.oDrugClassEnum;
import static edu.stanford.hivdb.graphql.MutationDef.oMutation;
import static edu.stanford.hivdb.graphql.MutationDef.oMutationType;
import static edu.stanford.hivdb.graphql.HivdbVersionDef.oHivdbVersion;
import static edu.stanford.hivdb.graphql.HivdbVersionDef.currentHIVDBVersionFetcher;
import static edu.stanford.hivdb.graphql.ConditionalCommentDef.oCommentsByType;

public class DrugResistanceDef {

	public static GraphQLEnumType oSIR = GraphQLEnumType.newEnum()
		.name("SIR")
		.description("Three steps of resistance level.")
		.value("S", SIREnum.S, "Susceptible level.")
		.value("I", SIREnum.I, "Intermediate level.")
		.value("R", SIREnum.R, "Resistance level.")
		.build();

	private static DataFetcher<List<Map<String, Object>>> drugScoresDataFetcher = env -> {
		DrugClass<HIV> drugClassArg = env.getArgument("drugClass");
		GeneDR<HIV> geneDR = env.getSource();
		Gene<HIV> gene = geneDR.getGene();
		List<Map<String, Object>> results = new ArrayList<>();
		Set<DrugClass<HIV>> drugClasses = null;
		if (drugClassArg == null) {
			drugClasses = gene.getDrugClasses();
		}
		else {
			drugClasses = new HashSet<>();
			drugClasses.add(drugClassArg);
		}
		for (DrugClass<HIV> drugClass : drugClasses) {
			for (Drug<HIV> drug : drugClass.getDrugs()) {
				Map<String, Object> map = new HashMap<>();
				map.put("drug", drug);
				map.put("drugClass", drugClass);
				map.put("SIR", SIREnum.valueOf(geneDR.getDrugLevelSIR(drug)));
				map.put("score", geneDR.getTotalDrugScore(drug));
				map.put("level", geneDR.getDrugLevel(drug));
				map.put("text", geneDR.getDrugLevelText(drug));
				List<Map<String, Object>> partialScores = new ArrayList<>();

				// aggregate scores by positions instead of mutations
				// TODO: this should be moved to GeneDR
				Map<String, MutationSet<HIV>> gpMutations = new TreeMap<>();
				Map<String, Double> gpPartialScores = new HashMap<>();
				for (Map.Entry<Mutation<HIV>, Double> e :
						geneDR.getScoredIndividualMutsForDrug(drug).entrySet()) {
					Mutation<HIV> mut = e.getKey();

					List<String> gpKeyList = new ArrayList<>();
					gpKeyList.add(mut.getGenePosition().toString());
					String gpKey = StringUtils.join(gpKeyList, "+");

					MutationSet<HIV> muts = gpMutations.getOrDefault(gpKey, new MutationSet<>());
					muts = muts.mergesWith(mut);

					gpMutations.put(gpKey, muts);
					gpPartialScores.put(gpKey, gpPartialScores.getOrDefault(gpKey, .0) + e.getValue());
				}
				for (Map.Entry<MutationSet<HIV>, Double> e :
						geneDR.getScoredComboMutsForDrug(drug).entrySet()) {
					MutationSet<HIV> muts = e.getKey();

					List<String> gpKeyList = (
						muts.stream()
						.map(m -> m.getGenePosition().toString())
						.collect(Collectors.toList())
					);
					String gpKey = StringUtils.join(gpKeyList, "+");

					MutationSet<HIV> prevMuts = gpMutations.getOrDefault(gpKey, new MutationSet<>());
					muts = muts.mergesWith(prevMuts);

					gpMutations.put(gpKey, muts);
					gpPartialScores.put(gpKey, gpPartialScores.getOrDefault(gpKey, .0) + e.getValue());
				}

				for (String gpkey : gpMutations.keySet()) {
					MutationSet<HIV> muts = gpMutations.get(gpkey);
					Double score = gpPartialScores.get(gpkey);

					Map<String, Object> partialScore = new HashMap<>();
					partialScore.put("mutations", muts);
					partialScore.put("score", score);
					partialScores.add(partialScore);
				}
				/*partialScores.sort((ps1, ps2) -> {
					@SuppressWarnings("unchecked")
					MutationSet<HIV> mutsLeft = (MutationSet<HIV>) ps1.get("mutations");
					@SuppressWarnings("unchecked")
					MutationSet<HIV> mutsRight = (MutationSet<HIV>) ps2.get("mutations");
					return mutsLeft.compareTo(mutsRight);
				});*/
				map.put("partialScores", partialScores);
				results.add(map);
			}
		}
		return results;
	};

	private static DataFetcher<List<Map<String, Object>>> mutationsByTypesDataFetcher = env -> {
		GeneDR<HIV> geneDR = env.getSource();
		Gene<HIV> gene = geneDR.getGene();
		return gene.getMutationTypes()
			.stream()
			.map(mutType -> {
				Map<String, Object> result = new TreeMap<>();
				result.put("mutationType", mutType);
				result.put("mutations", geneDR.getMutationsByType(mutType));
				return result;
			})
			.collect(Collectors.toList());
	};

	private static DataFetcher<List<Map<String, Object>>> commentsByTypesDataFetcher = env -> {
		GeneDR<HIV> geneDR = env.getSource();
		return geneDR.groupCommentsByTypes()
			.entrySet()
			.stream()
			.map(e -> {
				Map<String, Object> result = new LinkedHashMap<>();
				result.put("commentType", e.getKey());
				result.put("mutationType", e.getKey());
				result.put("comments", e.getValue());
				return result;
			})
			.collect(Collectors.toList());
	};


	public static GraphQLObjectType oDrugPartialScore = newObject()
		.name("DrugPartialScore")
		.description("Partial score by mutation.")
		.field(field -> field
			.type(new GraphQLList(oMutation))
			.name("mutations")
			.description("Score triggering mutations."))
		.field(field -> field
			.type(GraphQLFloat)
			.name("score")
			.description("Score number."))
		.build();

	public static GraphQLObjectType oDrugScore = newObject()
		.name("DrugScore")
		.field(field -> field
			.type(oDrugClass)
			.name("drugClass")
			.description("The drug class."))
		.field(field -> field
			.type(oDrug)
			.name("drug")
			.description("The drug."))
		.field(field -> field
			.type(oSIR)
			.name("SIR")
			.description(
				"One of the three step resistance levels of the drug."))
		.field(field -> field
			.type(GraphQLFloat)
			.name("score")
			.description("Resistance score of the drug."))
		.field(field -> field
			.type(GraphQLInt)
			.name("level")
			.description("Resistance level (1 - 5) of the drug."))
		.field(field -> field
			.type(GraphQLString)
			.name("text")
			.description("Readable resistance level of the drug."))
		.field(field -> field
			.type(new GraphQLList(oDrugPartialScore))
			.name("partialScores")
			.description(
				"List of partial scores that contributed to this total score."))
		.build();

	public static GraphQLObjectType oMutationsByType = newObject()
		.name("MutationsByType")
		.field(field -> field
			.type(oMutationType)
			.name("mutationType")
			.description("Type of these mutations."))
		.field(field -> field
			.type(new GraphQLList(oMutation))
			.name("mutations")
			.description("Mutations belong to this type."))
		.build();

	public static GraphQLCodeRegistry drugResistanceCodeRegistry = newCodeRegistry()
		.dataFetcher(
			coordinates("DrugResistance", "version"),
			currentHIVDBVersionFetcher
		)
		.dataFetcher(
			coordinates("DrugResistance", "drugScores"),
			drugScoresDataFetcher
		)
		.dataFetcher(
			coordinates("DrugResistance", "mutationsByTypes"),
			mutationsByTypesDataFetcher
		)
		.dataFetcher(
			coordinates("DrugResistance", "commentsByTypes"),
			commentsByTypesDataFetcher
		)
		.build();

	public static GraphQLObjectType oDrugResistance = newObject()
		.name("DrugResistance")
		.field(field -> field
			.type(oHivdbVersion)
			.name("version")
			.description("Current algorithm version."))
		.field(field -> field
			.type(new GraphQLTypeReference("Gene"))
			.name("gene")
			.description("Gene of the drug resistance report."))
		.field(field -> field
			.type(new GraphQLList(oDrugScore))
			.name("drugScores")
			.description("List of drug levels and scores.")
			.argument(arg -> arg
				.type(oDrugClassEnum)
				.name("drugClass")
				.description(
					"Specify drug class. Leave this argument " +
					"empty will return all drugs.")
			))
		.field(field -> field
			.type(new GraphQLList(oMutationsByType))
			.name("mutationsByTypes"))
		.field(field -> field
			.type(new GraphQLList(oCommentsByType))
			.name("commentsByTypes"))
		.build();
}
