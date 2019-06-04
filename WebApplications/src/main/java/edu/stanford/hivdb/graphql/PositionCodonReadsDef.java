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
import static edu.stanford.hivdb.graphql.GeneDef.oGene;
import static edu.stanford.hivdb.graphql.GeneDef.enumGene;
import static graphql.schema.GraphQLObjectType.newObject;
import static graphql.schema.GraphQLInputObjectType.newInputObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.GeneEnum;
import edu.stanford.hivdb.mutations.PositionCodonReads;
import edu.stanford.hivdb.mutations.Strain;
import static edu.stanford.hivdb.graphql.ExtendedFieldDefinition.*;

public class PositionCodonReadsDef {

	public static PositionCodonReads toPositionCodonReads(Strain strain, Map<?, ?> input) {
		Map<String, Long> allCodonReads = (
			((List<?>) input.get("allCodonReads"))
			.stream()
			.map(o -> (Map<?, ?>) o)
			.collect(Collectors.toMap(
				o -> (String) o.get("codon"),
				o -> (Long) o.get("reads"),
				(r1, r2) -> r1 + r2,
				HashMap::new))
		);
		Long totalReads = (Long) input.get("totalReads");
		if (totalReads < 0) {
			totalReads = allCodonReads.values().stream().reduce(Long::sum).get();
		}
		return new PositionCodonReads(
			Gene.valueOf(strain, (GeneEnum) input.get("gene")),
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
			.name("aminoAcid")
			.description(
				"The corresponding amino acid."
			))
		.field(newFieldDefinition()
			.type(GraphQLFloat)
			.name("codonPercent")
			.description("Codon prevalence in HIVDB database (0.0 - 1.0)")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLFloat)
			.name("aaPercent")
			.description("Amino acid prevalence in HIVDB database (0.0 - 1.0)")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("isReference")
			.description("The amino acid is the same as the reference (consensus) amino acid.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("isDRM")
			.description(
				"The amino acid is a known drug resistance mutation (DRM).")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("isUnusual")
			.description("The amino acid is an unusual mutation.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("isApobecMutation")
			.description("The amino acid is a signature APOBEC-mediated hypermutation.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLBoolean)
			.name("isApobecDRM")
			.description(
				"The amino acid is a drug resistance mutation (DRM) might " +
				"be caused by APOBEC-mediated G-to-A hypermutation.")
			.build())
		.build();

	public static GraphQLInputObjectType iPositionCodonReads = newInputObject()
		.name("PositionCodonReadsInput")
		.description("Codon reads at a single position.")
		.field(field -> field
			.type(enumGene)
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
		.build();

	public static GraphQLObjectType oPositionCodonReads = newObject()
		.name("PositionCodonReads")
		.description("Codon reads at a single position.")
		.field(field -> field
			.type(oGene)
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
		.field(field -> field
			.type(new GraphQLList(oOneCodonReads))
			.name("codonReads")
			.description("All codon reads at this position."))
		.build();


}
