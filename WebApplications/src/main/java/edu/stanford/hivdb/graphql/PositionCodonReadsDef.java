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
import static graphql.schema.GraphQLInputObjectType.newInputObject;
import static graphql.schema.GraphQLCodeRegistry.newCodeRegistry;
import static graphql.schema.FieldCoordinates.coordinates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import edu.stanford.hivdb.viruses.Strain;
import edu.stanford.hivdb.viruses.Virus;
import edu.stanford.hivdb.mutations.CodonReads;
import edu.stanford.hivdb.mutations.PositionCodonReads;
import edu.stanford.hivdb.utilities.SimpleMemoizer;

import static edu.stanford.hivdb.graphql.GeneDef.oGene;
import static edu.stanford.hivdb.graphql.GeneDef.enumGene;

public class PositionCodonReadsDef {

	public static <VirusT extends Virus<VirusT>> PositionCodonReads<VirusT> toPositionCodonReads(Strain<VirusT> strain, Map<?, ?> input) {
		Map<String, Long> allCodonReads;
		allCodonReads = (
			((List<?>) input.get("allCodonReads"))
			.stream()
			.map(o -> (Map<?, ?>) o)
			.collect(Collectors.toMap(
				o -> {
					String codon = ((String) o.get("codon")).toUpperCase();
					if (codon.length() < 3) {
						codon = (codon + "---").substring(0, 3);
					}
					return codon;
				},
				o -> o.containsKey("reads") ? ((Long) o.get("reads")) : 0L,
				(r1, r2) -> r1 + r2,
				HashMap::new))
		);
		Long totalReads = (Long) input.get("totalReads");
		if (totalReads < 0) {
			totalReads = allCodonReads.values().stream().reduce(Long::sum).get();
		}
		return new PositionCodonReads<>(
			strain.getGene((String) input.get("gene")),
			(Integer) input.get("position"),
			totalReads,
			allCodonReads);
	}

	public static GraphQLInputObjectType iOneCodonReads = newInputObject()
		.name("OneCodonReadsInput")
		.description("A single codon reads.")
		.field(field -> field
			.type(GraphQLString)
			.name("codon")
			.description(
				"The triplet codon. Insertion should be append to " +
				"the triplet NAs directly. Deletion should use '-'."))
		.field(field -> field
			.type(GraphQLLong)
			.name("reads")
			.description("Number of reads for this codon."))
		.build();
	
	private static DataFetcher<Double> aaPercentDataFetcher = env -> {
		CodonReads<?> cr = env.getSource();
		return cr.getAAPercent();
	};
	
	private static GraphQLCodeRegistry oneCodonReadsCodeRegistry = newCodeRegistry()
		.dataFetcher(
			coordinates("OneCodonReads", "aaPercent"),
			aaPercentDataFetcher
		)
		.build();

	public static GraphQLObjectType oOneCodonReads = newObject()
		.name("OneCodonReads")
		.description("A single codon reads.")
		.field(field -> field
			.type(GraphQLString)
			.name("codon")
			.description(
				"The triplet codon. Insertion should be append to " +
				"the triplet NAs directly. Deletion should use '-'."))
		.field(field -> field
			.type(GraphQLLong)
			.name("reads")
			.description("Number of reads for this codon."))
		.field(field -> field
			.type(GraphQLString)
			.name("refAminoAcid")
			.description("The corresponding reference amino acid."))
		.field(field -> field
			.type(GraphQLString)
			.name("aminoAcid")
			.description("The corresponding amino acid."))
		.field(field -> field
			.type(GraphQLFloat)
			.name("proportion")
			.description("Codon proportion of current position (0.0 - 1.0)"))
		.field(field -> field
			.type(GraphQLFloat)
			.name("codonPercent")
			.description("Codon prevalence in HIVDB database (0.0 - 1.0)"))
		.field(field -> field
			.type(GraphQLFloat)
			.name("aaPercent")
			.description("Amino acid prevalence in HIVDB database (0.0 - 1.0)"))
		.field(field -> field
			.type(GraphQLBoolean)
			.name("isReference")
			.description("The amino acid is the same as the reference (consensus) amino acid."))
		.field(field -> field
			.type(GraphQLBoolean)
			.name("isDRM")
			.description(
				"The amino acid is a known drug resistance mutation (DRM)."))
		.field(field -> field
			.type(GraphQLBoolean)
			.name("isUnusual")
			.description("The amino acid is an unusual mutation."))
		.field(field -> field
			.type(GraphQLBoolean)
			.name("isApobecMutation")
			.description("The amino acid is a signature APOBEC-mediated hypermutation."))
		.field(field -> field
			.type(GraphQLBoolean)
			.name("isApobecDRM")
			.description(
				"The amino acid is a drug resistance mutation (DRM) might " +
				"be caused by APOBEC-mediated G-to-A hypermutation."))
		.build();
	
