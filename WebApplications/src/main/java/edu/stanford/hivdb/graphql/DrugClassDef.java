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

import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.utilities.SimpleMemoizer;
import edu.stanford.hivdb.viruses.Virus;


public class DrugClassDef {

	public static SimpleMemoizer<GraphQLEnumType> oDrugClassEnum = new SimpleMemoizer<>(
		name -> {
			Virus<?> virusIns = Virus.getInstance(name);
			GraphQLEnumType.Builder
				newDrugClassEnum = GraphQLEnumType.newEnum()
				.name("DrugClassEnum");
			for (DrugClass<?> drugClass : virusIns.getDrugClasses()) {
				String dcText = drugClass.toString();
				newDrugClassEnum.value(dcText, dcText);
			}
			return newDrugClassEnum.build();
		}
	);

	public static SimpleMemoizer<GraphQLObjectType> oDrugClass = new SimpleMemoizer<>(
		name -> (
			newObject()
			.name("DrugClass")
			.description("HIV drug class.")
			.field(field -> field
				.type(oDrugClassEnum.get(name))
				.name("name")
				.description("Name of the drug class."))
			.field(field -> field
				.type(GraphQLString)
				.name("fullName")
				.description("Full name of the drug class."))
			.field(field -> field
				.type(new GraphQLList(new GraphQLTypeReference("Drug")))
				.name("drugs")
				.description("Drugs of this drug class."))
			.field(field -> field
				.type(new GraphQLTypeReference("Gene"))
				.name("gene")
				.description("Gene the drug class belongs to."))
			.build()
		)
	);
}
