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
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLCodeRegistry.newCodeRegistry;
import static graphql.schema.FieldCoordinates.coordinates;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.GeneDRAsi;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.ValidationResult;

import static edu.stanford.hivdb.graphql.DrugResistanceDef.*;
import static edu.stanford.hivdb.graphql.ValidationResultDef.*;
import static edu.stanford.hivdb.graphql.MutationPrevalenceDef.*;
import static edu.stanford.hivdb.graphql.AlgorithmComparisonDef.*;

public class MutationsAnalysisDef {

	private static Map<Gene<HIV>, MutationSet<HIV>> getMutationsByGeneFromSource(DataFetchingEnvironment env) {
		Pair<Set<Gene<HIV>>, MutationSet<HIV>> data = env.getSource();
		Set<Gene<HIV>> knownGenes = data.getLeft();
		MutationSet<HIV> mutations = data.getRight();
		
		Map<Gene<HIV>, MutationSet<HIV>> mutationsByGene = mutations.groupByGene();
		for (Gene<HIV> gene : knownGenes) {
			if (!mutationsByGene.containsKey(gene)) {
				mutationsByGene.put(gene, new MutationSet<>());
			}
		}
		return mutationsByGene;
	}

	private static MutationSet<HIV> getMutationSetFromSource(DataFetchingEnvironment env) {
		Pair<Set<Gene<HIV>>, MutationSet<HIV>> data = env.getSource();
		return data.getRight();
	}
	
	private static DataFetcher<List<ValidationResult>> mutValidationResultDataFetcher = env -> {
		HIV hiv = HIV.getInstance();
		MutationSet<HIV> mutations = getMutationSetFromSource(env);
		return hiv.validateMutations(mutations);
	};
	

	private static DataFetcher<List<GeneDR<HIV>>> mutDRDataFetcher = env -> {
		HIV hiv = HIV.getInstance();
		Map<Gene<HIV>, MutationSet<HIV>> mutationsByGene = getMutationsByGeneFromSource(env);
		return mutationsByGene
			.entrySet()
			.stream()
			.map(e -> new GeneDRAsi<>(e.getKey(), e.getValue(), hiv.getLatestDrugResistAlgorithm("HIVDB")))
			.collect(Collectors.toList());
	};
	
	private static DataFetcher<List<Map<String, Object>>> mutMutPrevDataFetcher = env -> {
		MutationSet<HIV> mutations = getMutationSetFromSource(env);
		return getBoundMutationPrevalenceList(mutations);
	};
	
	private static DataFetcher<List<Map<String, Object>>> mutAlgCmpDataFetcher = env -> {
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
		MutationSet<HIV> mutations = getMutationSetFromSource(env);
		return fetchAlgorithmComparisonData(mutations, asiAlgs, customAlgs2);
	};

	public static GraphQLCodeRegistry mutationsAnalysisCodeRegistry = newCodeRegistry()
		.dataFetcher(
			coordinates("MutationsAnalysis", "validationResults"),
			mutValidationResultDataFetcher
		)
		.dataFetcher(
			coordinates("MutationsAnalysis", "drugResistance"),
			mutDRDataFetcher
		)
		.dataFetcher(
			coordinates("MutationsAnalysis", "mutationPrevalences"),
			mutMutPrevDataFetcher
		)
		.dataFetcher(
			coordinates("MutationsAnalysis", "algorithmComparison"),
			mutAlgCmpDataFetcher
		)
		.build();
	
	public static GraphQLObjectType oMutationsAnalysis = newObject()
		.name("MutationsAnalysis")
		.field(field -> field
			.type(new GraphQLList(oValidationResult))
			.name("validationResults")
			.description("Validation results for the mutation list."))
		.field(field -> field
			.type(new GraphQLList(oDrugResistance))
			.name("drugResistance")
			.description("List of drug resistance results by genes."))
		.field(field -> field
			.type(new GraphQLList(oBoundMutationPrevalence))
			.name("mutationPrevalences")
			.description("List of mutation prevalence results."))
		.field(field -> field
			.type(new GraphQLList(oAlgorithmComparison))
			.name("algorithmComparison")
			.description("List of ASI comparison results.")
			.argument(aASIAlgorithmArgument)
			.argument(aASICustomAlgorithmArgument))
		.build();

}
