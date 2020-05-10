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
import graphql.schema.GraphQLCodeRegistry.Builder;

import static graphql.Scalars.*;
import static graphql.schema.GraphQLArgument.newArgument;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLCodeRegistry.newCodeRegistry;
import static graphql.schema.FieldCoordinates.coordinates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.hivfacts.hiv2.HIV2;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.seqreads.SequenceReads;
import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.NucAminoAligner;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.utilities.SimpleMemoizer;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.viruses.Virus;

import static edu.stanford.hivdb.graphql.ExtGraphQL.ExtPropertyDataFetcher;
import static edu.stanford.hivdb.graphql.Exceptions.*;
import static edu.stanford.hivdb.graphql.UnalignedSequenceDef.*;
import static edu.stanford.hivdb.graphql.StrainDef.*;
import static edu.stanford.hivdb.graphql.GeneDef.*;
import static edu.stanford.hivdb.graphql.MutationDef.*;
import static edu.stanford.hivdb.graphql.DrugResistanceAlgorithmDef.*;
import static edu.stanford.hivdb.graphql.SierraVersionDef.*;
import static edu.stanford.hivdb.graphql.DrugResistanceDef.*;
import static edu.stanford.hivdb.graphql.SequenceAnalysisDef.*;
import static edu.stanford.hivdb.graphql.SequenceReadsAnalysisDef.*;
import static edu.stanford.hivdb.graphql.DescriptiveStatisticsDef.*;
import static edu.stanford.hivdb.graphql.MutationPrevalenceDef.*;
import static edu.stanford.hivdb.graphql.MutationsAnalysisDef.*;
import static edu.stanford.hivdb.graphql.PositionCodonReadsDef.*;

public class SierraSchema {

	private static int MAXIMUM_SEQUENCES_PER_PAYLOAD;

	static {
		String maxSeqs = System.getenv("MAXIMUM_SEQUENCES_PER_PAYLOAD");
		if (maxSeqs == null) {
			maxSeqs = "120";
		}
		MAXIMUM_SEQUENCES_PER_PAYLOAD = Integer.parseInt(maxSeqs);
	}

	private static <VirusT extends Virus<VirusT>> DataFetcher<List<AlignedSequence<VirusT>>> makeSequenceAnalysisDataFetcher(VirusT virusIns) {
		return env -> {
			List<Map<String, String>> seqs = env.getArgument("sequences");
			if (seqs.size() > MAXIMUM_SEQUENCES_PER_PAYLOAD) {
				throw new NumSequencesLimitExceededException(String.format(
					"Too many sequences submitted in one request. (%d > %d)",
					seqs.size(), MAXIMUM_SEQUENCES_PER_PAYLOAD));
			}
			List<Sequence> seqList = toSequenceList(seqs);
			return NucAminoAligner.getInstance(virusIns).parallelAlign(seqList);
		};
	};

	private static DataFetcher<List<SequenceReads<?>>> sequenceReadsAnalysisDataFetcher = env -> {
		List<Map<String, Object>> seqReads = env.getArgument("sequenceReads");
		return (
			seqReads.stream()
			.map(s -> toSequenceReadsList(s))
			.collect(Collectors.toList()));
	};

	private static <VirusT extends Virus<VirusT>> Triple<Set<Gene<VirusT>>, MutationSet<VirusT>, String>
	prepareMutationsAnalysisData(VirusT virusIns, List<String> mutations, String name) {
		return Triple.of(
			virusIns.extractMutationGenes(mutations),
			virusIns.newMutationSet(mutations),
			name
		);
	}
	
	private static <VirusT extends Virus<VirusT>> DataFetcher<Triple<Set<Gene<VirusT>>, MutationSet<VirusT>, String>>
	makeMutationsAnalysisDataFetcher(VirusT virusIns) {
		return env -> {
			List<String> mutations = env.getArgument("mutations");
			return prepareMutationsAnalysisData(virusIns, mutations, null);
		};
	};

	private static <VirusT extends Virus<VirusT>> DataFetcher<List<Triple<Set<Gene<VirusT>>, MutationSet<VirusT>, String>>>
	makePatternAnalysisDataFetcher(VirusT virusIns) {
		return env -> {
			List<List<String>> patterns = env.getArgument("patterns");
			List<String> patternNames = env.getArgument("patternNames");
			List<Triple<Set<Gene<VirusT>>, MutationSet<VirusT>, String>> results = new ArrayList<>();
			for (int i=0; i < patterns.size(); i ++) {
				String patternName = null;
				if (patternNames != null && patternNames.size() > i) {
					patternName = patternNames.get(i);
				}
				results.add(
					prepareMutationsAnalysisData(virusIns, patterns.get(i), patternName)
				);
			}
			return results;
		};
	};

