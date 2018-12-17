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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.utilities.JdbcDatabase;
import edu.stanford.hivdb.utilities.Cachable;

public class Tsms {
	@Cachable.CachableField
	private static Map<DrugClass, Collection<Mutation>> tsmsByDrugClass;
	private static Collection<Mutation> allTsms;

	static {
		Cachable.setup(Tsms.class, () -> {
			try {
				populateAllTsms();
			} catch (SQLException e) {
				throw new ExceptionInInitializerError(e);
			}
		});
		allTsms = new ArrayList<>();
		tsmsByDrugClass.values().forEach(allTsms::addAll);
	}

	public static MutationSet getAllTsms(MutationSet muts) {
		return muts.intersectsWith(allTsms);
	}

	public static MutationSet getTsmsForDrugClass(DrugClass drugClass, MutationSet mutations) {
		return mutations.intersectsWith(tsmsByDrugClass.get(drugClass));
	}

	private static void populateAllTsms() throws SQLException {
		final JdbcDatabase db = JdbcDatabase.getDefault();
		final String sqlStatement = "SELECT DrugClass, Pos, AA FROM tblTsms " +
				"WHERE Include = 'Y' ORDER BY DrugClass, Pos, AA";

		tsmsByDrugClass = db.iterateMap(sqlStatement, (rs, map) -> {
			DrugClass drugClass = DrugClass.valueOf(rs.getString(1));
			int pos = rs.getInt(2);
			String aas = rs.getString(3);
			map.putIfAbsent(drugClass, new ArrayList<>());
			map.get(drugClass).add(new Mutation(drugClass.gene(), pos, aas));
			return null;
		});
	}
}
