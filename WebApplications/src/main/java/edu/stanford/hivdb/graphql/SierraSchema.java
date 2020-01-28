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
import static graphql.schema.GraphQLCodeRegistry.newCodeRegistry;
import static graphql.schema.FieldCoordinates.coordinates;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.mutations.MutationSet;
import edu.stanford.hivdb.seqreads.SequenceReads;
import edu.stanford.hivdb.sequences.AlignedSequence;
import edu.stanford.hivdb.sequences.NucAminoAligner;
import edu.stanford.hivdb.sequences.Sequence;
import edu.stanford.hivdb.viruses.Gene;
import edu.stanford.hivdb.viruses.Strain;

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

	private static DataFetcher<List<AlignedSequence<HIV>>> sequenceAnalysisDataFetcher = env -> {
		List<Map<String, String>> seqs = env.getArgument("sequences");
		if (seqs.size() > MAXIMUM_SEQUENCES_PER_PAYLOAD) {
			throw new NumSequencesLimitExceededException(String.format(
				"Too many sequences submitted in one request. (%d > %d)",
				seqs.size(), MAXIMUM_SEQUENCES_PER_PAYLOAD));
		}
		HIV hiv = HIV.getInstance();
		List<Sequence> seqList = toSequenceList(seqs);
		return NucAminoAligner.getInstance(hiv).parallelAlign(seqList);
	};

	private static DataFetcher<List<SequenceReads<HIV>>> sequenceReadsAnalysisDataFetcher = env -> {
		List<Map<String, Object>> seqReads = env.getArgument("sequenceReads");
		return (
			seqReads.stream()
			.map(s -> toSequenceReadsList(s))
			.collect(Collectors.toList()));
	};

	private static Pair<Set<Gene<HIV>>, MutationSet<HIV>> prepareMutationsAnalysisData(List<String> mutations) {
		HIV hiv = HIV.getInstance();
		return Pair.of(
			extractKnownGenes(mutations),
			hiv.newMutationSet(mutations)
		);
	}
	
	private static DataFetcher<Pair<Set<Gene<HIV>>, MutationSet<HIV>>> mutationsAnalysisDataFetcher = env -> {
		List<String> mutations = env.getArgument("mutations");
		return prepareMutationsAnalysisData(mutations);
	};

	private static DataFetcher<List<Pair<Set<Gene<HIV>>, MutationSet<HIV>>>> patternAnalysisDataFetcher = env -> {
		List<List<String>> patterns = env.getArgument("patterns");
		return patterns
			.stream()
			.map(mutations -> prepareMutationsAnalysisData(mutations))
			.collect(Collectors.toList());
	};

	private static Set<Gene<HIV>> extractKnownGenes(List<String> mutations) {
		HIV hivObj = HIV.getInstance();
		// TODO: support HIV2
		Strain<HIV> strain = hivObj.getStrain("HIV1");
		Set<Gene<HIV>> knownGenes = new TreeSet<>();
		int numGenes = strain.getGenes().size();
		for (String mutText : mutations) {
			Gene<HIV> gene = hivObj.extractMutationGene(mutText);
			knownGenes.add(gene);
			if (knownGenes.size() == numGenes) {
				break;
			}
		}
		return knownGenes;
	}

	private static DataFetcher<Collection<Gene<HIV>>> geneDataFetcher = env -> {
		HIV hiv = HIV.getInstance();
		List<Gene<HIV>> genes = env.getArgument("names");
		if (genes == null || genes.isEmpty()) {
			return hiv.getStrain("HIV1").getGenes();
		}
		else {
			return genes;
		}
	};

	private static DataFetcher<List<String>> mutationPrevalenceSubtypesDataFetcher = env -> {
		HIV hivObj = HIV.getInstance();
		return hivObj.getMainSubtypes(hivObj.getStrain("HIV1"));
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
	
	public static GraphQLObjectType oRoot = newObject()
		.name("Root")
		.field(field -> field
			.type(oDrugResistanceAlgorithm)
			.name("currentVersion")
			.description("Current HIVDB algorithm version."))
		.field(field -> field
			.type(oSierraVersion)
			.name("currentProgramVersion")
			.description("Current Sierra program version."))
		.field(field -> field
			.type(new GraphQLList(oSequenceAnalysis))
			.name("sequenceAnalysis")
			.description("Analyze sequences and output results.")
			.argument(arg -> arg
				.name("sequences")
				.type(new GraphQLList(iUnalignedSequence))
				.description("Sequences to be analyzed.")))
		.field(field -> field
			.type(new GraphQLList(oSequenceReadsAnalysis))
			.name("sequenceReadsAnalysis")
			.description("Analyze sequence reads and output results.")
			.argument(newArgument()
				.name("sequenceReads")
				.type(new GraphQLList(iSequenceReads))
				.description("Sequence reads to be analyzed.")
				.build()))
		.field(field -> field
			.type(oMutationsAnalysis)
			.name("mutationsAnalysis")
			.description("Analyze a list of mutations belong to a single sequence and output result.")
			.argument(newArgument()
				.name("mutations")
				.type(new GraphQLList(GraphQLString))
				.description("Mutations to be analyzed.")
				.build()))
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
				.build()))
		.field(field -> field
			.type(new GraphQLList(oGene))
			.name("genes")
			.description("List all supported genes.")
			.argument(newArgument()
				.name("names")
				.type(new GraphQLList(enumGene))
				.description("Genes to be requested.")
				.build()))
		.field(field -> field
			.type(new GraphQLList(oMutationPrevalenceSubtype))
			.name("mutationPrevalenceSubtypes")
			.description("List all supported HIV-1 subtypes by mutation prevalence.")
		)
		.build();

	public static GraphQLCodeRegistry codeRegistry = attachDefaultDataFetcher(oRoot, newCodeRegistry()
		.dataFetcher(
			coordinates("Root", "currentVersion"),
			currentHIVDBVersionFetcher
		)
		.dataFetcher(
			coordinates("Root", "currentProgramVersion"),
			currentSierraVersionFetcher
		)
		.dataFetcher(
			coordinates("Root", "sequenceAnalysis"),
			sequenceAnalysisDataFetcher
		)
		.dataFetcher(
			coordinates("Root", "sequenceReadsAnalysis"),
			sequenceReadsAnalysisDataFetcher
		)
		.dataFetcher(
			coordinates("Root", "mutationsAnalysis"),
			mutationsAnalysisDataFetcher
		)
		.dataFetcher(
			coordinates("Root", "patternAnalysis"),
			patternAnalysisDataFetcher
		)
		.dataFetcher(
			coordinates("Root", "genes"),
			geneDataFetcher
		)
		.dataFetcher(
			coordinates("Root", "mutationPrevalenceSubtypes"),
			mutationPrevalenceSubtypesDataFetcher
		)
		.dataFetchers(sequenceAnalysisCodeRegistry)
		.dataFetchers(descriptiveStatisticsCodeRegistry)
		.dataFetchers(geneCodeRegistry)
		.dataFetchers(drugResistanceCodeRegistry)
		.dataFetchers(mutationCodeRegistry)
		.dataFetchers(mutationPrevalenceSubtypeCodeRegistry)
		.dataFetchers(mutationsAnalysisCodeRegistry)
		.dataFetchers(positionCodonCodeRegistry)
		.dataFetchers(sequenceReadsCodeRegistry)
		.dataFetchers(strainCodeRegistry)
		.build()
	);

	public static GraphQLSchema schema = GraphQLSchema.newSchema()
		.query(oRoot)
		.codeRegistry(codeRegistry)
		.build();
}