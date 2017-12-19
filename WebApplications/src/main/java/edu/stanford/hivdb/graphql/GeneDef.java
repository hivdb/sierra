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

public class GeneDef {

	public static GraphQLEnumType oGeneEnum;
	public static GraphQLObjectType oGene;

	static {
		GraphQLEnumType.Builder newGeneEnum =
			GraphQLEnumType.newEnum().name("GeneEnum");
		for (Gene gene : Gene.values()) {
			newGeneEnum.value(gene.toString(), gene);
		}
		oGeneEnum = newGeneEnum.build();

	oGene = newObject()
		.name("Gene")
		.description("HIV genes. Accept PR, RT or IN.")
		.field(newFieldDefinition()
			.type(oGeneEnum)
			.name("name")
			.description("Name of the gene.")
			.dataFetcher(pipeLineDataFetcher)
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("consensus")
			.description("(Type B) consensus sequence of the gene.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLInt)
			.name("length")
			.description("Length of current gene.")
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(new GraphQLTypeReference("DrugClass")))
			.name("drugClasses")
			.description("Supported drug classes of current gene.")
			.build())
		.field(newFieldDefinition()
			.type(new GraphQLList(new GraphQLTypeReference("MutationType")))
			.name("mutationTypes")
			.description("Supported mutation types of current gene.")
			.build())
		.build();
	}

}
