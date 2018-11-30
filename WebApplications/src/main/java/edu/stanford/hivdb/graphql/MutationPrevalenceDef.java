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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationPrevalences;
import edu.stanford.hivdb.mutations.MutationSet;
import static edu.stanford.hivdb.graphql.GeneDef.oGene;
import static edu.stanford.hivdb.graphql.ExtendedFieldDefinition.*;
import static edu.stanford.hivdb.graphql.MutationDef.*;

public class MutationPrevalenceDef {

	public static List<Map<String, Object>>
			getBoundMutationPrevalenceList(MutationSet mutations) {
		return mutations.getPrevalences()
			.entrySet()
			.stream()
			.map(e -> {
				Mutation mainMut = e.getKey();
				Map<Mutation, Map<String, Object>>
					allResults = e.getValue().stream()
					.collect(
						Collectors.groupingBy(mp -> mp.mutation))
					.entrySet().stream()
					.collect(Collectors.toMap(
						f -> f.getKey(),
						f -> {
							Map<String, Object> r = new HashMap<>();
							r.put("AA", f.getKey().getDisplayAAs());
							r.put("subtypes", f.getValue());
							return r;
						}
					));

				List<Map<String, Object>> mainResults = new ArrayList<>();
				for (Mutation singleAA : mainMut.split()) {
					Map<String, Object> r = allResults.remove(singleAA);
					if (r == null) {
						r = new HashMap<>();
						r.put("AA", singleAA.getAAs());
						r.put("subtypes", Collections.emptyList());
					}
					mainResults.add(r);
				}
				List<Map<String, Object>>
					otherResults = new ArrayList<>(allResults.values());
				Map<String, Object> ret = new HashMap<>();
				ret.put("boundMutation", mainMut);
				ret.put("matched", mainResults);
				ret.put("others", otherResults);
				return ret;
			})
			.collect(Collectors.toList());
	}

	private static DataFetcher<List<Map<String, Object>>> subtypeStatsDataFetcher = new DataFetcher<List<Map<String, Object>>>() {

		@Override
		public List<Map<String, Object>> get(DataFetchingEnvironment environment) {
			String subtype = (String) environment.getSource();
			return MutationPrevalences.getNumPatients()
				.entrySet().stream()
				.map(e -> {
					Gene gene = e.getKey();
					Integer[] stat = e.getValue().get(subtype);
					Map<String, Object> r = new HashMap<>();
					r.put("gene", gene);
					r.put("totalNaive", stat[0]);
					r.put("totalTreated", stat[1]);
					return r;
				})
				.collect(Collectors.toList());
		}

	};

	public static GraphQLObjectType oMutationPrevalenceSubtypeStat = newObject()
		.name("MutationPrevalenceSubtypeStat")
		.description("Statistics data for the subtype.")
		.field(newFieldDefinition()
			.name("gene")
			.type(oGene)
			.description("Gene the statistic belongs to.")
			.build())
		.field(newFieldDefinition()
			.name("totalNaive")
			.type(GraphQLInt)
			.description("Total number of naive samples.")
			.build())
		.field(newFieldDefinition()
			.name("totalTreated")
			.type(GraphQLInt)
			.description("Total number of treated samples.")
			.build())
		.build();

	public static GraphQLObjectType oMutationPrevalenceSubtype = newObject()
		.name("MutationPrevalenceSubtype")
		.description("Main HIV-1 subtype that mutation prevalence supported.")
		.field(newFieldDefinition()
			.name("name")
			.type(GraphQLString)
			.description("Subtype name.")
			.dataFetcher(pipeLineDataFetcher)
			.build())
		.field(newFieldDefinition()
			.name("stats")
			.type(new GraphQLList(oMutationPrevalenceSubtypeStat))
			.description("Sbutype statistics by genes.")
			.dataFetcher(subtypeStatsDataFetcher)
			.build())
		.build();

	public static GraphQLObjectType oMutationPrevalence = newObject()
		.name("MutationPrevalence")
		.description("Prevalence data for a single mutation.")
		.field(newFieldDefinition()
			.name("AA")
			.type(GraphQLString)
			.description("The amino acid at this position.")
			.build())
		.field(newFieldDefinition()
			.name("subtype")
			.type(oMutationPrevalenceSubtype)
			.description("HIV-1 subtype this prevalence belongs to.")
			.build())
		.field(newFieldDefinition()
			.name("totalNaive")
			.type(GraphQLInt)
			.description("Total number of naive samples which contain the mutation position.")
			.build())
		.field(newFieldDefinition()
			.name("frequencyNaive")
			.type(GraphQLInt)
			.description("Total number of naive samples which contain the mutation.")
			.build())
		.field(newFieldDefinition()
			.name("percentageNaive")
			.type(GraphQLFloat)
			.description(
				"Proportion of certain mutation occured in " +
				"the naive samples which contain that position. " +
				"Equals to 100 * frequencyNaive / totalNaive.")
			.build())
		.field(newFieldDefinition()
			.name("totalTreated")
			.type(GraphQLInt)
			.description("Total number of treated samples which contain the mutation position.")
			.build())
		.field(newFieldDefinition()
			.name("frequencyTreated")
			.type(GraphQLInt)
			.description("Total number of treated samples which contain the mutation.")
			.build())
		.field(newFieldDefinition()
			.name("percentageTreated")
			.type(GraphQLFloat)
			.description(
				"Proportion of certain mutation occured in " +
				"the treated samples which contain that position. " +
				"Equals to 100 * frequencyTreated / totalTreated.")
			.build())
		.build();

	public static GraphQLObjectType oMutationPrevalenceByAA = newObject()
		.name("MutationPrevalenceByAA")
		.description(
			"Prevalence data for a single AA (or ins/deletion).")
		.field(newFieldDefinition()
			.name("AA")
			.type(GraphQLString)
			.description("The amino acid.")
			.build())
		.field(newFieldDefinition()
			.name("subtypes")
			.type(new GraphQLList(oMutationPrevalence))
			.description("Prevalence data of each subtype.")
			.build())
		.build();

	public static GraphQLObjectType oBoundMutationPrevalence = newObject()
		.name("BoundMutationPrevalence")
		.description("Prevalence data for an input mutation.")
		.field(newFieldDefinition()
			.name("boundMutation")
			.type(oMutation)
			.description("The mutation matched these prevalence data.")
			.build())
		.field(newFieldDefinition()
			.name("matched")
			.type(new GraphQLList(oMutationPrevalenceByAA))
			.description("Prevalence data that matched the mutation.")
			.build())
		.field(newFieldDefinition()
			.name("others")
			.type(new GraphQLList(oMutationPrevalenceByAA))
			.description("Other prevalence data at the mutation position.")
			.build())
		.build();

}
