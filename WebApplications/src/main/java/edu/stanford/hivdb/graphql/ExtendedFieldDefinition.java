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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Set;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.PropertyDataFetcher;

public class ExtendedFieldDefinition {

	public static DataFetcher pipeLineDataFetcher = new DataFetcher() {
		@Override
		public Object get(DataFetchingEnvironment environment) {
			return environment.getSource();
		}
	};

	public static class ExtendedPropertyDataFetcher extends PropertyDataFetcher {

		private final String propertyName;

		public ExtendedPropertyDataFetcher(String propertyName) {
			super(propertyName);
			this.propertyName = propertyName;
		}

		@Override
		public Object get(DataFetchingEnvironment environment) {
			Object result = super.get(environment);
			if (result == null) {
				Object source = environment.getSource();
		        if (source == null) return null;
		        result = getPropertyViaMethod(source, propertyName);
			}
			result = postProcess(result, environment);
			if (result instanceof Set) {
				// cast all set to list
				result = new ArrayList<>((Set<?>) result);
			}
			return result;
		}

		/**
		 * This protected method provide subclasses ability to process
		 * the fetched object.
		 * @param object
		 * @return object
		 */
		protected Object postProcess(Object object, DataFetchingEnvironment environment) {
			return object;
		}

		private Object getPropertyViaMethod(Object object, String propertyName) {
	        try {
	            Method method = object.getClass().getMethod(propertyName);
	            return method.invoke(object);
	        } catch (NoSuchMethodException e) {
	        	if (propertyName == "text") {
	        		return getPropertyViaMethod(object, "toString");
	        	}
	            return getPropertyViaField(object, propertyName);
	        } catch (IllegalAccessException e) {
	            throw new RuntimeException(e);
	        } catch (InvocationTargetException e) {
	            throw new RuntimeException(e);
	        }
	    }

		private Object getPropertyViaField(Object object, String propertyName) {
			try {
				Field field = object.getClass().getField(propertyName);
				return field.get(object);
			} catch (NoSuchFieldException e) {
				return null;
	        } catch (IllegalAccessException e) {
	            throw new RuntimeException(e);
	        }
		}
	}

	public static class Builder extends GraphQLFieldDefinition.Builder {

		private String name;
		private DataFetcher dataFetcher;

		@Override
		public Builder name(String name) {
			super.name(name);
			this.name = name;
			return this;
		}

		@Override
		public Builder dataFetcher(DataFetcher dataFetcher) {
			super.dataFetcher(dataFetcher);
			this.dataFetcher = dataFetcher;
			return this;
		}

		@Override
		public GraphQLFieldDefinition build() {
			if (dataFetcher == null) {
				super.dataFetcher(new ExtendedPropertyDataFetcher(name));
			}
			return super.build();
		}
	}

	public static Builder newFieldDefinition() {
		return new Builder();
	}

}
