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

import graphql.GraphQLException;
import graphql.schema.*;
import static graphql.Scalars.*;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLCodeRegistry.newCodeRegistry;
import static graphql.schema.FieldCoordinates.coordinates;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.algorithm.DrugResistanceAlgorithm;
import edu.stanford.hivdb.genotypes.BoundGenotype;
import edu.stanford.hivdb.genotypes.GenotypeResult;
import edu.stanford.hivdb.mutations.PositionCodonReads;
import edu.stanford.hivdb.seqreads.GeneSequenceReads;
import edu.stanford.hivdb.seqreads.OneCodonReadsCoverage;
import edu.stanford.hivdb.seqreads.SequenceReads;
import edu.stanford.hivdb.utilities.Json;
import edu.stanford.hivdb.utilities.SimpleMemoizer;
import edu.stanford.hivdb.viruses.Strain;
import edu.stanford.hivdb.viruses.Virus;

import static edu.stanford.hivdb.graphql.MutationSetDef.*;
import static edu.stanford.hivdb.graphql.GeneDef.*;
import static edu.stanford.hivdb.graphql.StrainDef.*;
import static edu.stanford.hivdb.graphql.ValidationResultDef.*;
import static edu.stanford.hivdb.graphql.GeneSequenceReadsDef.*;
import static edu.stanford.hivdb.graphql.DrugResistanceDef.*;
import static edu.stanford.hivdb.graphql.SubtypeV2Def.*;
import static edu.stanford.hivdb.graphql.PositionCodonReadsDef.*;
import static edu.stanford.hivdb.graphql.SequenceReadsHistogramDef.*;
import static edu.stanford.hivdb.graphql.SequenceReadsHistogramByCodonCountDef.*;
import static edu.stanford.hivdb.graphql.DrugResistanceAlgorithmDef.*;
import static edu.stanford.hivdb.graphql.DescriptiveStatisticsDef.*;

public class SequenceReadsAnalysisDef {
	
	private static <VirusT extends Virus<VirusT>> DataFetcher<List<BoundGenotype<VirusT>>> makeSubtypesDataFetcher(VirusT virusIns) {
		return env -> {
			int first = env.getArgument("first");
			SequenceReads<VirusT> seqReads = env.getSource();
			GenotypeResult<VirusT> subtypeResult = seqReads.getSubtypeResult();
			if (subtypeResult == null) {
				return Collections.emptyList();
			}
			return subtypeResult.getAllMatches().subList(0, first);
		};
	};

	private static <VirusT extends Virus<VirusT>> DataFetcher<List<GeneDR<VirusT>>> makeDrugResistanceDataFetcher(VirusT virusIns) {
		return env -> {
			SequenceReads<VirusT> seqReads = env.getSource();
			String algName = env.getArgument("algorithm");
			DrugResistanceAlgorithm<VirusT> alg = virusIns.getDrugResistAlgorithm(algName);
			List<GeneSequenceReads<VirusT>> allGeneSeqReads = seqReads.getAllGeneSequenceReads();
			return new ArrayList<>(GeneDR.newFromGeneSequenceReads(allGeneSeqReads, alg).values());
		};
	};
	
	public static <VirusT extends Virus<VirusT>> SequenceReads<VirusT> toSequenceReadsList(Map<String, Object> input) {
		String name = (String) input.get("name");
		if (name == null) {
			throw new GraphQLException("`name` is a required field but doesn't have value");
		}
		@SuppressWarnings("unchecked")
		Strain<VirusT> strain = (Strain<VirusT>) input.get("strain");
		List<PositionCodonReads<VirusT>> allReads = (
			((List<?>) input.get("allReads"))
			.stream()
			.map(pcr -> toPositionCodonReads(strain, (Map<?, ?>) pcr))
			.collect(Collectors.toList()));
		if (allReads == null) {
			throw new GraphQLException("`allReads` is a required field but doesn't have value");
		}
		Double minPrevalence = (Double) input.get("minPrevalence");
		Long minCodonCount = (Long) input.get("minCodonCount");
		if (minPrevalence == null && minCodonCount == null) {
			throw new GraphQLException(
				"Must specify prevalence cutoff via `minPrevalence` (percentage) or `minCodonCount` (fixed number)"
			);
		}
		
		return SequenceReads.fromCodonReadsTable(
			(String) input.get("name"),
			strain,
			allReads,
			minPrevalence,
			minCodonCount,
			(Long) input.get("minReadDepth"));
	}

