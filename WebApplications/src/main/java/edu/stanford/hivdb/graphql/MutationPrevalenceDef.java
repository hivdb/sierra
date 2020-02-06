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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.SimpleMemoizer;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.viruses.Virus;

import static edu.stanford.hivdb.graphql.GeneDef.oGene;
import static edu.stanford.hivdb.graphql.ExtGraphQL.*;
import static edu.stanford.hivdb.graphql.MutationDef.*;

public class MutationPrevalenceDef {

	public static List<Map<String, Object>>
			getBoundMutationPrevalenceList(MutationSet<?> mutations) {
		return mutations.getPrevalences()
			.entrySet()
			.stream()
			.map(e -> {
				Mutation<?> mainMut = e.getKey();
				Map<Mutation<?>, Map<String, Object>>	allResults = (
					e.getValue()
					.stream()
					.collect(Collectors.groupingBy(mp -> mp.getMutation()))
					.entrySet().stream()
					.collect(Collectors.toMap(
						f -> f.getKey(),
						f -> {
							Map<String, Object> r = new HashMap<>();
							r.put("AA", f.getKey().getDisplayAAs());
							r.put("subtypes", f.getValue());
							return r;
						}
		))
	);

				List<Map<String, Object>> mainResults = new ArrayList<>();
				for (Mutation<?> singleAA : mainMut.split()) {
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

	private static <VirusT extends Virus<VirusT>> DataFetcher<List<Map<String, Object>>> makeSubtypeStatsDataFetcher(VirusT virusIns) {
		return env -> {
			String subtype = env.getSource();
			return virusIns.getNumPatientsForAAPercents(virusIns.getMainStrain())
				.entrySet().stream()
				.map(e -> {
					Gene<VirusT> gene = e.getKey();
					Integer[] stat = e.getValue().get(subtype);
					Map<String, Object> r = new HashMap<>();
					r.put("gene", gene);
					r.put("totalNaive", stat[0]);
					r.put("totalTreated", stat[1]);
					return r;
				})
				.collect(Collectors.toList());
		};
	};

	public static SimpleMemoizer<GraphQLObjectType> oMutationPrevalenceSubtypeStat = new SimpleMemoizer<>(
		name -> (
			newObject()
			.name("MutationPrevalenceSubtypeStat")
			.description("Statistics data for the subtype.")
			.field(field -> field
				.name("gene")
				.type(oGene.get(name))
				.description("Gene the statistic belongs to."))
			.field(field -> field
				.name("totalNaive")
				.type(GraphQLInt)
				.description("Total number of naive samples."))
			.field(field -> field
				.name("totalTreated")
				.type(GraphQLInt)
				.description("Total number of treated samples."))
			.build()
		)
	);
	
	public static <VirusT extends Virus<VirusT>> GraphQLCodeRegistry makeMutationPrevalenceSubtypeCodeRegistry(VirusT virusIns) {
		return (
			newCodeRegistry()
			.dataFetcher(
				coordinates("MutationPrevalenceSubtype", "name"),
				pipeLineDataFetcher
			)
			.dataFetcher(
				coordinates("MutationPrevalenceSubtype", "stats"),
				makeSubtypeStatsDataFetcher(virusIns)
			)
			.build()
		);
	}

	public static SimpleMemoizer<GraphQLObjectType> oMutationPrevalenceSubtype = new SimpleMemoizer<>(
		name -> (
			newObject()
			.name("MutationPrevalenceSubtype")
			.description("Main subtype that mutation prevalence supported.")
			.field(field -> field
				.name("name")
				.type(GraphQLString)
				.description("Subtype name."))
			.field(field -> field
				.name("stats")
				.type(new GraphQLList(oMutationPrevalenceSubtypeStat.get(name)))
				.description("Sbutype statistics by genes."))
			.build()
		)
	);

	public static SimpleMemoizer<GraphQLObjectType> oMutationPrevalence = new SimpleMemoizer<>(
		name -> (
			newObject()
			.name("MutationPrevalence")
			.description("Prevalence data for a single mutation.")
			.field(field -> field
				.name("AA")
				.type(GraphQLString)
				.description("The amino acid at this position."))
			.field(field -> field
				.name("subtype")
				.type(oMutationPrevalenceSubtype.get(name))
				.description("Subtype this prevalence belongs to."))
			.field(field -> field
				.name("totalNaive")
				.type(GraphQLInt)
				.description("Total number of naive samples which contain the mutation position."))
			.field(field -> field
				.name("frequencyNaive")
				.type(GraphQLInt)
				.description("Total number of naive samples which contain the mutation."))
			.field(field -> field
				.name("percentageNaive")
				.type(GraphQLFloat)
				.description(
					"Proportion of certain mutation occured in " +
					"the naive samples which contain that position. " +
					"Equals to 100 * frequencyNaive / totalNaive."))
			.field(field -> field
				.name("totalTreated")
				.type(GraphQLInt)
				.description("Total number of treated samples which contain the mutation position."))
			.field(field -> field
				.name("frequencyTreated")
				.type(GraphQLInt)
				.description("Total number of treated samples which contain the mutation."))
			.field(field -> field
				.name("percentageTreated")
				.type(GraphQLFloat)
				.description(
					"Proportion of certain mutation occured in " +
					"the treated samples which contain that position. " +
					"Equals to 100 * frequencyTreated / totalTreated."))
			.build()
		)
	);

	public static SimpleMemoizer<GraphQLObjectType> oMutationPrevalenceByAA = new SimpleMemoizer<>(
		name -> (
			newObject()
			.name("MutationPrevalenceByAA")
			.description(
				"Prevalence data for a single AA (or ins/deletion).")
			.field(field -> field
				.name("AA")
				.type(GraphQLString)
				.description("The amino acid."))
			.field(field -> field
				.name("subtypes")
				.type(new GraphQLList(oMutationPrevalence.get(name)))
				.description("Prevalence data of each subtype."))
			.build()
		)
	);

	public static SimpleMemoizer<GraphQLObjectType> oBoundMutationPrevalence = new SimpleMemoizer<>(
		name -> (
			newObject()
			.name("BoundMutationPrevalence")
			.description("Prevalence data for an input mutation.")
			.field(field -> field
				.name("boundMutation")
				.type(oMutation.get(name))
				.description("The mutation matched these prevalence data."))
			.field(field -> field
				.name("matched")
				.type(new GraphQLList(oMutationPrevalenceByAA.get(name)))
				.description("Prevalence data that matched the mutation."))
			.field(field -> field
				.name("others")
				.type(new GraphQLList(oMutationPrevalenceByAA.get(name)))
				.description("Other prevalence data at the mutation position."))
			.build()
		)
	);

}
