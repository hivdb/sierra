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
import static graphql.schema.GraphQLCodeRegistry.newCodeRegistry;
import static graphql.schema.FieldCoordinates.coordinates;

import edu.stanford.hivdb.hivfacts.HIV;
import edu.stanford.hivdb.viruses.Strain;

public class StrainDef {

	public static GraphQLEnumType enumStrain;

	static {
		HIV hiv = HIV.getInstance();
		GraphQLEnumType.Builder newEnumStrain =
			GraphQLEnumType.newEnum().name("StrainEnum");
		for (Strain<HIV> strain : hiv.getStrains()) {
			newEnumStrain.value(strain.toString(), strain);
		}
		enumStrain = newEnumStrain.build();
	}
	
	private static DataFetcher<String> strainDisplayDataFetcher = env -> {
		Strain<HIV> strain = env.getSource();
		return strain.getDisplayText();
	};
	
	public static GraphQLCodeRegistry strainCodeRegistry = newCodeRegistry()
		.dataFetcher(
			coordinates("Strain", "display"),
			strainDisplayDataFetcher
		)
		.build();
	
	public static GraphQLObjectType oStrain = newObject()
		.name("Strain")
		.description("HIV strain.")
		.field(field -> field
			.type(GraphQLString)
			.name("name")
			.description("Short name of this strain."))
		.field(field -> field
			.type(GraphQLString)
			.name("display")
			.description("Full name of this strain."))
		.build();

}
