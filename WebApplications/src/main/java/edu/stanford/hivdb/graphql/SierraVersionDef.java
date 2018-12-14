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

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import static edu.stanford.hivdb.graphql.ExtendedFieldDefinition.*;

public class SierraVersionDef {

	public static GraphQLObjectType oSierraVersion = newObject()
		.name("SierraVersion")
		.description("Version of Sierra.")
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

	public static DataFetcher<Map<String, String>> currentSierraVersionFetcher = new DataFetcher<Map<String, String>>() {
		@Override
		public Map<String, String> get(DataFetchingEnvironment environment) {
			Properties prop = new Properties();
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			InputStream stream = loader.getResourceAsStream("version.properties");
			try {
				prop.load(stream);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			Map<String, String> result = new LinkedHashMap<>();
			result.put("text", prop.getProperty("version"));
			result.put("publishDate", prop.getProperty("versionDate"));
			return result;
		}
	};


}
