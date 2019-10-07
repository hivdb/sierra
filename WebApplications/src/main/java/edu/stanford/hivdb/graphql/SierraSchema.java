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
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.stanford.hivdb.alignment.AlignedSequence;
import edu.stanford.hivdb.alignment.Aligner;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationPrevalences;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.utilities.Sequence;

import static edu.stanford.hivdb.graphql.Exceptions.*;
import static edu.stanford.hivdb.graphql.UnalignedSequenceDef.*;
import static edu.stanford.hivdb.graphql.GeneDef.*;
import static edu.stanford.hivdb.graphql.HivdbVersionDef.*;
import static edu.stanford.hivdb.graphql.SierraVersionDef.*;
import static edu.stanford.hivdb.graphql.MutationPrevalenceDef.oMutationPrevalenceSubtype;
import static edu.stanford.hivdb.graphql.ExtendedFieldDefinition.newFieldDefinition;
import static edu.stanford.hivdb.graphql.SequenceAnalysisDef.oSequenceAnalysis;
import static edu.stanford.hivdb.graphql.MutationsAnalysisDef.oMutationsAnalysis;

public class SierraSchema {

	private static final int MAXIMUM_SEQUENCES_PER_PAYLOAD;

	private static DataFetcher<List<AlignedSequence>> getSequenceAnalysisDataFetcher() {
		return new DataFetcher<List<AlignedSequence>>() {
			@Override
			public List<AlignedSequence> get(DataFetchingEnvironment environment) {
				List<Map<String, String>> seqs = environment.getArgument("sequences");
				if (seqs.size() > MAXIMUM_SEQUENCES_PER_PAYLOAD) {
					throw new NumSequencesLimitExceededException(String.format(
							"Too many sequences submitted in one request. (%d > %d)",
							seqs.size(), MAXIMUM_SEQUENCES_PER_PAYLOAD));
				}
				List<Sequence> seqList = toSequenceList(seqs);

				return Aligner.parallelAlign(seqList);
			}

		};
	}

	private static Set<Gene> extractKnownGenes(List<String> mutations) {
		Set<Gene> knownGenes = EnumSet.noneOf(Gene.class);
		int numGenes = Gene.values().length;
		for (String mutText : mutations) {
			Gene gene = Mutation.extractGene(mutText);
			knownGenes.add(gene);
			if (knownGenes.size() == numGenes) {
				break;
			}
		}
		return knownGenes;
	}

	private static List<Object> prepareMutationsAnalysisData(List<String> mutations) {
		List<Object> result = new ArrayList<>();
		result.add(extractKnownGenes(mutations));
		result.add(new MutationSet(null, mutations));
		return result;
	}

	private static DataFetcher<List<Gene>> geneDataFetcher = new DataFetcher<List<Gene>>() {
		@Override
		public List<Gene> get(DataFetchingEnvironment environment) {
			List<Gene> genes = environment.getArgument("names");
			if (genes == null || genes.isEmpty()) {
				return Arrays.asList(Gene.values());
			}
			else {
				return genes;
			}
		}
	};

	private static DataFetcher<Boolean> rootDataFetcher = new DataFetcher<Boolean>() {
		@Override
		public Boolean get(DataFetchingEnvironment environment) {
			return true;
		}
	};

	private static DataFetcher<List<String>> mutationPrevalenceSubtypesDataFetcher = new DataFetcher<List<String>>() {
		@Override
		public List<String> get(DataFetchingEnvironment environment) {
			return MutationPrevalences.getAllTypes();
		}
	};

	// https://github.com/facebook/relay/issues/112
	public static GraphQLObjectType oViewer = newObject()
		.name("Viewer")
		.field(newFieldDefinition()
			.type(oHivdbVersion)
			.name("currentVersion")
			.description("Current HIVDB algorithm version.")
			.dataFetcher(currentHIVDBVersionFetcher)
			.build())
		.field(newFieldDefinition()
			.type(oSierraVersion)
			.name("currentProgramVersion")
			.description("Current Sierra program version.")
			.dataFetcher(currentSierraVersionFetcher)
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oSequenceAnalysis))
			.name("sequenceAnalysis")
			.description("Analyze sequences and output results.")
			.argument(newArgument()
				.name("sequences")
				.type(new GraphQLList(iUnalignedSequence))
				.description("Sequences to be analyzed.")
				.build())
			.dataFetcher(getSequenceAnalysisDataFetcher())
			.build())
		.field(newFieldDefinition()
			.type(oMutationsAnalysis)
			.name("mutationsAnalysis")
			.description("Analyze a list of mutations belong to a single sequence and output result.")
			.argument(newArgument()
				.name("mutations")
				.type(new GraphQLList(GraphQLString))
				.description("Mutations to be analyzed.")
				.build())
			.dataFetcher(env -> {
				List<String> mutations = env.getArgument("mutations");
				return prepareMutationsAnalysisData(mutations);
			}))
		.field(field -> field
			.type(new GraphQLList(oMutationsAnalysis))
			.name("patternAnalysis")
			.description(
				"Analyze mutation patterns (multiple lists of mutations) and output result.\n" +
				"The output list will be in the same order as the input list.")
			.argument(newArgument()
				.name("patterns")
				.type(new GraphQLList(new GraphQLList(GraphQLString)))
				.description("Lists of mutations to be analyzed.")
				.build())
			.dataFetcher(env -> {
				List<List<String>> patterns = env.getArgument("patterns");
				return patterns
					.stream()
					.map(mutations -> prepareMutationsAnalysisData(mutations))
					.collect(Collectors.toList());
				
			}))
		.field(newFieldDefinition()
			.type(new GraphQLList(oGene))
			.name("genes")
			.description("List all supported genes.")
			.argument(newArgument()
				.name("names")
				.type(new GraphQLList(oGeneEnum))
				.description("Genes to be requested.")
				.build())
			.dataFetcher(geneDataFetcher)
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oMutationPrevalenceSubtype))
			.name("mutationPrevalenceSubtypes")
			.description("List all supported HIV-1 subtypes by mutation prevalence.")
			.dataFetcher(mutationPrevalenceSubtypesDataFetcher)
			.build())
		.build();

	public static GraphQLObjectType oRoot = newObject()
		.name("Root")
		.field(newFieldDefinition()
			.type(oViewer)
			.name("viewer")
			.description("Root viewer of all accessiable objects.")
			.dataFetcher(rootDataFetcher)
			.build())
		.build();

	public static GraphQLSchema schema = GraphQLSchema.newSchema()
		.query(oRoot)
		.build();

	static {
		String maxSeqs = System.getenv("MAXIMUM_SEQUENCES_PER_PAYLOAD");
		if (maxSeqs == null) {
			maxSeqs = "120";
		}
		MAXIMUM_SEQUENCES_PER_PAYLOAD = Integer.parseInt(maxSeqs);
	}
}
