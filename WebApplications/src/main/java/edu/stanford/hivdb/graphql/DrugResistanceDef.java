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

import org.apache.commons.lang3.tuple.Pair;

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.algorithm.ASIDrugSusc;
import edu.stanford.hivdb.drugresistance.algorithm.SIREnum;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.viruses.Virus;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.SimpleMemoizer;

import static edu.stanford.hivdb.graphql.DrugDef.oDrug;
import static edu.stanford.hivdb.graphql.DrugClassDef.oDrugClass;
import static edu.stanford.hivdb.graphql.DrugClassDef.oDrugClassEnum;
import static edu.stanford.hivdb.graphql.MutationDef.oMutation;
import static edu.stanford.hivdb.graphql.MutationDef.oMutationType;
import static edu.stanford.hivdb.graphql.DrugResistanceAlgorithmDef.oDrugResistanceAlgorithm;
import static edu.stanford.hivdb.graphql.ConditionalCommentDef.oCommentsByType;

public class DrugResistanceDef {

	public static GraphQLEnumType oSIR = GraphQLEnumType.newEnum()
		.name("SIR")
		.description("Three steps of resistance level.")
		.value("S", SIREnum.S, "Susceptible level.")
		.value("I", SIREnum.I, "Intermediate level.")
		.value("R", SIREnum.R, "Resistance level.")
		.build();

	private static <VirusT extends Virus<VirusT>> DataFetcher<List<Map<String, Object>>> makeDrugScoresDataFetcher(VirusT virusIns) {
		return env -> {
			DrugClass<VirusT> drugClassArg = env.getArgument("drugClass");
			GeneDR<VirusT> geneDR = env.getSource();
			Gene<VirusT> gene = geneDR.getGene();
			List<Map<String, Object>> results = new ArrayList<>();
			Set<DrugClass<VirusT>> drugClasses = null;
			if (drugClassArg == null) {
				drugClasses = gene.getDrugClasses();
			}
			else {
				drugClasses = new HashSet<>();
				drugClasses.add(drugClassArg);
			}
			for (DrugClass<VirusT> drugClass : drugClasses) {
				for (Drug<VirusT> drug : drugClass.getDrugs()) {
					ASIDrugSusc<VirusT> drugSusc = geneDR.getDrugSusc(drug);
					Map<String, Object> map = new HashMap<>();
					map.put("drug", drug);
					map.put("drugClass", drugClass);
					map.put("SIR", drugSusc.getSIR());
					map.put("score", drugSusc.getScore());
					map.put("level", drugSusc.getLevel());
					map.put("text", drugSusc.getLevelText());
					List<Map<String, Object>> partialScores = new ArrayList<>();
     
					for (Pair<MutationSet<VirusT>, Double> pair : drugSusc.getParialScorePairs()) {
						Map<String, Object> partialScore = new LinkedHashMap<>();
						partialScore.put("mutations", pair.getLeft());
						partialScore.put("score", pair.getRight());
						partialScores.add(partialScore);
					}
					/*partialScores.sort((ps1, ps2) -> {
						@SuppressWarnings("unchecked")
						MutationSet<VirusT> mutsLeft = (MutationSet<VirusT>) ps1.get("mutations");
						@SuppressWarnings("unchecked")
						MutationSet<VirusT> mutsRight = (MutationSet<VirusT>) ps2.get("mutations");
						return mutsLeft.compareTo(mutsRight);
					});*/
					map.put("partialScores", partialScores);
					results.add(map);
				}
			}
			return results;
		};
	};

	private static <VirusT extends Virus<VirusT>> DataFetcher<List<Map<String, Object>>> makeMutationsByTypesDataFetcher(VirusT virusIns) {
		return env -> {
			GeneDR<VirusT> geneDR = env.getSource();
			Gene<VirusT> gene = geneDR.getGene();
			return gene.getMutationTypes()
				.stream()
				.map(mutType -> {
					Map<String, Object> result = new TreeMap<>();
					result.put("mutationType", mutType);
					result.put("mutations", geneDR.getMutations(mutType));
					return result;
				})
				.collect(Collectors.toList());
		};
	};