	private static <VirusT extends Virus<VirusT>> DataFetcher<Collection<Gene<VirusT>>> makeGeneDataFetcher(VirusT virusIns) {
		return env -> {
			List<Gene<VirusT>> genes = env.getArgument("names");
			if (genes == null || genes.isEmpty()) {
				return (
					virusIns
					.getStrains()
					.stream()
					.flatMap(s -> s.getGenes().stream())
					.collect(Collectors.toList())
				);
			}
			else {
				return genes;
			}
		};
	};

	private static <VirusT extends Virus<VirusT>> DataFetcher<List<String>> makeMutationPrevalenceSubtypesDataFetcher(VirusT virusIns) {
		return env -> {
			return (
				virusIns
				.getStrains()
				.stream()
				.flatMap(s -> virusIns.getMainSubtypes(s).stream())
				.collect(Collectors.toList())
			);
		};
	};

	private static GraphQLCodeRegistry attachDefaultDataFetcher(GraphQLType gqObj, GraphQLCodeRegistry codeRegistry) {
		GraphQLCodeRegistry.Builder myCodeRegistryBuilder = newCodeRegistry();
		Stack<Pair<FieldCoordinates, GraphQLType>> gqObjects = new Stack<>();
		Set<FieldCoordinates> processedCoords = new HashSet<>();
		gqObjects.push(Pair.of(null, gqObj));
		while (!gqObjects.empty()) {
			Pair<FieldCoordinates, GraphQLType> gqObjPair = gqObjects.pop();
			FieldCoordinates coord = gqObjPair.getLeft();
			gqObj = gqObjPair.getRight();
			if (gqObj instanceof GraphQLList) {
				// list object
				gqObjects.push(Pair.of(coord, ((GraphQLList) gqObj).getWrappedType()));
			}
			if (gqObj instanceof GraphQLObjectType) {
				for (GraphQLFieldDefinition gqField : ((GraphQLObjectType) gqObj).getFieldDefinitions()) {
					FieldCoordinates childCoord = coordinates(gqObj.getName(), gqField.getName());
					if (processedCoords.contains(childCoord)) {
						// This node has been processed, may be caused by included in
						// another object, e.g. referenced by a child object
						continue;
					}
					gqObjects.push(Pair.of(childCoord, gqField.getType()));
				}
			}
			if (coord != null) {
				myCodeRegistryBuilder.dataFetcher(coord, new ExtPropertyDataFetcher<>(coord.getFieldName()));
			}
			
		}
		return myCodeRegistryBuilder.dataFetchers(codeRegistry).build();
	}
	
	public static SimpleMemoizer<GraphQLObjectType> oRoot = new SimpleMemoizer<>(
		name -> {
			Supplier<graphql.schema.GraphQLObjectType.Builder> rootBuilder = () -> newObject()
			.field(field -> field
				.type(oDrugResistanceAlgorithm)
				.name("currentVersion")
				.description("Current HIVDB algorithm version."))
			.field(field -> field
				.type(oSierraVersion)
				.name("currentProgramVersion")
				.description("Current Sierra program version."))
			.field(field -> field
				.type(new GraphQLList(oSequenceAnalysis.get(name)))
				.name("sequenceAnalysis")
				.description("Analyze sequences and output results.")
				.argument(arg -> arg
					.name("sequences")
					.type(new GraphQLList(iUnalignedSequence))
					.description("Sequences to be analyzed.")))
			.field(field -> field
				.type(new GraphQLList(oSequenceReadsAnalysis.get(name)))
				.name("sequenceReadsAnalysis")
				.description("Analyze sequence reads and output results.")
				.argument(newArgument()
					.name("sequenceReads")
					.type(new GraphQLList(iSequenceReads.get(name)))
					.description("Sequence reads to be analyzed.")
					.build()))
			.field(field -> field
				.type(oMutationsAnalysis.get(name))
				.name("mutationsAnalysis")
				.description("Analyze a list of mutations belong to a single sequence and output result.")
				.argument(newArgument()
					.name("mutations")
					.type(new GraphQLList(GraphQLString))
					.description("Mutations to be analyzed.")
					.build()))
			.field(field -> field
				.type(new GraphQLList(oMutationsAnalysis.get(name)))
				.name("patternAnalysis")
				.description(
					"Analyze mutation patterns (multiple lists of mutations) and output result.\n" +
					"The output list will be in the same order as the input list.")
				.argument(arg -> arg
					.name("patterns")
					.type(new GraphQLList(new GraphQLList(GraphQLString)))
					.description("Lists of mutations to be analyzed.")
				)
				.argument(arg -> arg
					.name("patternNames")
					.type(new GraphQLList(GraphQLString))
					.description("Optional name for each mutation set. Length must be same to patterns.")
				))
			.field(field -> field
				.type(new GraphQLList(oGene.get(name)))
				.name("genes")
				.description("List all supported genes.")
				.argument(arg -> arg
					.name("names")
					.type(new GraphQLList(enumGene.get(name)))
					.description("Genes to be requested.")
				))
			.field(field -> field
				.type(new GraphQLList(oMutationPrevalenceSubtype.get(name)))
				.name("mutationPrevalenceSubtypes")
				.description("List all supported HIV-1 subtypes by mutation prevalence.")
			);
			
			return rootBuilder.get()
				.name("Root")
				.field(field -> field
					.type(rootBuilder.get().name("Viewer").build())
					.name("viewer")
					.deprecate("Use `Root` directly.")
					.description("Same as Root. Keep for compatible reason.")
				)
				.build();
		}
	);
	
