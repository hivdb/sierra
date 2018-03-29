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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.stanford.hivdb.alignment.MutationListValidator;
import edu.stanford.hivdb.drugresistance.GeneDRFast;
import edu.stanford.hivdb.drugresistance.algorithm.Algorithm;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.MutationSet;
import static edu.stanford.hivdb.graphql.DrugResistanceDef.*;
import static edu.stanford.hivdb.graphql.ValidationResultDef.*;
import static edu.stanford.hivdb.graphql.MutationPrevalenceDef.*;
import static edu.stanford.hivdb.graphql.AlgorithmComparisonDef.*;
import static edu.stanford.hivdb.graphql.ExtendedFieldDefinition.*;

public class MutationsAnalysisDef {
	
	private static Map<Gene, MutationSet> getMutationsByGeneFromSource(DataFetchingEnvironment env) {
		List<?> data = (List<?>) env.getSource();
		Set<Gene> knownGenes = ((Set<?>) data.get(0)).stream().map(g -> ((Gene) g)).collect(Collectors.toSet());
		MutationSet mutations = (MutationSet) data.get(1);
		Map<Gene, MutationSet> mutationsByGene = mutations.groupByGene();
		for (Gene gene : knownGenes) {
			if (!mutationsByGene.containsKey(gene)) {
				mutationsByGene.put(gene, new MutationSet());
			}
		}
		return mutationsByGene;
	}
	
	private static MutationSet getMutationSetFromSource(DataFetchingEnvironment env) {
		List<?> data = (List<?>) env.getSource();
		return (MutationSet) data.get(1);
	}

	private static DataFetcher drugResistanceDataFetcher = new DataFetcher() {
		@Override
		public Object get(DataFetchingEnvironment environment) {
			Map<Gene, MutationSet> mutationsByGene = getMutationsByGeneFromSource(environment);
			return mutationsByGene
				.entrySet()
				.stream()
				.map(e -> new GeneDRFast(e.getKey(), e.getValue()))
				.collect(Collectors.toList());
		}
	};

	private static DataFetcher mutationPrevalencesDataFetcher = new DataFetcher() {
		@Override
		public Object get(DataFetchingEnvironment environment) {
			MutationSet mutations = getMutationSetFromSource(environment);
			return getBoundMutationPrevalenceList(mutations);
		}
	};

	private static DataFetcher algorithmComparisonDataFetcher = new DataFetcher() {
		@Override
		public Object get(DataFetchingEnvironment environment) {
			List<Algorithm> asiAlgs = environment.getArgument("algorithms");
			List<Map<String, String>> customAlgs = environment.getArgument("customAlgorithms");
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
			Map<Gene, MutationSet> mutationsByGene = getMutationsByGeneFromSource(environment);
			return fetchAlgorithmComparisonData(mutationsByGene, asiAlgs, customAlgs2);
		}
	};

	private static DataFetcher validationResultsDataFetcher = new DataFetcher() {

		@Override
		public Object get(DataFetchingEnvironment environment) {
			MutationSet mutations = getMutationSetFromSource(environment);
			MutationListValidator validator = new MutationListValidator(mutations);
			return validator.getValidationResults();
		}

	};

	public static GraphQLObjectType oMutationsAnalysis = newObject()
		.name("MutationsAnalysis")
		.field(newFieldDefinition()
			.type(new GraphQLList(oValidationResult))
			.name("validationResults")
			.description("Validation results for the mutation list.")
			.dataFetcher(validationResultsDataFetcher)
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oDrugResistance))
			.name("drugResistance")
			.description("List of drug resistance results by genes.")
			.dataFetcher(drugResistanceDataFetcher)
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oBoundMutationPrevalence))
			.name("mutationPrevalences")
			.description("List of mutation prevalence results.")
			.dataFetcher(mutationPrevalencesDataFetcher)
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oAlgorithmComparison))
			.name("algorithmComparison")
			.description("List of ASI comparison results.")
			.dataFetcher(algorithmComparisonDataFetcher)
			.argument(aASIAlgorithmArgument)
			.argument(aASICustomAlgorithmArgument)
			.build())
		.build();

}
