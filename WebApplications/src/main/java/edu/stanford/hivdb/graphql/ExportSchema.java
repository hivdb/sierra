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

import java.io.IOException;
import java.net.URISyntaxException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import edu.stanford.hivdb.utilities.MyFileUtils;
import graphql.GraphQL;

public class ExportSchema {

	private static final ObjectMapper MAPPER = new ObjectMapper();
	private static final GraphQL GRAPHQL = GraphQL.newGraphQL(SierraSchema.schema).build();
	private static final String INTROSPECTION_QUERY =
		"query IntrospectionQuery {\n" +
		"  __schema {\n" +
		"    queryType { name }\n" +
		"    mutationType { name }\n" +
		"    subscriptionType { name }\n" +
		"    types {\n" +
		"      ...FullType\n" +
		"    }\n" +
		"    directives {\n" +
		"      name\n" +
		"      description\n" +
		"      locations\n" +
		"      args {\n" +
		"        ...InputValue\n" +
		"      }\n" +
		"    }\n" +
		"  }\n" +
		"}\n" +
		"fragment FullType on __Type {\n" +
		"  kind\n" +
		"  name\n" +
		"  description\n" +
		"  fields(includeDeprecated: true) {\n" +
		"    name\n" +
		"    description\n" +
		"    args {\n" +
		"      ...InputValue\n" +
		"    }\n" +
		"    type {\n" +
		"      ...TypeRef\n" +
		"    }\n" +
		"    isDeprecated\n" +
		"    deprecationReason\n" +
		"  }\n" +
		"  inputFields {\n" +
		"    ...InputValue\n" +
		"  }\n" +
		"  interfaces {\n" +
		"    ...TypeRef\n" +
		"  }\n" +
		"  enumValues(includeDeprecated: true) {\n" +
		"    name\n" +
		"    description\n" +
		"    isDeprecated\n" +
		"    deprecationReason\n" +
		"  }\n" +
		"  possibleTypes {\n" +
		"    ...TypeRef\n" +
		"  }\n" +
		"}\n" +
		"fragment InputValue on __InputValue {\n" +
		"  name\n" +
		"  description\n" +
		"  type { ...TypeRef }\n" +
		"  defaultValue\n" +
		"}\n" +
		"fragment TypeRef on __Type {\n" +
		"  kind\n" +
		"  name\n" +
		"  ofType {\n" +
		"    kind\n" +
		"    name\n" +
		"    ofType {\n" +
		"      kind\n" +
		"      name\n" +
		"      ofType {\n" +
		"        kind\n" +
		"        name\n" +
		"        ofType {\n" +
		"          kind\n" +
		"          name\n" +
		"          ofType {\n" +
		"            kind\n" +
		"            name\n" +
		"            ofType {\n" +
		"              kind\n" +
		"              name\n" +
		"              ofType {\n" +
		"                kind\n" +
		"                name\n" +
		"              }\n" +
		"            }\n" +
		"          }\n" +
		"        }\n" +
		"      }\n" +
		"    }\n" +
		"  }\n" +
		"}";

	public static void main(String[] args) throws IOException, URISyntaxException {
		MAPPER.enable(SerializationFeature.INDENT_OUTPUT);
		MyFileUtils.writeFile("__output/sierra-schema.json", jsonSchema());
	}

	private static String jsonSchema() throws JsonProcessingException {
		return MAPPER.writeValueAsString(executeQuery());
	}

	private static Object executeQuery() {
		return GRAPHQL.execute(INTROSPECTION_QUERY);
	}
}
