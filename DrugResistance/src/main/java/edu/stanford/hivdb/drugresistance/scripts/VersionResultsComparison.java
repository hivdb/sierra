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

package edu.stanford.hivdb.drugresistance.scripts;

import edu.stanford.hivdb.drugresistance.reports.TabularPatternsComparison;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.utilities.MyFileUtils;

/**
 * Compare the results obtained using the current version of the algorithm and the new version of
 * the algorithm using data in HIVDB_Results. Note: the new version of the algorithm is the
 * default "live" version of the algorithm in the HIVDB_Scores database
 */
public class VersionResultsComparison {

	public static void main (String [] args) {
		for (DrugClass drugClass : DrugClass.values()) {
			String output =
				TabularPatternsComparison.getInstance(drugClass).toString();
			String filePath = "__output/VersionComparisons/" + drugClass + ".tsv";
			MyFileUtils.writeFile(filePath, output);
			System.out.println(String.format("%s created.", filePath));
		}
	}

}
