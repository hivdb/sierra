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
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLObjectType.newObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.algorithm.Asi;
import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;

import static edu.stanford.hivdb.graphql.DrugDef.oDrug;
import static edu.stanford.hivdb.graphql.DrugClassDef.oDrugClass;
import static edu.stanford.hivdb.graphql.DrugClassDef.oDrugClassEnum;
import static edu.stanford.hivdb.graphql.MutationDef.oMutation;
import static edu.stanford.hivdb.graphql.MutationDef.oMutationType;
import static edu.stanford.hivdb.graphql.HivdbVersionDef.oHivdbVersion;
import static edu.stanford.hivdb.graphql.HivdbVersionDef.currentHIVDBVersionFetcher;
import static edu.stanford.hivdb.graphql.ConditionalCommentDef.oCommentsByType;
import static edu.stanford.hivdb.graphql.ExtendedFieldDefinition.newFieldDefinition;

public class DrugResistanceDef {

	public static GraphQLEnumType oSIR = GraphQLEnumType.newEnum()
		.name("SIR")
		.description("Three steps of resistance level.")
		.value("S", Asi.SIREnum.S, "Susceptible level.")
		.value("I", Asi.SIREnum.I, "Intermediate level.")
		.value("R", Asi.SIREnum.R, "Resistance level.")
		.build();

	private static DataFetcher drugScoresDataFetcher = new DataFetcher() {
		@Override
		public Object get(DataFetchingEnvironment environment) {
			DrugClass drugClassArg = environment.getArgument("drugClass");
			GeneDR geneDR = (GeneDR) environment.getSource();
			Gene gene = geneDR.getGene();
			List<Map<String, Object>> results = new ArrayList<>();
			List<DrugClass> drugClasses = null;
			if (drugClassArg == null) {
				drugClasses = gene.getDrugClasses();
			}
			else {
				drugClasses = new ArrayList<>();
				drugClasses.add(drugClassArg);
			}
			for (DrugClass drugClass : drugClasses) {
				for (Drug drug : drugClass.getDrugsForHivdbTesting()) {
					Map<String, Object> map = new HashMap<>();
					map.put("drug", drug);
					map.put("drugClass", drugClass);
					map.put("SIR", Asi.SIREnum.valueOf(geneDR.getDrugLevelSIR(drug)));
					map.put("score", geneDR.getTotalDrugScore(drug));
					map.put("level", geneDR.getDrugLevel(drug));
					map.put("text", geneDR.getDrugLevelText(drug));
					List<Map<String, Object>> partialScores = new ArrayList<>();
					for (Map.Entry<Mutation, Double> e :
							geneDR.getScoredIndividualMutsForDrug(drug).entrySet()) {
						Map<String, Object> partialScore = new HashMap<>();
						List<Mutation> rule = new ArrayList<>();
						rule.add(e.getKey());
						partialScore.put("mutations", rule);
						partialScore.put("score", e.getValue());
						partialScores.add(partialScore);
					}
					for (Map.Entry<MutationSet, Double> e :
							geneDR.getScoredComboMutsForDrug(drug).entrySet()) {
						Map<String, Object> partialScore = new HashMap<>();
						List<Mutation> rule = new ArrayList<>(e.getKey());
						partialScore.put("mutations", rule);
						partialScore.put("score", e.getValue());
						partialScores.add(partialScore);
					}
					map.put("partialScores", partialScores);
					results.add(map);
				}
			}
			return results;
		}
	};

	private static DataFetcher mutationsByTypesDataFetcher = new DataFetcher() {
		@Override
		public Object get(DataFetchingEnvironment environment) {
			GeneDR geneDR = (GeneDR) environment.getSource();
			Gene gene = geneDR.getGene();
			return gene.getMutationTypes()
				.stream()
				.map(mutType -> {
					Map<String, Object> result = new HashMap<>();
					result.put("mutationType", mutType);
					result.put("mutations", geneDR.getMutationsByType(mutType));
					return result;
				})
				.collect(Collectors.toList());
		}
	};

	private static DataFetcher commentsByTypesDataFetcher = new DataFetcher() {
		@Override
		public Object get(DataFetchingEnvironment environment) {
			GeneDR geneDR = (GeneDR) environment.getSource();
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
		}
	};


	public static GraphQLObjectType oDrugPartialScore = newObject()
		.name("DrugPartialScore")
		.description("Partial score by mutation.")
		.field(newFieldDefinition()
			.type(new GraphQLList(oMutation))
			.name("mutations")
			.description("Score triggering mutations.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLFloat)
			.name("score")
			.description("Score number.")
			.build())
		.build();

	public static GraphQLObjectType oDrugScore = newObject()
		.name("DrugScore")
		.field(newFieldDefinition()
			.type(oDrugClass)
			.name("drugClass")
			.description("The drug class.")
			.build())
		.field(newFieldDefinition()
			.type(oDrug)
			.name("drug")
			.description("The drug.")
			.build())
		.field(newFieldDefinition()
			.type(oSIR)
			.name("SIR")
			.description(
				"One of the three step resistance levels of the drug.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLFloat)
			.name("score")
			.description("Resistance score of the drug.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLInt)
			.name("level")
			.description("Resistance level (1 - 5) of the drug.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("text")
			.description("Readable resistance level of the drug.")
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oDrugPartialScore))
			.name("partialScores")
			.description(
				"List of partial scores that contributed to this total score.")
			.build())
		.build();

	public static GraphQLObjectType oMutationsByType = newObject()
		.name("MutationsByType")
		.field(newFieldDefinition()
			.type(oMutationType)
			.name("mutationType")
			.description("Type of these mutations.")
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oMutation))
			.name("mutations")
			.description("Mutations belong to this type.")
			.build())
		.build();

	public static GraphQLObjectType oDrugResistance = newObject()
		.name("DrugResistance")
		.field(newFieldDefinition()
			.type(oHivdbVersion)
			.name("version")
			.description("Current algorithm version.")
			.dataFetcher(currentHIVDBVersionFetcher)
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLTypeReference("Gene"))
			.name("gene")
			.description("Gene of the drug resistance report.")
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oDrugScore))
			.name("drugScores")
			.description("List of drug levels and scores.")
			.argument(newArgument()
				.type(oDrugClassEnum)
				.name("drugClass")
				.description(
					"Specify drug class. Leave this argument " +
					"empty will return all drugs.")
				.build())
			.dataFetcher(drugScoresDataFetcher)
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oMutationsByType))
			.name("mutationsByTypes")
			.dataFetcher(mutationsByTypesDataFetcher)
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oCommentsByType))
			.name("commentsByTypes")
			.dataFetcher(commentsByTypesDataFetcher)
			.build())
		.build();
}
