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

import edu.stanford.hivdb.drugresistance.database.HivdbVersion;

import static edu.stanford.hivdb.graphql.ExtendedFieldDefinition.*;

public class HivdbVersionDef {

	public static GraphQLObjectType oHivdbVersion = newObject()
		.name("HivdbVersion")
		.description("Version of HIVDB algorithm.")
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("text")
			.description("Version text.")
			.build())
		.field(newFieldDefinition()
			.type(GraphQLString)
			.name("publishDate")
			.description("Publish date of this version.")
			.build())
		.build();

	public static DataFetcher<HivdbVersion> currentHIVDBVersionFetcher = new DataFetcher<HivdbVersion>() {
		@Override
		public HivdbVersion get(DataFetchingEnvironment environment) {
			HivdbVersion current = HivdbVersion.getLatestVersion();
			return current;
		}
	};


}