	public static SimpleMemoizer<GraphQLInputType> iSequenceReads = new SimpleMemoizer<>(
		name -> (
			newInputObject()
			.name("SequenceReadsInput")
			.field(field -> field
				.type(GraphQLString)
				.name("name")
				.description("An identifiable name for identifying the result from the returning list."))
			.field(field -> field
				.type(enumStrain.get(name))
				.name("strain")
				.description("Strain of this sequence, choice: HIV1, HIV2A, HIV2B."))
			.field(field -> field
				.type(new GraphQLList(iPositionCodonReads.get(name)))
				.name("allReads")
				.description("List of all reads belong to this sequence."))
			.field(field -> field
				.type(GraphQLFloat)
				.name("minPrevalence")
				.defaultValue(0.)
				.description(
					"The minimal prevalence cutoff to apply on each **codon**. " +
					"Default to zero if this field was left empty or had a " +
					"negative number specified."))
			.field(field -> field
				.type(GraphQLLong)
				.name("minCodonCount")
				.defaultValue(0L)
				.description(
					"The minimal read count cutoff to apply on each **codon**. " +
					"Default to zero if this field was left empty or had a " +
					"negative number specified."))
			.field(field -> field
				.type(GraphQLLong)
				.name("minReadDepth")
				.defaultValue(1000L)
				.description(
					"The minal read depth for each **position**. Default to 1000 " +
					"if this field was left empty or had a negative number" +
					"specified."))
			.build()
		)
	);

	private static DataFetcher<Boolean> isTrimmedDataFetcher = env -> {
		OneCodonReadsCoverage<?> ocrc = env.getSource();
		return ocrc.isTrimmed();
	};
	
	private static GraphQLCodeRegistry oneCodonReadsCoverageCodeRegistry = newCodeRegistry()
		.dataFetcher(
			coordinates("OneCodonReadsCoverage", "isTrimmed"),
			isTrimmedDataFetcher
		)
		.build();

	public static SimpleMemoizer<GraphQLObjectType> oOneCodonReadsCoverage = new SimpleMemoizer<>(
		name -> (
			newObject()
			.name("OneCodonReadsCoverage")
			.field(field -> field
				.type(oGene.get(name))
				.name("gene")
				.description("Gene of this record.")
			)
			.field(field -> field
				.type(GraphQLLong)
				.name("position")
				.description("Codon position in this gene.")
			)
			.field(field -> field
				.type(GraphQLLong)
				.name("totalReads")
				.description("Total reads of this position.")
			)
			.field(field -> field
				.type(GraphQLBoolean)
				.name("isTrimmed")
				.description("This position is trimmed or not.")
			)
			.build()
		)
	);
	
	private static DataFetcher<String> internalJsonCodonReadsCoverageDataFetcher = env -> {
		SequenceReads<?> sr = env.getSource();
		return Json.dumpsUgly(
			sr
			.getCodonReadsCoverage()
			.stream()
			.map(rc -> rc.extMap())
			.collect(Collectors.toList()));
	};