	private static <VirusT extends Virus<VirusT>> DataFetcher<List<Map<String, Object>>> makeCommentsByTypesDataFetcher(VirusT virusIns) {
		return env -> {
			GeneDR<VirusT> geneDR = env.getSource();
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
	};

	public static SimpleMemoizer<GraphQLObjectType> oDrugPartialScore = new SimpleMemoizer<>(
		name -> (
			newObject()
			.name("DrugPartialScore")
			.description("Partial score by mutation.")
			.field(field -> field
				.type(new GraphQLList(oMutation.get(name)))
				.name("mutations")
				.description("Score triggering mutations."))
			.field(field -> field
				.type(GraphQLFloat)
				.name("score")
				.description("Score number."))
			.build()
		)
	);

	public static SimpleMemoizer<GraphQLObjectType> oDrugScore = new SimpleMemoizer<>(
		name -> (
			newObject()
			.name("DrugScore")
			.field(field -> field
				.type(oDrugClass.get(name))
				.name("drugClass")
				.description("The drug class."))
			.field(field -> field
				.type(oDrug.get(name))
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
				.type(new GraphQLList(oDrugPartialScore.get(name)))
				.name("partialScores")
				.description(
					"List of partial scores that contributed to this total score."))
			.build()
		)
	);
		

	public static SimpleMemoizer<GraphQLObjectType> oMutationsByType = new SimpleMemoizer<>(
		name -> (
			newObject()
			.name("MutationsByType")
			.field(field -> field
				.type(oMutationType.get(name))
				.name("mutationType")
				.description("Type of these mutations."))
			.field(field -> field
				.type(new GraphQLList(oMutation.get(name)))
				.name("mutations")
				.description("Mutations belong to this type."))
			.build()
		)
	);

	public static <VirusT extends Virus<VirusT>> GraphQLCodeRegistry makeDrugResistanceCodeRegistry(VirusT virusIns) {
		return (
			newCodeRegistry()
			.dataFetcher(
				coordinates("DrugResistance", "drugScores"),
				makeDrugScoresDataFetcher(virusIns)
			)
			.dataFetcher(
				coordinates("DrugResistance", "mutationsByTypes"),
				makeMutationsByTypesDataFetcher(virusIns)
			)
			.dataFetcher(
				coordinates("DrugResistance", "commentsByTypes"),
				makeCommentsByTypesDataFetcher(virusIns)
			)
			.build()
		);
	}

	public static SimpleMemoizer<GraphQLObjectType> oDrugResistance = new SimpleMemoizer<>(
		name -> (
			newObject()
			.name("DrugResistance")
			.field(field -> field
				.type(oDrugResistanceAlgorithm)
				.name("version")
				.deprecate("Use field `algorithm` instead."))
			.field(field -> field
				.type(oDrugResistanceAlgorithm)
				.name("algorithm")
				.description("Get used drug resistance algorithm."))
			.field(field -> field
				.type(new GraphQLTypeReference("Gene"))
				.name("gene")
				.description("Gene of the drug resistance report."))
			.field(field -> field
				.type(new GraphQLList(oDrugScore.get(name)))
				.name("drugScores")
				.description("List of drug levels and scores.")
				.argument(arg -> arg
					.type(oDrugClassEnum.get(name))
					.name("drugClass")
					.description(
						"Specify drug class. Leave this argument " +
						"empty will return all drugs.")
				))
			.field(field -> field
				.type(new GraphQLList(oMutationsByType.get(name)))
				.name("mutationsByTypes"))
			.field(field -> field
				.type(new GraphQLList(oCommentsByType.get(name)))
				.name("commentsByTypes"))
			.build()
		)
	);
}
