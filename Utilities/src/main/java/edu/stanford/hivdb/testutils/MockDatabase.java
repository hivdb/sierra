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

package edu.stanford.hivdb.testutils;

import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.stanford.hivdb.utilities.Database;

public class MockDatabase implements Database {

	private static class Sql {
		private final String statement;
		private final Object[] arguments;

		private Sql(String statement, Object[] arguments) {
			this.statement = statement;
			this.arguments = arguments;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) { return true; }
			if (o == null) { return false; }
			if (!(o instanceof Sql)) { return false;}
			Sql s = (Sql) o;

			return new EqualsBuilder()
				.append(statement, s.statement)
				.append(arguments, s.arguments)
				.isEquals();
		}

		@Override
		public int hashCode() {
			return new HashCodeBuilder(12453647, 4294581)
				.append(statement)
				.append(arguments)
				.toHashCode();
		}

		@Override
		public String toString() {
			return String.format("%s %s", statement, Arrays.asList(arguments));
		}
	}

	private Map<Sql, PreparedStatement> preparedStatements = new HashMap<>();

	@Override
	public Connection getConnection() throws SQLException {
		return mock(Connection.class);
	}

	@Override
	public PreparedStatement prepareStatement(Connection conn, String sqlStatement, Object... sqlArguments)
			throws SQLException {
		PreparedStatement defaultPstmt = mock(PreparedStatement.class);
		ResultSet defaultRs = mock(ResultSet.class);
		when(defaultRs.next()).thenReturn(false);
		when(defaultPstmt.executeQuery()).thenReturn(defaultRs);
		return preparedStatements.getOrDefault(
			new Sql(sqlStatement, sqlArguments), defaultPstmt);
	}

	private Map<Sql, List<Answer<Boolean>>> resultSetAnswers = new HashMap<>();

	private void mockResultSetNext(Sql sql, ResultSet rs) throws SQLException {
		if (resultSetAnswers.get(sql).isEmpty()) {
			when(rs.next()).thenReturn(false);
		}
		else {
			Answer<Boolean> nextAnswer = resultSetAnswers.get(sql).remove(0);
			when(rs.next()).then(nextAnswer);
		}
	}

	private void mockResultSet(
			Sql sql, ResultSet rs, String[] resultFields,
			Object[] rowValues) throws SQLException {
		resultSetAnswers.get(sql).add(new Answer<Boolean>() {
			@Override
			public Boolean answer(InvocationOnMock invocation) throws SQLException {
				reset(rs);
				for (int i=0; i < resultFields.length; i ++) {
					Integer ii = i + 1;
					String f = resultFields[i];
					Object v = rowValues[i];
					String vStr = "" + v;
					Integer vInt = 0;
					Long vLong = 0l;
					Double vDouble = .0;
					Float vFloat = .0f;
					Boolean vBoolean = false;
					try {
						vInt = Integer.parseInt(vStr);
						vLong = Long.parseLong(vStr);
						vDouble = Double.parseDouble(vStr);
						vFloat = Float.parseFloat(vStr);
						vBoolean = Boolean.parseBoolean(vStr);
					}
					catch (NumberFormatException e) {
						//pass
					}
					when(rs.getString(ii)).thenReturn(vStr);
					when(rs.getString(f)).thenReturn(vStr);
					when(rs.getInt(ii)).thenReturn(vInt);
					when(rs.getInt(f)).thenReturn(vInt);
					when(rs.getLong(ii)).thenReturn(vLong);
					when(rs.getLong(f)).thenReturn(vLong);
					when(rs.getDouble(ii)).thenReturn(vDouble);
					when(rs.getDouble(f)).thenReturn(vDouble);
					when(rs.getFloat(ii)).thenReturn(vFloat);
					when(rs.getFloat(f)).thenReturn(vFloat);
					when(rs.getBoolean(ii)).thenReturn(vBoolean);
					when(rs.getBoolean(f)).thenReturn(vBoolean);
				}
				mockResultSetNext(sql, rs);
				return true;
			}
		});
	}

	public void whenSelect(
			String sqlStatement, Object[] sqlArguments,
			String[] resultFields, Object[][] resultValues) {
		PreparedStatement pstmt = mock(PreparedStatement.class);
		Sql sql = new Sql(sqlStatement, sqlArguments);
		preparedStatements.put(sql, pstmt);
		resultSetAnswers.put(sql,  new ArrayList<>());
		ResultSet rs = mock(ResultSet.class);

		try {
			for (Object[] rowValues : resultValues) {
				mockResultSet(sql, rs, resultFields, rowValues);
			}
			mockResultSetNext(sql, rs);
			when(pstmt.executeQuery()).thenReturn(rs);
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> T selectAll(String sqlStatement, FunctionThrowsSQLException<ResultSet, T> rsCallback,
			Object... sqlArguments) throws SQLException {
		PreparedStatement pstmt = prepareStatement(null, sqlStatement, sqlArguments);
		ResultSet rs = pstmt.executeQuery();
		return rsCallback.apply(rs);
	}

	@Override
	public <T> List<T> iterate(String sqlStatement, FunctionThrowsSQLException<ResultSet, T> rsCallback,
			Object... sqlArguments) throws SQLException {
		return selectAll(sqlStatement, rs -> {
			List<T> r = new ArrayList<>();
			while (rs.next()) {
				r.add(rsCallback.apply(rs));
			}
			return r;
		}, sqlArguments);
	}

	@Override
	public <K, V> Map<K, V> iterateMap(String sqlStatement,
			BiFunctionThrowsSQLException<ResultSet, Map<K, V>, Void> rsCb, Object... sqlArguments) throws SQLException {
		return selectAll(sqlStatement, rs -> {
			Map<K, V> resultMap = new LinkedHashMap<K, V>();
			while (rs.next()) {
				rsCb.apply(rs, resultMap);
			}
			return resultMap;
		}, sqlArguments);
	}

	@Override
	public Integer update(String sqlStatement, Object... sqlArguments) throws SQLException {
		// TODO Add actual code
		return null;
	}

	@Override
	public Integer insert(String sqlStatement, Object... sqlArguments) throws SQLException {
		// TODO Add actual code
		return null;
	}

}
