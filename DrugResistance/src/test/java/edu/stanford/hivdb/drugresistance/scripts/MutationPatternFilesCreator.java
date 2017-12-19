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

import java.util.List;
import edu.stanford.hivdb.drugresistance.database.MutationPatterns;
import edu.stanford.hivdb.drugs.DrugClass;
import edu.stanford.hivdb.utilities.MyFileUtils;

/**
 * Creates 4 files, each containing all patterns of drug-resistance mutations for a drug class
 * The patterns are listed in order of their frequency and are given a number in a header line
 * beginning with ">".
 *
 * Because this queries HIVDB_Scores, it only retrieves those patterns according to the active
 * version of the Hivdb algorithm. Need to confirm this.
 *
 * The files are output to src/test/resources/MutationPatternsFiles/
 * where they can be used for j-unit testing
 *
 */
public class MutationPatternFilesCreator {
	private static final String OUTPUT_DIR = "src/test/resources/MutationPatternsFiles/";

	public static void main (String [] args) {
		for (DrugClass drugClass : DrugClass.values()) {
			MutationPatterns mutPatterns = new MutationPatterns(drugClass);
			List<String> orderedMutPatterns = mutPatterns.getOrderedMutPatterns();

			String filePath = OUTPUT_DIR + "Patterns" + drugClass + ".txt";
			StringBuffer output = new StringBuffer();
			int patternNo = 0;
			for (String pattern : orderedMutPatterns) {
				patternNo++;
				int count = mutPatterns.getPatternCount(pattern);
				output.append(">" + drugClass + patternNo + " count:" + count + "\n");
				output.append(pattern + "\n");
			}
			MyFileUtils.writeFile(filePath, output.toString());

		}
	}




}