	private static <VirusT extends Virus<VirusT>> Builder makeRootRegistryBuilder(VirusT virusIns, String rootNodeName) {
		return newCodeRegistry()
			.dataFetcher(
				coordinates(rootNodeName, "currentVersion"),
				makeCurrentHIVDBVersionFetcher(virusIns)
			)
			.dataFetcher(
				coordinates(rootNodeName, "currentProgramVersion"),
				currentSierraVersionFetcher
			)
			.dataFetcher(
				coordinates(rootNodeName, "sequenceAnalysis"),
				makeSequenceAnalysisDataFetcher(virusIns)
			)
			.dataFetcher(
				coordinates(rootNodeName, "sequenceReadsAnalysis"),
				sequenceReadsAnalysisDataFetcher
			)
			.dataFetcher(
				coordinates(rootNodeName, "mutationsAnalysis"),
				makeMutationsAnalysisDataFetcher(virusIns)
			)
			.dataFetcher(
				coordinates(rootNodeName, "patternAnalysis"),
				makePatternAnalysisDataFetcher(virusIns)
			)
			.dataFetcher(
				coordinates(rootNodeName, "genes"),
				makeGeneDataFetcher(virusIns)
			)
			.dataFetcher(
				coordinates(rootNodeName, "mutationPrevalenceSubtypes"),
				makeMutationPrevalenceSubtypesDataFetcher(virusIns)
			);
	}
	
	private static DataFetcher<Object> viewerDataFetcher = env -> Collections.emptyMap();
 
	private static <VirusT extends Virus<VirusT>> GraphQLCodeRegistry makeCodeRegistry(VirusT virusIns) {
		return attachDefaultDataFetcher(
			oRoot.get(virusIns.getName()),
			makeRootRegistryBuilder(virusIns, "Root")
			.dataFetcher(coordinates("Root", "viewer"), viewerDataFetcher)
			.dataFetchers(makeRootRegistryBuilder(virusIns, "Viewer").build())
			.dataFetchers(makeSequenceAnalysisCodeRegistry(virusIns))
			.dataFetchers(descriptiveStatisticsCodeRegistry)
			.dataFetchers(geneCodeRegistry)
			.dataFetchers(makeDrugResistanceCodeRegistry(virusIns))
			.dataFetchers(mutationCodeRegistry)
			.dataFetchers(makeMutationPrevalenceSubtypeCodeRegistry(virusIns))
			.dataFetchers(makeMutationsAnalysisCodeRegistry(virusIns))
			.dataFetchers(makePositionCodonCodeRegistry(virusIns))
			.dataFetchers(makeSequenceReadsCodeRegistry(virusIns))
			.dataFetchers(strainCodeRegistry)
			.build()
		);
	}
	
	// Virus class needs to be initialized first for Virus.getInstance lookup
	private static HIV hiv = HIV.getInstance();
	private static HIV2 hiv2 = HIV2.getInstance();
	
	public static GraphQLSchema schema = GraphQLSchema.newSchema()
		.query(oRoot.get("HIV"))
		.codeRegistry(makeCodeRegistry(hiv))
		.build();

	public static GraphQLSchema hiv2Schema = GraphQLSchema.newSchema()
			.query(oRoot.get("HIV2"))
			.codeRegistry(makeCodeRegistry(hiv2))
			.build();
}