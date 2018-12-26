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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.junit.Test;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class MySqlUtilsTest {

	@Test
	public void testSelectAll() throws SQLException {
		String testSql = "SELECT * FROM Sequence";

		JdbcDatabase db = spy(new JdbcDatabase(null, null, null));
		Connection conn = mock(Connection.class);
		doReturn(conn).when(db).getConnection();
		PreparedStatement stmt = mock(PreparedStatement.class);
		when(conn.prepareStatement(any(String.class))).thenReturn(stmt);

		db.selectAll(testSql, rs -> null);
		verify(conn, times(1)).prepareStatement(testSql);
	}

	@Test
	public void testIterate() throws SQLException {
		String testSql = "SELECT * FROM Sequence";

		JdbcDatabase db = spy(new JdbcDatabase(null, null, null));
		Connection conn = mock(Connection.class);
		doReturn(conn).when(db).getConnection();
		PreparedStatement stmt = mock(PreparedStatement.class);
		ResultSet thers = mock(ResultSet.class);
		when(thers.next()).thenReturn(true, true, true, false);
		when(stmt.executeQuery()).thenReturn(thers);
		when(conn.prepareStatement(any(String.class))).thenReturn(stmt);

		List<Integer> result = db.iterate(testSql, rs -> 17);
		verify(conn, times(1)).prepareStatement(testSql);
		assertEquals(Arrays.asList(new Integer[] {17, 17, 17}), result);
	}

	@Test
	public void testIterateMap() throws SQLException {
		String testSql = "SELECT * FROM Sequence";

		JdbcDatabase db = spy(new JdbcDatabase(null, null, null));
		Connection conn = mock(Connection.class);
		doReturn(conn).when(db).getConnection();
		PreparedStatement stmt = mock(PreparedStatement.class);
		ResultSet thers = mock(ResultSet.class);
		when(thers.next()).thenReturn(true, true, true, false);
		when(thers.getInt(0)).thenReturn(13, 14, 15);
		when(stmt.executeQuery()).thenReturn(thers);
		when(conn.prepareStatement(any(String.class))).thenReturn(stmt);

		Map<String, Integer> result = db.iterateMap(testSql, (rs, map) -> {
			int r = rs.getInt(0);
			map.put("" + r, r);
			return null;
		});
		verify(conn, times(1)).prepareStatement(testSql);
		Map<String, Integer> expected = new HashMap<>();
		expected.put("13", 13);
		expected.put("14", 14);
		expected.put("15", 15);

		assertEquals(expected, result);
	}

	@Test
	public void testInsert() throws SQLException {
		String testSql =
			"INSERT INTO User (name, email) VALUES ('test', 'test@example.com')";

		JdbcDatabase db = spy(new JdbcDatabase(null, null, null));
		Connection conn = mock(Connection.class);
		doReturn(conn).when(db).getConnection();
		PreparedStatement stmt = mock(PreparedStatement.class);
		when(stmt.executeUpdate()).thenReturn(125);
		when(conn.prepareStatement(any(String.class))).thenReturn(stmt);

		assertEquals((Integer) 125, db.insert(testSql));
		verify(conn, times(1)).prepareStatement(testSql);
		verify(stmt, times(1)).executeUpdate();

		doThrow(new RuntimeException("error from conn.close()")).when(conn).close();
		try {
			db.insert(testSql);
			assertTrue(false);
		} catch (RuntimeException e) {
			assertEquals("error from conn.close()", e.getMessage());
		}

		doThrow(new RuntimeException("error from stmt.close()")).when(stmt).close();
		try {
			db.insert(testSql);
			assertTrue(false);
		} catch (RuntimeException e) {
			assertEquals("error from stmt.close()", e.getMessage());
		}

		when(stmt.executeUpdate()).thenThrow(new RuntimeException("error from stmt.executeUpdate()"));
		try {
			db.insert(testSql);
			assertTrue(false);
		} catch (RuntimeException e) {
			assertEquals("error from stmt.executeUpdate()", e.getMessage());
		}

		when(conn.prepareStatement(any(String.class))).thenReturn(null);
		try {
			db.insert(testSql);
			assertTrue(false);
		} catch (NullPointerException e) {
			// pass
		}

		when(conn.prepareStatement(any(String.class))).thenThrow(new RuntimeException("error from conn.prepareStatement()"));
		try {
			db.insert(testSql);
			assertTrue(false);
		} catch (RuntimeException e) {
			assertEquals("error from conn.prepareStatement()", e.getMessage());
		}

		doThrow(new SQLException()).when(db).getConnection();
		try {
			db.insert(testSql);
			assertTrue(false);
		} catch (SQLException e) {
			// pass
		}

		doReturn(null).when(db).getConnection();
		try {
			db.insert(testSql);
			assertTrue(false);
		} catch (NullPointerException e) {
			// pass
		}
	}

	@Test
	public void testSelectAllExceptionThrowed() throws SQLException {
		String testSql = "SELECT * FROM Isolate";

		JdbcDatabase db = spy(new JdbcDatabase(null, null, null));
		Connection conn = mock(Connection.class);
		doReturn(conn).when(db).getConnection();
		PreparedStatement stmt = mock(PreparedStatement.class);
		when(conn.prepareStatement(any(String.class))).thenReturn(stmt);

		try {
			db.selectAll(testSql, rs -> {
				throw new RuntimeException();
			});
			assertTrue(false);
		}
		catch (RuntimeException e) {
			// pass
		}
		verify(conn, times(1)).prepareStatement(testSql);
	}

	@Test
	public void testPrepareStatement() throws SQLException {
		String testSql = "SELECT 1 FROM Sequence WHERE id=? AND name=?)";
		JdbcDatabase db = new JdbcDatabase(null, null, null);
		Connection conn = mock(Connection.class);
		PreparedStatement stmt = mock(PreparedStatement.class);
		when(conn.prepareStatement(any(String.class))).thenReturn(stmt);

		db.prepareStatement(
			conn, testSql, 146, "test");
		verify(conn, times(1)).prepareStatement(testSql);
		verify(stmt, times(1)).setObject(1, 146);
		verify(stmt, times(1)).setObject(2, "test");
	}

	@Test
	public void testGetConnect() throws SQLException {
		JdbcDatabase db = spy(new JdbcDatabase(null, null, null));
		DataSource ds = mock(DataSource.class);
		Connection conn = mock(Connection.class);
		when(ds.getConnection()).thenReturn(conn);
		doReturn(ds).when(db).getDataSource();

		assertEquals(conn, db.getConnection());
	}

	@Test
	public void testGetDataSource() {
		JdbcDatabase db = new JdbcDatabase("HOST", "USER", "PASSWORD");
		DataSource ds = db.getDataSource();
		DataSource ds2 = db.getDataSource();
		assertEquals(ds, ds2);
	}

	@Test
	public void testGetXXX() {
		assertEquals(JdbcDatabase.getDefault(), JdbcDatabase.getDefault());
		assertEquals(JdbcDatabase.getResultsDB(), JdbcDatabase.getResultsDB());
	}

}