	public static <VirusT extends Virus<VirusT>> GraphQLCodeRegistry makeSequenceReadsCodeRegistry(VirusT virusIns) {
		return (
			newCodeRegistry()
			.dataFetcher(
				coordinates("SequenceReadsAnalysis", "subtypes"),
				makeSubtypesDataFetcher(virusIns)
			)
			.dataFetcher(
				coordinates("SequenceReadsAnalysis", "drugResistance"),
				makeDrugResistanceDataFetcher(virusIns)
			)
			.dataFetcher(
				coordinates("SequenceReadsAnalysis", "internalJsonCodonReadsCoverage"),
				internalJsonCodonReadsCoverageDataFetcher
			)
			.dataFetcher(
				coordinates("SequenceReadsAnalysis", "histogram"),
				seqReadsHistogramDataFetcher
			)
			.dataFetcher(
				coordinates("SequenceReadsAnalysis", "histogramByCodonCount"),
				seqReadsHistogramByCodonCountDataFetcher
			)
			.dataFetcher(
				coordinates("SequenceReadsAnalysis", "mutations"),
				new MutationSetDataFetcher<>(virusIns, "mutations")
			)
			.dataFetchers(oneCodonReadsCoverageCodeRegistry)
			.dataFetchers(makeGeneSequenceReadsCodeRegistry(virusIns))
			.build()
		);
	}
			
	
	public static SimpleMemoizer<GraphQLObjectType> oSequenceReadsAnalysis = new SimpleMemoizer<>(
		virusName -> (
			newObject()
			.name("SequenceReadsAnalysis")
			.field(field -> field
				.type(GraphQLString)
				.name("name")
				.description("Name of this sequence."))
			.field(field -> field
				.type(oStrain)
				.name("strain")
				.description("Strain of this sequence."))
			.field(field -> field
				.type(GraphQLFloat)
				.name("cutoffSuggestionLooserLimit")
				.description(
					"Algorithm suggested minimal prevalence cutoff. " +
					"This cutoff is looser and may include more problematic mutations."))
			.field(field -> field
				.type(GraphQLFloat)
				.name("cutoffSuggestionStricterLimit")
				.description(
					"Algorithm suggested minimal prevalence cutoff. " +
					"This cutoff is stricter and include less problematic mutations."))
			.field(field -> field
				.type(new GraphQLList(oValidationResult))
				.name("validationResults")
				.description("Validation results for the sequence reads."))
			.field(field -> field
				.type(GraphQLFloat)
				.name("minPrevalence")
				.description(
					"The minimal prevalence cutoff applied on this sequence."
				))
			.field(field -> field
				.type(GraphQLLong)
				.name("minCodonCount")
				.description(
					"The minimal codon count cutoff applied on this sequence."
				))
			.field(field -> field
				.type(GraphQLLong)
				.name("minReadDepth")
				.description(
					"The minimal read depth for each position of the sequence reads."
				))
			.field(field -> field
				.type(new GraphQLList(oGene.get(virusName)))
				.name("availableGenes")
				.description("Available genes found in the sequence reads."))
			.field(field -> field
				.type(new GraphQLList(oGeneSequenceReads.get(virusName)))
				.name("allGeneSequenceReads")
				.description("List of sequence reads distinguished by genes."))
			.field(field -> field
				.type(new GraphQLList(oBoundSubtypeV2))
				.name("subtypes")
				.argument(arg -> arg
					.type(GraphQLInt)
					.name("first")
					.defaultValue(2)
					.description(
						"Fetch only the first nth closest subtypes. Default to 2."))
				.description(
					"List of HIV1 groups or subtypes, or HIV species. " +
					"Sorted by the similarity from most to least."))
			.field(field -> field
				.type(oBoundSubtypeV2)
				.name("bestMatchingSubtype")
				.description(
					"The best matching subtype."))
			.field(field -> field
				.type(GraphQLFloat)
				.name("mixturePcnt")
				.description(
					"Mixture pecentage of the consensus. Notes only RYMWKS " +
					"are counted."))
			.field(field -> newMutationSet(virusName, field, "mutations")
				.description("All mutations found in the sequence reads."))
			.field(field -> field
				.type(new GraphQLList(oDrugResistance.get(virusName)))
				.name("drugResistance")
				.argument(arg -> arg
					.name("algorithm")
					.type(oASIAlgorithm.get(virusName))
					.defaultValue(Virus.getInstance(virusName).getLatestDrugResistAlgorithm("HIVDB").getName())
					.description("One of the built-in ASI algorithms."))
				.description("List of drug resistance results by genes."))
			.field(oSeqReadsHistogramBuilder)
			.field(oSeqReadsHistogramByCodonCountBuilder)
			.field(field -> field
				.name("readDepthStats")
				.type(oDescriptiveStatistics)
				.description("Descriptive statistics of read depth for all positions.")
			)
			.field(field -> field
				.name("readDepthStatsDRP")
				.type(oDescriptiveStatistics)
				.description("Descriptive statistics of read depth for drug resistance positions.")
			)
			.field(field -> field
				.name("codonReadsCoverage")
				.type(new GraphQLList(oOneCodonReadsCoverage.get(virusName)))
				.description("Codon reads coverage.")
			)
			.field(field -> field
				.type(GraphQLString)
				.name("internalJsonCodonReadsCoverage")
				.description(
					"Position codon reads in this gene sequence (json formated)."))
			.build()
		)
	);

}
