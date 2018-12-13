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

import java.util.List;

import edu.stanford.hivdb.drugs.Drug;
import edu.stanford.hivdb.drugs.DrugClass;

import static edu.stanford.hivdb.graphql.ExtendedFieldDefinition.*;

public class DrugClassDef {

	public static GraphQLEnumType oDrugClassEnum;
	public static GraphQLObjectType oDrugClass;

	private static DataFetcher<List<Drug>> drugsDataFetcher = new DataFetcher<List<Drug>>() {

		@Override
		public List<Drug> get(DataFetchingEnvironment environment) {
			return ((DrugClass) environment.getSource()).getDrugsForHivdbTesting();
		}
	};

	static {
		GraphQLEnumType.Builder
			newDrugClassEnum = GraphQLEnumType.newEnum()
			.name("DrugClassEnum");
		for (DrugClass drugClass : DrugClass.values()) {
			newDrugClassEnum.value(drugClass.toString(), drugClass);
		}
		oDrugClassEnum = newDrugClassEnum.build();

		oDrugClass = newObject()
			.name("DrugClass")
			.description("HIV drug class.")
			.field(newFieldDefinition()
				.type(oDrugClassEnum)
				.name("name")
				.description("Name of the drug class.")
				.dataFetcher(pipeLineDataFetcher)
				.build())
			.field(newFieldDefinition()
				.type(GraphQLString)
				.name("fullName")
				.description("Full name of the drug class.")
				.build())
			.field(newFieldDefinition()
				.type(new GraphQLList(new GraphQLTypeReference("Drug")))
				.name("drugs")
				.description("Drugs of this drug class.")
				.dataFetcher(drugsDataFetcher)
				.build())
			.field(newFieldDefinition()
				.type(new GraphQLTypeReference("Gene"))
				.name("gene")
				.description("Gene the drug class belongs to.")
				.build())
			.build();
	}
}
