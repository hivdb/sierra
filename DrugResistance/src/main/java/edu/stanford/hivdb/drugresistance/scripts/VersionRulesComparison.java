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

import edu.stanford.hivdb.drugresistance.database.HivdbVersion;
import edu.stanford.hivdb.drugresistance.reports.TabularRulesComparison;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.utilities.MyFileUtils;


/**
 *
 * The main method in this class compares the scores and combination scores for two
 *   versions of the HivDb algorithm. The CURRENT_VERSION which is usually what is
 *   currently running on the web and the NEW_VERSION which is the latest version being
 *   tested.
 * The output files are 'temporary files' written to the DrugResistance directory
 *   and are named "AllScoreChanges_CURRENTVERSION__NEWVERSION_DATE.tsv"
 *
 */
public class VersionRulesComparison {

	public static void main (String [] args) {
		for (DrugClass drugClass : DrugClass.values()) {
			String output =
				TabularRulesComparison.getInstance(HivdbVersion.getPrevMajorVersion() + ":" + HivdbVersion.getLatestVersion(), drugClass).toString();
			String filePath = "__output/VersionComparisons/" + drugClass + "-Rules.tsv";
			MyFileUtils.writeFile(filePath, output);
			System.out.println(String.format("%s created.", filePath));
		}
	}

}
