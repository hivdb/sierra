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
import edu.stanford.hivdb.utilities.SimpleMemoizer;
import edu.stanford.hivdb.viruses.Virus;


public class DrugDef {

	public static SimpleMemoizer<GraphQLEnumType> oDrugEnum = new SimpleMemoizer<>(
		name -> {
			Virus<?> virusIns = Virus.getInstance(name);
			GraphQLEnumType.Builder
				newDrugClassEnum = GraphQLEnumType.newEnum()
				.name("DrugEnum");
			for (Drug<?> drug : virusIns.getDrugs()) {
				String drugText = drug.toString();
				newDrugClassEnum.value(drugText, drugText);
			}
			return newDrugClassEnum.build();
		}
	);

	public static SimpleMemoizer<GraphQLObjectType> oDrug = new SimpleMemoizer<>(
		name -> (
			newObject()
			.name("Drug")
			.description("HIV drug.")
			.field(field -> field
				.type(oDrugEnum.get(name))
				.name("name")
				.description("Name of the drug."))
			.field(field -> field
				.type(GraphQLString)
				.name("displayAbbr")
				.description("Display abbreviation of the drug."))
			.field(field -> field
				.type(GraphQLString)
				.name("fullName")
				.description("Full name of the drug."))
			.field(field -> field
				.type(new GraphQLTypeReference("DrugClass"))
				.name("drugClass")
				.description("Drug class the drug belongs to."))
			.build()
		)
	);

}
