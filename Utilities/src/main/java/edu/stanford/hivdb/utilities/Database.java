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

import java.util.Map;
import java.util.List;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;

public interface Database {

	@FunctionalInterface
	public static interface FunctionThrowsSQLException<T, R> {
		R apply(T t) throws SQLException;
	}

	@FunctionalInterface
	public static interface BiFunctionThrowsSQLException<T, U, R> {
		R apply(T t, U u) throws SQLException;
	}

	public Connection getConnection() throws SQLException;

	public PreparedStatement prepareStatement(
			Connection conn, String sqlStatement, Object... sqlArguments)
			throws SQLException;

	public <T> T selectAll(
			String sqlStatement,
			FunctionThrowsSQLException<ResultSet, T> rsCallback,
			Object... sqlArguments) throws SQLException;

	public <T> List<T> iterate(
			String sqlStatement,
			FunctionThrowsSQLException<ResultSet, T> rsCallback,
			Object... sqlArguments) throws SQLException;

	public <K, V> Map<K, V> iterateMap(
			String sqlStatement,
			BiFunctionThrowsSQLException<ResultSet, Map<K, V>, Void> rsCb,
			Object... sqlArguments) throws SQLException;

	public Integer update(
			String sqlStatement, Object... sqlArguments)
			throws SQLException;

	public Integer insert(
			String sqlStatement, Object... sqlArguments)
			throws SQLException;
}
