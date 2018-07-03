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

package edu.stanford.hivdb.drugresistance.reports;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import edu.stanford.hivdb.drugresistance.database.HivdbVersion;
import edu.stanford.hivdb.drugresistance.reports.TabularRulesComparison.ComparisonDataLoader;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.testutils.MockDatabase;

public class TabularRulesComparisonTest {

	// This was intend to hard coded. Review the changes when you made an upgrade and change the version pair here
	final static private String versionPair = "7.0:8.6";

	@Test
	public void testGetInstance() {
		TabularRulesComparison instance1 = TabularRulesComparison.getInstance(versionPair, DrugClass.INSTI);
		TabularRulesComparison instance2 = TabularRulesComparison.getInstance(versionPair, DrugClass.INSTI);
		assertEquals(instance1, instance2);
	}

	@Test
	public void testToString() {
		TabularRulesComparison instance = TabularRulesComparison.getInstance(versionPair, DrugClass.INSTI);
		assertEquals(
			"Rule\tBIC\tDTG\tEVG\tRAL\tNum Diffs\tMax Diff",
			instance.toString().split("\n")[0]);
	}

	@Test
	public void testComparisonDataLoader() throws SQLException {
		MockDatabase db = new MockDatabase();
		ComparisonDataLoader dl = spy(new ComparisonDataLoader());
		dl.db = db;
		String stmt1 =
			"SELECT Gene, Pos, AA, Drug, Version, Score FROM tblScoresWithVersions " +
			"WHERE DrugClass=? AND Version in (?, ?) ORDER BY Pos, AA, Drug, Version";
		String stmt2 =
			"SELECT Rule, Drug, Version, Score FROM tblCombinationScoresWithVersions " +
			"WHERE DrugClass=? AND Version in (?, ?) ORDER BY Rule, Drug, Version";
		db.whenSelect(
			stmt1,
			new Object[] {
				DrugClass.PI.name(),
				HivdbVersion.V7_0.name(),
				HivdbVersion.V8_6.name()
			},
			new String[] {
				"Gene", "Pos", "AA", "Drug", "Version", "Score"
			},
			new Object[][] {
				{"PR", 10, "F", "DRV", "V7_0", 5},
				{"PR", 10, "F", "DRV", "V8_6", 15},
				{"PR", 10, "F", "NFV", "V8_6", 15},
				{"PR", 11, "I", "DRV", "V7_0", 5},
			});
		db.whenSelect(
			stmt1,
			new Object[] {
				DrugClass.NRTI.name(),
				HivdbVersion.V7_0.name(),
				HivdbVersion.V8_6.name()
			},
			new String[] {
				"Gene", "Pos", "AA", "Drug", "Version", "Score"
			},
			new Object[][] {
				{"RT", 40, "F", "ABC", "V7_0", 5},
				{"RT", 40, "F", "ABC", "V8_6", 5},
			});
		db.whenSelect(
			stmt2,
			new Object[] {
				DrugClass.NRTI.name(),
				HivdbVersion.V7_0.name(),
				HivdbVersion.V8_6.name()
			},
			new String[] {
				"Rule", "Drug", "Version", "Score"
			},
			new Object[][] {
				{"115F+184VI", "TDF", "V7_0", 10},
				{"184VI+74VI", "ABC", "V7_0", 15},
				{"184VI+74VI", "ABC", "V8_6", 15},
				{"210W+215FY", "LMV", "V8_6", 5},
				{"210W+215FY", "ABC", "V7_0", 10},
				{"210W+215FY", "ABC", "V8_6", 10},
				{"210W+215FY", "AZT", "V7_0", 10},
				{"210W+215FY", "AZT", "V8_6", 10}
			});
		Map<DrugClass, List<List<String>>> result = dl.load().get(versionPair);
		// For IndexOutOfBoundsException: Did you just update the HIVdb version?
		assertEquals("E40F, 5, 0, 0, 0, 0, 0, 0, 0, 0", String.join(", ", result.get(DrugClass.NRTI).get(0)));
		assertEquals("Y115F+M184IV, 0, 0, 0, 0, 0, 0, 10 => 0, 1, 10", String.join(", ", result.get(DrugClass.NRTI).get(1)));
		assertEquals("L74IV+M184IV, 15, 0, 0, 0, 0, 0, 0, 0, 0", String.join(", ", result.get(DrugClass.NRTI).get(2)));
		assertEquals("L210W+T215FY, 10, 10, 0, 0, 0, 0 => 5, 0, 1, 5", String.join(", ", result.get(DrugClass.NRTI).get(3)));
		assertTrue(result.get(DrugClass.NNRTI).isEmpty());
		assertTrue(result.get(DrugClass.INSTI).isEmpty());
		assertEquals("L10F, 0, 5 => 15, 0, 0, 0, 0 => 15, 0, 0, 2, 15", String.join(", ", result.get(DrugClass.PI).get(0)));
		assertEquals("V11I, 0, 5 => 0, 0, 0, 0, 0, 0, 0, 1, 5", String.join(", ", result.get(DrugClass.PI).get(1)));
		assertEquals("allRows", dl.getFieldName());
	}

}
