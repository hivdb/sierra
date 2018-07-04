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

public class MutationStatsDef {

	public static GraphQLObjectType oMutationStats;

	static {
		oMutationStats = newObject()
			.name("MutationStats")
			.description("Statistics of different groups of mutations.")
			.field(newFieldDefinition()
				.type(GraphQLFloat)
				.name("minPrevalence")
				.description("The cutoff used by sequence reads.")
				.build())
			.field(newFieldDefinition()
				.type(GraphQLFloat)
				.name("numUsualMutations")
				.description("Number of usual mutations.")
				.build())
			.field(newFieldDefinition()
				.type(GraphQLFloat)
				.name("numUnusualMutations")
				.description("Number of unusual mutations.")
				.build())
			.field(newFieldDefinition()
				.type(GraphQLFloat)
				.name("numDRMs")
				.description("Number of drug resistance mutations.")
				.build())
			.field(newFieldDefinition()
				.type(GraphQLFloat)
				.name("numSDRMs")
				.description("Number of major drug resistance mutations (SDRMs for PR/RT, Major DRMs for IN).")
				.build())
			.field(newFieldDefinition()
				.type(GraphQLFloat)
				.name("numStopCodons")
				.description("Number of mutations contained stop codons.")
				.build())
			.field(newFieldDefinition()
				.type(GraphQLFloat)
				.name("numApobecMutations")
				.description("Number of signature APOBEC mutations.")
				.build())
			.field(newFieldDefinition()
				.type(GraphQLFloat)
				.name("numApobecDRMs")
				.description("Number of drug resistance mutations might caused by G-A hypermutation.")
				.build())
			.build();
	}

}
