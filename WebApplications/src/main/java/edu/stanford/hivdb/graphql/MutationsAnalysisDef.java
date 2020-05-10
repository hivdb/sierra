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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Triple;

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.viruses.Virus;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.SimpleMemoizer;
import edu.stanford.hivdb.utilities.ValidationResult;

import static edu.stanford.hivdb.graphql.DrugResistanceDef.*;
import static edu.stanford.hivdb.graphql.ValidationResultDef.*;
import static edu.stanford.hivdb.graphql.MutationPrevalenceDef.*;
import static edu.stanford.hivdb.graphql.AlgorithmComparisonDef.*;
import static edu.stanford.hivdb.graphql.DrugResistanceAlgorithmDef.*;

public class MutationsAnalysisDef {
	
	private static <VirusT extends Virus<VirusT>> Map<Gene<VirusT>, MutationSet<VirusT>> getMutationsByGeneFromSource(DataFetchingEnvironment env) {
		Triple<Set<Gene<VirusT>>, MutationSet<VirusT>, String> data = env.getSource();
		Set<Gene<VirusT>> knownGenes = data.getLeft();
		MutationSet<VirusT> mutations = data.getMiddle();
		
		Map<Gene<VirusT>, MutationSet<VirusT>> mutationsByGene = mutations.groupByGene();
		for (Gene<VirusT> gene : knownGenes) {
			if (!mutationsByGene.containsKey(gene)) {
				mutationsByGene.put(gene, new MutationSet<>());
			}
		}
		return mutationsByGene;
	}

	private static <VirusT extends Virus<VirusT>> MutationSet<VirusT> getMutationSetFromSource(DataFetchingEnvironment env) {
		Triple<Set<Gene<VirusT>>, MutationSet<VirusT>, String> data = env.getSource();
		return data.getMiddle();
	}
	
	private static <VirusT extends Virus<VirusT>> DataFetcher<List<ValidationResult>> makeMutValidationResultDataFetcher(VirusT virusIns) {
		return env -> {
			MutationSet<VirusT> mutations = getMutationSetFromSource(env);
			return virusIns.validateMutations(mutations);
		};
	};

	private static DataFetcher<String> mutsNameDataFetcher = env -> {
		Triple<Set<Gene<?>>, MutationSet<?>, String> data = env.getSource();
		return data.getRight();
	};

	private static <VirusT extends Virus<VirusT>> DataFetcher<List<GeneDR<VirusT>>> makeMutDRDataFetcher(VirusT virusIns) {
		return env -> {
			Map<Gene<VirusT>, MutationSet<VirusT>> mutationsByGene = getMutationsByGeneFromSource(env);
			String algName = env.getArgument("algorithm");
			return mutationsByGene
				.entrySet()
				.stream()
				.map(e -> new GeneDR<>(
					e.getKey(), e.getValue(), virusIns.getDrugResistAlgorithm(algName)
				))
				.collect(Collectors.toList());
		};
	};
	
	private static DataFetcher<List<Map<String, Object>>> mutMutPrevDataFetcher = env -> {
		MutationSet<?> mutations = getMutationSetFromSource(env);
		return getBoundMutationPrevalenceList(mutations);
	};
	
	private static <VirusT extends Virus<VirusT>> DataFetcher<List<Map<String, Object>>> makeMutAlgCmpDataFetcher(VirusT virusIns) {
		return env -> {
			List<String> asiAlgs = env.getArgument("algorithms");
			List<Map<String, String>> customAlgs = env.getArgument("customAlgorithms");
			if (asiAlgs == null) { asiAlgs = Collections.emptyList(); }
			if (customAlgs == null) { customAlgs = Collections.emptyList(); }
			if (asiAlgs.isEmpty() && customAlgs.isEmpty()) {
				return Collections.emptyList();
			}
			asiAlgs = asiAlgs
				.stream().filter(alg -> alg != null)
				.collect(Collectors.toList());
			Map<String, String> customAlgs2 = customAlgs
				.stream()
				.filter(map -> map != null)
				.collect(Collectors.toMap(
					map -> map.get("name"),
					map -> map.get("xml"),
					(x1, x2) -> x2,
					LinkedHashMap::new
				));
			MutationSet<VirusT> mutations = getMutationSetFromSource(env);
			return fetchAlgorithmComparisonData(virusIns, mutations, asiAlgs, customAlgs2);
		};
	};

	public static <VirusT extends Virus<VirusT>> GraphQLCodeRegistry makeMutationsAnalysisCodeRegistry(VirusT virusIns) {
		return (
			newCodeRegistry()
			.dataFetcher(
				coordinates("MutationsAnalysis", "name"),
				mutsNameDataFetcher
			)
			.dataFetcher(
				coordinates("MutationsAnalysis", "validationResults"),
				makeMutValidationResultDataFetcher(virusIns)
			)
			.dataFetcher(
				coordinates("MutationsAnalysis", "drugResistance"),
				makeMutDRDataFetcher(virusIns)
			)
			.dataFetcher(
				coordinates("MutationsAnalysis", "mutationPrevalences"),
				mutMutPrevDataFetcher
			)
			.dataFetcher(
				coordinates("MutationsAnalysis", "algorithmComparison"),
				makeMutAlgCmpDataFetcher(virusIns)
			)
			.build()
		);
	};
	
	public static SimpleMemoizer<GraphQLObjectType> oMutationsAnalysis = new SimpleMemoizer<>(
		name -> (
			newObject()
			.name("MutationsAnalysis")
			.field(field -> field
				.type(GraphQLString)
				.name("name")
				.description("Optional name provided by client to identify this mutation list."))
			.field(field -> field
				.type(new GraphQLList(oValidationResult))
				.name("validationResults")
				.description("Validation results for the mutation list."))
			.field(field -> field
				.type(new GraphQLList(oDrugResistance.get(name)))
				.name("drugResistance")
				.argument(arg -> arg
					.name("algorithm")
					.type(oASIAlgorithm.get(name))
					.defaultValue(Virus.getInstance(name).getLatestDrugResistAlgorithm("HIVDB").getName())
					.description("One of the built-in ASI algorithms."))
				.description("List of drug resistance results by genes."))
			.field(field -> field
				.type(new GraphQLList(oBoundMutationPrevalence.get(name)))
				.name("mutationPrevalences")
				.description("List of mutation prevalence results."))
			.field(field -> field
				.type(new GraphQLList(oAlgorithmComparison.get(name)))
				.name("algorithmComparison")
				.description("List of ASI comparison results.")
				.argument(aASIAlgorithmArgument.get(name))
				.argument(aASICustomAlgorithmArgument))
			.build()
		)
	);

}
