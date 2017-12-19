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

import edu.stanford.hivdb.drugs.Drug;
import static edu.stanford.hivdb.graphql.ExtendedFieldDefinition.*;

public class DrugDef {

	public static GraphQLEnumType oDrugEnum;
	public static GraphQLObjectType oDrug;

	static {
		GraphQLEnumType.Builder
			newDrugClassEnum = GraphQLEnumType.newEnum()
			.name("DrugEnum");
		for (Drug drug : Drug.values()) {
			newDrugClassEnum.value(drug.toString(), drug);
		}
		oDrugEnum = newDrugClassEnum.build();

		oDrug = newObject()
			.name("Drug")
			.description("HIV drug.")
			.field(newFieldDefinition()
				.type(oDrugEnum)
				.name("name")
				.description("Name of the drug.")
				.dataFetcher(pipeLineDataFetcher)
				.build())
			.field(newFieldDefinition()
				.type(GraphQLString)
				.name("displayAbbr")
				.description("Display abbreviation of the drug.")
				.build())
			.field(newFieldDefinition()
				.type(GraphQLString)
				.name("fullName")
				.description("Full name of the drug.")
				.build())
			.field(newFieldDefinition()
				.type(new GraphQLTypeReference("DrugClass"))
				.name("drugClass")
				.description("Drug class the drug belongs to.")
				.build())
			.build();
	}

}
