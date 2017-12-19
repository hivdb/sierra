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

package edu.stanford.hivdb.utilities;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import edu.stanford.hivdb.mutations.GenePosition;
import edu.stanford.hivdb.mutations.Mutation;
import edu.stanford.hivdb.mutations.MutationSet;

public class Json {

	private static final Gson gson;

	private static class ExtendedTypeAdapterFactory implements TypeAdapterFactory {

		public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

			final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
			Type classType = type.getType();
			if (classType == MutationSet.class) {
				return mutationSetAdapter(delegate);
			}
			else if (classType == GenePosition.class) {
				return genePositionAdapter(delegate);
			}
			return delegate;
		}

		private <T> TypeAdapter<T> mutationSetAdapter(TypeAdapter<T> delegate) {

			return new TypeAdapter<T>() {
				public void write(JsonWriter out, T value) throws IOException {
					delegate.write(out, value);
				}

				@SuppressWarnings("unchecked")
				public T read(JsonReader reader) throws IOException {
					final TypeAdapter<ArrayList<Mutation>> arrayListAdapter =
						gson.getAdapter(new TypeToken<ArrayList<Mutation>>(){});
					return (T) new MutationSet(arrayListAdapter.read(reader));
				}
			};
		}

		private <T> TypeAdapter<T> genePositionAdapter(TypeAdapter<T> delegate) {

			return new TypeAdapter<T>() {
				public void write(JsonWriter out, T value) throws IOException {
					delegate.write(out, value);
				}

				@SuppressWarnings("unchecked")
				public T read(JsonReader reader) throws IOException {
					final TypeAdapter<String> stringAdapter =
						gson.getAdapter(new TypeToken<String>(){});
					return (T) new GenePosition(stringAdapter.read(reader));
				}
			};
		}
	}

	static {
		gson = new GsonBuilder()
			.registerTypeAdapterFactory(new ExtendedTypeAdapterFactory())
			.setPrettyPrinting().create();
	}

	public static String dumps(Object object) {
		return gson.toJson(object);
	}

	public static <T> T loads(String json, Class<T> type) {
		return gson.fromJson(json, type);
	}

	public static <T> T loads(String json, Type type) {
		return gson.fromJson(json, type);
	}

	public static <T> T loads(Reader json, Class<T> type) {
		return gson.fromJson(json, type);
	}

	public static <T> T loads(Reader json, Type type) {
		return gson.fromJson(json, type);
	}

}