	protected static UnaryOperator<GraphQLFieldDefinition.Builder> codonReadsArgs = field -> field
		.argument(arg -> arg
			.type(GraphQLBoolean)
			.name("mutationOnly")
			.defaultValue(false)
			.description("Exclude codons matched subtype B consensus.")
		)
		.argument(arg -> arg
			.type(GraphQLFloat)
			.name("maxProportion")
			.description("Exclude codons with proportions higher than specified value (0 - 1).")
		)
		.argument(arg -> arg
			.type(GraphQLFloat)
			.name("minProportion")
			.description("Exclude codons with proportions lower than specified value (0 - 1).")
		);
	
	public static SimpleMemoizer<GraphQLInputObjectType> iPositionCodonReads = new SimpleMemoizer<>(
		name -> (
			newInputObject()
			.name("PositionCodonReadsInput")
			.description("Codon reads at a single position.")
			.field(field -> field
				.type(enumGene.get(name))
				.name("gene")
				.description("Gene of this position."))
			.field(field -> field
				.type(GraphQLInt)
				.name("position")
				.description("Codon/amino acid position."))
			.field(field -> field
				.type(GraphQLLong)
				.name("totalReads")
				.description(
					"Total reads at this position. The field will be automatically " +
					"calculated from `allCodonReads` if it's absent.")
				.defaultValue((long) -1))
			.field(field -> field
				.type(new GraphQLList(iOneCodonReads))
				.name("allCodonReads")
				.description("All codon reads at this position."))
			.build()
		)
	);
	
	private static <VirusT extends Virus<VirusT>> DataFetcher<List<CodonReads<VirusT>>> makeOneCodonReadsDataFetcher(VirusT virusIns) {
		return env -> {
			PositionCodonReads<VirusT> pcr = env.getSource();
			return pcr.getCodonReads(
				(Boolean) env.getArgument("mutationOnly"),
				(double) env.getArgument("maxProportion"),
				(double) env.getArgument("minProportion")
			);
		};
	};
	
	public static <VirusT extends Virus<VirusT>> GraphQLCodeRegistry makePositionCodonCodeRegistry(VirusT virusIns) {
		return (
			newCodeRegistry()
			.dataFetcher(
				coordinates("PositionCodonReads", "codonReads"),
				makeOneCodonReadsDataFetcher(virusIns)
			)
			.dataFetchers(oneCodonReadsCodeRegistry)
			.build()
		);
	}

	public static SimpleMemoizer<GraphQLObjectType> oPositionCodonReads = new SimpleMemoizer<>(
		name -> (
			newObject()
			.name("PositionCodonReads")
			.description("Codon reads at a single position.")
			.field(field -> field
				.type(oGene.get(name))
				.name("gene")
				.description("Gene of this position."))
			.field(field -> field
				.type(GraphQLInt)
				.name("position")
				.description("Codon/amino acid position."))
			.field(field -> field
				.type(GraphQLLong)
				.name("totalReads")
				.description(
					"Total reads at this position. The field will be automatically " +
					"calculated from `allCodonReads` if it's absent."))
			.field(field -> codonReadsArgs.apply(field)
				.type(new GraphQLList(oOneCodonReads))
				.name("codonReads")
				.description("All codon reads at this position."))
			.build()
		)
	);

}
