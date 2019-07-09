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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.stanford.hivdb.alignment.AlignedGeneSeq;
import edu.stanford.hivdb.alignment.AlignedSequence;
import edu.stanford.hivdb.drugresistance.GeneDR;
import edu.stanford.hivdb.drugresistance.GeneDRAsi;
import edu.stanford.hivdb.genotyper.BoundGenotype;
import edu.stanford.hivdb.genotyper.HIVGenotypeResult;
import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.subtype.Subtype;

import static edu.stanford.hivdb.graphql.UnalignedSequenceDef.*;
import static edu.stanford.hivdb.graphql.MutationSetDef.*;
import static edu.stanford.hivdb.graphql.GeneDef.*;
import static edu.stanford.hivdb.graphql.FrameShiftDef.*;
import static edu.stanford.hivdb.graphql.SubtypeDef.*;
import static edu.stanford.hivdb.graphql.AlignedSequenceDef.*;
import static edu.stanford.hivdb.graphql.DrugResistanceDef.*;
import static edu.stanford.hivdb.graphql.ValidationResultDef.*;
import static edu.stanford.hivdb.graphql.SubtypeV2Def.*;
import static edu.stanford.hivdb.graphql.ExtendedFieldDefinition.newFieldDefinition;

public class SequenceAnalysisDef {

	private static DataFetcher<List<Map<String, Object>>> subtypesDataFetcher = new DataFetcher<List<Map<String, Object>>>() {
		@Override
		public List<Map<String, Object>> get(DataFetchingEnvironment environment) {
			int first = environment.getArgument("first");
			AlignedSequence alignedSeq = (AlignedSequence) environment.getSource();
			HIVGenotypeResult subtypeResult = alignedSeq.getSubtypeResult();
			if (subtypeResult == null) {
				return Collections.emptyList();
			}
			List<BoundGenotype> subtypes = subtypeResult.getAllMatches().subList(0, first);
			return subtypes
			.stream()
			.map(g -> {
				Map<String, Object> r = new HashMap<>();
				String distancePcnt = g.getDistancePcnt();
				distancePcnt = distancePcnt.substring(0, distancePcnt.length() - 1);
				r.put("name", Subtype.valueOf(g));
				r.put("distancePcnt", Double.parseDouble(distancePcnt));
				r.put("display", g.getDisplay());
				return r;
			})
			.collect(Collectors.toList());
		}
	};

	private static DataFetcher<List<BoundGenotype>> subtypesDataFetcherV2 = new DataFetcher<List<BoundGenotype>>() {
		@Override
		public List<BoundGenotype> get(DataFetchingEnvironment environment) {
			int first = environment.getArgument("first");
			AlignedSequence alignedSeq = (AlignedSequence) environment.getSource();
			HIVGenotypeResult subtypeResult = alignedSeq.getSubtypeResult();
			if (subtypeResult == null) {
				return Collections.emptyList();
			}
			return subtypeResult.getAllMatches().subList(0, first);
		}
	};

	private static DataFetcher<List<GeneDR>> drugResistanceDataFetcher = new DataFetcher<List<GeneDR>>() {
		@Override
		public List<GeneDR> get(DataFetchingEnvironment environment) {
			AlignedSequence alignedSeq = (AlignedSequence) environment.getSource();
			Map<Gene, AlignedGeneSeq> geneSeqMap = alignedSeq.getAlignedGeneSequenceMap();
			return new ArrayList<>(GeneDRAsi.getResistanceByGene(geneSeqMap).values());
		}
	};

	public static GraphQLObjectType oSequenceAnalysis = newObject()
		.name("SequenceAnalysis")
		.field(newFieldDefinition()
			.type(oUnalignedSequence)
			.name("inputSequence")
			.description("The original unaligned sequence.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("isReverseComplement")
			.description("True if the alignment result was based on the reverse complement of input sequence.")
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oGene))
			.name("availableGenes")
			.description("Available genes found in the sequence.")
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oValidationResult))
			.name("validationResults")
			.description("Validation results for this sequence.")
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oAlignedGeneSequence))
			.name("alignedGeneSequences")
			.description("List of aligned sequence distinguished by genes.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLInt)
			.name("absoluteFirstNA")
			.description(
				"The first aligned position (start from 1) in DNA " +
				"relative to whole HIV1 type B reference sequence.")
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oBoundSubtypeV2))
			.name("subtypesV2")
			.argument(newArgument()
				.type(GraphQLInt)
				.name("first")
				.defaultValue(2)
				.description(
					"Fetch only the first nth closest subtypes. Default to 2.")
				.build())
			.dataFetcher(subtypesDataFetcherV2)
			.description(
				"List of HIV1 groups or subtypes, or HIV species. " +
				"Sorted by the similarity from most to least.")
			.build())
		.field(newFieldDefinition()
			.type(oBoundSubtypeV2)
			.name("bestMatchingSubtype")
			.description(
				"The best matching subtype.")
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oBoundSubtypeV2))
			.name("genotypes")
			.argument(newArgument()
				.type(GraphQLInt)
				.name("first")
				.defaultValue(2)
				.description(
					"Fetch only the first nth closest genotypes. Default to 2.")
				.build())
			.dataFetcher(subtypesDataFetcherV2)
			.deprecate("Use field `subtypesV2` instead.")
			.description(
				"List of HIV1 groups or subtypes, or HIV species. " +
				"Sorted by the similarity from most to least.")
			.build())
		.field(newFieldDefinition()
			.type(oBoundSubtypeV2)
			.name("bestMatchingGenotype")
			.deprecate("Use field `bestMatchingSubtype` instead.")
			.description(
				"The best matching genotype.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLFloat)
			.name("mixturePcnt")
			.description(
				"Mixture pecentage of the sequence. Notes only RYMWKS " +
				"are counted.")
			.build())
		.field(newMutationSet("mutations")
			.description("All mutations found in the aligned sequence.")
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oFrameShift))
			.name("frameShifts")
			.description("All frame shifts found in the aligned sequence.")
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oDrugResistance))
			.name("drugResistance")
			.description("List of drug resistance results by genes.")
			.dataFetcher(drugResistanceDataFetcher)
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(oBoundSubtype))
			.name("subtypes")
			.deprecate("Use field `subtypesV2` instead.")
			.argument(newArgument()
				.type(GraphQLInt)
				.name("first")
				.defaultValue(2)
				.description(
					"Fetch only the first nth closest subtypes. Default to 2.")
				.build())
			.dataFetcher(subtypesDataFetcher)
			.description(
				"List of HIV1 groups or subtypes, or HIV species. " +
				"Sorted by the similarity from most to least.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("subtypeText")
			.deprecate("Use field `bestMatchingSubtype { display }` instead.")
			.description(
				"Formatted text for best matching subtype.")
			.build())
		.build();

}
