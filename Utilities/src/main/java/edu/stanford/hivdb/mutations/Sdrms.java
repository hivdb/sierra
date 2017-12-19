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

package edu.stanford.hivdb.mutations;

import java.sql.SQLException;
import java.util.Collection;

import edu.stanford.hivdb.utilities.JdbcDatabase;
import edu.stanford.hivdb.utilities.Cachable;

public class Sdrms {
	@Cachable.CachableField
	private static Collection<Mutation> sdrms;
	private static final JdbcDatabase db;

	static {
		db = JdbcDatabase.getDefault();
		Cachable.setup(Sdrms.class, () -> {
			try {
				populateSDRMs();
			} catch (SQLException e) {
				throw new ExceptionInInitializerError(e);
			}
		});
	}

	/**
	 * Return the set of SDRMs present in the list of submitted mutations
	 * @param seqMuts
	 * @return just those mutations that are SDRMs
	 */
	public static MutationSet getSdrms(MutationSet seqMuts) {
		return seqMuts.intersectsWith(sdrms);
	}

	private static void populateSDRMs() throws SQLException {
		final String sqlStatement =
			"SELECT Gene, Pos, AAs FROM tblSDRMs ORDER BY Gene, Pos, AAs";

		sdrms = db.iterate(sqlStatement, rs -> {
			return new Mutation(
				Gene.valueOf(rs.getString("Gene")),
				rs.getInt("Pos"),
				rs.getString("AAs"));
		});
	}
}
