/*

    Copyright (C) 2018 Stanford HIVDB team

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

import graphql.GraphQLException;

public class Exceptions {

	protected static class NumSequencesLimitExceededException extends GraphQLException {
		private static final long serialVersionUID = 2748011944232838833L;

		protected NumSequencesLimitExceededException(String message) {
	        super(message);
	    }
	}

	protected static class SequenceSizeLimitExceededException extends GraphQLException {
		private static final long serialVersionUID = -4610799539466655566L;

		protected SequenceSizeLimitExceededException(String message) {
	        super(message);
	    }
	}

}
