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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLInputObjectField.newInputObjectField;

import edu.stanford.hivdb.drugresistance.algorithm.Algorithm;
import edu.stanford.hivdb.drugresistance.algorithm.Asi;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.drugresistance.algorithm.AlgorithmComparison;

import static edu.stanford.hivdb.graphql.DrugDef.oDrug;
import static edu.stanford.hivdb.graphql.DrugClassDef.oDrugClass;
import static edu.stanford.hivdb.graphql.DrugResistanceDef.oSIR;
import static edu.stanford.hivdb.graphql.ExtendedFieldDefinition.*;

public class AlgorithmComparisonDef {

	private static GraphQLEnumType newASIAlgorithmEnum() {
		GraphQLEnumType.Builder builder = GraphQLEnumType.newEnum()
			.name("ASIAlgorithm")
			.description("ASI algorithm.");
		for (Algorithm alg : Algorithm.values()) {
			builder.value(alg.toString(), alg);
		}
		return builder.build();
	}

	private static Map<Gene, List<Asi>>
			getAsiListMap(
				Map<Gene, MutationSet> allMuts,
				Collection<Algorithm> algorithms,
				Map<String, String> customAlgorithms) {
		return allMuts.entrySet()
		.stream()
		.collect(Collectors.toMap(
			e -> e.getKey(),
			e -> {
				Gene gene = e.getKey();
				MutationSet muts = e.getValue();
				List<Asi> asiList = AlgorithmComparison
					.calcAsiListFromAlgorithms(gene, muts, algorithms);
				asiList.addAll(AlgorithmComparison
					.calcAsiListFromCustomAlgorithms(
						gene, muts, customAlgorithms));
				return asiList;
			},
			(l1, l2) -> l1,
			LinkedHashMap::new
		));
	}

	protected static List<Map<String, Object>> fetchAlgorithmComparisonData(
			Map<Gene, MutationSet> allMuts,
			Collection<Algorithm> algorithms,
			Map<String, String> customAlgorithms) {
		AlgorithmComparison algCmp =
			new AlgorithmComparison(getAsiListMap(allMuts, algorithms, customAlgorithms));
		return algCmp.getComparisonResults()
			.stream()
			.collect(Collectors.groupingBy(cds -> cds.drug.getDrugClass()))
			.entrySet()
			.stream()
			.map(e -> {
				Map<String, Object> r = new HashMap<>();
				r.put("drugClass", e.getKey());
				r.put("drugScores", e.getValue());
				return r;
			})
			.collect(Collectors.toList());
	}

	public static GraphQLEnumType oASIAlgorithm = newASIAlgorithmEnum();

	public static GraphQLInputObjectType
		iASICustomAlgorithm = newInputObject()
		.name("CustomASIAlgorithm")
		.field(newInputObjectField()
			.name("name")
			.type(GraphQLString)
			.description("Algorithm name.")
			.build())
		.field(newInputObjectField()
			.name("xml")
			.type(GraphQLString)
			.description("ASI XML data.")
			.build())
		.build();

	public static GraphQLArgument aASIAlgorithmArgument = newArgument()
		.name("algorithms")
		.description("One or more of the built-in ASI algorithms.")
		.type(new GraphQLList(oASIAlgorithm))
		.build();

	public static GraphQLArgument aASICustomAlgorithmArgument = newArgument()
		.name("customAlgorithms")
		.description("One or more of custom ASI algorithms.")
		.type(new GraphQLList(iASICustomAlgorithm))
		.build();

	public static GraphQLObjectType oComparableDrugScore = newObject()
		.name("ComparableDrugScore")
		.field(newFieldDefinition()
			.name("drug")
			.type(oDrug)
			.description("Drug of this score.")
			.build())
		.field(newFieldDefinition()
			.name("algorithm")
			.type(GraphQLString)
			.description("The name of algorithm which calculated this score.")
			.build())
		.field(newFieldDefinition()
			.name("SIR")
			.type(oSIR)
			.description(
				"One of the three step resistance levels of the drug.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("interpretation")
			.description(
				"Readable resistance level defined by the algorithm for the drug.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("explanation")
			.description(
				"Text explanation on how this level get calculated.")
			.build())
		.build();

	public static GraphQLObjectType oAlgorithmComparison = newObject()
		.name("AlgorithmComparison")
		.field(newFieldDefinition()
			.name("drugClass")
			.type(oDrugClass)
			.build())
		.field(newFieldDefinition()
			.name("drugScores")
			.type(new GraphQLList(oComparableDrugScore))
			.build())
		.build();
}
