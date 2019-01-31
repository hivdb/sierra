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
import static edu.stanford.hivdb.graphql.ExtendedFieldDefinition.*;

import edu.stanford.hivdb.mutations.Gene;
import edu.stanford.hivdb.mutations.GeneEnum;
import edu.stanford.hivdb.mutations.Strain;

public class GeneDef {

	public static GraphQLEnumType enumGene;
	public static GraphQLEnumType enumStrain;
	public static GraphQLObjectType oGene;

	static {
		GraphQLEnumType.Builder newEnumGene =
			GraphQLEnumType.newEnum().name("EnumGene");
		for (GeneEnum gene : GeneEnum.values()) {
			newEnumGene.value(gene.toString(), gene);
		}
		enumGene = newEnumGene.build();
		
		GraphQLEnumType.Builder newEnumStrain =
			GraphQLEnumType.newEnum().name("Strain");
		for (Strain strain : Strain.values()) {
			newEnumStrain.value(strain.toString(), strain);
		}
		enumStrain = newEnumStrain.build();

		oGene = newObject()
			.name("Gene")
			.description("HIV genes. Accept PR, RT or IN.")
			.field(field -> field
				.type(enumGene)
				.name("name")
				.description("Name of the gene.")
				.dataFetcher(pipeLineDataFetcher))
			.field(field -> field
				.type(enumStrain)
				.name("strain")
				.description("HIV strain referred by this gene."))
			.field(field -> field
				.type(GraphQLString)
				.name("reference")
				.description("(Subtype B) reference sequence of the gene."))
			.field(field -> field
				.type(GraphQLString)
				.name("consensus")
				.dataFetcher(env -> ((Gene) env.getSource()).getReference())
				.deprecate("Use field `reference` instead."))
			.field(field -> field
				.type(GraphQLInt)
				.name("length")
				.description("Length of current gene."))
			.field(field -> field
				.type(new GraphQLList(new GraphQLTypeReference("DrugClass")))
				.name("drugClasses")
				.description("Supported drug classes of current gene."))
			.field(field -> field
				.type(new GraphQLList(new GraphQLTypeReference("MutationType")))
				.name("mutationTypes")
				.description("Supported mutation types of current gene."))
			.build();
	}

}
