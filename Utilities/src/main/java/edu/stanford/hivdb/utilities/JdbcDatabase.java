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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.beans.PropertyVetoException;
import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class JdbcDatabase implements Database {

	private static JdbcDatabase defaultMySqlUtils;
	private static JdbcDatabase resultsMySqlUtils;

	private static final String DEFAULT_URL =
		ProgramProperties.getProperty("mysqlHIVDB_Scores");
	private static final String DEFAULT_USER =
		ProgramProperties.getProperty("mysqlHIVDB_ScoresUser");
	private static final String DEFAULT_PWD =
		ProgramProperties.getProperty("mysqlHIVDB_ScoresPWD");

	private static final String RESULTS_URL =
		ProgramProperties.getProperty("mysqlHIVDB_Results");
	private static final String RESULTS_USER =
		ProgramProperties.getProperty("mysqlHIVDB_ResultsUser");
	private static final String RESULTS_PWD =
		ProgramProperties.getProperty("mysqlHIVDB_ResultsPWD");

	static {
		defaultMySqlUtils = new JdbcDatabase(
			DEFAULT_URL, DEFAULT_USER, DEFAULT_PWD);
		resultsMySqlUtils = new JdbcDatabase(
			RESULTS_URL, RESULTS_USER, RESULTS_PWD);
	}

	private final String JDBC_URL;
	private final String JDBC_USER;
	private final String JDBC_PWD;

	private DataSource ds;

	public JdbcDatabase(String url, String user, String pwd) {
		JDBC_URL = url;
		JDBC_USER = user;
		JDBC_PWD = pwd;
	}

	public static JdbcDatabase getDefault() {
		return defaultMySqlUtils;
	}

	public static JdbcDatabase getResultsDB() {
		return resultsMySqlUtils;
	}

	protected DataSource getDataSource() {
		if (ds == null) {
			ComboPooledDataSource cpds = new ComboPooledDataSource();
			try {
				cpds.setDriverClass("com.mysql.jdbc.Driver");
			} catch (PropertyVetoException e) {
				throw new RuntimeException(e);
			}
			cpds.setJdbcUrl(JDBC_URL);
			cpds.setUser(JDBC_USER);
			cpds.setPassword(JDBC_PWD);
			ds = cpds;
		}
		return ds;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return getDataSource().getConnection();
	}

	@Override
	public PreparedStatement prepareStatement(
			Connection conn, String sqlStatement, Object... sqlArguments)
			throws SQLException {
		PreparedStatement pstmt = conn.prepareStatement(sqlStatement);
		for (int i=0; i < sqlArguments.length; i ++) {
			pstmt.setObject(i + 1, sqlArguments[i]);
		}
		return pstmt;
	}

	@Override
	public <T> T selectAll(
			String sqlStatement,
			FunctionThrowsSQLException<ResultSet, T> rsCallback,
			Object... sqlArguments) throws SQLException {
		try (
			Connection conn = getConnection();
			PreparedStatement pstmt =
				prepareStatement(conn, sqlStatement, sqlArguments);
			ResultSet rs = pstmt.executeQuery();
		) {
			return rsCallback.apply(rs);
		}
	}

	@Override
	public <T> List<T> iterate(
			String sqlStatement,
			FunctionThrowsSQLException<ResultSet, T> rsCallback,
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
	public <K, V> Map<K, V> iterateMap(
			String sqlStatement,
			BiFunctionThrowsSQLException<ResultSet, Map<K, V>, Void> rsCb,
			Object... sqlArguments) throws SQLException {
		return selectAll(sqlStatement, rs -> {
			Map<K, V> resultMap = new LinkedHashMap<K, V>();
			while (rs.next()) {
				rsCb.apply(rs, resultMap);
			}
			return resultMap;
		}, sqlArguments);
	}

	@Override
	public Integer update(
			String sqlStatement, Object... sqlArguments)
			throws SQLException {
		try (
			Connection conn = getConnection();
			PreparedStatement pstmt =
				prepareStatement(conn, sqlStatement, sqlArguments);
		) {
			return pstmt.executeUpdate();
		}
	}

	@Override
	public Integer insert(
			String sqlStatement, Object... sqlArguments)
			throws SQLException {
		return update(sqlStatement, sqlArguments);
	}

}
